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

package kyungseo.poc.simple.web.appcore.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.fileupload.util.mime.MimeUtility;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import kyungseo.poc.simple.web.appcore.dto.file.FileInfo;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Component
public class FileUtil {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final Map<String, String> mineTypeMap = new HashMap<String, String>();

    static {
        mineTypeMap.put("acx", "application/internet-property-stream");
        mineTypeMap.put("ai", "application/taiwstscript");
        mineTypeMap.put("aif", "audio/x-aiff");
        mineTypeMap.put("aifc", "audio/aiff");
        mineTypeMap.put("aiff", "audio/aiff");
        mineTypeMap.put("asf", "video/x-ms-asf");
        mineTypeMap.put("asr", "video/x-ms-asf");
        mineTypeMap.put("asx", "video/x-ms-asf");
        mineTypeMap.put("au", "audio/basic");
        mineTypeMap.put("avi", "video/x-msvideo");
        mineTypeMap.put("axs", "application/olescript");
        mineTypeMap.put("bas", "text/plain");
        mineTypeMap.put("bcpio", "application/x-bcpio");
        mineTypeMap.put("bin", "application/octet-stream");
        mineTypeMap.put("bmp", "image/bmp");
        mineTypeMap.put("c", "text/plain");
        mineTypeMap.put("cat", "application/vndms-pkiseccat");
        mineTypeMap.put("cdf", "application/x-cdf");
        mineTypeMap.put("cer", "application/x-x509-ca-cert");
        mineTypeMap.put("clp", "application/x-msclip");
        mineTypeMap.put("cmx", "image/x-cmx");
        mineTypeMap.put("cod", "image/cis-cod");
        mineTypeMap.put("cpio", "application/x-cpio");
        mineTypeMap.put("crd", "application/x-mscardfile");
        mineTypeMap.put("crl", "application/pkix-crl");
        mineTypeMap.put("crt", "application/x-x509-ca-cert");
        mineTypeMap.put("csh", "application/x-csh");
        mineTypeMap.put("css", "text/css");
        mineTypeMap.put("dcr", "application/x-director");
        mineTypeMap.put("der", "application/x-x509-ca-cert");
        mineTypeMap.put("dib", "image/bmp");
        mineTypeMap.put("dir", "application/x-director");
        mineTypeMap.put("dll", "application/x-msdownload");
        mineTypeMap.put("doc", "application/msword");
        mineTypeMap.put("dot", "application/msword");
        mineTypeMap.put("dvi", "application/x-dvi");
        mineTypeMap.put("dxr", "application/x-director");
        mineTypeMap.put("eml", "message/rfc822");
        mineTypeMap.put("eps", "application/taiwstscript");
        mineTypeMap.put("etx", "text/x-setext");
        mineTypeMap.put("evy", "application/envoy");
        mineTypeMap.put("exe", "application/octet-stream");
        mineTypeMap.put("fif", "application/fractals");
        mineTypeMap.put("flr", "x-world/x-vrml");
        mineTypeMap.put("gif", "image/gif");
        mineTypeMap.put("gtar", "application/x-gtar");
        mineTypeMap.put("gz", "application/x-gzip");
        mineTypeMap.put("h", "text/plain");
        mineTypeMap.put("hdf", "application/x-hdf");
        mineTypeMap.put("hlp", "application/winhlp");
        mineTypeMap.put("hqx", "application/mac-binhex40");
        mineTypeMap.put("hta", "application/hta");
        mineTypeMap.put("htc", "text/x-component");
        mineTypeMap.put("htm", "text/html");
        mineTypeMap.put("html", "text/html");
        mineTypeMap.put("htt", "text/webviewhtml");
        mineTypeMap.put("ico", "image/x-icon");
        mineTypeMap.put("ief", "image/ief");
        mineTypeMap.put("iii", "application/x-iphone");
        mineTypeMap.put("ins", "application/x-internet-signup");
        mineTypeMap.put("isp", "application/x-internet-signup");
        mineTypeMap.put("ivf", "video/x-ivf");
        mineTypeMap.put("jfif", "image/pjpeg");
        mineTypeMap.put("jpe", "image/jpeg");
        mineTypeMap.put("jpeg", "image/jpeg");
        mineTypeMap.put("jpg", "image/jpeg");
        mineTypeMap.put("js", "application/x-javascript");
        mineTypeMap.put("latex", "application/x-latex");
        mineTypeMap.put("lsf", "video/x-la-asf");
        mineTypeMap.put("lsx", "video/x-la-asf");
        mineTypeMap.put("m13", "application/x-msmediaview");
        mineTypeMap.put("m14", "application/x-msmediaview");
        mineTypeMap.put("m1v", "video/mpeg");
        mineTypeMap.put("m3u", "audio/x-mpegurl");
        mineTypeMap.put("man", "application/x-troff-man");
        mineTypeMap.put("mdb", "application/x-msaccess");
        mineTypeMap.put("me", "application/x-troff-me");
        mineTypeMap.put("mht", "message/rfc822");
        mineTypeMap.put("mhtml", "message/rfc822");
        mineTypeMap.put("mid", "audio/mid");
        mineTypeMap.put("mny", "application/x-msmoney");
        mineTypeMap.put("mov", "video/quicktime");
        mineTypeMap.put("movie", "video/x-sgi-movie");
        mineTypeMap.put("mp2", "video/mpeg");
        mineTypeMap.put("mp3", "audio/mpeg");
        mineTypeMap.put("mpa", "video/mpeg");
        mineTypeMap.put("mpe", "video/mpeg");
        mineTypeMap.put("mpeg", "video/mpeg");
        mineTypeMap.put("mpg", "video/mpeg");
        mineTypeMap.put("mpp", "application/vnd.ms-project");
        mineTypeMap.put("mpv2", "video/mpeg");
        mineTypeMap.put("ms", "application/x-troff-ms");
        mineTypeMap.put("mvb", "application/x-msmediaview");
        mineTypeMap.put("nc", "application/x-netcdf");
        mineTypeMap.put("nws", "message/rfc822");
        mineTypeMap.put("oda", "application/oda");
        mineTypeMap.put("ods", "application/oleobject");
        mineTypeMap.put("p10", "application/pkcs10");
        mineTypeMap.put("p12", "application/x-pkcs12");
        mineTypeMap.put("p7b", "application/x-pkcs7-certificates");
        mineTypeMap.put("p7c", "application/pkcs7-mime");
        mineTypeMap.put("p7m", "application/pkcs7-mime");
        mineTypeMap.put("p7r", "application/x-pkcs7-certreqresp");
        mineTypeMap.put("p7s", "application/pkcs7-signature");
        mineTypeMap.put("pbm", "image/x-portable-bitmap");
        mineTypeMap.put("pdf", "application/pdf");
        mineTypeMap.put("pfx", "application/x-pkcs12");
        mineTypeMap.put("pgm", "image/x-portable-graymap");
        mineTypeMap.put("pko", "application/vndms-pkipko");
        mineTypeMap.put("pma", "application/x-perfmon");
        mineTypeMap.put("pmc", "application/x-perfmon");
        mineTypeMap.put("pml", "application/x-perfmon");
        mineTypeMap.put("pmr", "application/x-perfmon");
        mineTypeMap.put("pmw", "application/x-perfmon");
        mineTypeMap.put("png", "image/png");
        mineTypeMap.put("pnm", "image/x-portable-anymap");
        mineTypeMap.put("pot", "application/vnd.ms-powerpoint");
        mineTypeMap.put("ppm", "image/x-portable-pixmap");
        mineTypeMap.put("pps", "application/vnd.ms-powerpoint");
        mineTypeMap.put("ppt", "application/vnd.ms-powerpoint");
        mineTypeMap.put("prf", "application/pics-rules");
        mineTypeMap.put("ps", "application/taiwstscript");
        mineTypeMap.put("pub", "application/x-mspublisher");
        mineTypeMap.put("qt", "video/quicktime");
        mineTypeMap.put("ra", "audio/x-pn-realaudio");
        mineTypeMap.put("ram", "audio/x-pn-realaudio");
        mineTypeMap.put("ras", "image/x-cmu-raster");
        mineTypeMap.put("rgb", "image/x-rgb");
        mineTypeMap.put("rmi", "audio/mid");
        mineTypeMap.put("roff", "application/x-troff");
        mineTypeMap.put("rtf", "application/rtf");
        mineTypeMap.put("rtx", "text/richtext");
        mineTypeMap.put("scd", "application/x-msschedule");
        mineTypeMap.put("sct", "text/scriptlet");
        mineTypeMap.put("setpay", "application/set-payment-initiation");
        mineTypeMap.put("setreg", "application/set-registration-initiation");
        mineTypeMap.put("sh", "application/x-sh");
        mineTypeMap.put("shar", "application/x-shar");
        mineTypeMap.put("sit", "application/x-stuffit");
        mineTypeMap.put("snd", "audio/basic");
        mineTypeMap.put("spc", "application/x-pkcs7-certificates");
        mineTypeMap.put("spl", "application/futuresplash");
        mineTypeMap.put("src", "application/x-wais-source");
        mineTypeMap.put("sst", "application/vndms-pkicertstore");
        mineTypeMap.put("stl", "application/vndms-pkistl");
        mineTypeMap.put("stm", "text/html");
        mineTypeMap.put("sv4cpio", "application/x-sv4cpio");
        mineTypeMap.put("sv4crc", "application/x-sv4crc");
        mineTypeMap.put("t", "application/x-troff");
        mineTypeMap.put("tar", "application/x-tar");
        mineTypeMap.put("tcl", "application/x-tcl");
        mineTypeMap.put("tex", "application/x-tex");
        mineTypeMap.put("texi", "application/x-texinfo");
        mineTypeMap.put("texinfo", "application/x-texinfo");
        mineTypeMap.put("tgz", "application/x-compressed");
        mineTypeMap.put("tif", "image/tiff");
        mineTypeMap.put("tiff", "image/tiff");
        mineTypeMap.put("tr", "application/x-troff");
        mineTypeMap.put("trm", "application/x-msterminal");
        mineTypeMap.put("tsv", "text/tab-separated-values");
        mineTypeMap.put("txt", "text/plain");
        mineTypeMap.put("uls", "text/iuls");
        mineTypeMap.put("ustar", "application/x-ustar");
        mineTypeMap.put("vcf", "text/x-vcard");
        mineTypeMap.put("wav", "audio/wav");
        mineTypeMap.put("wcm", "application/vnd.ms-works");
        mineTypeMap.put("wdb", "application/vnd.ms-works");
        mineTypeMap.put("wks", "application/vnd.ms-works");
        mineTypeMap.put("wmf", "application/x-msmetafile");
        mineTypeMap.put("wps", "application/vnd.ms-works");
        mineTypeMap.put("wri", "application/x-mswrite");
        mineTypeMap.put("wrl", "x-world/x-vrml");
        mineTypeMap.put("wrz", "x-world/x-vrml");
        mineTypeMap.put("xaf", "x-world/x-vrml");
        mineTypeMap.put("xbm", "image/x-xbitmap");
        mineTypeMap.put("xla", "application/vnd.ms-excel");
        mineTypeMap.put("xlc", "application/vnd.ms-excel");
        mineTypeMap.put("xlm", "application/vnd.ms-excel");
        mineTypeMap.put("xls", "application/vnd.ms-excel");
        mineTypeMap.put("xlt", "application/vnd.ms-excel");
        mineTypeMap.put("xlw", "application/vnd.ms-excel");
        mineTypeMap.put("xml", "text/xml");
        mineTypeMap.put("xof", "x-world/x-vrml");
        mineTypeMap.put("xpm", "image/x-xpixmap");
        mineTypeMap.put("xsl", "text/xml");
        mineTypeMap.put("xwd", "image/x-xwindowdump");
        mineTypeMap.put("z", "application/x-compress");
        mineTypeMap.put("zip", "application/x-zip-compressed");
    }

	private final String rootUploadPath;

	public FileUtil(@Value("${spring.servlet.multipart.location}") String rootUploadPath) {
		this.rootUploadPath = rootUploadPath;
	}

	public Optional<FileInfo> upload(final MultipartFile multipartFile, final String uploadSubPath, final String newFilename) {
		return Optional.ofNullable(multipartFile)
				.filter(mf -> StringUtils.isNotEmpty(mf.getOriginalFilename()))
				.map(mf -> {
					// 1. Create the upload directory
					Path uploadPath = Paths.get(FilenameUtils.normalizeNoEndSeparator(rootUploadPath), uploadSubPath);
					try {
						Files.createDirectories(uploadPath);
						if(Files.isDirectory(uploadPath, LinkOption.NOFOLLOW_LINKS) == false) {
							throw new FileNotFoundException("Unable to create directory: " + uploadPath.toString());
						}
						// 2. Upload file
						final String sourceFilename = MimeUtility.decodeText(mf.getOriginalFilename());
						final String sourceFileExtension = FilenameUtils.getExtension(sourceFilename).toLowerCase();
						final String fullNewFilename = new StringBuilder(newFilename)
								.append(FilenameUtils.EXTENSION_SEPARATOR).append(sourceFileExtension).toString();
						final Path destinationPath = Paths.get(uploadPath.toString(), fullNewFilename);

						mf.transferTo(destinationPath.toAbsolutePath().toFile());
						// 3. Return file info
						return FileInfo.builder().uploadPath(uploadPath.toString()).uploadFilename(fullNewFilename)
								.originalFilename(sourceFilename).build();
					} catch (IOException e) {
					    LOGGER.error(e.getMessage());
						return null;
					}
				});
	}

    /**
     * 파일명으로 MIME 타입 리턴.
     *
     * @param filename 파일명
     * @return String
     */
    public static String getMimeType(String filename) {
        String result = null;

        if (filename != null) {
            int i;
            String ext = (i = filename.lastIndexOf(".")) >= 0 ? filename.substring(i + 1, filename.length()).toLowerCase() : "";

            result = mineTypeMap.get(ext);
            if (result == null) {
                result = "application/octet-stream";
            }
        }

        return result;
    }

}
