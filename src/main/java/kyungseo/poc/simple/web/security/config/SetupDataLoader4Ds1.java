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

package kyungseo.poc.simple.web.security.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kyungseo.poc.simple.web.appcore.util.RandomUtil;
import kyungseo.poc.simple.web.appcore.util.StringUtil;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.Privilege;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.Role;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.PrivilegeRepository;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.RoleRepository;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.UserRepository;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class SetupDataLoader4Ds1 implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private final String PASSWORD = "password";

    @Value("${app.data.init}")
    private boolean initData;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) return;

        // 기초 데이터 - privileges 생성
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege deletePrivilege = createPrivilegeIfNotFound("DELETE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // 기초 데이터 - roles 생성
        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, deletePrivilege, passwordPrivilege));
        final List<Privilege> staffPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));

        final Role admin = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        final Role staff = createRoleIfNotFound("ROLE_STAFF", staffPrivileges);
        final Role user = createRoleIfNotFound("ROLE_USER", userPrivileges);

        List<Role> adminRoles = Arrays.asList(admin, staff, user);
        List<Role> staffRoles = Arrays.asList(staff, user);
        List<Role> userRoles = Arrays.asList(user);

        // 기초 데이터 - admin user 생성 (2)
        createUserIfNotFound("Kyungseo.Park@gmail.com", "박경서", PASSWORD, adminRoles);
        createUserIfNotFound("admin@company.com", "어드민", PASSWORD, adminRoles);

        // 기초 데이터 - staff user 생성 (3)
        createUserIfNotFound("staff01@company.com", "스탭01", PASSWORD, staffRoles);
        createUserIfNotFound("staff02@company.com", "스탭02", PASSWORD, staffRoles);
        createUserIfNotFound("staff03@company.com", "스탭03", PASSWORD, staffRoles);

        // 기초 데이터 - 일반 user 생성 (10)
        IntStream.range(1, 11).forEach( n -> {
            String suffix = StringUtil.leftPad(n + "", 3, '0');
            String email = "user" + suffix + "@company.com";
            String name = "유저" + suffix;
            String password = PASSWORD;
            createUserIfNotFound(email, name, password, userRoles);
        });

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(final String membername) {
        Privilege privilege = privilegeRepository.findByName(membername);
        if (privilege == null) {
            privilege = new Privilege(membername);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(final String membername, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(membername);
        if (role == null) {
            role = new Role(membername);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }

    @Transactional
    User createUserIfNotFound(final String email, final String membername, final String password, final Collection<Role> roles) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setMembername(membername);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setAge(RandomUtil.getRandomNumber(18, 50));
            user.setPhoneNumber(generatesPhoneNumber());
            user.setCountry("KR");
            user.setBirthdate(RandomUtil.getRandomDateOfBirthDate(1990, 2010));
            user.setEnabled(true);
        }
        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }

    private String generatesPhoneNumber() {
        Random generator = new Random();
        int set2 = generator.nextInt(8999) + 1000;
        int set3 = generator.nextInt(8999) + 1000;

        return "82-10-" + set2 + "-" + set3;
    }

}