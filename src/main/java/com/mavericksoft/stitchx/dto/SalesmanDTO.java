package com.mavericksoft.stitchx.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mavericksoft.stitchx.enums.Status;

import lombok.Data;
@Data
public class SalesmanDTO {
	
	@NotEmpty
	@NotNull
	@Size(min = 10, max=10)
	String mobileNumber;
	
	@NotEmpty
	@NotNull
	String password;
	
	@NotEmpty
	@NotNull
	String name;
	
	@NotEmpty
	@NotNull
	String email;

	Status salesmanStatus;

	String profilePic;
	
	String address;
	
	String adharNumber;	

}
