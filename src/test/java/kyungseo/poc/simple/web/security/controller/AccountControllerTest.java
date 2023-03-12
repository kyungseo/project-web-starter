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

package kyungseo.poc.simple.web.security.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import kyungseo.poc.simple.web.security.dto.request.SignUpForm;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.RoleRepository;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class AccountControllerTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    //@Autowired
    private MockMvc mockMvc;

    //private WebTestClient webClient;

    // ------------------------------------------------------------------------

    private final String TEST_USER_NAME = "kyungseo";
    private final String TEST_USER_PW = "password";
    private final String TEST_USER_EMAIL = "Kyungseo.Park@gmail.com";

    // ------------------------------------------------------------------------

    // @WithMockUser : Member 생성
    //   -> @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"USER","ADMIN"})
    //
    // @WithAnonymousUser : 익명의 유저 생성
    //
    // @WithUserDetails : Member 조회
    //   -> @WithUserDetails(value = "test@a.b", userDetailsServiceBeanName = "userService")
    //
    // https://www.arhohuttunen.com/spring-security-testing/
    // https://developer.okta.com/blog/2021/05/19/spring-security-testing

    // ------------------------------------------------------------------------

    /*
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void signUp100MembersWithRole() {
            // Given
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_USER");

            // 1 - 96 까지는 USER, 97 - 99 까지는 STAFF, 100은 ADMIN
            IntStream.rangeClosed(1,99).forEach(i -> {
                try {
                    if (i == 97) {
                        roles.add("ROLE_STAFF");
                    }

                    if (i == 100) {
                        roles.add("ROLE_ADMIN");
                    }

                    SignUpForm signUpRequest = SignUpForm.builder()
                            .email("user" + i + "@kico.co.kr")
                            .name("user" + i)
                            .password(TEST_USER_PW)
                            .role(roles)
                            .build();

                    // When
                        mockMvc.perform(post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(signUpRequest)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("success").exists())
                                .andExpect(jsonPath("message").exists())
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
    }

    @Test
    public void signUp() throws Exception {
        // Given
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_STAFF");
        roles.add("ROLE_USER");

        roles.forEach((String name) -> {
            LOGGER.debug(name);
        });

        SignUpForm signUpRequest = SignUpForm.builder()
                .name(TEST_USER_NAME)
                .password(TEST_USER_PW)
                .email(TEST_USER_EMAIL)
                .role(roles)
                .build();

        // When
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("message").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // Then
        //assertThat(email).isEqualTo(request.getEmail());
    }

    // form Login 기능 테스트 코드 --------------------------------------------

    @Test
    @DisplayName("사용자 정보를 반환하면 성공")
    @WithUserDetails(value="Kyungseo.Park@gmail.com")
    public void getUserInfo() throws Exception {
        mockMvc.perform(get("/user/info"))
                .andExpect(status().isOk());
    }

    @DisplayName("인증실패")
    @Test
    void forbiddenTest() throws Exception {
        this.mockMvc.perform(get("/api/test")
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void loginSuccess() throws Exception {
        String username = "user2";
        String password = "password";

        mockMvc.perform(formLogin().user(username).password(password))
                .andExpect(authenticated());
    }

    @Test
    @Transactional
    public void loginFail() throws Exception {
        String username = "admin";

        mockMvc.perform(formLogin().user(username).password("12345"))
                .andExpect(unauthenticated());
    }

    // ROLE Test --------------------------------------------------------------

    @Test
    @WithAnonymousUser
    public void index_anonymous() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void index_user() throws Exception {
        mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "staff1", roles = "STAFF")
    public void admin_user() throws Exception {
        mockMvc.perform(get("/staff"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void admin_admin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    */

}
