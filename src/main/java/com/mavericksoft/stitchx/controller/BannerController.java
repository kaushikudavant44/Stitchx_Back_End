package com.mavericksoft.stitchx.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.RunningBanner;
import com.mavericksoft.stitchx.service.RunningBannerService;
import com.mavericksoft.stitchx.utilities.Utility;

@RequestMapping(Constant.API)
@RestController
@CrossOrigin
public class BannerController {
	
	@Autowired
	private RunningBannerService runningBannerService;
	
	@Autowired
	private Utility utility;
	
	@GetMapping("banners")
	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN)
	public ResponseEntity<List<RunningBanner>> showBanners() {

		return new ResponseEntity<>(runningBannerService.getBannerByStatus(Status.ACTIVE), HttpStatus.OK);
	}

	@PostMapping(value="banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize(Constant.ACCESS_ADMIN)
	public ResponseEntity<RunningBanner> showBanners(HttpServletRequest request,
			@RequestPart(value = "file", required = true) MultipartFile file, @ModelAttribute("banner") RunningBanner banner) {

		if (!file.isEmpty()) {
			try {
				banner.setImageUrl(utility.getSiteURL(request)+utility.uploadFile(file));
			} catch (Exception e) {
				e.getMessage();
				return new ResponseEntity<>(null,HttpStatus.OK);			
			}

		} 
		
		return new ResponseEntity<>(runningBannerService.save(banner),HttpStatus.OK);

	}

}
