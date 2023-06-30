/**
 * 
 */
package com.mavericksoft.stitchx.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import com.mavericksoft.stitchx.enums.DeliveryTime;
import com.mavericksoft.stitchx.enums.OrderStatus;
import com.mavericksoft.stitchx.models.PantMeasurement;
import com.mavericksoft.stitchx.models.ShirtMeasurement;

import lombok.Data;

/**
 * @author ADMIN
 *
 */
@Data
public class CustomerDto {
	
	private Long id;
	
	private String customerName;
	
	@NotNull
	private Long salesmanId;

	@NotNull
	private String salesman;

	private String mobileNumber;
	
	@NotNull
	private Long productId;	

	@NotNull
	private String productName;	

	@Enumerated(EnumType.STRING)
	@NotNull
	private DeliveryTime deliveryIn;
	
	@NotNull
	private String username;
	
	private String orderCreatedDate;
	
	private String orderUpdatedDate;
	
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
