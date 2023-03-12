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

package kyungseo.poc.simple.web.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /*
    @Resource
    private UserService userService;

    private final String TEST_USER_NAME = "test-user";
    private final String TEST_USER_PW = "password";
    private final String TEST_USER_EMAIL = "test-user@kico.co.kr";

    // 순서대로 실행할 것! ------------------------------------------------------------

    @Test
    public void getUserList() {
        //List<Member> userList = userService.userList();
        userService.userList().forEach(user -> System.out.println(user));
    }

    @Test
    public void getUserPagingList() {
        //List<Member> userList = userService.userList();
    }

    @Test
    public void addUser() {
        Member newUser = Member.builder()
                .username(this.TEST_USER_NAME)
                .password(this.TEST_USER_PW)
                .role("USER")
                .email(this.TEST_USER_EMAIL)
                .gender(Gender.MAN)
                .nickName(this.TEST_USER_NICK)
                .build();
        userService.register(newUser);
        System.out.println(userService.getUserByName(TEST_USER_NAME));
    }

    @Test
    public void addUserRaiseError1() {
        Member newUser = Member.builder()
                .username(this.TEST_USER_NAME) // PK 중복
                .password(this.TEST_USER_PW)
                .role("USER")
                .email(this.TEST_USER_EMAIL)
                .gender(Gender.MAN)
                .nickName(this.TEST_USER_NICK)
                .build();
        userService.register(newUser);
    }

    @Test
    public void addUserRaiseError2() {
        Member newUser = Member.builder()
                .username(this.TEST_USER_NAME)
                .password(this.TEST_USER_PW)
                .role("USER")
                .email(this.TEST_USER_EMAIL)
                .gender(Gender.MAN)
                .nickName("12345678901234567890123456789012345678901234567890") // Data too long for column 'NICK_NAME'
                .build();
        userService.register(newUser);
        System.out.println(userService.loadUserByUsername(TEST_USER_NAME));
    }

    @Test
    public void getUserByName() {
        UserDetails user = userService.getUserByName(TEST_USER_NAME);
        System.out.println(user);
    }

    @Test
    public void getUserByEmail() {
        UserDetails user = userService.getUserByEmail(TEST_USER_EMAIL);
        System.out.println(user);
    }

    @Test
    public void updateUser() {
        Member newUser = Member.builder()
                .username(this.TEST_USER_NAME)
                .password(this.TEST_USER_PW)
                .role("STAFF")              // 변경
                .email(this.TEST_USER_EMAIL)
                .gender(Gender.WOMAN)        // 변경
                .nickName("닉값 변경")       // 변경
                .build();
        System.out.println(userService.modify(newUser));
    }

    @Test
    public void updateUserPassword1() {
        Member newUser = Member.builder()
                .username(this.TEST_USER_NAME)
                .password("비밀번호1")      // 변경
                .role("USER")
                .email(this.TEST_USER_EMAIL)
                .gender(Gender.MAN)
                .nickName(this.TEST_USER_NICK)
                .build();
        // userRepository.save
        System.out.println(userService.updatePassword1(newUser));
    }

    @Test
    public void updateUserPassword2() {
        int affectedCount = userService.updatePassword2(this.TEST_USER_NAME, this.TEST_USER_EMAIL, "비밀번호2");
        System.out.println("affectedCount: " + affectedCount);
        System.out.println(userService.getUserByName(TEST_USER_NAME));
    }

    @Test
    public void deleteUser1() {
        userService.removeUser1(TEST_USER_NAME);

        System.out.println(userService.getUserByName(TEST_USER_NAME));
    }

    @Test
    public void deleteUser2() {
        // deleteUser1 실행으로 해당 user 삭제됨
        this.addUser(); // 삭제 테스트를 위해 새로 등록

        userService.removeUser2(TEST_USER_NAME);

        System.out.println(userService.getUserByName(TEST_USER_NAME));
    }

    @Test
    public void deleteUserRaiseError() {
        userService.removeUser1("TEST_USER_NAME");  // 존재하지 않는 ID
    }

    // Spring Security TEST ---------------------------------------------------

    @Test
    public void testLoadUserByUsername() {
        //Member user = userService.getUserByEmail("admin@kico.co.kr");
        Member user = (Member) userService.loadUserByUsername("admin");
        System.out.println(user);
    }
    */

}
