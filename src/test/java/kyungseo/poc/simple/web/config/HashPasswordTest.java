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

package kyungseo.poc.simple.web.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import kyungseo.poc.simple.web.site.common.model.Member;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class HashPasswordTest {

    private static final String USERNAME = "kyungseo";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "Kyungseo.Park@gmail.com";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("비밀번호 Hash")
    void hashPassword() throws Exception {
        Member user = Member.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .username(USERNAME)
                .build();

        System.out.println("plainPassword : " + user.getPassword());
        System.out.println("encryptedPassword : " + passwordEncoder.encode(user.getPassword()));

        System.out.println("plainPassword : 1111");
        System.out.println("encryptedPassword : " + passwordEncoder.encode("1111"));
    }

}
