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

package kyungseo.poc.simple.web.security.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kyungseo.poc.simple.web.appcore.dto.response.GenericResponse;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponseBody;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.UserRepository;
import kyungseo.poc.simple.web.security.service.CurrentUser;
import kyungseo.poc.simple.web.security.web.error.ResourceNotFoundException;
import kyungseo.poc.simple.web.security.web.error.UserNotFoundException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth/users")
public class JwtUserApiController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public User getCurrentUser(@CurrentUser UserDetails userPrincipal) {
        final User user = userRepository.findByEmail(userPrincipal.getUsername());
        if (user == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다. - " + userPrincipal.getUsername());
        }

        LOGGER.info("현재 사용자: " + user.getEmail());

        return user;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUserProfile(@RequestParam(value = "email", required = false) Optional<String> email) {
        List<User> users = new ArrayList<>();
        if (email.isPresent()) {
            User user = userRepository.findByEmail(email.get());
            if (user == null) {
                throw new ResourceNotFoundException("User", "email", email.get());
            }
            users.add(user);
        } else {
            users = userRepository.findAll();
        }
        return users;
    }

    @GetMapping("/byID/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserProfileById(@PathVariable(value = "id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return user;
    }

    @PutMapping("/byID/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseBody> deactivateUserById(@PathVariable(value = "id") Long id) {
    	User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.deactivate();
        userRepository.save(user);

        return GenericResponse.success("사용자 비활성화 성공!", user);
    }

    @PutMapping("/byID/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseBody> activateUserById(@PathVariable(value = "id") Long id) {
       User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.activate();
        userRepository.save(user);

        return GenericResponse.success("사용자 활성화 성공!", user);
    }

    @DeleteMapping("/byID/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseBody> deleteUser(@PathVariable(value = "id") Long id) {
    	User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);

        return GenericResponse.success("사용자 삭제 성공!", user);
    }

}
