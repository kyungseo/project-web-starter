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

package kyungseo.poc.simple.web.appcore.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import kyungseo.poc.simple.web.appcore.AppConstants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Getter
@Setter
@Builder
@ToString
public class GenericResponseBody {

    // Http Status
    private int state;

    // 성공 여부
    private boolean success;

    // 성공 메시지
    private String message;

    // 에러 메시지
    private String error;

    // field 에러 전체 메시지
    private String fieldErrors;

    // 결과 데이터
    private Object data;

    // Timestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstants.DATETIME_FORMAT)
    private LocalDateTime timestamp;

}
