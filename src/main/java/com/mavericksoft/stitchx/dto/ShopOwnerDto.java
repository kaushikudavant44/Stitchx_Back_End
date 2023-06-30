package com.mavericksoft.stitchx.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ShopOwnerDto {
	
	@NotEmpty
	@NotNull
	@Size(min = 10, max=10)
	String mobileNumber;
	
	@NotEmpty
	@NotNull
	String password;
	
	@NotEmpty
	@NotNull
	String email;
	
	@NotEmpty
	@NotNull
	String shopName;

	@NotEmpty
	@NotNull
	String ownerName;

	String contactPerson;

	String contactPersonNumber;

	String gst;
	
	@NotEmpty
	@NotNull
	String address;

	Double longitude;

	Double latitude;

}
