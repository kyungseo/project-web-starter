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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import kyungseo.poc.simple.web.appcore.dto.file.FileBucket;
import kyungseo.poc.simple.web.appcore.dto.file.MultiFileBucket;
import kyungseo.poc.simple.web.appcore.validation.FileValidator;
import kyungseo.poc.simple.web.appcore.validation.MultiFileValidator;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@RequestMapping(value = "/gw/api/v2")
public class FileUploadController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	private Environment env;

	@Autowired
	FileValidator fileValidator;

	@Autowired
	MultiFileValidator multiFileValidator;

	@InitBinder("fileBucket")
	protected void initBinderFileBucket(WebDataBinder binder) {
		binder.setValidator(fileValidator);
	}

	@InitBinder("multiFileBucket")
	protected void initBinderMultiFileBucket(WebDataBinder binder) {
		binder.setValidator(multiFileValidator);
	}

	/*
		<form:form method="POST" modelAttribute="fileBucket" enctype="multipart/form-data">
			<label for="file">Upload a file</label>
			<form:input type="file" path="file" id="file"/>
			<input type="submit" value="Upload">
		</form:form>
	 */
	@RequestMapping(value = "/singleFileUpload", method = RequestMethod.POST)
	public ResponseEntity<Boolean> singleFileUpload(FileBucket fileBucket, BindingResult result) {
		ResponseEntity<Boolean> responseEntity = null;

		try {
			LOGGER.debug("Fetching file");
			MultipartFile multipartFile = fileBucket.getFile();

			// Now do something with file...
			FileCopyUtils.copy(fileBucket.getFile().getBytes(),
					new File(env.getProperty("app.fileupload.dir") + fileBucket.getFile().getOriginalFilename()));
			String fileName = multipartFile.getOriginalFilename();

			responseEntity = new ResponseEntity<>(true, HttpStatus.OK);
		}
		catch (Exception ex) {
			LOGGER.debug("validation errors");

			responseEntity = new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return responseEntity;
	}

	@RequestMapping(value = "/multiFileUpload", method = RequestMethod.POST)
	public ResponseEntity<Boolean> multiFileUpload(MultiFileBucket multiFileBucket, BindingResult result) {
		ResponseEntity<Boolean> responseEntity = null;

		try {
			LOGGER.debug("Fetching files");
			List<String> fileNames = new ArrayList<String>();

			for (FileBucket bucket : multiFileBucket.getFiles()) {
				FileCopyUtils.copy(bucket.getFile().getBytes(),
						new File(env.getProperty("app.fileupload.dir") + bucket.getFile().getOriginalFilename()));
				fileNames.add(bucket.getFile().getOriginalFilename());
			}

			responseEntity = new ResponseEntity<>(true, HttpStatus.OK);
		}
		catch (Exception ex) {
			LOGGER.debug("validation errors");

			responseEntity = new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return responseEntity;
	}

}
