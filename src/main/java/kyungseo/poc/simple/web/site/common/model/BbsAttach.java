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

package kyungseo.poc.simple.web.site.common.model;


import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class BbsAttach {
	private static final long serialVersionUID = 1L;

	private String bbsId;

	private String docNumber;

	private String attSeqno;

	private String fileid;

	private String logicalfilename;

	private String physicalfilename;

	private String filepath;

	private String filesize;

	private String createdby;

	private String createdate;

	private String modifiedby;

	private String modifydate;

	private String version;

	private String fileurl;

	private String ext;

	private String attFilepath;

	public String getBbsId() {
		return bbsId;
	}

	public void setBbsId(String bbsId) {
		this.bbsId = bbsId;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getAttSeqno() {
		return attSeqno;
	}

	public void setAttSeqno(String attSeqno) {
		this.attSeqno = attSeqno;
	}

	public String getFileid() {
		return fileid;
	}

	public void setFileid(String fileid) {
		this.fileid = fileid;
	}

	public String getLogicalfilename() {
		return logicalfilename;
	}

	public void setLogicalfilename(String logicalfilename) {
		this.logicalfilename = logicalfilename;
	}

	public String getPhysicalfilename() {
		return physicalfilename;
	}

	public void setPhysicalfilename(String physicalfilename) {
		this.physicalfilename = physicalfilename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getModifiedby() {
		return modifiedby;
	}

	public void setModifiedby(String modifiedby) {
		this.modifiedby = modifiedby;
	}

	public String getModifydate() {
		return modifydate;
	}

	public void setModifydate(String modifydate) {
		this.modifydate = modifydate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileurl() {
		return fileurl;
	}

	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getAttFilepath() {
		return attFilepath;
	}

	public void setAttFilepath(String attFilepath) {
		this.attFilepath = attFilepath;
	}

	@Override
	public String toString() {
		return "BbsAttach [bbsId=" + bbsId + ", docNumber=" + docNumber + ", attSeqno=" + attSeqno + ", fileid="
				+ fileid + ", logicalfilename=" + logicalfilename + ", physicalfilename=" + physicalfilename
				+ ", filepath=" + filepath + ", filesize=" + filesize + ", createdby=" + createdby + ", createdate="
				+ createdate + ", modifiedby=" + modifiedby + ", modifydate=" + modifydate + ", version=" + version
				+ ", fileurl=" + fileurl + ", ext=" + ext + ", attFilepath=" + attFilepath + "]";
	}

}
