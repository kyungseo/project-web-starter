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

package kyungseo.poc.simple.web.security.components.location;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import kyungseo.poc.simple.web.security.persistence.entity.ds1.NewLocationToken;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SuppressWarnings("serial")
public class OnDifferentLocationLoginEvent extends ApplicationEvent {

    private final Locale locale;
    private final String username;
    private final String ip;
    private final NewLocationToken token;
    private final String appUrl;

    public OnDifferentLocationLoginEvent(Locale locale, String username, String ip, NewLocationToken token, String appUrl) {
        super(token);
        this.locale = locale;
        this.username = username;
        this.ip = ip;
        this.token = token;
        this.appUrl = appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getUsername() {
        return username;
    }

    public String getIp() {
        return ip;
    }

    public NewLocationToken getToken() {
        return token;
    }

    public String getAppUrl() {
        return appUrl;
    }

}
