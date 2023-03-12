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

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.service.DeviceService;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component("customAuthenticationSuccessHandler")
public class CustomSimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Value("${session.timeout}")
    private String interval;

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private Environment env;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        LOGGER.info("CustomSimpleUrlAuthenticationSuccessHandler.onAuthenticationSuccess: Start!");

        addWelcomeCookie(gerUserName(authentication), response);
        handle(request, response, authentication);

        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(Integer.parseInt(interval));

            String username;
            if (authentication.getPrincipal() instanceof User) {
            	username = ((User) authentication.getPrincipal()).getEmail();
            }
            else {
            	username = authentication.getName();
            }
            LoggedUser user = new LoggedUser(username, activeUserStore);
            session.setAttribute(AppConstants.SESSION_LOGGEDUSER_KEY, user);
        }
        clearAuthenticationAttributes(request);

        loginNotification(authentication, request);
    }

    private void loginNotification(Authentication authentication, HttpServletRequest request) {
        try {
            if (authentication.getPrincipal() instanceof User && isGeoIpLibEnabled()) {
                deviceService.verifyDevice(((User) authentication.getPrincipal()), request);
            }
        }
        catch (Exception e) {
            LOGGER.error("디바이스 또는 위치(장소)를 확인하는 도중 오류 발생", e);
            throw new RuntimeException(e);
        }

    }

    protected void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        final String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            LOGGER.debug("Response가 이미 커밋되어 '" + targetUrl + "'로 리디렉션할 수 없습니다.");
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(final Authentication authentication) {
        boolean isUser = false;
        boolean isAdmin = false;
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // TODO admin, staff, user 판단 방식 정비할 것
        for (final GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("READ_PRIVILEGE")) {
                isUser = true;
            }
            else if (grantedAuthority.getAuthority().equals("WRITE_PRIVILEGE")) {
                isAdmin = true;
                isUser = false;
                break;
            }
        }
        if (isUser) {
        	 String username;
             if (authentication.getPrincipal() instanceof User) {
             	username = ((User)authentication.getPrincipal()).getEmail();
             }
             else {
             	username = authentication.getName();
             }

            LOGGER.debug("\tdetermineTargetUrl: " + AppConstants.USER_HOME_URL + "?user="+username);
            return AppConstants.USER_HOME_URL + "?user="+username;
        }
        else if (isAdmin) {
            LOGGER.debug("\tdetermineTargetUrl: " + AppConstants.ADMIN_HOME_URL);
            return AppConstants.ADMIN_HOME_URL;
        }
        else {
            throw new IllegalStateException();
        }
    }

    private String gerUserName(final Authentication authentication) {
        return ((User) authentication.getPrincipal()).getMembername();
    }

    private void addWelcomeCookie(final String user, final HttpServletResponse response) {
        Cookie welcomeCookie = getWelcomeCookie(user);
        response.addCookie(welcomeCookie);
    }

    private Cookie getWelcomeCookie(final String user) {
        Cookie welcomeCookie = new Cookie("welcome", user);
        welcomeCookie.setMaxAge(60 * 60 * 24 * 30); // 30 일
        //welcomeCookie.setMaxAge(60 * 10); // for test: 10 분
        return welcomeCookie;
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    private boolean isGeoIpLibEnabled() {
        return Boolean.parseBoolean(env.getProperty("geo.ip.lib.enabled"));
    }

}