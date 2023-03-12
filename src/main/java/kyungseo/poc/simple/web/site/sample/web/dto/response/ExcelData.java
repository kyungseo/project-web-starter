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

package kyungseo.poc.simple.web.site.sample.web.dto.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.CollectionUtils;

import kyungseo.poc.simple.web.appcore.data.enums.ExcelColumnType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Getter @Setter
public class ExcelData {

	private String sheetName = "sheet1";
	private List<List<String>> headerList;
	private List<List<String>> dataList;
	private List<ExcelColumnType> typeList;
	private List<String> mergeList;
	private List<CellRangeAddress> mergeInfoList;

	public ExcelData() {}

	public ExcelData(final String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * 헤더 로우 추가
	 * @param headers
	 */
	public void addRowHeaders(final String... headers) {
		if(CollectionUtils.isEmpty(headerList)) {
			this.headerList = new ArrayList<>();
		}
		this.headerList.add(Arrays.asList(headers));
	}

	/**
	 * 데이터 로우 추가
	 * @param datas
	 */
	public void addRowDatas(final String... datas) {
		if(CollectionUtils.isEmpty(dataList)) {
			this.dataList = new ArrayList<>();
		}
		this.dataList.add(Arrays.asList(datas));
	}

	/**
	 * 컬럼 타입 추가
	 * @param columnType
	 */
	public void addColumnType(final ExcelColumnType columnType) {
		if(CollectionUtils.isEmpty(typeList)) {
			this.typeList = new ArrayList<>();
		}
		this.typeList.add(columnType);
	}

	/**
	 * 컬럼 타입 리스트 설정
	 * @param columnTypes
	 */
	public void setColumnTypes(final ExcelColumnType... columnTypes) {
		this.typeList = new ArrayList<>(Arrays.asList(columnTypes));
	}

	/**
	 * 병합정보 추가 : Standard Area Reference
	 * @param mergeString
	 */
	public void addMergeString(final String mergeString) {
		if(CollectionUtils.isEmpty(mergeList)) {
			this.mergeList = new ArrayList<>();
		}
		this.mergeList.add(mergeString);
	}

	/**
	 * 병합정보 설정 : Standard Area Reference
	 * @param mergeStrings
	 */
	public void setMergeStrings(final String... mergeStrings) {
		this.mergeList = new ArrayList<>(Arrays.asList(mergeStrings));
	}

	/**
	 * 병합정보 추가 : CellRangeAddress
	 * @param firstRow
	 * @param lastRow
	 * @param firstCol
	 * @param lastCol
	 */
	public void addMergeInfo(int firstRow, int lastRow, int firstCol, int lastCol) {
		if(CollectionUtils.isEmpty(mergeInfoList)) {
			this.mergeInfoList = new ArrayList<>();
		}
		this.mergeInfoList.add(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
	}

	/**
	 * 병합정보 설정 : CellRangeAddress<br>
	 * - 각각의 배열은 firstRow, lastRow, firstCol, lastCol 순이 되어야 함.
	 * @param mergeInfos
	 */
	public void setMergeInfos(final int[]... mergeInfos) {
		for(int[] mergeInfo : mergeInfos) {
			this.addMergeInfo(mergeInfo[0], mergeInfo[1], mergeInfo[2], mergeInfo[3]);
		}
	}

}
