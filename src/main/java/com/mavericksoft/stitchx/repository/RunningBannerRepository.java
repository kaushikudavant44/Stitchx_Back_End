package com.mavericksoft.stitchx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.RunningBanner;

public interface RunningBannerRepository extends JpaRepository<RunningBanner, Integer>{

	List<RunningBanner> findByBannerStatus(Status active);

}