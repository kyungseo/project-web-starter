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

package kyungseo.poc.simple.web.site.admin.usermgmt.web.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.appcore.util.SecurityUtil;
import kyungseo.poc.simple.web.appcore.util.SessionUtil;
import kyungseo.poc.simple.web.security.components.LoggedUser;
import lombok.RequiredArgsConstructor;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@PreAuthorize("hasRole({'ADMIN'})")
@RequestMapping("/view/admin")
@RequiredArgsConstructor // for 자동 DI
public class AdminHomeController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @GetMapping("/home")
    public String adminHome(Model model) {
        LOGGER.debug("/view/admin/home: Start!");

        LOGGER.debug("\tUser Name: " +
                SecurityContextHolder.getContext().getAuthentication().getName());
        LOGGER.debug("\tSecurityUtil > currentUsername: " +
                SecurityUtil.getCurrentUsername());
        LOGGER.debug("\tSession > username: " +
                (String) SessionUtil.getAttribute(AppConstants.SESSION_USERNAME_KEY));
        LOGGER.debug("\tSession > LoggedUser > username: " +
                ((LoggedUser) SessionUtil.getAttribute(AppConstants.SESSION_LOGGEDUSER_KEY)).getUsername());
        LOGGER.debug("\tSecurityUtil > User: " +
                SecurityUtil.getCurrentUser().getMembername());
        //LOGGER.debug("\tSESSION_SCOPE_MODEL_NAME: " +
        //        ((User) SessionUtil.getAttribute(AppConstants.SESSION_SCOPE_MODEL_NAME)));

        DateFormat dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.LONG, DateFormat.LONG, Locale.getDefault());
        model.addAttribute("serverTime", dateFormat.format(new Date()));

        return "view/admin/home";
    }

}
