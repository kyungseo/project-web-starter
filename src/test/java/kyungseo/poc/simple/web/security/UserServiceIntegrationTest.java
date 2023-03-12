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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.config.TestDbConfig;
import kyungseo.poc.simple.web.config.TestIntegrationConfig;
import kyungseo.poc.simple.web.security.config.LoginNotificationConfig;
import kyungseo.poc.simple.web.security.config.ServiceConfig;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.Role;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.VerificationToken;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.RoleRepository;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.UserRepository;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.VerificationTokenRepository;
import kyungseo.poc.simple.web.security.service.IUserService;
import kyungseo.poc.simple.web.security.web.dto.UserDto;
import kyungseo.poc.simple.web.security.web.error.EmailAlreadyExistException;
import kyungseo.poc.simple.web.security.web.error.UserAlreadyExistException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest(classes = { TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class, LoginNotificationConfig.class})
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Test
    public void givenNewUser_whenRegistered_thenCorrect() throws EmailAlreadyExistException {
        final String userEmail = UUID.randomUUID().toString();
        final UserDto userDto = createUserDto(userEmail);

        final User user = userService.registerNewUserAccount(userDto);

        assertNotNull(user);
        assertNotNull(user.getEmail());
        assertEquals(userEmail, user.getEmail());
        assertNotNull(user.getId());
    }

    @Test
    public void givenDetachedUser_whenAccessingEntityAssociations_thenCorrect() {
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            roleRepository.saveAndFlush(new Role("ROLE_USER"));
        }

        // detached entity
        final User user = registerUser();

        // only roles are eagerly fetched
        assertNotNull(user.getRoles());

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        assertEquals(1, roles.size());
        assertEquals("ROLE_USER", roles.iterator().next());
    }

    @Test
    public void givenDetachedUser_whenServiceLoadById_thenCorrect() throws EmailAlreadyExistException {
        final User user = registerUser();
        final User user2 = userService.getUserByID(user.getId()).get();
        assertEquals(user, user2);
    }

    @Test
    public void givenDetachedUser_whenServiceLoadByEmail_thenCorrect() throws EmailAlreadyExistException {
        final User user = registerUser();
        final User user2 = userService.findUserByEmail(user.getEmail());
        assertEquals(user, user2);
    }

    @Test
    public void givenUserRegistered_whenDuplicatedRegister_thenCorrect() {
    	assertThrows(UserAlreadyExistException.class, () -> {

            final String email = UUID.randomUUID().toString();
            final UserDto userDto = createUserDto(email);
            userService.registerNewUserAccount(userDto);
            userService.registerNewUserAccount(userDto);
    	});

    }

    @Transactional
    public void givenUserRegistered_whenDtoRoleAdmin_thenUserNotAdmin() {
    	assertNotNull(roleRepository);
        final UserDto userDto = new UserDto();
        userDto.setEmail(UUID.randomUUID().toString());
        userDto.setPassword("SecretPassword");
        userDto.setMatchingPassword("SecretPassword");
        userDto.setMembername("Kyungseo Park");
        assertNotNull(roleRepository.findByName("ROLE_ADMIN"));
        final Long adminRoleId = roleRepository.findByName("ROLE_ADMIN").getId();
        assertNotNull(adminRoleId);
        userDto.setRole(adminRoleId.intValue());
        final User user = userService.registerNewUserAccount(userDto);
        assertFalse(user.getRoles().stream().map(Role::getId).anyMatch(ur -> ur.equals(adminRoleId)));
    }

    @Test
    public void givenUserRegistered_whenCreateToken_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
    }

    @Test
    public void givenUserRegistered_whenCreateTokenCreateDuplicate_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.createVerificationTokenForUser(user, token);
    }

    @Test
    public void givenUserAndToken_whenLoadToken_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        assertNotNull(verificationToken);
        assertNotNull(verificationToken.getId());
        assertNotNull(verificationToken.getUser());
        assertEquals(user, verificationToken.getUser());
        assertEquals(user.getId(), verificationToken.getUser().getId());
        assertEquals(token, verificationToken.getToken());
        assertTrue(verificationToken.getExpiryDate().toInstant().isAfter(Instant.now()));
    }

    @Test
    public void givenUserAndToken_whenRemovingUser_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.deleteUser(user);
    }

    @Test
    public void givenUserAndToken_whenRemovingUserByDao_thenFKViolation() {
    	assertThrows(DataIntegrityViolationException.class, () -> {
            final User user = registerUser();
            final String token = UUID.randomUUID().toString();
            userService.createVerificationTokenForUser(user, token);
            final long userId = user.getId();
            userService.getVerificationToken(token).getId();
            userRepository.deleteById(userId);
    	});
    }

    @Test
    public void givenUserAndToken_whenRemovingTokenThenUser_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long userId = user.getId();
        final long tokenId = userService.getVerificationToken(token).getId();
        tokenRepository.deleteById(tokenId);
        userRepository.deleteById(userId);
    }

    @Test
    public void givenUserAndToken_whenRemovingToken_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long tokenId = userService.getVerificationToken(token).getId();
        tokenRepository.deleteById(tokenId);
    }

    @Test
    public void givenUserAndToken_whenNewTokenRequest_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final VerificationToken origToken = userService.getVerificationToken(token);
        final VerificationToken newToken = userService.generateNewVerificationToken(token);
        assertNotEquals(newToken.getToken(), origToken.getToken());
        assertNotEquals(newToken.getExpiryDate(), origToken.getExpiryDate());
        assertNotEquals(newToken, origToken);
    }

    @Test
    public void givenTokenValidation_whenValid_thenUserEnabled_andTokenRemoved() {
        User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long userId = user.getId();
        final String token_status = userService.validateVerificationToken(token);
        assertEquals(token_status, AppConstants.AUTH_TOKEN_VALID_MESSAGE);
        user = userService.getUserByID(userId).get();
        assertTrue(user.getEnabled());
    }

    @Test
    public void givenTokenValidation_whenInvalid_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        final String invalid_token = "INVALID_" + UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.getVerificationToken(token).getId();
        final String token_status = userService.validateVerificationToken(invalid_token);
        token_status.equals(AppConstants.AUTH_TOKEN_INVALID_MESSAGE);
    }

    @Test
    public void givenTokenValidation_whenExpired_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        user.getId();
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        verificationToken.setExpiryDate(Date.from(verificationToken.getExpiryDate().toInstant().minus(2, ChronoUnit.DAYS)));
        tokenRepository.saveAndFlush(verificationToken);
        final String token_status = userService.validateVerificationToken(token);
        assertNotNull(token_status);
        token_status.equals(AppConstants.AUTH_TOKEN_EXPIRED_MESSAGE);
    }

    //

    private UserDto createUserDto(final String email) {
        final UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setPassword("password");
        userDto.setMatchingPassword("password");
        userDto.setMembername("Kyungseo Park");
        userDto.setRole(0);
        return userDto;
    }

    private User registerUser() {
        final String email = UUID.randomUUID().toString();
        final UserDto userDto = createUserDto(email);
        final User user = userService.registerNewUserAccount(userDto);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        return user;
    }

}
