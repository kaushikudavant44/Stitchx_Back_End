package com.mavericksoft.stitchx.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mavericksoft.stitchx.common.AuditModel;

import lombok.Data;

@Entity
@Table
@Data
public class GeneratedTokens extends AuditModel{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private String productName;
	
	private String imagePath;
	
	private String qrData;
	
}
