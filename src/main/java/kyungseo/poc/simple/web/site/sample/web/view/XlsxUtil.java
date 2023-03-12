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

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import kyungseo.poc.simple.web.appcore.data.enums.ExcelColumnType;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class XlsxUtil {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private final String REGEXP_DATE = "^(\\d{4})(\\d{2})(\\d{2})$";
	private final String REGEXP_DATEHHMM = "^(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})$";
	private final String REGEXP_DATETIME = "^(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})$";

	/**
	 * Xlsx Workbook 생성
	 * @return
	 */
	public Workbook createXlsxWorkbook() {
		return new SXSSFWorkbook();
	}

	/**
	 * Rendering the workbook
	 * @param workbook
	 * @param response
	 * @throws IOException
	 */
	public void renderWorkbook(final Workbook workbook, final HttpServletResponse response) throws IOException {
		workbook.write(response.getOutputStream());
		// java.io.Closeable only implemented as of POI 3.10
		if (workbook instanceof Closeable) {
			((Closeable) workbook).close();
		}
		// Dispose of temporary files in case of streaming variant...
		((SXSSFWorkbook) workbook).dispose();
	}

	/**
	 * Setting response headers
	 * @param response
	 * @param filename
	 */
	public void setResponseHeaders(final HttpServletResponse response, final String filename) {
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				new StringBuilder("attachment; filename=\"").append(filename).append(".xlsx\"").toString());
		response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=0, private, must-revalidate");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setContentType(CONTENT_TYPE);
	}

	/**
	 * Encoding file name
	 * @param request
	 * @param filename
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String makeFilename(final HttpServletRequest request, final String filename)
			throws UnsupportedEncodingException {

		final String userAgent = request.getHeader(HttpHeaders.USER_AGENT).toLowerCase();
		if (StringUtils.contains(userAgent, "msie") || StringUtils.contains(userAgent, "trident")
				|| StringUtils.contains(userAgent, "edge/")) {
			// MS IE, Edge
			return URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "\\ ");
		} else {
			// FF, Opera, Safari, Chrome
			return new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		}
	}

	/**
	 * 셀 생성 - 최종 데이터로 생성
	 * @param workbook
	 * @param sheet
	 * @param dataList
	 * @param typeList : Header 타입이면 null.
	 * @param rowIdx
	 * @return rowIdx - last row index
	 */
	public int createCell(final Workbook workbook, final Sheet sheet, final List<List<String>> dataList,
			final List<ExcelColumnType> typeList, int rowIdx) {

		if (CollectionUtils.isEmpty(dataList) == false) {
			final Map<ExcelColumnType, CellStyle> cellStyleMap = this.getCellStyleMap(workbook);

			for (final List<String> cellData : dataList) {
				this.createCell(workbook, sheet, cellData, typeList, rowIdx++, cellStyleMap);
			}
		}
		return rowIdx;
	}

	/**
	 * 셀 생성 - 로우별 데이터로 생성
	 * @param workbook
	 * @param sheet
	 * @param cellData
	 * @param typeList : Header 타입이면 null.
	 * @param rowIdx
	 * @param cellStyleMap
	 */
	public void createCell(final Workbook workbook, final Sheet sheet, final List<String> cellData,
			final List<ExcelColumnType> typeList, final int rowIdx, final Map<ExcelColumnType, CellStyle> cellStyleMap) {

		if (CollectionUtils.isEmpty(cellData) == false) {
			final Row row = sheet.createRow(rowIdx);
			final CreationHelper helper = workbook.getCreationHelper();

			for (int i = 0, columnSize = cellData.size(); i < columnSize; i++) {
				final Cell cell = row.createCell(i);

				if (CollectionUtils.isEmpty(typeList)) { // typeList가 null로 넘어오면 Header
					cell.setCellStyle(cellStyleMap.get(ExcelColumnType.HEADER));
					cell.setCellValue(helper.createRichTextString(cellData.get(i)));
				}
				else {
					cell.setCellStyle(cellStyleMap.get(typeList.get(i)));

					if (typeList.get(i) == ExcelColumnType.INTEGER || typeList.get(i) == ExcelColumnType.DOUBLE) {
						cell.setCellValue(
								Double.valueOf(StringUtils.isNotEmpty(cellData.get(i)) ? cellData.get(i) : "0"));
					}
					else {
						cell.setCellValue(helper.createRichTextString(this.replaceDateFormat(
								StringUtils.isNotEmpty(cellData.get(i)) ? cellData.get(i) : "", typeList.get(i))));
					}
				}
			}
		}
	}

	/**
	 * 셀 width 자동조정
	 * @param sheet
	 * @param columnSize
	 */
	public void autoSizeColumn(final Sheet sheet, final int columnSize) {
		if (sheet instanceof SXSSFSheet) {
			((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
		}
		for (int i=0; i<columnSize; i++) {
			sheet.autoSizeColumn(i, true);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
		}
	}

	/**
	 * 셀 병합 - 엑셀 Standard Area Reference 정보로 병합
	 * @param sheet
	 * @param mergeList
	 */
	public void mergeCellByReference(final Sheet sheet, final List<String> mergeList) {
		if (CollectionUtils.isEmpty(mergeList) == false) {
			for (final String range : mergeList) {
				sheet.addMergedRegion(CellRangeAddress.valueOf(range));
			}
		}
	}

	/**
	 * 셀 병합 - CellRangeAddress 정보로 병합
	 * @param sheet
	 * @param mergeInfoList
	 */
	public void mergeCellByAddress(final Sheet sheet, final List<CellRangeAddress> mergeInfoList) {
		if (CollectionUtils.isEmpty(mergeInfoList) == false) {
			for (final CellRangeAddress rangeAddress : mergeInfoList) {
				sheet.addMergedRegion(rangeAddress);
			}
		}
	}

	/**
	 * 시트 기본 스타일 설정
	 * @param sheet
	 */
	public void defaultSheetStyle(final Sheet sheet) {
		sheet.setDefaultColumnWidth(10);
		sheet.setDisplayGridlines(true);
	}

	/**
	 * 엑셀 컬럼 형식에 따른 CellStyle Map
	 * @param workbook
	 * @return
	 */
	public Map<ExcelColumnType, CellStyle> getCellStyleMap(final Workbook workbook) {
		final Map<ExcelColumnType, CellStyle> map = new HashMap<>();
		map.put(ExcelColumnType.HEADER, this.headerCellStyle(workbook));
		map.put(ExcelColumnType.STRING, this.stringCellStyle(workbook));
		map.put(ExcelColumnType.INTEGER, this.numberCellStyle(workbook));
		map.put(ExcelColumnType.DOUBLE, this.doubleCellStyle(workbook));
		map.put(ExcelColumnType.DATE, this.dateCellStyle(workbook));
		map.put(ExcelColumnType.DATEHHMM, this.datehhmmCellStyle(workbook));
		map.put(ExcelColumnType.DATETIME, this.datetimeCellStyle(workbook));
		return map;
	}

	/**
	 * 헤더 셀 스타일 설정
	 * @param workbook
	 * @return
	 */
	private CellStyle headerCellStyle(final Workbook workbook) {
		final CellStyle cellStyle = this.defaultCellStyle(workbook);
		cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		final Font font = workbook.createFont();
		font.setBold(true);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * 문자 셀 스타일 설정
	 * @param workbook
	 * @return
	 */
	private CellStyle stringCellStyle(final Workbook workbook) {
		return this.defaultCellStyle(workbook);
	}

	/**
	 * 숫자 셀 스타일 설정
	 * @param workbook
	 * @return
	 */
	private CellStyle numberCellStyle(final Workbook workbook) {
		final CellStyle cellStyle = this.defaultCellStyle(workbook);
		cellStyle.setAlignment(HorizontalAlignment.RIGHT);
		cellStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
		return cellStyle;
	}

	/**
	 * 소수점 숫자 셀 스타일 설정
	 * @param workbook
	 * @return
	 */
	private CellStyle doubleCellStyle(final Workbook workbook) {
		final CellStyle cellStyle = this.defaultCellStyle(workbook);
		cellStyle.setAlignment(HorizontalAlignment.RIGHT);
		cellStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
		return cellStyle;
	}

	/**
	 * 날짜 셀 스타일 설정
	 * @param workbook
	 * @return
	 */
	private CellStyle dateCellStyle(final Workbook workbook) {
		return this.defaultCellStyle(workbook);
	}

	/**
	 * 날짜시분 셀 스타일 설정
	 * @param workbook
	 * @return
	 */
	private CellStyle datehhmmCellStyle(final Workbook workbook) {
		return this.defaultCellStyle(workbook);
	}

	/**
	 * 날짜시분초 셀 스타일 설정
	 * @param workbook
	 * @return
	 */
	private CellStyle datetimeCellStyle(final Workbook workbook) {
		return this.defaultCellStyle(workbook);
	}

	/**
	 * 기본 셀 스타일 설정
	 * @param workbook
	 * @return
	 */
	private CellStyle defaultCellStyle(final Workbook workbook) {
		final CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(BorderStyle.NONE);
		cellStyle.setBorderLeft(BorderStyle.NONE);
		cellStyle.setBorderBottom(BorderStyle.NONE);
		cellStyle.setBorderRight(BorderStyle.NONE);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		return cellStyle;
	}

	/**
	 * 날짜 포맷 변환 - 날짜가 아닌 형식은 그대로 반환
	 * @param data
	 * @param excelType
	 * @return
	 */
	private String replaceDateFormat(final String data, final ExcelColumnType excelType) {
		switch(excelType) {
		case DATE:
			return data.replaceAll(REGEXP_DATE, "$1-$2-$3");
		case DATEHHMM:
			return data.replaceAll(REGEXP_DATEHHMM, "$1-$2-$3 $4:$5");
		case DATETIME:
			return data.replaceAll(REGEXP_DATETIME, "$1-$2-$3 $4:$5:$6");
		default:
			return data;
		}
	}

}
