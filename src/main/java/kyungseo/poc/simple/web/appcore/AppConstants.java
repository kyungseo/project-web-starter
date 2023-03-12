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

package kyungseo.poc.simple.web.appcore;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public interface AppConstants {

    public static final String LOCALE_CHANGE_PARAM_NAME = "lang";

    public static final String TIMEZONE        = "Asia/Seoul";
    public static final String DATE_FORMAT     = "yyyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Double Submit 방지를 위한 request id 키값
    public static final String REQUEST_ID = "_requestId_";

    // ------------------------------------------------------------------------
    // Spring Security & Session
    // ------------------------------------------------------------------------

    public static final String SESSION_COOKIES_NAME          = "JSESSIONID";
    public static final String SESSION_SCOPE_MODEL_NAME      = "sessionScopeModel";
    public static final String SESSION_USERNAME_KEY          = "username";
    public static final String SESSION_LOGGEDUSER_KEY        = "user";

    public static final String REMEMBER_ME_KEY = "theRememberMeKey";

    // session.lastAccessedTime 이후 5분 동안 아무 작업이 없으면 로그아웃 시킴
    public static final boolean TIMER_INACTIVE_ENABLED       = false;
    public static final long TIMER_INACTIVE_SESSION_MAXTIME  = 6 * 10000 * 5; // ms 단위

    public static final String AUTH_BLOCKED_MESSAGE          = "blocked";
    public static final String AUTH_UNUSUAL_LOCATION_MESSAGE = "unusual location";
    public static final String AUTH_TOKEN_EXPIRED_MESSAGE    = "expired";
    public static final String AUTH_TOKEN_VALID_MESSAGE      = "valid";
    public static final String AUTH_TOKEN_INVALID_MESSAGE    = "invalidToken";
    public static final String AUTH_UNKNOWN_DEVICE_MESSAGE   = "UNKNOWN"; // 알 수 없는 장치

    // 2FA
    public static final String GOOGLE_OTP_APP_NAME = "Kyungseo.PoC";
    public static final String GOOGLE_QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

    // GeoIP
    public static final String GEO_IP_COUNTRY_DB_FILEPATH = "src/main/resources/maxmind/GeoLite2-Country.mmdb";

    // ------------------------------------------------------------------------
    // View Pages
    // ------------------------------------------------------------------------

    // Home
    public static final String HOME_VIEW_NAME            = "view/home";                // 메인 홈 뷰
    public static final String MAIN_HOME_URL             = "/view/home";               // 메인 홈
    public static final String ADMIN_HOME_URL            = "/view/admin/home";         // Admin 홈
    public static final String STAFF_HOME_URL            = "/view/staff/home";         // Staff 홈
    public static final String USER_HOME_URL             = "/view/user/home";          // User 홈

    // Auth
    public static final String AUTH_LOGIN_VIEW_NAME      = "view/common/sec/login";    // 로그인 뷰
    public static final String AUTH_LOGIN_URL            = "/view/common/sec/login";   // 로그인
    public static final String AUTH_LOGIN_FAIL_URL       = "/view/common/sec/login?error=true"; // 로그인 실패
    public static final String AUTH_LOGOUT_API           = "/api/v1/user/sec/logout";  // 로그아웃
    public static final String AUTH_LOGOUT_SUCCESS_URL   = "/view/common/sec/logout?logSucc=true"; // 로그아웃 성공
    public static final String AUTH_SESSION_INVALID_URL  = "/view/common/sec/invalidSession";
    public static final String AUTH_SESSION_EXPIRED_URL  = "/view/common/sec/login?messageKey=sec.message.session.max.expired";

    // JWT
    public static final String JWT_TOKEN_REFRESH_URL     = "/api/v1/auth/refresh";   // 로그인

    // Registration
    public static final String REGSTRATION_CONFIRM_URL   = "/view/common/sec/registrationConfirm?token=";

    // Result Page
    public static final String RESULT_NOTICE_VIEW_NAME   = "view/common/sec/notice";
    public static final String RESULT_NOTICE_URL         = "/view/common/sec/notice";

    // Error Pages
    public static final String ERROR_VIEW_NAME           = "view/common/error/error";
    public static final String ERROR_400_URL             = "/view/common/error/400";
    public static final String ERROR_401_URL             = "/view/common/error/401";
    public static final String ERROR_403_URL             = "/view/common/error/403";
    public static final String ERROR_404_URL             = "/view/common/error/404";
    public static final String ERROR_405_URL             = "/view/common/error/405";
    public static final String ERROR_409_URL             = "/view/common/error/409";
    public static final String ERROR_500_URL             = "/view/common/error/500";

    // Common
    public static final String FILEUPLOAD_URL            = "/view/common/fileupload";
    public static final String FILEUPLOAD_STATUS_URL     = "/view/common/fileupload/uploadStatus";
    public static final String FILEDOWNLOAD_URL          = "/view/common/filedownload";

}