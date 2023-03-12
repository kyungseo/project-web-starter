/* ============================================================================
 * [ Development Templates based on Spring Boot ]
 * ----------------------------------------------------------------------------
 * Copyright 2023 Kyungseo Park <Kyungseo.Park@gmail.com>
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

package kyungseo.poc.simple.web.appcore.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.appcore.config.EnvironmentAwareConfig;
import kyungseo.poc.simple.web.appcore.util.SecurityUtil;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class SessionTimerInterceptor implements HandlerInterceptor {

    private Logger LOGGER = LoggerFactory.getLogger(SessionTimerInterceptor.class);

    @Autowired
    private HttpSession session;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        //LOGGER.info("[preHandle][시작 시간 확인]");
        if (! isInActiveEnabled()) return true;

        long startTime = System.currentTimeMillis();
        request.setAttribute("executionTime", startTime);

        if (SecurityUtil.isUserLogged()) {
            session = request.getSession();
            //LOGGER.info("이 Session의 마지막 요청 이후 시간: {} ms", System.currentTimeMillis() - request.getSession().getLastAccessedTime());
            if (System.currentTimeMillis() - session.getLastAccessedTime() > getInActiveMaxTime()) {
                // FIXME RememberMe 설정되어 있을 경우 그냥 return!
                LOGGER.warn("비활성 Session으로 인해 로그아웃");
                SecurityContextHolder.clearContext();
                request.logout();
                response.sendRedirect(AppConstants.AUTH_LOGOUT_API);
            }
        }

        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView model) throws Exception {
        //LOGGER.info("[postHandle][실행 시간 확인]");
        //long startTime = (Long) request.getAttribute("executionTime");
        //LOGGER.info("요청을 처리하기 위한 실행 시간: {} ms", System.currentTimeMillis() - startTime);
    }

    private boolean isInActiveEnabled() {
        String envValue = EnvironmentAwareConfig.getProperty("session.inactive.timer.enabled");
        if (StringUtils.isNotEmpty(envValue)) {
            return Boolean.parseBoolean(envValue);
        }
        return AppConstants.TIMER_INACTIVE_ENABLED;
    }

    private long getInActiveMaxTime() {
        String envValue = EnvironmentAwareConfig.getProperty("session.inactive.timer.max.time");
        if (StringUtils.isNotEmpty(envValue)) {
            return Long.parseLong(envValue);
        }
        return AppConstants.TIMER_INACTIVE_SESSION_MAXTIME;
    }

}