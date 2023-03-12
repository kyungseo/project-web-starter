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

package kyungseo.poc.simple.web.site.sample.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;

import kyungseo.poc.simple.web.appcore.validation.ValidationMarkers.Create;
import kyungseo.poc.simple.web.appcore.validation.ValidationMarkers.Update;
import kyungseo.poc.simple.web.security.dto.SessionScopeModel;
import kyungseo.poc.simple.web.security.web.controller.JwtViewController;
import kyungseo.poc.simple.web.site.common.model.Member;
import kyungseo.poc.simple.web.site.sample.model.Customer;
import kyungseo.poc.simple.web.site.sample.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/sample/customers")
@RequiredArgsConstructor
@Log4j2
public class CustomerRestController {

	private final CustomerService customerService;

	private final ObjectMapper objectMapper;

	private final SessionScopeModel sessionScopeModel;

	/**
	 * get customer list
	 * @return
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	//public List<Customer> getCustomerList(@AuthenticationPrincipal final Member user) {
	public List<Customer> getCustomerList() {
		//log.info(user.toString());
		return this.customerService.readAll();
	}

	/**
	 * get customer by customerId
	 * @param customerId
	 * @return
	 */
	@GetMapping(path = "/{customerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Customer getCustomer(@PathVariable final int customerId) {
		return this.customerService.read(customerId);
	}

	/**
	 * create customer
	 * @param customer
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void createCustomer(@RequestBody @Validated(Create.class) final Customer customer) {
		this.customerService.create(customer);
	}

	/**
	 * update customer
	 * @param customer
	 */
	@RequestMapping(path = "/{customerId}", method = {RequestMethod.PUT, RequestMethod.PATCH},
			consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void updateCustomer(@PathVariable final int customerId,
			@RequestBody @Validated(Update.class) final Customer customer) {
		customer.setCustomerId(customerId);
		this.customerService.update(customer);
	}

	/**
	 * delete customer by customerId
	 * @param customerId
	 */
	@DeleteMapping("/{customerId}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteCustomer(@PathVariable final int customerId) {
		this.customerService.delele(customerId);
	}

	@GetMapping("/paging")
	public PageInfo<Customer> getCustomersByPaging() throws JsonProcessingException {
		PageInfo<Customer> page = this.customerService.findPaginatedWithPage(1, 3);
		log.debug("Page Info: {}", this.objectMapper.writeValueAsString(page));
		return page;
	}

	@GetMapping("/signin")
	public void testSessionScope() {
	}

}
