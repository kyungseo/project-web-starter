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

package kyungseo.poc.simple.web.security;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kyungseo.poc.simple.web.SimpleWebApplication;
import kyungseo.poc.simple.web.config.TestDbConfig;
import kyungseo.poc.simple.web.config.TestIntegrationConfig;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.DeviceMetadata;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.DeviceMetadataRepository;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.UserRepository;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Transactional
@SpringBootTest(classes = {SimpleWebApplication.class, TestDbConfig.class, TestIntegrationConfig.class},
    properties = "geo.ip.lib.enabled=true", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DeviceServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private DeviceMetadataRepository deviceMetadataRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${local.server.port}")
    int port;

    //

    private Long userId;

    @BeforeEach
    public void init() {
        User user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            user = new User();
            user.setMembername("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setEnabled(true);
            user = userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode("test"));
            user = userRepository.save(user);
        }

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        userId = user.getId();
    }

    @Test
    public void givenValidLoginRequest_whenNoPreviousKnownDevices_shouldSendLoginNotification() {
        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "88.198.50.103") // Nuremberg
                .formParams("username", "test@test.com", "password", "test")
                .post("/view/common/sec/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/view/common/sec/notice", response.getHeader("Location"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingKnownDevice_shouldNotSendLoginNotification() {
        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Nuremberg");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.14");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "88.198.50.103") // Nuremberg
                .formParams("username", "test@test.com", "password", "test")
                .post("/view/common/sec/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/view/common/sec/notice", response.getHeader("Location"));
        verify(mailSender, times(0)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingNewDevice_shouldSendLoginNotification() {
        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Nuremberg");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.14");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Safari/605.1.15")
                .header("X-Forwarded-For", "88.198.50.103") // Nuremberg
                .formParams("username", "test@test.com", "password", "test")
                .post("/view/common/sec/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/view/common/sec/notice", response.getHeader("Location"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingKnownDeviceFromDifferentLocation_shouldSendLoginNotification() {
        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Nuremberg");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.14");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "81.47.169.143") // Barcelona
                .formParams("username", "test@test.com", "password", "test")
                .post("/view/common/sec/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/view/common/sec/notice", response.getHeader("Location"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}
