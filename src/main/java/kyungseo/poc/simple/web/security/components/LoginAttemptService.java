/* ============================================================================
 * [ Development Templates based on Spring Boot ]
 * ----------------------------------------------------------------------------
 * Copyright 2023 Kyungseo Park <Kyungseo.Park@gmail.com>
 *
 * Original Code: https://github.com/Baeldung/spring-security-registration
 * @author Baeldung, modified by Kyungseo Park
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================================
 * Author     Date            Description
 * --------   ----------      -------------------------------------------------
 * Kyungseo   2023-03-02      initial version
 * ========================================================================= */

package kyungseo.poc.simple.web.security.components;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import kyungseo.poc.simple.web.appcore.util.SecurityUtil;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Service
public class LoginAttemptService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    // 로그인 시도 횟수 제한: 제한 횟수가 넘어가면
    // 해당 client ip에 대해서 24시간 동안 차단(bloking)
    //   현재 10회로 설정 -> login.attempt.limit=10
    @Value("${login.attempt.limit}")
    private int MAX_ATTEMPT;

    private LoadingCache<String, Integer> attemptsCache;

    @Autowired
    private HttpServletRequest request;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(final String key) {
                        return 0;
                    }
                });
    }

    // key = client ip
    public void loginFailed(final String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        }
        catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);

        LOGGER.debug("attempt count: " + attempts);
    }

    public boolean isBlocked() {
        try {
            return attemptsCache.get(SecurityUtil.getClientIP(request)) >= MAX_ATTEMPT;
        }
        catch (final ExecutionException e) {
            return false;
        }
    }

}
