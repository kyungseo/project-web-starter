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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.view.AbstractView;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public abstract class AbstractXlsxCustomView extends AbstractView {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	public AbstractXlsxCustomView() {
		this.setContentType(CONTENT_TYPE);
	}

	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	@Override
	protected void renderMergedOutputModel(final Map<String, Object> model, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		Workbook workbook = null;
		if (model.containsKey("workbook")) {
			// 미리 생성된 workbook
			workbook = (Workbook) model.get("workbook");
		}
		else {
			// 엑셀 데이터로 workbook 생성
			workbook = createWorkbook();
			buildExcelDocument(model, workbook, request, response);
		}

		setResponseHeaders(response, makeFilename(request, (String) model.get("fileName")));

		renderWorkbook(workbook, response);
	}

	protected Workbook createWorkbook() {
		return new SXSSFWorkbook();
	}

	protected void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
		workbook.write(response.getOutputStream());
		if (workbook instanceof Closeable) {
			((Closeable) workbook).close();
		}
		((SXSSFWorkbook) workbook).dispose();
	}

	private void setResponseHeaders(final HttpServletResponse response, final String filename) {
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				new StringBuilder("attachment; filename=\"").append(filename).append(".xlsx\"").toString());
		response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=0, private, must-revalidate");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setContentType(getContentType());
	}

	private String makeFilename(final HttpServletRequest request, final String filename)
			throws UnsupportedEncodingException {

		final String userAgent = request.getHeader(HttpHeaders.USER_AGENT).toLowerCase();
		if (StringUtils.contains(userAgent, "msie") || StringUtils.contains(userAgent, "trident")
				|| StringUtils.contains(userAgent, "edge/")) {
			// MS IE, Edge
			return URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "\\ ");
		}
		else {
			// FF, Opera, Safari, Chrome
			return new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		}
	}

	protected abstract void buildExcelDocument(
			Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}
