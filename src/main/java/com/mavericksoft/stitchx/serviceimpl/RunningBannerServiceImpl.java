package com.mavericksoft.stitchx.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.RunningBanner;
import com.mavericksoft.stitchx.repository.RunningBannerRepository;
import com.mavericksoft.stitchx.service.RunningBannerService;

@Service
public class RunningBannerServiceImpl implements RunningBannerService {

	@Autowired
	RunningBannerRepository runningBannerRepository;

	@Override
	@PreAuthorize(Constant.ACCESS_ADMIN_AND_SHOPOWNER)
	public List<RunningBanner> getBannerByStatus(Status active) {
		return runningBannerRepository.findByBannerStatus(active);
	}

	@Override
	@PreAuthorize(Constant.ACCESS_ADMIN)
	public RunningBanner save(RunningBanner bannerForm) {
		
			return runningBannerRepository.save(bannerForm);
		
	}

}