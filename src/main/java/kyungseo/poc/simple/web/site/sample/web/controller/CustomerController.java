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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import kyungseo.poc.simple.web.site.sample.service.CustomerService;
import kyungseo.poc.simple.web.site.sample.web.view.XlsxDownloadView;
import lombok.RequiredArgsConstructor;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@RequestMapping("/view/sample/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	private final XlsxDownloadView xlsxDownloadView;

	@GetMapping("/customer")
	public void viewCustomer(final Model model) {}

	@GetMapping("/excel")
	public View testExcel(final Model model) {
		model.addAttribute("excelData", customerService.getExcelData());
		model.addAttribute("fileName", "testExcelFile");
		return this.xlsxDownloadView;
	}

}
