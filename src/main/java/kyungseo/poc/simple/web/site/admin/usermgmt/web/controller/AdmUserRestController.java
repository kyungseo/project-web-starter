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

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import kyungseo.poc.simple.web.appcore.dto.response.GenericResponse;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponseBody;
import kyungseo.poc.simple.web.appcore.exception.BizException;
import kyungseo.poc.simple.web.appcore.util.MessageSourceUtil;
import kyungseo.poc.simple.web.security.components.ActiveUserStore;
import kyungseo.poc.simple.web.security.dto.SessionScopeModel;
import kyungseo.poc.simple.web.security.web.error.ResourceNotFoundException;
import kyungseo.poc.simple.web.site.admin.usermgmt.model.AdmUser;
import kyungseo.poc.simple.web.site.admin.usermgmt.service.AdmUserServiceImpl;
import kyungseo.poc.simple.web.site.admin.usermgmt.service.AdmUserValidationService;
import kyungseo.poc.simple.web.site.admin.usermgmt.web.dto.AdmUserDTO;
import kyungseo.poc.simple.web.site.sample.web.view.XlsxDownloadView;
import lombok.RequiredArgsConstructor;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@RestController
@PreAuthorize("hasRole({'ADMIN'})")
@RequestMapping("/api/v1/admin/usermgmt/users")
@RequiredArgsConstructor // for 자동 DI
public class AdmUserRestController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final ApplicationEventPublisher eventPublisher;
    private final ActiveUserStore activeUserStore;
    private final SessionScopeModel sessionScopeModel;
    private final MessageSourceUtil messages;
    private final Environment env;
    private final ObjectMapper objectMapper;

    private final AdmUserServiceImpl userService;
    private final AdmUserValidationService userValidationService;

    @PostMapping("/update") // 수정 처리
    public GenericResponseBody update(
            @Valid final AdmUserDTO userDto, BindingResult errors) {
        LOGGER.debug("수정 요청된 사용자 정보: {}", userDto);
        String invalidError = userValidationService.validateUser(userDto);

        if (!invalidError.isEmpty()) {
            ObjectError error = new ObjectError("globalError", invalidError);
            errors.addError(error);
            //throw new BizException("CountryError", errors.getAllErrors());
        }

        if (errors.hasErrors()) {
            LOGGER.debug("\n\n에러진입\n\n");
            return GenericResponse.getErrorBody(
                    HttpStatus.BAD_REQUEST, "사용자 수정 실패", errors.getAllErrors());
        }

        userService.update(userDto);

        return GenericResponse.getBody("사용자 수정 완료!");
    }

    @PutMapping("/{id}/deactivate") // 계정 비활성화
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponseBody> deactivateUserById(@PathVariable(value = "id") Long id) {
        activateUserById(id, false);
        return GenericResponse.success("사용자 비활성화 성공!");
    }

    @PutMapping("/{id}/activate") // 계정 활성화
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponseBody> activateUserById(@PathVariable(value = "id") Long id) {
        activateUserById(id, true);
        return GenericResponse.success("사용자 활성화 성공!");
    }

    @PutMapping("/{id}/disable2fa") // 2FA 비활성화
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponseBody> disable2faUserById(@PathVariable(value = "id") Long id) {
        enable2faUserById(id, false);
        return GenericResponse.success("Google 2FA 비활성화 성공!");
    }

    @PutMapping("/{id}/enable2fa") // 2FA 활성화
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponseBody> enable2faUserById(@PathVariable(value = "id") Long id) {
        enable2faUserById(id, true);
        return GenericResponse.success("Google 2FA 활성화 성공!");
    }

    private void activateUserById(Long id, Boolean activated) {
        AdmUser user = userService.selectOne(id);
        if (user == null) {
            throw new ResourceNotFoundException("AdmUser", "id", id);
        }
        userService.updateEnabled(id, activated);
    }

    private void enable2faUserById(Long id, Boolean enabled) {
        AdmUser user = userService.selectOne(id);
        if (user == null) {
            throw new ResourceNotFoundException("AdmUser", "id", id);
        }
        AdmUserDTO userDto = new AdmUserDTO();
        BeanUtils.copyProperties(user, userDto);
        userDto.setIsUsing2FA(enabled);
        userService.update(userDto);
    }

}
