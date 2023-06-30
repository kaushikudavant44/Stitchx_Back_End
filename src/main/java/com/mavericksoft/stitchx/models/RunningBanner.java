package com.mavericksoft.stitchx.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.mavericksoft.stitchx.common.AuditModel;
import com.mavericksoft.stitchx.enums.Status;

@Entity
@Table(name = "running_banner")
public class RunningBanner extends AuditModel {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String title;

	private String name;

	private String imageUrl;

	private Status bannerStatus;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Status getBannerStatus() {
		return bannerStatus;
	}

	public void setBannerStatus(Status bannerStatus) {
		this.bannerStatus = bannerStatus;
	}

}
