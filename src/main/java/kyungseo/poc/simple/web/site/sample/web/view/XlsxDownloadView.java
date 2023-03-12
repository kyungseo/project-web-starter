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

package kyungseo.poc.simple.web.site.sample.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import kyungseo.poc.simple.web.site.sample.web.dto.response.ExcelData;
import lombok.RequiredArgsConstructor;

/**
 * Excel Download View component class
 *
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class XlsxDownloadView extends AbstractXlsxCustomView {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final XlsxUtil xlsxUtil;

	@Override
	protected void buildExcelDocument(final Map<String, Object> model, final Workbook workbook,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		Assert.notNull(model, "Excel data는 필수.");

		logger.info("Excel 다운로드...");

		final ExcelData excelData = (ExcelData) model.get("excelData");

		// 시트정보
		final Sheet sheet = workbook.createSheet(excelData.getSheetName());
		this.xlsxUtil.defaultSheetStyle(sheet);

		// 헤더생성
		int rowIdx = this.xlsxUtil.createCell(workbook, sheet, excelData.getHeaderList(), null, 0);
		// 데이터 생성
		this.xlsxUtil.createCell(workbook, sheet, excelData.getDataList(), excelData.getTypeList(), rowIdx);
		// 셀 병합
		this.xlsxUtil.mergeCellByReference(sheet, excelData.getMergeList());
		this.xlsxUtil.mergeCellByAddress(sheet, excelData.getMergeInfoList());
		// 셀 width 조정
		this.xlsxUtil.autoSizeColumn(sheet, excelData.getTypeList().size());
	}

}
