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

package kyungseo.poc.simple.web.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    // AuthenticationEntryPoint: 인증 처리 과정에서 예외가 발생한 경우 예외를 핸들링하는 인터페이스
    //  e.g. AuthenticationException(인증되지 않은 요청)인 경우 AuthenticationEntryPoint를 사용하여 처리
    //  -> 인증 정보 없을 때 401 에러 (또는 로그인페이지로 리다이렉트?)

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e)
                             throws IOException, ServletException {
        LOGGER.error("Unauthorized error. Message - {}", e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error -> Unauthorized");
    }

    /*
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e)
                             throws IOException, ServletException {
        logger.error("Unauthorized error. Message - {}", e.getMessage());

        // [1] API(ajax) 요청일 경우 상태코드 전송
        if (StringUtils.contains(request.getHeader(HttpHeaders.ACCEPT), MediaType.APPLICATION_JSON_VALUE)) {
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error -> Unauthorized");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.flushBuffer();
        // [2] 일반 요청일 경우 세션만료(403) 페이지로 리다이렉트
        } else {
            response.sendRedirect("/view/common/error/403");
        }
    }
    */

}
