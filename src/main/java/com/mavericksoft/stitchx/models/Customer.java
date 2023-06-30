/**
 * 
 */
package com.mavericksoft.stitchx.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mavericksoft.stitchx.common.AuditModel;
import com.mavericksoft.stitchx.enums.DeliveryTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kaushikudavant
 *
 */
@Entity
@Table
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=false)
public class Customer extends AuditModel{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	@NotNull
	private String customerName;

	@Column
	@NotNull
	private Long salesmanId;

	@Column
	private String mobileNumber;

	@Column
	@NotNull
	private Long productId;	

	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	private DeliveryTime deliveryIn;
	
	@Column
	@NotNull
	private String username;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(  name = "customer_shirt_measurement", 
	        joinColumns = @JoinColumn(name = "customer_id"), 
	        inverseJoinColumns = @JoinColumn(name = "shirt_measurement_id"))
	private List<ShirtMeasurement> shirtMeasurement = new ArrayList<>();
	
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(  name = "customer_pant_measurement", 
	        joinColumns = @JoinColumn(name = "customer_id"), 
	        inverseJoinColumns = @JoinColumn(name = "pant_measurement_id"))
	private List<PantMeasurement> pantMeasurement = new ArrayList<>();

}
