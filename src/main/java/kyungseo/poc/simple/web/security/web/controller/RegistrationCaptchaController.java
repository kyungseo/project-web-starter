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

package kyungseo.poc.simple.web.security.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kyungseo.poc.simple.web.appcore.dto.response.GenericResponse;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponseBody;
import kyungseo.poc.simple.web.appcore.util.SecurityUtil;
import kyungseo.poc.simple.web.security.captcha.CaptchaServiceV3;
import kyungseo.poc.simple.web.security.captcha.ICaptchaService;
import kyungseo.poc.simple.web.security.event.OnRegistrationCompleteEvent;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.service.IUserService;
import kyungseo.poc.simple.web.security.web.dto.UserDto;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/user/sec")
public class RegistrationCaptchaController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ICaptchaService captchaService;

    @Autowired
    private ICaptchaService captchaServiceV3;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public RegistrationCaptchaController() {
        super();
    }

    // Registration
    @PostMapping("/registrationCaptcha")
    public GenericResponseBody captchaRegisterUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
        final String response = request.getParameter("g-recaptcha-response");
        captchaService.processResponse(response);

        return registerNewUserHandler(accountDto, request);
    }

    // Registration reCaptchaV3
    @PostMapping("/registrationCaptchaV3")
    public GenericResponseBody captchaV3RegisterUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
        final String response = request.getParameter("response");
        captchaServiceV3.processResponse(response, CaptchaServiceV3.REGISTER_ACTION);

        return registerNewUserHandler(accountDto, request);
    }

    private GenericResponseBody registerNewUserHandler(final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("사용자 계정 정보: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), SecurityUtil.getAppUrl(request)));

        return GenericResponse.getBody("success");
    }

}
