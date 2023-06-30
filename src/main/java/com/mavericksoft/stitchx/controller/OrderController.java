/**
 * 
 */
package com.mavericksoft.stitchx.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.DocumentException;
import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.dto.CustomerDto;
import com.mavericksoft.stitchx.enums.OrderStatus;
import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.models.Customer;
import com.mavericksoft.stitchx.models.Measurements;
import com.mavericksoft.stitchx.service.AdminService;
import com.mavericksoft.stitchx.service.CustomerService;
import com.mavericksoft.stitchx.user.api.response.QCResponse;
import com.mavericksoft.stitchx.user.exception.CustomerMeasurementDoesNotExist;
import com.mavericksoft.stitchx.user.exception.InvalidQRCode;
import com.mavericksoft.stitchx.user.exception.OrderDoesNotExist;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;
import com.mavericksoft.stitchx.user.exception.TokenDoesNotExist;
import com.mavericksoft.stitchx.utilities.UserPDFExporter;
import com.mavericksoft.stitchx.validations.ProductValidations;

import io.swagger.v3.oas.annotations.Operation;


/**
 * @author kaushikudavant
 *
 */
@RestController
@RequestMapping(Constant.API)
@CrossOrigin
public class OrderController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AdminService adminService;

	@Autowired
	ProductValidations productValidation;

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN)
	@PostMapping(value = "new/customer/order/{productId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<Customer> saveOrder(@RequestPart("customerOrderDto") String customerOrder,
			@RequestPart("file") List<MultipartFile> files, @PathVariable("productId") Long productId, HttpServletRequest request)
			throws Exception {

		if (Boolean.FALSE.equals(productValidation.isProductAvailable(productId))) {
			throw new ProductExistsException("Given product is not present... Please select correct product");
		}

		return new ResponseEntity<>(customerService.saveOrder(customerOrder, files, productId, request), HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN_AND_QUALITY_CHECKER)
	@GetMapping("product/measurement/details")
	public ResponseEntity<Map<String, List<String>>> getProducts(@RequestParam("Products") Products product)
			throws ProductExistsException {
		return new ResponseEntity<>(adminService.getProductMeasurements(product), HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN)
	@GetMapping("customer/orders")
	public ResponseEntity<List<CustomerDto>> getAllShopsCustomerOrders(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "desc") String sortOrder,
			@RequestParam(required = true, defaultValue = "ALL") String orderStatus,@RequestParam(required = false) String searchBy) {
		Pageable pageRequest = null;
		if (sortOrder.equalsIgnoreCase(Constant.ASC)) {
			pageRequest = PageRequest.of(page, size, Sort.by(sort).ascending());
		} else {
			pageRequest = PageRequest.of(page, size, Sort.by(sort).descending());
		}
		List<CustomerDto> orders = customerService.getMeasurementsByUserNameAndOrderStatus(pageRequest, orderStatus, searchBy);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN)
	@GetMapping("customer/orders/{page}/{size}")
	public ResponseEntity<List<CustomerDto>> getShopsCustomerOrders(@PathVariable int page, @PathVariable int size) {
		Pageable pageRequest = PageRequest.of(page, size);
		String search = "";
		return new ResponseEntity<>(customerService.getMeasurementsByUserNameAndOrderStatus(pageRequest,
				OrderStatus.ORDER_INITIALIZE.name(), search), HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN)
	@PutMapping("delete/order/{orderId}/{measurementId}")
	public ResponseEntity<Customer> deleteOrder(@PathVariable("orderId") Long customerId,
			@PathVariable("measurementId") Long measurementId) throws CustomerMeasurementDoesNotExist {
		return new ResponseEntity<>(customerService.deleteOrderByOrderIdAndMeasurementId(customerId, measurementId),
				HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_ADMIN_AND_SHOPOWNER)
	@GetMapping("customer/order/{orderId}/{measurementId}")
	public ResponseEntity<CustomerDto> getOrderDetails(@PathVariable("orderId") Long orderId,
			@PathVariable("measurementId") Long measurementId, HttpServletResponse response) throws CustomerMeasurementDoesNotExist, DocumentException, IOException {
		
		return new ResponseEntity<>(customerService.getOrderDetailsById(orderId, measurementId), HttpStatus.OK);
	}
	
	@PreAuthorize(Constant.ACCESS_SHOP_OWNER_AND_SALESMAN_AND_ADMIN_AND_DELIVERY_BOY)
	@GetMapping("customer/order/{orderId}/{measurementId}/pdf")
	public void downloadOrderPDF(@PathVariable("orderId") Long orderId,
			@PathVariable("measurementId") Long measurementId, HttpServletResponse response) throws CustomerMeasurementDoesNotExist, DocumentException, IOException {
	
		CustomerDto orderDetailsById = customerService.getOrderDetailsById(orderId, measurementId);
		response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename="+orderDetailsById.getCustomerName()+"_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
		UserPDFExporter exporter = new UserPDFExporter(orderDetailsById);
        exporter.export(response);
		
	}

	@PreAuthorize(Constant.ACCESS_SHOPOWNER)
	@PutMapping("place/order/{orderId}/{measurementId}/{qrCodeId}")
	public ResponseEntity<Customer> placeOrder(@PathVariable("orderId") Long customerId,
			@PathVariable("measurementId") Long measurementId, @PathVariable("qrCodeId") String qrCodeId)
			throws CustomerMeasurementDoesNotExist, TokenDoesNotExist, OrderDoesNotExist, InvalidQRCode {
		return new ResponseEntity<>(customerService.placeOrder(customerId, measurementId, qrCodeId), HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_DELIVERY_BOY)
	@PutMapping("collect/order/{qrCodeId}")
	@Operation(tags = "delivery-boy-controller")
	public ResponseEntity<String> orderCollectFromShop(@PathVariable("qrCodeId") String qrCodeId, @RequestParam(required = true, defaultValue = "QR_SCAN_AND_ORDERED") String orderStatus)
			throws InvalidQRCode, OrderDoesNotExist {
		return new ResponseEntity<>(customerService.collectOrder(qrCodeId,orderStatus), HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_ADMIN_AND_DELIVERY_BOY)
	@GetMapping("collected/customer/orders")
	@Operation(tags = "delivery-boy AND admin-controller")
	public ResponseEntity<List<CustomerDto>> getShopsCustomerOrders(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "desc") String sortOrder,@RequestParam(required = true, defaultValue = "ORDER_DELIVER_FROM_SHOP") String orderStatus,
			@RequestParam(required = false) String search, @RequestParam String username) {
		Pageable pageRequest = null;
		
		if (sortOrder.equalsIgnoreCase(Constant.ASC)) {
			pageRequest = PageRequest.of(page, size, Sort.by(sort).ascending());
		} 
		List<CustomerDto> orders = customerService.getOrdesByShopNameAndOrderStatus(pageRequest,
				orderStatus, search, username);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@PreAuthorize(Constant.ACCESS_ADMIN)
	@PutMapping("deliverd/order/stitchx/{qrCodeId}")
	@Operation(tags = "admin-controller")
	public ResponseEntity<CustomerDto> orderDeliverdToStitchx(@PathVariable("qrCodeId") String qrCodeId)
			throws InvalidQRCode, OrderDoesNotExist, NumberFormatException, CustomerMeasurementDoesNotExist {
		return new ResponseEntity<>(customerService.orderDeliverdToStitchx(qrCodeId), HttpStatus.OK);
	}

	@PreAuthorize(Constant.QUALITY_CHECKER)
	@PostMapping("deliverd/order/stitchx/{qrCodeId}")
	@Operation(tags = "quality-check-controller")
	public ResponseEntity<List<QCResponse>> checkOrderMeasurement(@PathVariable("qrCodeId") String qrCodeId,
			 @RequestBody Measurements measurements) throws InvalidQRCode, OrderDoesNotExist {
		return new ResponseEntity<>(customerService.checkOrderMeasurement(qrCodeId, measurements), HttpStatus.OK);
	}

}
