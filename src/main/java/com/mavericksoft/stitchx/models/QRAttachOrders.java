package com.mavericksoft.stitchx.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.mavericksoft.stitchx.common.AuditModel;

import lombok.Data;

@Data
public class QRAttachOrders extends AuditModel{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@Column
	@NotNull
	Long orderId;
	
	@Column
	@NotNull
	Long measurmentId;
	
	@Column
	@NotNull
	Long tokenId;
	
	@Column
	@NotNull
	Long qrCodeId;	

}
