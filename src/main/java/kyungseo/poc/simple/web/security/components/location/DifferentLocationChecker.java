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

package kyungseo.poc.simple.web.security.components.location;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.appcore.util.SecurityUtil;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.NewLocationToken;
import kyungseo.poc.simple.web.security.service.IUserService;
import kyungseo.poc.simple.web.security.web.error.UnusualLocationException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class DifferentLocationChecker implements UserDetailsChecker {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void check(UserDetails userDetails) {
        LOGGER.debug("DifferentLocationChecker.check: Start!");

        final String ip = SecurityUtil.getClientIP(request);
        final NewLocationToken token = userService.isNewLoginLocation(userDetails.getUsername(), ip);
        if (token != null) {
            eventPublisher.publishEvent(new OnDifferentLocationLoginEvent(
                    request.getLocale(), userDetails.getUsername(), ip, token, SecurityUtil.getAppUrl(request))
                );
            throw new UnusualLocationException(AppConstants.AUTH_UNUSUAL_LOCATION_MESSAGE);
        }
    }

}
