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

package kyungseo.poc.simple.web.site.user.web.controller;

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
import org.springframework.web.servlet.ModelAndView;

import kyungseo.poc.simple.web.appcore.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@PreAuthorize("hasRole({'USER'})")
@RequestMapping("/view/user")
@RequiredArgsConstructor // for 자동 DI
public class UserHomeController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @GetMapping("/home")
    public String userHome(Model model) {
        LOGGER.debug("/view/user/home: Start!");
        LOGGER.debug("\tUser Name: " + SecurityContextHolder.getContext().getAuthentication().getName());

        DateFormat dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.LONG, DateFormat.LONG, Locale.getDefault());
        model.addAttribute("serverTime", dateFormat.format(new Date()));

        model.addAttribute("userInfo", SecurityUtil.getCurrentUser());
        model.addAttribute("adminMessage","Admin 컨텐츠에 접근 가능");
        model.addAttribute("staffMessage","Staff 컨텐츠에 접근 가능");
        model.addAttribute("userMessage","User 컨텐츠에 접근 가능");

        return "view/user/home";
    }

}
