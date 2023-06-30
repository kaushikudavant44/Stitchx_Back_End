package com.mavericksoft.stitchx.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.models.GeneratedTokens;
import com.mavericksoft.stitchx.models.Measurements;
import com.mavericksoft.stitchx.service.AdminService;
import com.mavericksoft.stitchx.service.TokenService;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;

import io.swagger.v3.oas.annotations.Hidden;

@PreAuthorize(Constant.ACCESS_ADMIN)
@RestController
@RequestMapping(Constant.API)
@CrossOrigin
public class AdminController {

	@Autowired
	AdminService adminService;

	@Autowired
	TokenService tokenService;

	@Hidden
	@PostMapping("add/{product}/measurement/")
	public ResponseEntity<Measurements> saveProductMeasurements(@PathVariable("product") Products product,
			@RequestBody Measurements measurements) {

		measurements.setProduct(product);
		return new ResponseEntity<>(adminService.saveMeasurements(measurements), HttpStatus.OK);
	}

	@GetMapping("{product}/measurement/")
	public ResponseEntity<Map<String, List<String>>> getProductMeasurements(@PathVariable("product") Products product)
			throws ProductExistsException {

		return new ResponseEntity<>(adminService.getProductMeasurements(product), HttpStatus.OK);
	}

	@PostMapping(value = "create/{product}/{quantity}/token", produces = "application/zip")
	public void generateTokenForProduct(@PathVariable("product") Products product,
			@PathVariable("quantity") Long quantity, HttpServletResponse response)
			throws ProductExistsException, WriterException, IOException {
		
		List<GeneratedTokens> generateTokens = tokenService.generateTokens(quantity, product);
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
		response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=QR"+Constant.DASH+currentDateTime+Constant.DASH+quantity+".zip");
		try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for(GeneratedTokens fileName : generateTokens) {
                FileSystemResource fileSystemResource = new FileSystemResource(fileName.getImagePath());
                ZipEntry zipEntry = new ZipEntry(fileSystemResource.getFilename());
                zipEntry.setSize(fileSystemResource.contentLength());
                zipEntry.setTime(System.currentTimeMillis());

                zipOutputStream.putNextEntry(zipEntry);

                StreamUtils.copy(fileSystemResource.getInputStream(), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
        } catch (IOException e) {
           e.getMessage();
        }
	}

}
