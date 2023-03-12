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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class DifferentLocationLoginListener implements ApplicationListener<OnDifferentLocationLoginEvent> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Override
    public void onApplicationEvent(final OnDifferentLocationLoginEvent event) {
        LOGGER.debug("DifferentLocationLoginListener.onApplicationEvent: Start!");

        final String enableLocUri = event.getAppUrl() + "/view/common/sec/enableNewLoc?token=" + event.getToken().getToken();
        final String changePassUri = event.getAppUrl() + "/view/common/sec/changePassword";
        final String recipientAddress = event.getUsername();
        final String subject = "다른 장소(Location)에서의 로그인 시도";
        final String message = messages.getMessage(
                "sec.message.differentLocation",
                new Object[] {
                        new Date().toString(),
                        event.getToken().getUserLocation().getCountry(), event.getIp(),
                        enableLocUri,
                        changePassUri
                    },
                event.getLocale());
        LOGGER.debug("\tMessage: " + message);

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(env.getProperty("support.email"));

        mailSender.send(email);
    }

}