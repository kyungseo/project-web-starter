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

package kyungseo.poc.simple.web.security.web.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import kyungseo.poc.simple.web.appcore.dto.response.GenericResponse;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponseBody;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
// ------------------------------------------------------------------------
// 현재 예외 처리는 다음에서 핸들링하고 있다!
//   > kyungseo.poc.simple.web.site.common.base.handler.GlobalRestResponseEntityExceptionHandler
//   ※ kyungseo.poc.simple.web.site.common.base.handler.GlobalControllerExceptionHandler
// 여기서 핸들링해야 할 경우, @ControllerAdvice 주석 해제!
// ------------------------------------------------------------------------
//@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messages;

    public RestResponseEntityExceptionHandler() {
        super();
    }

    // 400
    @Override
    protected ResponseEntity<Object> handleBindException(
            final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final BindingResult result = ex.getBindingResult();
        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.BAD_REQUEST, "Invalid" + result.getObjectName(), result.getAllErrors());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // Validation 결과가 오류일때...
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final BindingResult result = ex.getBindingResult();
        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.BAD_REQUEST, "Invalid" + result.getObjectName(), result.getAllErrors());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ InvalidOldPasswordException.class })
    public ResponseEntity<Object> handleInvalidOldPassword(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);

        final List<ObjectError> allErrors = new ArrayList<>();
        final ObjectError invalidOldPasswordError = new ObjectError("invalidOldPassword",
                messages.getMessage("sec.message.invalidOldPassword", null, request.getLocale()));
        allErrors.add(invalidOldPasswordError);

        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.BAD_REQUEST, "InvalidOldPassword", allErrors);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ ReCaptchaInvalidException.class })
    public ResponseEntity<Object> handleReCaptchaInvalid(final RuntimeException ex, final WebRequest request) {
        logger.error("400 Status Code", ex);

        final List<ObjectError> allErrors = new ArrayList<>();
        final ObjectError invalidReCaptchaError = new ObjectError("invalidReCaptcha",
                messages.getMessage("sec.message.invalidReCaptcha", null, request.getLocale()));
        allErrors.add(invalidReCaptchaError);

        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.BAD_REQUEST, "InvalidReCaptcha", allErrors);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // 404
    @ExceptionHandler({ UserNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFound(final RuntimeException ex, final WebRequest request) {
        logger.error("404 Status Code", ex);

        final List<ObjectError> allErrors = new ArrayList<>();
        final ObjectError userNotFoundError = new ObjectError("userNotFound",
                messages.getMessage("sec.message.userNotFound", null, request.getLocale()));
        allErrors.add(userNotFoundError);

        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.NOT_FOUND, "UserNotFound", allErrors);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /*
    // 405
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public ResponseEntity<Object> handle405Error(HttpRequestMethodNotSupportedException ex) {
        logger.error("405 Status Code", ex);
        return new ResponseEntity<>(
                GenericResponse.getErrorBody(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.METHOD_NOT_ALLOWED);
    }
    */

    // 409
    @ExceptionHandler({ UserAlreadyExistException.class })
    public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException ex, final WebRequest request) {
        logger.error("409 Status Code", ex);

        final List<ObjectError> allErrors = new ArrayList<>();
        final ObjectError userAlreadyExistError = new ObjectError("email",
                messages.getMessage("sec.message.regError", null, request.getLocale()));
        allErrors.add(userAlreadyExistError);

        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.CONFLICT, "UserAlreadyExist", allErrors);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // 500
    @ExceptionHandler({ MailAuthenticationException.class })
    public ResponseEntity<Object> handleMail(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);

        final List<ObjectError> allErrors = new ArrayList<>();
        final ObjectError mailError = new ObjectError("emailConfig",
                messages.getMessage("sec.message.email.config.error", null, request.getLocale()));
        allErrors.add(mailError);

        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.INTERNAL_SERVER_ERROR, "MailError", allErrors);
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ ReCaptchaUnavailableException.class })
    public ResponseEntity<Object> handleReCaptchaUnavailable(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);

        final List<ObjectError> allErrors = new ArrayList<>();
        final ObjectError reCaptchaError = new ObjectError("invalidReCaptch",
                messages.getMessage("sec.message.unavailableReCaptcha", null, request.getLocale()));
        allErrors.add(reCaptchaError);

        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.INTERNAL_SERVER_ERROR, "InvalidReCaptcha", allErrors);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);

        final List<ObjectError> allErrors = new ArrayList<>();
        final ObjectError internalError = new ObjectError("internalError",
                messages.getMessage("sec.message.error", null, request.getLocale()));
        allErrors.add(internalError);

        final GenericResponseBody bodyOfResponse = GenericResponse.getErrorBody(
                HttpStatus.INTERNAL_SERVER_ERROR, "InternalError", allErrors);
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
