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

package kyungseo.poc.simple.web.appcore.dto.response.temp;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public enum ServerResponseCode {

    // 시스템 모듈
    SUCCESS(0, "작업 성공"),
    ERROR(1, "작업 실패"),
    SERVER_ERROR(500, "서버 에러"),

    // 공통 모듈 1xxxx
    ILLEGAL_ARGUMENT(10000, "매개변수 오류"),
    ACCESS_LIMIT(10002, "요청이 너무 많습니다. 나중에 다시 시도하십시오."),
    MAIL_SEND_SUCCESS(10003, "이메일 전송 성공"),

    // 사용자 모듈 2xxxx
    NEED_LOGIN(20001, "인증 필요"),
    ;

    ServerResponseCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
