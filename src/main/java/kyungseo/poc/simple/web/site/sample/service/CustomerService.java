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

package kyungseo.poc.simple.web.site.sample.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import kyungseo.poc.simple.web.appcore.data.enums.ExcelColumnType;
import kyungseo.poc.simple.web.appcore.exception.ResourceConflictException;
import kyungseo.poc.simple.web.security.web.error.ResourceNotFoundException;
import kyungseo.poc.simple.web.site.sample.model.Customer;
import kyungseo.poc.simple.web.site.sample.persistence.mapper.ds1.CustomerMapper;
import kyungseo.poc.simple.web.site.sample.web.dto.response.ExcelData;
import lombok.RequiredArgsConstructor;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerMapper customerMapper;

	public List<Customer> readAll() {
		return this.customerMapper.selectCustomer(null);
	}

    public PageInfo<Customer> findPaginatedWithPage(final int pageNo, final int PageSize) {
        return PageHelper.startPage(pageNo, PageSize).doSelectPageInfo(() -> this.customerMapper.selectCustomer(null));
    }

	public Customer read(final int customerId) {
		return this.customerMapper.selectCustomer(customerId).stream()
				.findFirst().orElseThrow(ResourceNotFoundException::new);
	}

	@Transactional
	public void create(final Customer customer) {
		this.customerMapper.selectCustomer(customer.getCustomerId()).stream().findFirst().ifPresent(s -> {
			throw new ResourceConflictException("Customer already exists.");
		});
		this.customerMapper.insertCustomer(customer);
	}

	@Transactional
	public void update(final Customer customer) {
		this.customerMapper.selectCustomer(customer.getCustomerId()).stream().findFirst()
				.orElseThrow(ResourceNotFoundException::new);
		this.customerMapper.updateCustomer(customer);
	}

	@Transactional
	public void delele(final int customerId) {
		this.customerMapper.deleteCustomer(customerId);
	}

	public ExcelData getExcelData() {
		final List<Customer> list = this.readAll();

		// create excel data & sheet name
		final ExcelData excelData = new ExcelData("customers");

		// create header names
		excelData.addRowHeaders("고객정보리스트", "", "");
		excelData.addRowHeaders("고객ID", "고객명", "업체명");
		excelData.addRowHeaders("", "고객명", "업체명");

		// set merge info : Standard Area Reference
		excelData.setMergeStrings("A1:C1", "A2:A3");

		// set merge info : CellRangeAddress
		//excelData.setMergeInfos(new int[] {0, 0, 0, 2}, new int[] {1, 2, 0, 0});

		// create data
		list.stream().forEach(c -> {
			excelData.addRowDatas(
					String.valueOf(c.getCustomerId()),
					c.getCustomerName(),
					c.getCompany());
		});

		// set column type
		excelData.setColumnTypes(
				ExcelColumnType.INTEGER,
				ExcelColumnType.STRING,
				ExcelColumnType.STRING);

		return excelData;
	}

}
