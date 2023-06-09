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

package kyungseo.poc.simple.web.appcore.dto.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class GenericResponse {

    private final static Logger LOGGER = LoggerFactory.getLogger(GenericResponse.class);

    // ------------------------------------------------------------------------
    // Success
    // ------------------------------------------------------------------------

    public static ResponseEntity<GenericResponseBody> success() {
        return success(HttpStatus.OK, StringUtils.EMPTY, Collections.emptyList());
    }

    public static ResponseEntity<GenericResponseBody> success(String message) {
        return success(HttpStatus.OK, message, Collections.emptyList());
    }

    public static ResponseEntity<GenericResponseBody> success(HttpStatus status, String message) {
        return success(status, message, Collections.emptyList());
    }

    public static ResponseEntity<GenericResponseBody> success(Object data) {
        return success(HttpStatus.OK, StringUtils.EMPTY, data);
    }

    public static ResponseEntity<GenericResponseBody> success(String message, Object data) {
        return success(HttpStatus.OK, message, data);
    }

    public static ResponseEntity<GenericResponseBody> success(HttpStatus status, String message, Object data) {
        return ResponseEntity.ok(getBody(status, message, data));
    }

    // ------------------------------------------------------------------------
    // Fail
    // ------------------------------------------------------------------------

    public static ResponseEntity<GenericResponseBody> fail() {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, StringUtils.EMPTY, Collections.emptyList(), Collections.emptyList());
    }

    public static ResponseEntity<GenericResponseBody> fail(String error) {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, error);
    }

    public static ResponseEntity<GenericResponseBody> fail(HttpStatus status, String error) {
        return fail(status, error, Collections.emptyList(), Collections.emptyList());
    }

    public static ResponseEntity<GenericResponseBody> fail(List<ObjectError> allErrors) {
        return fail(allErrors, StringUtils.EMPTY);
    }

    public static ResponseEntity<GenericResponseBody> fail(List<ObjectError> allErrors, String error) {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, allErrors, error);
    }

    public static ResponseEntity<GenericResponseBody> fail(HttpStatus status, List<ObjectError> allErrors) {
        return fail(status, allErrors, StringUtils.EMPTY);
    }

    public static ResponseEntity<GenericResponseBody> fail(HttpStatus status, List<ObjectError> allErrors, String error) {
        return fail(status, error, Collections.emptyList(), allErrors);
    }

    public static ResponseEntity<GenericResponseBody> fail(HttpStatus status, String error, Object data, List<ObjectError> allErrors) {
        return ResponseEntity.ok(getErrorBody(status, error, data, convertString(allErrors)));
    }

    // ------------------------------------------------------------------------
    // Utils
    // ------------------------------------------------------------------------

    // getBody

    public static GenericResponseBody getBody(String message) {
        return GenericResponse.getBody(HttpStatus.OK, message, Collections.emptyList());
    }

    public static GenericResponseBody getBody(String message, Object data) {
        return GenericResponse.getBody(HttpStatus.OK, message, data);
    }

    public static GenericResponseBody getBody(HttpStatus status, String message, Object data) {
        GenericResponseBody body = GenericResponseBody.builder()
                .state(status.value())
                .success(true)
                .message(message)
                .error(StringUtils.EMPTY)
                .fieldErrors(StringUtils.EMPTY)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        LOGGER.debug("GenericResponseBody: " + body.getMessage());
        return body;
    }

    // getErrorBody

    public static GenericResponseBody getErrorBody(HttpStatus status, String error) {
        return GenericResponse.getErrorBody(status, error, Collections.emptyList());
    }

    public static GenericResponseBody getErrorBody(HttpStatus status, String error, List<ObjectError> allErrors) {
        return GenericResponse.getErrorBody(status, error, Collections.emptyList(), allErrors);
    }

    public static GenericResponseBody getErrorBody(HttpStatus status, String error, Object data, List<ObjectError> allErrors) {
        return GenericResponse.getErrorBody(status, error, data, convertString(allErrors));
    }

    public static GenericResponseBody getErrorBody(HttpStatus status, String error, String allErrors) {
        return GenericResponse.getErrorBody(status, error, Collections.emptyList(), allErrors);
    }

    public static GenericResponseBody getErrorBody(HttpStatus status, String error, Object data, String allErrors) {
        return GenericResponseBody.builder()
                .state(status.value())
                .success(false)
                .message(StringUtils.EMPTY)
                .error(error)
                .fieldErrors(allErrors)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // convertString

    private static String convertString(List<ObjectError> allErrors) {
        //allErrors
        //    .stream().map(x -> x.getDefaultMessage())
        //    .collect(Collectors.joining(","));
        String fieldErrors = allErrors.stream().map(e -> {
            if (e instanceof FieldError) {
                //return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\"'" + ((FieldError) e).getField() + "' 값이 잘못되었습니다.\"}";
                return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\"" + e.getDefaultMessage().replace("\"", "'") + "\"}";
            } else {
                //return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\"'" + e.getObjectName() + "' 값이 잘못되었습니다.\"}";
                return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\"" + e.getDefaultMessage().replace("\"", "'") + "\"}";
            }
        }).collect(Collectors.joining(","));

        return "[" + fieldErrors + "]";
    }

}