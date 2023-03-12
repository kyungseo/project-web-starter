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

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kyungseo.poc.simple.web.security.persistence.entity.ds1.JwtRefreshToken;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.JwtRefreshTokenRepository;
import kyungseo.poc.simple.web.security.web.error.TokenRefreshException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Service
public class JwtRefreshTokenService {

	@Autowired
    private JwtRefreshTokenRepository refreshTokenRepository;

    public Optional<JwtRefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public JwtRefreshToken save(JwtRefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public JwtRefreshToken createRefreshToken(long refreshTokenExpireTime) {
        JwtRefreshToken refreshToken = new JwtRefreshToken();
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpireTime));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRefreshCount(0L);
        return refreshToken;
    }

    public void verifyExpiration(JwtRefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(token.getToken(), "만료된 토큰입니다. 새 요청을 발행하십시오.");
        }
    }

    public void deleteById(Long id) {
        refreshTokenRepository.deleteById(id);
    }

    public void increaseCount(JwtRefreshToken refreshToken) {
        refreshToken.incrementRefreshCount();
        save(refreshToken);
    }

}
