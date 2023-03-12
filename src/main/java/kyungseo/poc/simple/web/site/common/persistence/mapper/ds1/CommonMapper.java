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

package kyungseo.poc.simple.web.site.common.persistence.mapper.ds1;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import kyungseo.poc.simple.web.site.common.model.Member;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Mapper
public interface CommonMapper {

    //Spring Security는 내부적으로 사용자의 아이디만 데이터베이스에서 조회를 한 뒤에
    // 가져온 값에서 비밀번호(password) 값을 추출하여 데이터베이스 결과와 매핑하도록 구현되어 있다.
    // 따라서 사용자 비밀번호를 데이터베이스에서 조회하는 코드는 작성해서는 안된다.
	@Select("SELECT username, password, role FROM USERS WHERE username = #{username}")
	Member getUser(@Param("username") String username);

	@Select("SELECT username, password, role FROM USERS WHERE email = #{email}")
	Member getUserByEmail(@Param("email") String email);

}
