package com.mavericksoft.stitchx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalDetailsDto {

	String name;
	
	String email;
	
	String mobile;
	
	String shopName;
	
	String address;
	
	String image;
}
