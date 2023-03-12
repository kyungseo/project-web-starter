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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ServerResponse implements Serializable {

    private Integer status;

    private String msg;

    private Object data;

    public ServerResponse() {
        //
    }

    public ServerResponse(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ServerResponseCode.SUCCESS.getCode();
    }

    public static ServerResponse success() {
        return new ServerResponse(ServerResponseCode.SUCCESS.getCode(), null, null);
    }

    public static ServerResponse success(String msg) {
        return new ServerResponse(ServerResponseCode.SUCCESS.getCode(), msg, null);
    }

    public static ServerResponse success(Object data) {
        return new ServerResponse(ServerResponseCode.SUCCESS.getCode(), null, data);
    }

    public static ServerResponse success(String msg, Object data) {
        return new ServerResponse(ServerResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static ServerResponse error(String msg) {
        return new ServerResponse(ServerResponseCode.ERROR.getCode(), msg, null);
    }

    public static ServerResponse error(Object data) {
        return new ServerResponse(ServerResponseCode.ERROR.getCode(), null, data);
    }

    public static ServerResponse error(String msg, Object data) {
        return new ServerResponse(ServerResponseCode.ERROR.getCode(), msg, data);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
