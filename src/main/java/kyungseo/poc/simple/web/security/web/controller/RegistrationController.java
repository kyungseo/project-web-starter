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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.security.components.ActiveUserStore;
import kyungseo.poc.simple.web.security.components.ISecurityUserService;
import kyungseo.poc.simple.web.security.components.LoggedUser;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.Privilege;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.Role;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.service.IUserService;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@RequestMapping("/view/common/sec")
public class RegistrationController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${session.timeout}")
    private String interval;

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    public RegistrationController() {
        super();
    }

    // registration.html에서 회원 등록 후 submit 한 다음, RegistrationListener에서 사용자에게 확인 이메일을 전송한다.
    // 사용자가 받는 이메일에 다음 내용이 포함
    //   | 회원 등록을 완료하였습니다. 최종 등록을 완료하려면 아래 링크를 클릭하십시오.
    //   | http://localhost:8080/view/common/sec/registrationConfirm?token=cb114ed9-2992-4acf-847c-1c9050e54020
   @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(
            final HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token)
                throws UnsupportedEncodingException, IOException {
        Locale locale = request.getLocale();
        model.addAttribute(AppConstants.LOCALE_CHANGE_PARAM_NAME, locale.getLanguage());
        final String result = userService.validateVerificationToken(token);

        // VerificationToken이 유효하다면...
        if (result.equals(AppConstants.AUTH_TOKEN_VALID_MESSAGE)) {
            final User user = userService.getUser(token);
            // if (user.isUsing2FA()) {
            //   model.addAttribute("qr", userService.generateQRUrl(user));
            //   return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }
            authWithoutPassword(request, user);
            model.addAttribute("messageKey", "sec.message.accountVerified");
            return new ModelAndView("redirect:" + AppConstants.RESULT_NOTICE_URL, model);
        }

        // VerificationToken이 유효하지 않다면...
        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", AppConstants.AUTH_TOKEN_EXPIRED_MESSAGE.equals(result));
        model.addAttribute("token", token);
        return new ModelAndView("redirect:/view/common/sec/badUser", model);
    }

    @GetMapping("/notice")
    public ModelAndView notice(final HttpServletRequest request, final ModelMap model,
            @RequestParam("message" ) final Optional<String> message,
            @RequestParam("messageKey") final Optional<String> messageKey) {
        Locale locale = request.getLocale();
        messageKey.ifPresent( key -> {
                String msg = messages.getMessage(key, null, locale);
                LOGGER.debug("message: " + msg);
                model.addAttribute("message", msg);
            }
        );

        message.ifPresent( m ->  {
                LOGGER.debug("message: " + m);
                model.addAttribute("message", m);
            }
        );

        return new ModelAndView(AppConstants.RESULT_NOTICE_VIEW_NAME, model);
    }

    @GetMapping("/badUser")
    public ModelAndView badUser(final HttpServletRequest request, final ModelMap model,
            @RequestParam("messageKey" ) final Optional<String> messageKey,
            @RequestParam("expired" ) final Optional<String> expired,
            @RequestParam("token" ) final Optional<String> token) {
        Locale locale = request.getLocale();
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        expired.ifPresent( e -> model.addAttribute("expired", e));
        token.ifPresent( t -> model.addAttribute("token", t));

        return new ModelAndView("view/common/sec/badUser", model);
    }

    // 비밀번호 재설정 시 호출된다
    //    - forgetPassword 화면에서 submit 하면, "/api/v1/user/sec/resetPassword"가 호출되어 사용자에게 메일이 전송된다.
    // 결과적으로 사용자가 받는 이메일에 다음 내용이 포함
    //   | 비밀번호 분실 -> 재설정
    //   | http://localhost:8080/view/common/sec/changePasswordProc?token=7984e58b-b672-40ac-9b36-9596ff9fbc73
    @GetMapping("/changePasswordProc")
    public ModelAndView showChangePasswordPage(final ModelMap model, @RequestParam("token") final String token) {
        LOGGER.debug("token: " + token);

        final String result = securityUserService.validatePasswordResetToken(token);

        if (result != null) { // resetToken이 유효하지 않으면...
            String messageKey = "auth.message." + result;
            model.addAttribute("messageKey", messageKey);
            return new ModelAndView("redirect:" + AppConstants.AUTH_LOGIN_URL, model);
        }
        else { // resetToken이 유효하면...
            model.addAttribute("token", token);
            return new ModelAndView("redirect:/view/common/sec/updatePasswordProc");
        }
    }

    // 비밀번호 변경
    @GetMapping("/updatePasswordProc")
    public ModelAndView updatePassword(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey" ) final Optional<String> messageKey) {
        Locale locale = request.getLocale();
        model.addAttribute(AppConstants.LOCALE_CHANGE_PARAM_NAME, locale.getLanguage());
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        return new ModelAndView("view/common/sec/updatePassword", model);
    }

    @GetMapping("/login")
    public ModelAndView login(
            final HttpServletRequest request, final ModelMap model,
            @RequestParam("messageKey" ) final Optional<String> messageKey,
            @RequestParam("message" ) final Optional<String> message,
            @RequestParam("error" ) final Optional<String> error) {
        Locale locale = request.getLocale();
        model.addAttribute(AppConstants.LOCALE_CHANGE_PARAM_NAME, locale.getLanguage());
        messageKey.ifPresent( key -> {
                String msg = messages.getMessage(key, null, locale);
                LOGGER.debug("message: " + msg);
                model.addAttribute("message", msg);
            }
        );

        message.ifPresent( m ->  {
                LOGGER.debug("message: " + m);
                model.addAttribute("message", m);
            }
        );

        error.ifPresent( e ->  {
            LOGGER.debug("error: " + e);
            model.addAttribute("error", e);
        }
                );

        return new ModelAndView(AppConstants.AUTH_LOGIN_VIEW_NAME, model);
    }

    @RequestMapping(value = "/enableNewLoc", method = RequestMethod.GET)
    public String enableNewLoc(Locale locale, Model model, @RequestParam("token") String token) {
        final String loc = userService.isValidNewLocationToken(token);
        if (loc != null) {
            model.addAttribute("message", messages.getMessage("sec.message.newLoc.enabled", new Object[] { loc }, locale));
        }
        else {
            model.addAttribute("message", messages.getMessage("sec.message.error", null, locale));
        }

        return "redirect:" + AppConstants.AUTH_LOGIN_URL + "?" + AppConstants.LOCALE_CHANGE_PARAM_NAME + "=" + locale.getLanguage();
    }

    public void authWithoutPassword_Org(User user) {
        List<Privilege> privileges = user.getRoles()
                .stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        List<GrantedAuthority> authorities = privileges.stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 위 원 소스(authWithoutPassword_Org)는 제대로 작동하지 않는다.
    // 다음과 같이 수정함 - 2023-02-18, Kyungseo.Park@gmail.com
    public void authWithoutPassword(HttpServletRequest request, User user) throws IOException {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, getAuthorities(user.getRoles()));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 정상적 로그인 시 customAuthenticationSuccessHandler의
        // onAuthenticationSuccess에서 수행되던 작업도 함께 처리해야 함
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(Integer.parseInt(interval));

            String username;
            if (authentication.getPrincipal() instanceof User) {
                username = ((User)authentication.getPrincipal()).getEmail();
            }
            else {
                username = authentication.getName();
            }
            LoggedUser loggedUser = new LoggedUser(username, activeUserStore);
            session.setAttribute(AppConstants.SESSION_LOGGEDUSER_KEY, loggedUser);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(final Collection<Role> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<Privilege> collection = new ArrayList<>();
        for (final Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (final Privilege item : collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

}
