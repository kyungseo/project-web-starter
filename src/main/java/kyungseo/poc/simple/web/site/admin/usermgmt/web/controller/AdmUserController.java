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

package kyungseo.poc.simple.web.site.admin.usermgmt.web.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import kyungseo.poc.simple.web.appcore.dto.request.PagingRequestDTO;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponse;
import kyungseo.poc.simple.web.appcore.dto.response.GenericResponseBody;
import kyungseo.poc.simple.web.appcore.util.MessageSourceUtil;
import kyungseo.poc.simple.web.security.components.ActiveUserStore;
import kyungseo.poc.simple.web.security.dto.SessionScopeModel;
import kyungseo.poc.simple.web.security.web.error.ResourceNotFoundException;
import kyungseo.poc.simple.web.security.web.error.UserNotFoundException;
import kyungseo.poc.simple.web.site.admin.usermgmt.model.AdmUser;
import kyungseo.poc.simple.web.site.admin.usermgmt.service.AdmUserServiceImpl;
import kyungseo.poc.simple.web.site.admin.usermgmt.service.AdmUserValidationService;
import kyungseo.poc.simple.web.site.admin.usermgmt.web.dto.AdmUserDTO;
import kyungseo.poc.simple.web.site.common.service.Helper;
import lombok.RequiredArgsConstructor;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@PreAuthorize("hasRole({'ADMIN'})")
@RequestMapping("/view/admin/usermgmt/users")
@RequiredArgsConstructor // for 자동 DI
public class AdmUserController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final ApplicationEventPublisher eventPublisher;
    private final ActiveUserStore activeUserStore;
    private final SessionScopeModel sessionScopeModel;
    private final MessageSourceUtil messages;
    private final Environment env;
    private final ObjectMapper objectMapper;

    private final AdmUserServiceImpl userService;
    private final AdmUserValidationService userValidationService;

    // read - all
    @GetMapping({"", "/"})
    public String findPaginatedWithPageable(Optional<PagingRequestDTO> searchDTO, Optional<String> message, Model model) {
        // Optional<String> message:
        //   AdmUserRestController.update에서 수정 처리 중 발생한 에러 message를 받는다.
        //   AdmUserController.editView의 다음 라인과는 무관
        //     -> redirectAttributes.addFlashAttribute("message", e.getMessage());

        PagingRequestDTO dto = null;

        if (searchDTO.isPresent()) {
            dto = searchDTO.get();
        }
        else {
            PagingRequestDTO defaultDTO = new PagingRequestDTO();
            defaultDTO.setSearchType("n"); // 성명
            defaultDTO.setSearchKeyword("유저");
            dto = defaultDTO;
        }

        if (message.isPresent()) {
            model.addAttribute("message", message.get());
        }
        model.addAttribute("searchDTO", dto); // 검색 및 페이징 조건 유지
        model.addAttribute("result", userService.findPaginatedWithPageable(dto));

        return "view/admin/usermgmt/users";
    }

    /*
    @GetMapping("/users/new") // 등록 페이지
    public String addView(Model model) {
        model.addAttribute("userDto", new AdmUserDTO());
        return "view/admin/usermgmt/user_form";
    }

    @PostMapping("/users/insert") // 등록 처리
    public String insert(@Valid @ModelAttribute AdmUserDTO userDto, BindingResult errors, Model model) {
        if (!errors.hasErrors()) {
            userService.insert(userDto);
            PagingRequestDTO pagingRequestDTO = new PagingRequestDTO();
            model.addAttribute("users", userService.findPaginated(pagingRequestDTO));
        }

        return ((errors.hasErrors()) ? "view/admin/usermgmt/user_form" : "view/admin/usermgmt/users");
    }
    */

    // read - one
    @GetMapping("/{id}") // 수정 페이지
    public String editView(@PathVariable("id") Long id,
            @RequestParam("pageNum") Optional<Integer> pageNum,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam("searchType") Optional<String> searchType,
            @RequestParam("searchKeyword") Optional<String> searchKeyword,
            //@RequestParam(defaultValue = "n") String searchType,
            //@RequestParam(defaultValue = "") String searchKeyword,
            Model model, RedirectAttributes redirectAttributes) {

        PagingRequestDTO searchDTO = new PagingRequestDTO();
        searchDTO.setPageNum(pageNum.orElse(1));
        searchDTO.setPageSize(pageSize.orElse(5));
        searchDTO.setSearchType(searchType.orElse("n"));
        searchDTO.setSearchKeyword(searchKeyword.orElse(""));
        model.addAttribute("searchDTO", searchDTO); // 검색 및 페이징 조건 유지

        try {
            AdmUser user = userService.selectOne(id);
            AdmUserDTO userDto = new AdmUserDTO();
            BeanUtils.copyProperties(user, userDto);
            model.addAttribute("userDto", userDto);
            model.addAttribute("countries", Helper.getCountries());

            return "view/admin/usermgmt/user_form";
        }
        catch (Exception e) {
            // 수정 페이지를 가져오는 중 발생한 에러에 대한 message를 세팅한다.
            // 이때, findPaginatedWithPageable에서 message를 받을 필요없이 html에서 바로 받을 수 있다.
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/view/admin/usermgmt/users";
        }
    }

    @PostMapping("/update") // 수정 처리
    public String update(
            Optional<PagingRequestDTO> searchDTO,
            @Valid @ModelAttribute("userDto") AdmUserDTO userDto,
            BindingResult errors, RedirectAttributes redirectAttributes) {
        String err = userValidationService.validateUser(userDto);

        if (!err.isEmpty()) {
            ObjectError error = new ObjectError("globalError", err);
            errors.addError(error);
        }

        if (errors.hasErrors()) {
            return "view/admin/usermgmt/user_form";
        }

        userService.update(userDto);

        if (searchDTO.isPresent()) {
            // 검색 및 페이징 조건 유지
            redirectAttributes.addFlashAttribute("searchDTO", searchDTO.get());
        }
        redirectAttributes.addFlashAttribute("message", "사용자 수정 완료!");

        return "redirect:/view/admin/usermgmt/users";
    }

    //@DeleteMapping("/{id}")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        AdmUser user = userService.selectOne(id);
        if (user == null) {
            throw new ResourceNotFoundException("AdmUser", "id", id);
        }

        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "사용자 id=" + id + "가 삭제되었습니다.");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/view/admin/usermgmt/users";
    }

}
