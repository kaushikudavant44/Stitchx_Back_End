/**
 * 
 */
package com.mavericksoft.stitchx.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mavericksoft.stitchx.enums.DeliveryTime;
import com.mavericksoft.stitchx.models.PantMeasurement;
import com.mavericksoft.stitchx.models.ShirtMeasurement;

import lombok.Data;

/**
 * @author kaushikudavant
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerOrderDto {
	
	
	String customerName;
	
	String mobileNumber;
	
	Long salesmanId;
	
	DeliveryTime deliveryIn;
	
	List<ShirtMeasurement> shirts;

	List<PantMeasurement> pants;
}
