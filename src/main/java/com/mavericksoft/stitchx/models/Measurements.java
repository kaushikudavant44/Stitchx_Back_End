package com.mavericksoft.stitchx.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mavericksoft.stitchx.enums.Products;

import lombok.Data;

@Table
@Entity
@Data
public class Measurements {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	Products product;
	
	String shirtTypes;
	
	String neckTypes;

	String lambi;
	String sholder;
	String bahi;
	String chest;
	String lowerChest;
	String stomache;
	String seat;
	String neck;

	String kambar;
	String thigh;
	String knee;
	String bottom;
	String latak;
	


}
