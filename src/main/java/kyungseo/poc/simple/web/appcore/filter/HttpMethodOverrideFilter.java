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

package kyungseo.poc.simple.web.appcore.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class HttpMethodOverrideFilter extends OncePerRequestFilter {

	private final String HTTP_METHOD_OVERRIDE_HEADER_NAME = "X-HTTP-Method-Override";

	@Override
	protected void doFilterInternal(
	        final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {

	    final String headerValue = request.getHeader(this.HTTP_METHOD_OVERRIDE_HEADER_NAME);
		if (StringUtils.isNotEmpty(headerValue) && StringUtils.equalsIgnoreCase(request.getMethod(), "POST")) {
			filterChain.doFilter(new HttpMethodRequestWrapper(request, headerValue), response);
		}
		else {
			filterChain.doFilter(request, response);
		}
	}

	private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {
		private final String method;

		public HttpMethodRequestWrapper(final HttpServletRequest request, final String method) {
			super(request);
			this.method = method;
		}

		@Override
		public String getMethod() {
			return this.method;
		}
	}

}
