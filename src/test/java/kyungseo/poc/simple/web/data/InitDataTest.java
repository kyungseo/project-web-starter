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

package kyungseo.poc.simple.web.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import kyungseo.poc.simple.web.security.persistence.entity.ds1.Role;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.RoleRepository;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.UserRepository;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class InitDataTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    /*
    @Test
    public void init1RoleData() {
        //List<RoleName> roles = Arrays.asList(RoleName.ROLE_ADMIN, RoleName.ROLE_STAFF, RoleName.ROLE_USER);
        List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_STAFF", "ROLE_USER");
        roles.forEach(i -> createRoleIfNotFound(i));
    }

    private Role createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role != null) {
            Role newRole = new Role();
            newRole.setName(roleName);
            newRole = roleRepository.save(newRole);
        }
        return role;
    }

    @Test
    public void init2UserData() {
        IntStream.rangeClosed(1,100).forEach(i -> {
            // Creating user's account
            User user = new User();
            user.setName("user" + i);
            user.setEmail(user.getName() + "@kico.co.kr");
            user.setPassword(encoder.encode("password"));
            user.setRoles(getRoleByIndex(i));
            user.activate();
            LOGGER.debug(userRepository.save(user).toString());
        });
    }

    private Set<Role> getRoleByIndex(int index) {
        Set<Role> roles = new HashSet<>();
        // 1은 ADMIN, 2-4는 STAFF, 나머지는 USER 역할
        if (index == 1) {
            Role role = new Role();
            role.setId(1L);
            role.setName("ROLE_ADMIN");
            roles.add(role);
        }
        else if (index < 5) {
            Role role = new Role();
            role.setId(2L);
            role.setName("ROLE_STAFF");
            roles.add(role);
        }
        else {
            Role role = new Role();
            role.setId(3L);
            role.setName("ROLE_USER");
            roles.add(role);
        }
        return roles;
    }
    */

}
