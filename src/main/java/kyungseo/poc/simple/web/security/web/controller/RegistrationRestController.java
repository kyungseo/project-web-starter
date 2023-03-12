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

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kyungseo.poc.simple.web.appcore.dto.response.GenericResponse;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponseBody;
import kyungseo.poc.simple.web.appcore.util.SecurityUtil;
import kyungseo.poc.simple.web.security.components.ISecurityUserService;
import kyungseo.poc.simple.web.security.event.OnRegistrationCompleteEvent;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.VerificationToken;
import kyungseo.poc.simple.web.security.service.IUserService;
import kyungseo.poc.simple.web.security.web.dto.PasswordDto;
import kyungseo.poc.simple.web.security.web.dto.UserDto;
import kyungseo.poc.simple.web.security.web.error.InvalidOldPasswordException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/user/sec")
public class RegistrationRestController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    public RegistrationRestController() {
        super();
    }

    @PostMapping("/registration")
    public GenericResponseBody registerUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        userService.addUserLocation(registered, SecurityUtil.getClientIP(request));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), SecurityUtil.getAppUrl(request)));
        return GenericResponse.getBody("success");
    }

    @GetMapping("/resendRegistrationToken")
    public GenericResponseBody resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(SecurityUtil.getAppUrl(request), request.getLocale(), newToken, user));
        return GenericResponse.getBody(messages.getMessage("sec.message.resendToken", null, request.getLocale()));
    }

    @PostMapping("/resetPassword")
    public GenericResponseBody resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final User user = userService.findUserByEmail(userEmail);
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(SecurityUtil.getAppUrl(request), request.getLocale(), token, user));
        }
        return GenericResponse.getBody(messages.getMessage("sec.message.resetPasswordEmail", null, request.getLocale()));
    }

    @PostMapping("/savePassword")
    public GenericResponseBody savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

        if (result != null) {
            return GenericResponse.getBody(messages.getMessage("auth.message." + result, null, locale));
        }

        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent()) {
            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            return GenericResponse.getBody(messages.getMessage("sec.message.resetPasswordSuc", null, locale));
        }
        else {
            return GenericResponse.getBody(messages.getMessage("auth.message.invalid", null, locale));
        }
    }

    @PostMapping("/updatePassword")
    public GenericResponseBody changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = userService.findUserByEmail(
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return GenericResponse.getBody(messages.getMessage("sec.message.updatePasswordSuc", null, locale));
    }

    @PostMapping("/update/2fa")
    public GenericResponseBody modifyUser2FA(@RequestParam("use2FA") final boolean use2FA) throws UnsupportedEncodingException {
        final User user = userService.updateUser2FA(use2FA);
        if (use2FA) {
            return GenericResponse.getBody(userService.generateQRUrl(user));
        }
        return GenericResponse.getBody("2FA 갱신 성공");
    }

    private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/view/common/sec/registrationConfirm?token=" + newToken.getToken();
        final String message = messages.getMessage("sec.message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
        //final String url = contextPath + "/view/common/sec/changePassword?token=" + token;
        final String url = contextPath + "/view/common/sec/changePasswordProc?token=" + token;
        final String message = messages.getMessage("sec.message.resetPassword", null, locale);
        return constructEmail("비밀번호 재설정(Reset)", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

}
