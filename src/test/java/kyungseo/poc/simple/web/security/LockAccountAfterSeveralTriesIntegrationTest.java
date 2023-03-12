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
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.specification.RequestSpecification;
import kyungseo.poc.simple.web.SimpleWebApplication;
import kyungseo.poc.simple.web.config.TestDbConfig;
import kyungseo.poc.simple.web.config.TestIntegrationConfig;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.UserRepository;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest(classes = { SimpleWebApplication.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LockAccountAfterSeveralTriesIntegrationTest {

    // 로그인 시도 횟수 제한: 제한 횟수가 넘어가면 해당 client ip에 대해서 24시간 동안 차단(bloking)
    //   login.attempt.limit=10
    @Value("${login.attempt.limit}")
    private int MAX_ATTEMPT;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    int port;

    private FormAuthConfig formConfig;

    @BeforeEach
    public void init() {
        User user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            user = new User();
            user.setMembername("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode("test"));
            userRepository.save(user);
        }

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        formConfig = new FormAuthConfig("/view/common/sec/login", "username", "password");
    }

    @Test
    public void givenLoggedInUser_whenUsernameOrPasswordIsIncorrectAfterMaxAttempt_thenUserBlockFor24Hours() {
        //first request where a user tries several incorrect credential
        for (int i = 0; i < MAX_ATTEMPT - 2; i++) {
            final RequestSpecification requestIncorrect = RestAssured.given().auth().form("testtesefsdt.com" + i, "tesfsdft", formConfig);

            requestIncorrect.when().get("/view/common/sec/notice").then().assertThat().statusCode(200).and().body(not(containsString("home")));
        }

        //then user tries a correct user
        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        request.when().get("/view/common/sec/notice").then().assertThat().statusCode(200).and().body(containsString("home"));

        for (int i = 0; i < 3; i++) {
            final RequestSpecification requestSecond = RestAssured.given().auth().form("testtesefsdt.com", "tesfsdft", formConfig);

            requestSecond.when().get("/view/common/sec/notice").then().assertThat().statusCode(200).and().body(not(containsString("home")));
        }

        //the third request where we can see that the user is blocked even if he previously entered a correct credential
        final RequestSpecification requestCorrect = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        requestCorrect.when().get("/view/common/sec/notice").then().assertThat().statusCode(200).and().body(not(containsString("home")));
    }
}