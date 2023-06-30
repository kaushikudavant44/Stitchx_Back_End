package com.mavericksoft.stitchx.service;

import java.util.List;

import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.RunningBanner;

public interface RunningBannerService {

	List<RunningBanner> getBannerByStatus(Status created);

	RunningBanner save(RunningBanner bannerForm);

}