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

package kyungseo.poc.simple.web.appcore.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * JavaScript 호환 UTF-8 인코딩 및 디코딩을 위한 유틸리티 클래스
 *
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class EncodingUtil {

	private EncodingUtil() {
		super();
	}

	/**
	 * JavaScript의 <code>decodeURIComponent</code> 함수와 호환되는 알고리즘을 사용하여
	 * 전달된 UTF-8 문자열을 디코딩한다.
	 */
	public static String decodeURIComponent(String text) {
		if (text == null) {
			return null;
		}

		String result = null;

		try {
			result = URLDecoder.decode(text, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			result = text;
		}

		return result;
	}

	/**
	 * JavaScript의 <code>encodeURIComponent</code> 함수와 호환되는 알고리즘을 사용하여
	 * 전달된 문자열을 UTF-8로 인코딩한다.
	 */
	public static String encodeURIComponent(String text) {
		String result = null;

		try {
			result = URLEncoder.encode(text, "UTF-8")
					.replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		}
		catch (UnsupportedEncodingException e) {
			result = text;
		}

		return result;
	}

}
