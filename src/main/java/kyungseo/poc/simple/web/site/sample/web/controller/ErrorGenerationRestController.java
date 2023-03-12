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

package kyungseo.poc.simple.web.site.sample.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kyungseo.poc.simple.web.appcore.exception.BizException;
import kyungseo.poc.simple.web.security.web.error.InvalidOldPasswordException;
import kyungseo.poc.simple.web.security.web.error.UserAlreadyExistException;
import kyungseo.poc.simple.web.security.web.error.UserNotFoundException;

/**
 * 예외 처리 테스트를 위한 RestController
 *
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@RestController
@RequestMapping("/api/vi/sample/raise/error")
public class ErrorGenerationRestController {

    @GetMapping("/400")
    public ResponseEntity<Void> raise400Error() {
        throw new InvalidOldPasswordException("BAD_REQUEST!");
    }

    @GetMapping("/404")
    public ResponseEntity<Void> raise404Error() {
        throw new UserNotFoundException("NOT_FOUND");
    }

    @GetMapping("/409")
    public ResponseEntity<Void> raise409Error() {
        throw new UserAlreadyExistException("CONFLICT");
    }

    @GetMapping("/500")
    public ResponseEntity<Void> raise500Error() {
        throw new MailAuthenticationException("INTERNAL_SERVER_ERROR");
    }

    @GetMapping("/biz")
    public ResponseEntity<Void> raiseBizError() {
        throw new BizException("INTERNAL_SERVER_ERROR");
    }

}