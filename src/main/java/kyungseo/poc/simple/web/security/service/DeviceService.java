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

package kyungseo.poc.simple.web.security.service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.appcore.geoip.CityResponse;
import kyungseo.poc.simple.web.appcore.geoip.DatabaseReader;
import kyungseo.poc.simple.web.appcore.util.SecurityUtil;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.DeviceMetadata;
import kyungseo.poc.simple.web.security.persistence.entity.ds1.User;
import kyungseo.poc.simple.web.security.persistence.repository.ds1.DeviceMetadataRepository;
import ua_parser.Client;
import ua_parser.Parser;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class DeviceService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${support.email}")
    private String from;

    private DeviceMetadataRepository deviceMetadataRepository;
    private DatabaseReader databaseReader;
    private Parser parser;
    private JavaMailSender mailSender;
    private MessageSource messages;

    public DeviceService(DeviceMetadataRepository deviceMetadataRepository,
                         @Qualifier("GeoIPCity") DatabaseReader databaseReader,
                         Parser parser,
                         JavaMailSender mailSender,
                         MessageSource messages) {
        this.deviceMetadataRepository = deviceMetadataRepository;
        this.databaseReader = databaseReader;
        this.parser = parser;
        this.mailSender = mailSender;
        this.messages = messages;
    }

    public void verifyDevice(User user, HttpServletRequest request) throws IOException {
        String ip = SecurityUtil.getClientIP(request);
        String location = getIpLocation(ip);

        String deviceDetails = getDeviceDetails(request.getHeader("user-agent"));
        DeviceMetadata existingDevice = findExistingDevice(user.getId(), deviceDetails, location);

        if (Objects.isNull(existingDevice)) {
            unknownDeviceNotification(deviceDetails, location, ip, user.getEmail(), request.getLocale());

            DeviceMetadata deviceMetadata = new DeviceMetadata();
            deviceMetadata.setUserId(user.getId());
            deviceMetadata.setLocation(location);
            deviceMetadata.setDeviceDetails(deviceDetails);
            deviceMetadata.setLastLoggedIn(new Date());
            deviceMetadataRepository.save(deviceMetadata);
        }
        else {
            existingDevice.setLastLoggedIn(new Date());
            deviceMetadataRepository.save(existingDevice);
        }
    }

    private String getDeviceDetails(String userAgent) {
        String deviceDetails = AppConstants.AUTH_UNKNOWN_DEVICE_MESSAGE;

        Client client = parser.parse(userAgent);
        if (Objects.nonNull(client)) {
            deviceDetails = client.userAgent.family + " " + client.userAgent.major + "." + client.userAgent.minor +
                    " - " + client.os.family + " " + client.os.major + "." + client.os.minor;
        }
        LOGGER.debug("deviceDetails: " + deviceDetails);

        return deviceDetails;
    }

    //private String getIpLocation(String ip) throws IOException, GeoIp2Exception {
    private String getIpLocation(String ip) throws IOException {
        String location = AppConstants.AUTH_UNKNOWN_DEVICE_MESSAGE;
        InetAddress ipAddress = InetAddress.getByName(ip);

        CityResponse cityResponse = databaseReader.city(ipAddress);
        if (Objects.nonNull(cityResponse) &&
                Objects.nonNull(cityResponse.getCity()) &&
                !Strings.isNullOrEmpty(cityResponse.getCity().getName())) {

            location = cityResponse.getCity().getName();
        }

        return location;
    }

    private DeviceMetadata findExistingDevice(Long userId, String deviceDetails, String location) {
        List<DeviceMetadata> knownDevices = deviceMetadataRepository.findByUserId(userId);

        for (DeviceMetadata existingDevice : knownDevices) {
            if (existingDevice.getDeviceDetails().equals(deviceDetails) &&
                    existingDevice.getLocation().equals(location)) {
                return existingDevice;
            }
        }

        return null;
    }

    private void unknownDeviceNotification(String deviceDetails, String location, String ip, String email, Locale locale) {
        final String subject = "새로운 로그인 알림";
        final SimpleMailMessage notification = new SimpleMailMessage();
        notification.setTo(email);
        notification.setSubject(subject);

        String text = getMessage("sec.message.login.notification.deviceDetails", locale) +
                " " + deviceDetails +
                "\n" +
                    getMessage("sec.message.login.notification.location", locale) +
                " " + location +
                "\n" +
                    getMessage("sec.message.login.notification.ip", locale) +
                " " + ip;

        notification.setText(text);
        notification.setFrom(from);

        mailSender.send(notification);
    }

    private String getMessage(String code, Locale locale) {
        try {
            return messages.getMessage(code, null, locale);
        }
        catch (NoSuchMessageException ex) {
            return messages.getMessage(code, null, Locale.ENGLISH);
        }
    }

}
