package com.mavericksoft.stitchx.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mavericksoft.stitchx.enums.Status;

import lombok.Data;

@Table
@Entity
@Data
public class ShopOwner {

	@Id
	@GeneratedValue
	Long shopId;

	@Column(unique = true)
	@NotNull
	String username;

	@Column
	@NotNull
	String shopName;

	@Column
	@NotNull
	String ownerName;

	@Column
	String contactPerson;

	@Column
	String contactPersonNumber;

	@Column
	String gst;

	@Column
	Double longitude;

	@Column
	Double latitude;
	
	@Column
	@NotNull
	String address;

	@Column
	String shopPhotoPath;
	
	@Enumerated(EnumType.STRING)
	Status status;

}
