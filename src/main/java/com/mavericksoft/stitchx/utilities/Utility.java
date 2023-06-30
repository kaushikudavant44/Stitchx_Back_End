/**
 * 
 */
package com.mavericksoft.stitchx.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mavericksoft.stitchx.common.Constant;

/**
 * @author kaushikudavant
 *
 */
@Component
public class Utility {

	@Value("${imageuploadpath}")
	private String uploadDir;

	@Value("${classpath.uploadImage}")
	private String classPath;

	public String uploadFile(MultipartFile file) throws Exception {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String fileName;

		if (file.isEmpty()) {
			throw new Exception("Failed to store empty file");
		}
		String url;
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		try {

			fileName = file.getOriginalFilename();

			InputStream is = file.getInputStream();

			File f1 = new File(classPath + uploadDir + Constant.FW_SLASH + userDetails.getUsername());

			f1.mkdir();

			url = f1.getPath() + Constant.FW_SLASH + timeStamp + "_" + fileName;
			Files.copy(is, Paths.get(url), StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException e) {

			e.printStackTrace();

			String msg = String.format("Failed to store file %f", file.getName());

			throw new Exception(msg, e);
		}
		return uploadDir + Constant.FW_SLASH + userDetails.getUsername() + Constant.FW_SLASH + timeStamp + "_"
				+ fileName;
	}

	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	public static LocalDateTime convertDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		return LocalDateTime.parse(date, formatter);
	}
}
