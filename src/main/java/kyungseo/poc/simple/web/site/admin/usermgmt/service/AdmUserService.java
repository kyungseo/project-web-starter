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
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import kyungseo.poc.simple.web.appcore.dto.request.PagingRequestDTO;
import kyungseo.poc.simple.web.appcore.dto.response.PagingResultDTO;
import kyungseo.poc.simple.web.site.admin.usermgmt.model.AdmUser;
import kyungseo.poc.simple.web.site.admin.usermgmt.web.dto.AdmUserDTO;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public interface AdmUserService {

    List<AdmUser> selectAll();

    PagingResultDTO<AdmUserDTO, AdmUser> findPaginatedWithPageable(PagingRequestDTO requestDTO);

    AdmUser selectOne(Long id);

    AdmUser selectByEmail(String email);

    //void insert(AdmUserDTO user);

    //void batchInsert(List<AdmUserDTO> userList);

    void update(AdmUserDTO user);

    void batchUpdate(List<AdmUserDTO> userList);

    void updateEnabled(Long id, Boolean enabled);

    void delete(Long id);

    void batchDelete(List<AdmUserDTO> userList);

    default AdmUser convertToEntity(AdmUserDTO dto) {
        AdmUser entity = AdmUser.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .membername(dto.getMembername())
                .password(dto.getPassword())
                .age(dto.getAge())
                .phoneNumber(dto.getPhoneNumber())
                .country(dto.getCountry())
                .roles(dto.getRoles())
                .secret(dto.getSecret())
                .isUsing2FA(dto.getIsUsing2FA())
                .regDate(dto.getRegDate())
                .modDate(dto.getModDate())
                .enabled(dto.getEnabled())
                .build();
        return entity;
    }

    default AdmUserDTO convertToDto(AdmUser entity){
        AdmUserDTO dto  = AdmUserDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .membername(entity.getMembername())
                .password(entity.getPassword())
                .age(entity.getAge())
                .phoneNumber(entity.getPhoneNumber())
                .country(entity.getCountry())
                .roles(entity.getRoles())
                .secret(entity.getSecret())
                .isUsing2FA(entity.getIsUsing2FA())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .enabled(entity.getEnabled())
                .build();
        return dto;
    }

    default List<AdmUser> toAdmUserList(List<AdmUserDTO> userList) {
        if (CollectionUtils.isEmpty(userList)) return null;

        return userList.stream().map(userDto -> {
            AdmUser user = AdmUser.builder().build();
            BeanUtils.copyProperties(userDto, user);
            return user;
        }).collect(Collectors.toList());
    }

    default List<AdmUserDTO> toAdmUserDtoList(List<AdmUser> userList) {
        if (CollectionUtils.isEmpty(userList)) return null;

        return userList.stream().map(user -> {
            AdmUserDTO userDto = AdmUserDTO.builder().build();
            BeanUtils.copyProperties(user, userDto);
            return userDto;
        }).collect(Collectors.toList());
    }

}
