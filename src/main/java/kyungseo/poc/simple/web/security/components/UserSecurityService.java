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

import java.util.Calendar;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.PasswordResetToken;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.PasswordResetTokenRepository;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Service
@Transactional
public class UserSecurityService implements ISecurityUserService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Override
    public String validatePasswordResetToken(String token) {
        LOGGER.info("UserSecurityService.validatePasswordResetToken: Start!");

        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        String result = !isTokenFound(passToken) ? AppConstants.AUTH_TOKEN_INVALID_MESSAGE
                : isTokenExpired(passToken) ? AppConstants.AUTH_TOKEN_EXPIRED_MESSAGE
                : null;

        LOGGER.debug("Result: " + result);
        return result;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

}