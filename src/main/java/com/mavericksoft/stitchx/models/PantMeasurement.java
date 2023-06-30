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

import com.mavericksoft.stitchx.enums.OrderStatus;

import lombok.Data;

/**
 * @author kaushikudavant
 *
 */
@Entity
@Table
@Data
public class PantMeasurement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	@NotNull
	private Double seat;

	@Column
	@NotNull
	private Double pantLambi;

	@Column
	@NotNull
	private Double kambar;

	@Column
	@NotNull
	private Double thigh;

	@Column
	@NotNull
	private Double knee;

	@Column
	@NotNull
	private Double bottom;

	@Column
	@NotNull
	private Double latak;
	
	@Column
	private String other;
	
	@Column
	@NotNull
	private String uploadDir;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	private OrderStatus orderStatus;
	
	@Column
	private String qrcodeNumber;
	
	@Column
	private String trialDate;
	
	@Column
	private String deliveryDate;

}
