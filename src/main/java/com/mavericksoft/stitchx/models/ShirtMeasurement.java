/**
 * 
 */
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
@Table
@Entity
@Data
public class ShirtMeasurement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@Column
	@NotNull
	Double shirtLambi;

	@Column
	@NotNull
	Double sholder;

	@Column
	@NotNull
	Double bahi;

	@Column
	@NotNull
	Double chest;

	@Column
	@NotNull
	Double stomache;

	@Column
	@NotNull
	Double seat;

	@Column
	@NotNull
	Double neck;
	
	@Column
	@NotNull
	private String shirtType;
	
	@Column
	@NotNull
	private String neckType;
	
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
	
	@Column
	private Double lowerChest;
	
	@Column
	private String buttonType;
	
}
