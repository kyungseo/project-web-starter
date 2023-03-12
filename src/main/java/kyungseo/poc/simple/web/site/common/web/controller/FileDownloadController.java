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

package kyungseo.poc.simple.web.site.common.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kyungseo.poc.simple.web.appcore.util.EncodingUtil;
import kyungseo.poc.simple.web.site.common.model.BbsAttach;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@RequestMapping(value = "/gw/api/v2")
public class FileDownloadController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadController.class);

	@Autowired
	private Environment env;

	//@Autowired
	//BbsAttachService bbsAttachService;

	@RequestMapping(value = "/imageView", method = RequestMethod.GET)
	public ResponseEntity<Boolean> imageView(HttpServletRequest request, HttpServletResponse response) {
		ResponseEntity<Boolean> responseEntity = null;

		try {

			String bbsId = request.getParameter("bbsId");
			String docNumber = request.getParameter("docNumber");
			String attSeqno = request.getParameter("attSeqno");

			BbsAttach attach = new BbsAttach();

			attach.setBbsId(bbsId);
			attach.setDocNumber(docNumber);
			attach.setAttSeqno(attSeqno);

			//BbsAttach result = bbsAttachService.selectOne(attach);
			//임시
			BbsAttach result = new BbsAttach();

			String filePath = result.getFileurl();

			if(request.getParameter("thumb") != null){
				int lastIndex = filePath.lastIndexOf(".");
				filePath = filePath.substring(0,lastIndex)+"_thumImg"+filePath.substring(lastIndex);
			}
			//파일정보 가져오기

			if (!"".equals(filePath) && filePath != null) {
		        File imgFile = new File(filePath);

		        if ( imgFile.exists() ) {
			        FileInputStream ifo = new FileInputStream(filePath);
			        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        byte[] buf = new byte[1024];
			        int readLength = 0;
			        while( (readLength = ifo.read(buf)) != -1) {
			        	baos.write(buf,0,readLength);
			        }

			        byte[] imgbuf = null;
			        imgbuf = baos.toByteArray();
			        baos.close();
			        ifo.close();

			        int length = imgbuf.length;
			        response.setContentType("image/jpeg");
			        ServletOutputStream out = response.getOutputStream();
			        out.write(imgbuf, 0, length);
			        out.close();
		        }
	        }
			responseEntity = new ResponseEntity<>(true, HttpStatus.OK);
		}
		catch (Exception ex) {
			LOGGER.debug("validation errors");

			responseEntity = new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return responseEntity;
	}


	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public void download(
			@RequestHeader(value = "filePath") String filePath,
			@RequestHeader(value = "logicalFileName") String logicalFileName,
			@RequestHeader(value = "physicalFileName") String physicalFileName,
			@RequestHeader(value = "ext") String ext,
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestHeader(value = "Authorization", defaultValue = "") String authorization,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		LOGGER.debug(">> filePath: " + filePath);
		LOGGER.debug(">> filePath (decoded): " + EncodingUtil.decodeURIComponent(filePath));
		LOGGER.debug(">> logicalFileName: " + logicalFileName);
		LOGGER.debug(">> physicalFileName: " + physicalFileName);
		LOGGER.debug(">> ext: " + ext);
		LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");


		BufferedInputStream fin = null;
		BufferedOutputStream outs = null;

		try {
			response.reset();

			if (ext.equals("doc")) {
				response.setContentType("application/msword;charset=MS949");
			} else if (ext.equals("xls")) {
				response.setContentType("application/x-msexcel;charset=MS949");
			} else if (ext.equals("pdf")) {
				response.setContentType("application/x-msdownload");
			} else if (ext.equals("ppt")) {
				response.setContentType("application/x-mspowerpoint");
			} else if (ext.equals("js")) {
				response.setContentType("application/x-javascript");
			} else if (ext.equals("zip")) {
				response.setContentType("application/zip");
			} else if (ext.equals("gif")) {
				response.setContentType("image/gif");
			} else if (ext.equals("jpeg") || ext.equals("jpg")
					|| ext.equals("jpe")) {
				response.setContentType("image/jpeg");
			} else if (ext.equals("css")) {
				response.setContentType("text/css");
			} else if (ext.equals("html") || ext.equals("htm")) {
				response.setContentType("text/html");
			} else if (ext.equals("xml")) {
				response.setContentType("text/xml");
			} else {
				response.setContentType("application/octet-stream");
			}

			//String contentDisposition = FileDownloadUtil.getContentDisposition(request, logicalFileName);
			//logger.debug(">> contentDisposition: " + contentDisposition);

			//response.setHeader("Content-Disposition", contentDisposition);
			response.setHeader("Content-Transfer-Encoding", "binary;");
			//if (file.getFileSize() != null) {
			//	response.setHeader("Content-Length", "" + file.getFileSize());
			//}
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");

			File f = new File(EncodingUtil.decodeURIComponent(filePath) + File.separator + physicalFileName);
			int fSize = (int) f.length();
			response.setHeader("Content-Length", "" + fSize);

			if (f.isFile()) {
				fin = new BufferedInputStream(new FileInputStream(f));
				outs = new BufferedOutputStream(response.getOutputStream());
				int read = 0;
				while ((read = fin.read()) != -1) {
					outs.write(read);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outs != null) {
				outs.flush();
				outs.close();
			}
			if (fin != null) {
				fin.close();
			}
		}
	}

}
