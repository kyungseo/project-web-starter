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

package kyungseo.poc.simple.web.site.admin.usermgmt.persistence.mapper.ds1;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import kyungseo.poc.simple.web.appcore.dto.request.PagingRequestDTO;
import kyungseo.poc.simple.web.site.admin.usermgmt.model.AdmUser;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Mapper
public interface AdmUserMapper {

    List<AdmUser> selectAll();

    /*
    @Select("select * from user_account")
    @Results({
            @Result(property = "username", column = "username", jdbcType = JdbcType.VARCHAR),
            @Result(property = "password", column = "password")
    })
    List<AdmUser> selectAll();
    */

    List<AdmUser> selectList(PagingRequestDTO pagingRequestDTO);

    Integer totalCount(PagingRequestDTO pagingRequestDTO);

    AdmUser selectOne(Long id);

    /*
    @Select("select * from user_account where id = #{id}")
    @Results({
            @Result(property = "username", column = "username", jdbcType = JdbcType.VARCHAR),
            @Result(property = "password", column = "password")
    })
    AdmUser selectOne(Long id);
    */

    AdmUser selectByEmail(@Param("email") String email);

    void insert(AdmUser user);

    /*
    @Insert("insert into user_account(username, password) values(#{username}, #{password})")
    void insert(AdmUser user);
    */

    void batchInsert(List<AdmUser> userList);

    void update(AdmUser user);

    /*
    @Update("update user_account set username=#{username}, password=#{password} where id = #{id}")
    void update(AdmUser user);
    */

    void batchUpdate(List<AdmUser> userList);

    @Update("update user_account set enabled=#{enabled} where id = #{id}")
    void updateEnabled(Long id, Boolean enabled);

    @Delete("delete from users_roles where user_id = #{id}")
    void deleteUserRoles(Long id);

    void batchDeleteUserRoles(List<AdmUser> userList);

    void delete(Long id);

    /*
    @Delete("delete from user_account where id = #{id}")
    void delete(Long id);
    */

    void batchDelete(List<AdmUser> userList);

}
