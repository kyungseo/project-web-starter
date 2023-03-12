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

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kyungseo.poc.simple.web.security.dto.request.DeviceInfo;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.JwtRefreshToken;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.JwtUserDevice;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.JwtUserDeviceRepository;
import kyungseo.poc.simple.web.security.web.error.TokenRefreshException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Service
public class JwtUserDeviceService {

	@Autowired
    private JwtUserDeviceRepository userDeviceRepository;

    public Optional<JwtUserDevice> findByUserId(Long userId) {
        return userDeviceRepository.findByUserId(userId);
    }

    public Optional<JwtUserDevice> findByRefreshToken(JwtRefreshToken refreshToken) {
        return userDeviceRepository.findByRefreshToken(refreshToken);
    }

    public JwtUserDevice createUserDevice(DeviceInfo deviceInfo) {
        JwtUserDevice userDevice = new JwtUserDevice();
        userDevice.setDeviceId(deviceInfo.getDeviceId());
        userDevice.setDeviceType(deviceInfo.getDeviceType());
        userDevice.setIsRefreshActive(true);
        return userDevice;
    }

    public void verifyRefreshAvailability(JwtRefreshToken refreshToken) {
        JwtUserDevice userDevice = findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException(refreshToken.getToken(), "Token에 일치하는 Device를 찾을 수 없습니다. 다시 로그인하십시오."));

        if (!userDevice.getIsRefreshActive()) {
            throw new TokenRefreshException(refreshToken.getToken(), "현재 Device에 대한 Refresh가 차단되었습니다. 다른 Device를 통해 로그인하세요.");
        }
    }

}
