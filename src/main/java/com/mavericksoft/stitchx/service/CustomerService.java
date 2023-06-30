/**
 * 
 */
package com.mavericksoft.stitchx.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.mavericksoft.stitchx.dto.CustomerDto;
import com.mavericksoft.stitchx.models.Customer;
import com.mavericksoft.stitchx.models.Measurements;
import com.mavericksoft.stitchx.user.api.response.QCResponse;
import com.mavericksoft.stitchx.user.exception.CustomerMeasurementDoesNotExist;
import com.mavericksoft.stitchx.user.exception.InvalidQRCode;
import com.mavericksoft.stitchx.user.exception.OrderDoesNotExist;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;
import com.mavericksoft.stitchx.user.exception.TokenDoesNotExist;

/**
 * @author kaushikudavant
 *
 */
public interface CustomerService {
	
	List<Customer> getMeasurements(String username);

	List<CustomerDto> getMeasurementsByUserNameAndOrderStatus(Pageable pageRequest, String init, String searchBy);

	Customer saveOrder(String customerOrder, List<MultipartFile> files, Long productId, HttpServletRequest request) throws ProductExistsException, Exception;

	CustomerDto getOrderDetailsById(Long orderId, Long measurementId) throws CustomerMeasurementDoesNotExist;
	
	Customer placeOrder(Long orderId, Long measurementId, String qrCodeId) throws CustomerMeasurementDoesNotExist, TokenDoesNotExist, OrderDoesNotExist, InvalidQRCode;

	Customer deleteOrderByOrderIdAndMeasurementId(Long customerId, Long measurementId) throws CustomerMeasurementDoesNotExist;

	String collectOrder(String qrCodeId, String orderStatus) throws InvalidQRCode, OrderDoesNotExist;

	CustomerDto orderDeliverdToStitchx(String qrCodeId) throws OrderDoesNotExist, InvalidQRCode, NumberFormatException, CustomerMeasurementDoesNotExist;

	List<QCResponse> checkOrderMeasurement(String qrCodeId, Measurements measurements) throws InvalidQRCode, OrderDoesNotExist;

	List<CustomerDto> getOrdesByShopNameAndOrderStatus(Pageable pageRequest, String orderStatus, String search, String username);
	
}
