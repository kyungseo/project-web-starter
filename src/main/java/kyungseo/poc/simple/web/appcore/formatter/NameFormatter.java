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

package kyungseo.poc.simple.web.appcore.formatter;

import org.springframework.format.Formatter;
import org.thymeleaf.util.StringUtils;

import java.text.ParseException;
import java.util.Locale;

/**
 * name(문자열)을 포맷, 공백(' ')을 ','로 대체한 값을 반환
 *
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class NameFormatter implements Formatter<String> {

    @Override
    public String print(String input, Locale locale) {
        return formatName(input, locale);
    }

    @Override
    public String parse(String input, Locale locale) throws ParseException {
        return formatName(input, locale);
    }

    private String formatName(String input, Locale locale) {
        return StringUtils.replace(input, " ", ",");
    }

}
