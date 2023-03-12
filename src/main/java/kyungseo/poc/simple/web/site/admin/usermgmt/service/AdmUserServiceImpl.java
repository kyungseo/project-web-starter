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

package kyungseo.poc.simple.web.site.admin.usermgmt.service;

import java.util.List;
import java.util.function.Function;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kyungseo.poc.simple.web.appcore.dto.request.PagingRequestDTO;
import kyungseo.poc.simple.web.appcore.dto.response.PagingResultDTO;
import kyungseo.poc.simple.web.site.admin.usermgmt.model.AdmUser;
import kyungseo.poc.simple.web.site.admin.usermgmt.persistence.mapper.ds1.AdmUserMapper;
import kyungseo.poc.simple.web.site.admin.usermgmt.web.dto.AdmUserDTO;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Service
//@Transactional
public class AdmUserServiceImpl implements AdmUserService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public static ModelMapper modelMapper;

    @Autowired
	private AdmUserMapper userMapper;

    @Override
    public List<AdmUser> selectAll() {
        return userMapper.selectAll();
    }

    @Override
    public PagingResultDTO<AdmUserDTO, AdmUser> findPaginatedWithPageable(PagingRequestDTO pagingRequestDTO) {
        LOGGER.debug("pagingRequestDTO.getPageNum: " + pagingRequestDTO.getPageNum());

        Pageable pageable = pagingRequestDTO.getPageable();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();

        List<AdmUser> list = userMapper.selectList(pagingRequestDTO);
        int totalCount = userMapper.totalCount(pagingRequestDTO);

        LOGGER.debug("pageSize: " + pageSize);
        LOGGER.debug("currentPage: " + currentPage);
        LOGGER.debug("totalCount: " + totalCount);
        LOGGER.debug("list.size(): " + list.size());

        Page<AdmUser> userPage = new PageImpl<AdmUser>(list, PageRequest.of(currentPage, pageSize), totalCount);
        Function<AdmUser, AdmUserDTO> fn = (entity -> convertToDto(entity));

        return new PagingResultDTO<>(userPage, fn );
    }

    @Override
    public AdmUser selectOne(final Long id) {
        return this.userMapper.selectOne(id);
    }

    @Override
    public AdmUser selectByEmail(final String email) {
        return this.userMapper.selectByEmail(email);
    }

    /*
    @Override
    public void insert(AdmUserDTO user) {
        this.userMapper.insert(dtoToEntity(user));
    }
    */

    /*
    @Override
    public void batchInsert(List<AdmUserDTO> userList) {
        this.userMapper.batchInsert(toAdmUserList(userList));
    }
    */

    @Override
    public void update(AdmUserDTO user) {
        this.userMapper.update(convertToEntity(user));
    }

    @Override
    public void batchUpdate(List<AdmUserDTO> userList) {
        this.userMapper.batchUpdate(toAdmUserList(userList));
    }

    @Override
    public void updateEnabled(Long id, Boolean enabled) {
        this.userMapper.updateEnabled(id, enabled);
    }

    @Override
    public void delete(final Long id) {
        this.userMapper.deleteUserRoles(id);
        this.userMapper.delete(id);
    }

    @Override
    public void batchDelete(List<AdmUserDTO> userList) {
        this.userMapper.batchDeleteUserRoles(toAdmUserList(userList));
        this.userMapper.batchDelete(toAdmUserList(userList));
    }

}
