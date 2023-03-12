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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.security.components.UserDetailsServiceImpl;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter { // extends GenericFilterBean

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private JwtProvider tokenProvider;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

    // https://www.baeldung.com/spring-boot-customize-jackson-objectmapper
    @Autowired
    private ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

	    // Jwt가 유효한 토큰인지 인증하기 위한 Filter

	    try {
    	    String path = request.getRequestURI();
    	    String contentType = request.getContentType();
    	    LOGGER.info("Request URL path : {}, Request content type: {}", path, contentType);

    	    if (! AppConstants.JWT_TOKEN_REFRESH_URL.equals(path)) {
    		    // Request에서 Access Token을 추출
    			String accessToken = tokenProvider.getJwt(request);
    			//accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyNUBraWNvLmNvLmtyIiwiaXNzIjoiS3l1bmdzZW8uUGFya0BnbWFpbC5jb20iLCJqdGkiOiI1IiwiaWF0IjoxNjc2MTg2MzU1LCJleHAiOjE2NzYxODk5NTV9.dQJDFFE31mMjfjHThwPah7mnV2Mglh4G_3QCj6690Cc";

    			// 1. accessToken이 유효한 지 체크
    			// 2. tokenProvider.validateJwtToken(accessToken) 메서드 내부에서 validateTokenIsNotForALoggedOutDevice(authToken)를 호출함으로써
    			//    accessToken이 blacklist에 있는지 여부를 확인하기 위해 추가 검사를 수행
    			if (accessToken != null && tokenProvider.validateJwtToken(accessToken)) {
    				String username = tokenProvider.getUserNameFromJwtToken(accessToken);

    				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
    						userDetails, null, userDetails.getAuthorities());
    				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    				// SecurityContext에 Authentication을 저장
    				SecurityContextHolder.getContext().setAuthentication(authentication);

    			}
    			else {
    			    // TODO 만료되어 유효하지 않은 Access Token인 경우, refresh token을 사용하여 access token을 갱신?!
    			    //   단, Server단에서의 구현은 복잡해지므로 여기보다는 Client에서 Polling으로 체크하도록 구현하는 것이 맞을 듯...
    			    //     -> Client에서는 AuthenticationEntryPoint의 응답을 통해 인증 실패 확인 가능
    			    //     -> 현재 tokenProvider.validateJwtToken 시 만료된 토큰일 경우 다음 에러 발생 함.
    			    //        ERROR k.p.s.web.security.jwt.JwtProvider - Expired JWT token -> Message: {}
    			    //        exception 발생시키지 말고 client에 만료 code를 던져주게끔 고치면 될 듯...
    			}
    	    }
    	} catch (Exception e) {
    	    logger.error("Can NOT set user authentication -> Message: {}", e);
    	    //((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	    //response.getWriter().print(this.objectMapper.writeValueAsString(new ErrorResponse(null, e.getMessage())));
    	    //response.flushBuffer();
    	}

		filterChain.doFilter(request, response);
	}

}
