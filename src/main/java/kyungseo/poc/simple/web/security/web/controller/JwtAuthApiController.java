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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.models.responses.ApiResponse;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponse;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponseBody;
import kyungseo.poc.simple.web.appcore.util.StringUtil;
import kyungseo.poc.simple.web.security.dto.request.LogInForm;
import kyungseo.poc.simple.web.security.dto.request.LogOutRequest;
import kyungseo.poc.simple.web.security.dto.request.SignUpForm;
import kyungseo.poc.simple.web.security.dto.request.TokenRefreshRequest;
import kyungseo.poc.simple.web.security.dto.response.JwtResponse;
import kyungseo.poc.simple.web.security.dto.response.UserIdentityAvailability;
import kyungseo.poc.simple.web.security.event.OnUserLogoutSuccessEvent;
import kyungseo.poc.simple.web.security.jwt.JwtProvider;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.JwtRefreshToken;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.JwtUserDevice;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.Role;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.RoleRepository;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.UserRepository;
import kyungseo.poc.simple.web.security.service.CurrentUser;
import kyungseo.poc.simple.web.security.service.JwtRefreshTokenService;
import kyungseo.poc.simple.web.security.service.JwtUserDeviceService;
import kyungseo.poc.simple.web.security.web.dto.UserPrincipal;
import kyungseo.poc.simple.web.security.web.error.TokenRefreshException;
import kyungseo.poc.simple.web.security.web.error.UserLogoutException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class JwtAuthApiController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    private JwtRefreshTokenService refreshTokenService;

    @Autowired
    private JwtUserDeviceService userDeviceService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/signin") // 로그인
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LogInForm loginRequest) {
        // email을 기반으로 유효한 사용자가 존재하는 지 검증
    	User user = userRepository.findByEmail(loginRequest.getEmail());
    	if (user == null) {
    	    new RuntimeException("Fail! -> 원인: 사용자를 찾을 수 없음.");
    	}

    	if (user.getEnabled()) {
    	    // Login ID/PW 를 기반으로 Spring Security의 Authentication 객체 생성
    	    //   -> loadUserByUsername 메서드 호출
    		Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtProvider.generateJwtToken(authentication);

            // 이미 사용자 Session과 연결된 Refresh Token이 있다면, 현재 새 Session을 생성하는 중이므로 삭제 처리
            userDeviceService.findByUserId(user.getId())
            .map(JwtUserDevice::getRefreshToken)
            .map(JwtRefreshToken::getId)
            .ifPresent(refreshTokenService::deleteById);

            // UserDevice와 Refresh Token 객체를 새로 생성
            JwtUserDevice userDevice = userDeviceService.createUserDevice(loginRequest.getDeviceInfo());
            JwtRefreshToken refreshToken = refreshTokenService.createRefreshToken(jwtProvider.getRefreshTokenExpiryDuration());

            userDevice.setUser(user);
            userDevice.setRefreshToken(refreshToken);
            refreshToken.setUserDevice(userDevice);

            // 새로 생성한 Refresh Token을 DB에 저장
            refreshToken = refreshTokenService.save(refreshToken);

            // Role String...
            List<String> roleList = new ArrayList<String>();
            user.getRoles().forEach(role -> {
                roleList.add(role.getName().toString());
            });
            String commaSeparatedRoleString = String.join(",", roleList);

            return ResponseEntity.ok(new JwtResponse(
                    accessToken,
                    refreshToken.getToken(),
                    StringUtil.removeLastComma(commaSeparatedRoleString),
                    jwtProvider.getAccessTokenExpiryDuration()));
    	}
    	return GenericResponse.fail(HttpStatus.BAD_REQUEST, "사용자가 비활성화되고 잠겼습니다.!!");
    }

    @PostMapping("/signup") // 회원 가입
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Fail -> 이메일이 이미 사용 중입니다!", HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User();
        user.setMembername(signUpRequest.getMembername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
          switch(role.toUpperCase()) {
          case "ROLE_ADMIN":
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (adminRole == null) {
                throw new RuntimeException("Fail! -> 원인: 사용자 역할을 찾을 수 없음.");
            }
            roles.add(adminRole);

            break;
          case "ROLE_STAFF":
            Role staffRole = roleRepository.findByName("ROLE_STAFF");
            if (staffRole == null) {
                throw new RuntimeException("Fail! -> 원인: 사용자 역할을 찾을 수 없음.");
            }
                roles.add(staffRole);

            break;
          default: // "ROLE_USER"
              Role userRole = roleRepository.findByName("ROLE_USER");
              if (userRole == null) {
                  throw new RuntimeException("Fail! -> 원인: 사용자 역할을 찾을 수 없음.");
              }
              roles.add(userRole);
          }
        });

        user.setRoles(roles);
        user.activate();
        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        LOGGER.debug("> registerUser: " + location.toString());

        return ResponseEntity.created(location)
                .body(GenericResponse.getBody("사용자가 성공적으로 등록되었습니다!"));
    }

    // 사용자가 실제로 애플리케이션을 사용하는 동안, JWT Token을 새로 고치기 위해 주기적으로 "/refresh" 요청을 보냄

    @PostMapping("/refresh") // Access Token의 만료시간 리셋
    public ResponseEntity<?> refreshJwtToken(@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
    	String requestRefreshToken = tokenRefreshRequest.getRefreshToken();

    	// 직전에 사용자가 명시적으로 logout 한 경우, DB에 Refresh Token이 존재하지 않는다.
    	Optional<String> accessToken = Optional.of(refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    refreshTokenService.verifyExpiration(refreshToken);        // refreshToken의 만료여부 체크
                    userDeviceService.verifyRefreshAvailability(refreshToken); // refreshToken과 매핑된 Device 속성 중 갱신가능여부 체크
                    refreshTokenService.increaseCount(refreshToken);           // refreshToken의 갱신횟수 업데이트
                    return refreshToken;
                })
                .map(JwtRefreshToken::getUserDevice)               // refreshToken으로 사용자 Device 정보 추출
                .map(JwtUserDevice::getUser)                       // 사용자 Device에서 User 정보 추출
                .map(u -> jwtProvider.generateTokenFromUser(u)) // User 정보를 사용해 현재 시간부로 만료시간이 갱신된 Access Token을 새로 생성하여 return
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "데이터베이스에 refresh token이 없습니다. 다시 로그인하십시오.")));

    	LOGGER.debug("AccessToken: " + accessToken.get());
    	LOGGER.debug("JwtRefreshToken: " + tokenRefreshRequest.getRefreshToken());
    	LOGGER.debug("Roles: " + "ROLE_ANONYMOUS");
    	LOGGER.debug("ExpiryDuration: " + jwtProvider.getAccessTokenExpiryDuration());

    	// Access Token은 새로 생성, Refresh Token은 그대로 사용, 만료시간 Reset
    	// TODO FIXME roles 세팅할 것!
        return ResponseEntity.ok().body(new JwtResponse(accessToken.get(), tokenRefreshRequest.getRefreshToken(), "ROLE_ANONYMOUS", jwtProvider.getAccessTokenExpiryDuration()));
    }

    @PutMapping("/logout") // 로그아웃
    public ResponseEntity<GenericResponseBody> logoutUser(@CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody LogOutRequest logOutRequest) {
        String deviceId = logOutRequest.getDeviceInfo().getDeviceId();
        JwtUserDevice userDevice = userDeviceService.findByUserId(currentUser.getId())
                .filter(device -> device.getDeviceId().equals(deviceId))
                .orElseThrow(() -> new UserLogoutException(logOutRequest.getDeviceInfo().getDeviceId(), "잘못된 Device ID가 제공되었습니다. 지정된 사용자와 일치하는 Device가 없습니다."));

        // UserDevice와 매핑되어 있는 RefreshToken을 삭제
        refreshTokenService.deleteById(userDevice.getRefreshToken().getId());

        // Logout 된 token을 Blacklist(Cache)에 저장
        OnUserLogoutSuccessEvent logoutSuccessEvent =
                new OnUserLogoutSuccessEvent(currentUser.getEmail(), logOutRequest.getToken(), logOutRequest);
        applicationEventPublisher.publishEvent(logoutSuccessEvent);

        // TODO SecurityContext를 비울 필요는 없는지?
        //SecurityContextHolder.getContext().setAuthentication(null);
        //SecurityContextHolder.clearContext();
        //Session 객체 가져와서 invalidate()
        //Cookie 날리기

        return GenericResponse.success("사용자가 성공적으로 로그아웃했습니다.!");
    }

    @GetMapping("/checkEmailAvailability") // email 중복 체크
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

}
