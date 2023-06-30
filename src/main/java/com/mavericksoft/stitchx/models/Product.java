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

import com.mavericksoft.stitchx.common.AuditModel;
import com.mavericksoft.stitchx.enums.ProductType;
import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.enums.TokenType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table
@Data
@EqualsAndHashCode(callSuper=false)
public class Product extends AuditModel{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	String productName;
	
	@Column
	@NotNull
	Status productStatus;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	TokenType tokenType;
	
	@Column
	@NotNull
	Double tokenAmount;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	ProductType productType;

}
