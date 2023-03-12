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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.appcore.util.SecurityUtil;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class UserInterceptor implements HandlerInterceptor {

    private Logger LOGGER = LoggerFactory.getLogger(UserInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        if (SecurityUtil.isUserLogged()) {
            addToModelUserDetails(request.getSession());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model) throws Exception {
        if (model != null && !isRedirectView(model)) {
            if (SecurityUtil.isUserLogged()) {
                addToModelUserDetails(model);
            }
        }
    }

    public static boolean isRedirectView(ModelAndView mv) {
        String viewName = mv.getViewName();
        if (viewName.startsWith("redirect:/")) {
            return true;
        }

        View view = mv.getView();
        return (view != null && view instanceof SmartView && ((SmartView) view).isRedirectView());
    }

    // 세션을 기반으로 모델이 생성되기 전에 사용됨
    private void addToModelUserDetails(HttpSession session) {
        String loggedUsername = SecurityUtil.getCurrentUsername();
        session.setAttribute(AppConstants.SESSION_USERNAME_KEY, loggedUsername);
        //LOGGER.info("user(" + loggedUsername + ") session : " + session);
    }

    // 모델이 있을 때 사용
    private void addToModelUserDetails(ModelAndView model) {
        String loggedUsername = SecurityUtil.getCurrentUsername();
        model.addObject(AppConstants.SESSION_USERNAME_KEY, loggedUsername);
        //LOGGER.trace("session : " + model.getModel());
    }

}