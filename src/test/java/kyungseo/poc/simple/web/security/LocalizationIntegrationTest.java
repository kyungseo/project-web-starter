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

import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import kyungseo.poc.simple.web.SimpleWebApplication;
import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.config.TestIntegrationConfig;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest(classes = { SimpleWebApplication.class, TestIntegrationConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LocalizationIntegrationTest {

    @Value("${local.server.port}")
    int port;

    @BeforeEach
    public void init() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void given_theLanuageParamterIsEnglish_then_the_title_of_the_log_page_is_Login() {
        final RequestSpecification request = RestAssured.given().param(AppConstants.LOCALE_CHANGE_PARAM_NAME, "en");
        request.when().get("/view/common/sec/login").then().assertThat().statusCode(200).and().body(containsString("<h1>Signin</h1>"));
    }

    @Test
    public void given_theLanuageParamterIsSpanish_then_the_title_of_the_log_page_is_Ingreso() {
        final RequestSpecification request = RestAssured.given().param(AppConstants.LOCALE_CHANGE_PARAM_NAME, "ko_KR");
        request.when().get("/view/common/sec/login").then().assertThat().statusCode(200).and().body(containsString("<h1>로그인</h1>"));
    }
}
