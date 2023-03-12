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

package kyungseo.poc.simple.web.security.jwt;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import kyungseo.poc.simple.web.security.event.OnUserLogoutSuccessEvent;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.web.dto.UserPrincipal;
import kyungseo.poc.simple.web.security.web.error.InvalidTokenRequestException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class JwtProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final String TOKEN_PREFIX = "Bearer ";

    private final long ACCESS_TOKEN_EXPIRE_TIME;  // 60L * 60L * 1000L            (1 시간)
    private final long REFRESH_TOKEN_EXPIRE_TIME; // 60L * 60L * 24L * 7L * 1000L (1 주일)

    private final String ACCESS_TOKEN_SECRET;
    private final String ACCESS_TOKEN_ISSUER = "Kyungseo.Park@gmail.com";

    @Autowired
    private LoggedOutJwtTokenCache loggedOutJwtTokenCache;

    public JwtProvider(@Value("${token.key.secret}") final String secret,
            @Value("${token.access.expire.time}") final String accessTokenExpiration,
            @Value("${token.refresh.expire.time}") final String refreshTokenExpiration) {
        this.ACCESS_TOKEN_SECRET = Base64.getEncoder().encodeToString(secret.getBytes());
        this.ACCESS_TOKEN_EXPIRE_TIME = Long.parseLong(accessTokenExpiration);
        this.REFRESH_TOKEN_EXPIRE_TIME = Long.parseLong(refreshTokenExpiration);
    }

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder()
                // header "alg": "HS512"
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject((userPrincipal.getUsername()))
                .setIssuer(this.ACCESS_TOKEN_ISSUER)
                .setId(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                // payload "exp": 1516239022 (예시)
                .setExpiration(Date.from(Instant.now().plusMillis(this.ACCESS_TOKEN_EXPIRE_TIME)))
                .signWith(SignatureAlgorithm.HS256, this.ACCESS_TOKEN_SECRET)
                .compact();
    }

    public String generateTokenFromUser(User user) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(user.getEmail())
                .setIssuer(this.ACCESS_TOKEN_ISSUER)
                .setId(Long.toString(user.getId()))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(this.ACCESS_TOKEN_EXPIRE_TIME)))
                .signWith(SignatureAlgorithm.HS256, this.ACCESS_TOKEN_SECRET)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(this.ACCESS_TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public Date getTokenExpiryFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(this.ACCESS_TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(this.ACCESS_TOKEN_SECRET).parseClaimsJws(authToken);
            validateTokenIsNotForALoggedOutDevice(authToken);
            return true;
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            LOGGER.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty -> Message: {}", e);
        }
        return false;
    }

    private void validateTokenIsNotForALoggedOutDevice(String authToken) {
        OnUserLogoutSuccessEvent previouslyLoggedOutEvent = loggedOutJwtTokenCache.getLogoutEventForToken(authToken);
        if (previouslyLoggedOutEvent != null) {
            String userEmail = previouslyLoggedOutEvent.getUserEmail();
            Date logoutEventDate = previouslyLoggedOutEvent.getEventTime();
            String errorMessage = String.format("Token corresponds to an already logged out user [%s] at [%s]. Please login again", userEmail, logoutEventDate);
            throw new InvalidTokenRequestException("JWT", authToken, errorMessage);
        }
    }

    public long getAccessTokenExpiryDuration() {
        return this.ACCESS_TOKEN_EXPIRE_TIME;
    }

    public long getRefreshTokenExpiryDuration() {
        return this.REFRESH_TOKEN_EXPIRE_TIME;
    }

    // Request의 QueryString 및 Header에서 token 값 - "Authorization" : "TOKEN 값'
    // TODO FIXME 단순 View를 호출하는 용도로 QueryString에 token을 포함하고 있는데,
    //   좋지 않은 구조이다. 다른 방법을 모색해볼 것!!!
    public String getJwt(HttpServletRequest request) {
        String authHeader = StringUtils.contains(request.getQueryString(), "token") ?
                request.getParameter("token") : request.getHeader(HttpHeaders.AUTHORIZATION);
        LOGGER.debug("authHeader: " + authHeader);
        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith(this.TOKEN_PREFIX)) {
            return authHeader.replace(this.TOKEN_PREFIX, "");
        }
        return null;
    }

}
