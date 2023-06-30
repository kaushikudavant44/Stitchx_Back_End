/**
 * 
 */
package com.mavericksoft.stitchx.serviceimpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.dto.CustomerDto;
import com.mavericksoft.stitchx.dto.CustomerOrderDto;
import com.mavericksoft.stitchx.enums.DeliveryTime;
import com.mavericksoft.stitchx.enums.OrderStatus;
import com.mavericksoft.stitchx.enums.ProductType;
import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.Customer;
import com.mavericksoft.stitchx.models.Measurements;
import com.mavericksoft.stitchx.models.PantMeasurement;
import com.mavericksoft.stitchx.models.Product;
import com.mavericksoft.stitchx.models.Salesman;
import com.mavericksoft.stitchx.models.ShirtMeasurement;
import com.mavericksoft.stitchx.models.TokenOrder;
import com.mavericksoft.stitchx.repository.CustomerRepository;
import com.mavericksoft.stitchx.repository.PantMeasurementRepository;
import com.mavericksoft.stitchx.repository.ShirtMeasurementsRepository;
import com.mavericksoft.stitchx.service.CustomerService;
import com.mavericksoft.stitchx.service.ProductService;
import com.mavericksoft.stitchx.service.SalesmanService;
import com.mavericksoft.stitchx.service.TokenService;
import com.mavericksoft.stitchx.user.api.response.QCResponse;
import com.mavericksoft.stitchx.user.exception.CustomerMeasurementDoesNotExist;
import com.mavericksoft.stitchx.user.exception.InvalidQRCode;
import com.mavericksoft.stitchx.user.exception.OrderDoesNotExist;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;
import com.mavericksoft.stitchx.user.exception.TokenDoesNotExist;
import com.mavericksoft.stitchx.utilities.Utility;

/**
 * @author kaushikudavant
 *
 */
@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ProductService productService;

	@Autowired
	private Utility utility;

	@Autowired
	private SalesmanService salesmanService;

	@Autowired
	private ModelMapper modelMapper;

	private static final String ROLE_SALESMAN = "ROLE_SALESMAN";

	@Autowired
	private TokenService tokenService;

	@Autowired
	private ShirtMeasurementsRepository shirtMeasurementsRepository;

	@Autowired
	private PantMeasurementRepository pantMeasurementRepository;

	private List<PantMeasurement> uploadPantPictures(List<PantMeasurement> pants, List<MultipartFile> files,
			HttpServletRequest request) {
		List<PantMeasurement> totalPants = new ArrayList<>();
		int pantTabNumber = 1;
		for (PantMeasurement pant : pants) {

			if (!files.isEmpty()) {
				for (MultipartFile file : files) {

					try {
						if (file.getOriginalFilename().contains("PANT" + pantTabNumber)) {
							String uploadURL = utility.uploadFile(file);
							if (pant.getUploadDir() != null) {
								pant.setUploadDir(pant.getUploadDir() + "," + Utility.getSiteURL(request) + uploadURL);
							} else {
								pant.setUploadDir(Utility.getSiteURL(request) + uploadURL);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				pantTabNumber++;
			}
			totalPants.add(pant);
		}
		return totalPants;

	}

	private List<ShirtMeasurement> uploadShirtPictures(List<ShirtMeasurement> shirts, List<MultipartFile> files,
			HttpServletRequest request) {
		List<ShirtMeasurement> totalShirts = new ArrayList<>();
		int shirtTabNumber = 1;
		for (ShirtMeasurement shirt : shirts) {
			if (!files.isEmpty()) {
				for (MultipartFile file : files) {
					if (Objects.nonNull(file.getOriginalFilename())) {
						try {
							if (file.getOriginalFilename().contains("SHIRT" + shirtTabNumber)) {
								String uploadURL = utility.uploadFile(file);
								if (shirt.getUploadDir() != null) {
									shirt.setUploadDir(
											shirt.getUploadDir() + "," + Utility.getSiteURL(request) + uploadURL);
								} else {
									shirt.setUploadDir(Utility.getSiteURL(request) + uploadURL);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				shirtTabNumber++;
			}

			totalShirts.add(shirt);
		}
		return totalShirts;
	}

	@Override
	public List<Customer> getMeasurements(String username) {
		return customerRepository.findByUsername(username);
	}

	@Override
	public List<CustomerDto> getMeasurementsByUserNameAndOrderStatus(Pageable pageRequest, String orderStatus,
			String searchBy) {
		List<CustomerDto> customerDtoList = new ArrayList<>();
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean anyMatch = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_SALESMAN));
		return getCustomerOrders(userDetails.getUsername(), customerDtoList, anyMatch, searchBy, pageRequest,
				orderStatus);
	}

	private List<CustomerDto> getCustomerOrders(String username, List<CustomerDto> customerDtoList, boolean anyMatch,
			String searchBy, Pageable pageRequest, String orderStatus) {
		if (anyMatch) {
			Salesman salesman = salesmanService.getSalesmanByUsername(username);
			List<Customer> customers = new ArrayList<>();
			if (searchBy == null || searchBy.equals("") || searchBy.trim().equals("")) {
				customers = customerRepository.findBySalesmanId(salesman.getId(), pageRequest);
			} else {
				customers = customerRepository
						.findBySalesmanIdAndCustomerNameORMobileNumberORUpdatedAt(salesman.getId(), searchBy);
			}
			for (Customer customer : customers) {
				if (!orderStatus.equalsIgnoreCase(Constant.ALL)) {
					generateOrderResponse(customer, customerDtoList, orderStatus);
				} else {
					customerDtoList.add(getOrderResponse(customer));
				}
			}
			return customerDtoList;
		}
		List<Customer> findByUsernameAndOrderStatus = new ArrayList<>();
		if (searchBy == null || searchBy.equals("") || searchBy.trim().equals("")) {
			findByUsernameAndOrderStatus = customerRepository.findByUsername(username, pageRequest);
		} else {
			findByUsernameAndOrderStatus = customerRepository
					.findByUsernameAndCustomerNameORMobileNumberORUpdatedAt(username, searchBy);
		}
		for (Customer customer : findByUsernameAndOrderStatus) {
			if (!orderStatus.equalsIgnoreCase(Constant.ALL)) {
				generateOrderResponse(customer, customerDtoList, orderStatus);
			} else {
				customerDtoList.add(getOrderResponse(customer));
			}
		}

		return customerDtoList;

	}

	private CustomerDto getOrderResponse(Customer customer) {

		CustomerDto customerdto = new CustomerDto();
		customerdto.setId(customer.getId());
		customerdto.setCustomerName(customer.getCustomerName());
		customerdto.setDeliveryIn(customer.getDeliveryIn());
		customerdto.setMobileNumber(customer.getMobileNumber());
		customerdto.setPantMeasurement(customer.getPantMeasurement());
		customerdto.setProductId(customer.getProductId());
		customerdto.setSalesmanId(customer.getSalesmanId());
		customerdto.setProductName(productService.getProductById(customer.getProductId()).getProductName());
		customerdto.setSalesman(salesmanService.getSalesmanById(customer.getSalesmanId()).getName());
		customerdto.setShirtMeasurement(customer.getShirtMeasurement());
		customerdto.setUsername(customer.getUsername());
		return customerdto;
	}

	private void generateOrderResponse(Customer customer, List<CustomerDto> customerDtoList, String orderStatus) {

		CustomerDto customerdto = new CustomerDto();

		customerdto.setId(customer.getId());
		customerdto.setCustomerName(customer.getCustomerName());
		customerdto.setDeliveryIn(customer.getDeliveryIn());
		customerdto.setMobileNumber(customer.getMobileNumber());
		List<PantMeasurement> validPantOrders = getValidPantOrders(customer.getPantMeasurement(),
				OrderStatus.valueOf(orderStatus));
		customerdto.setPantMeasurement(validPantOrders);
		customerdto.setProductId(customer.getProductId());
		customerdto.setProductName(productService.getProductById(customer.getProductId()).getProductName());
		customerdto.setSalesmanId(customer.getSalesmanId());
		customerdto.setSalesman(salesmanService.getSalesmanById(customer.getSalesmanId()).getName());
		List<ShirtMeasurement> validShirtOrders = getValidShirtOrders(customer.getShirtMeasurement(),
				OrderStatus.valueOf(orderStatus));
		customerdto.setShirtMeasurement(validShirtOrders);
		customerdto.setUsername(customer.getUsername());
		customerdto.setOrderCreatedDate(String.valueOf(customer.getCreatedAt()));
		customerdto.setOrderUpdatedDate(String.valueOf(customer.getUpdatedAt()));
		if (!validShirtOrders.isEmpty() || !validPantOrders.isEmpty()) {
			customerDtoList.add(customerdto);
		}
	}

	private List<PantMeasurement> getValidPantOrders(List<PantMeasurement> pantMeasurement, OrderStatus orderStatus) {
		return pantMeasurement.stream().filter(p -> p.getOrderStatus().equals(orderStatus))
				.collect(Collectors.toList());
	}

	private List<ShirtMeasurement> getValidShirtOrders(List<ShirtMeasurement> shirtMeasurement,
			OrderStatus orderStatus) {
		return shirtMeasurement.stream().filter(s -> s.getOrderStatus().equals(orderStatus))
				.collect(Collectors.toList());
	}

	@Override
	public Customer deleteOrderByOrderIdAndMeasurementId(Long orderId, Long measurementId)
			throws CustomerMeasurementDoesNotExist {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<Customer> existingCustomer = customerRepository.findByIdAndUsername(orderId,
				userDetails.getUsername());
		if (existingCustomer.isPresent()) {
			Customer customer = existingCustomer.get();
			if (Objects.nonNull(customer.getShirtMeasurement()) && !customer.getShirtMeasurement().isEmpty()) {

				List<ShirtMeasurement> shirtsMeasurement = customer.getShirtMeasurement();

				Optional<ShirtMeasurement> updateShirt = shirtsMeasurement.stream()
						.filter(shirts -> Objects.equals(shirts.getId(), (measurementId)))
						.collect(Collectors.reducing((a, b) -> null));

				if (!updateShirt.isPresent()) {
					throw new CustomerMeasurementDoesNotExist(Constant.SHIRT_MEASUREMENT_NOT_EXIST_MSG);
				}

				if (updateShirt.get().getOrderStatus().equals(OrderStatus.ORDER_INITIALIZE)
						|| updateShirt.get().getOrderStatus().equals(OrderStatus.QR_SCAN_AND_ORDERED)) {

					updateShirt.get().setOrderStatus(OrderStatus.ORDER_DELETED);
				} else {
					throw new CustomerMeasurementDoesNotExist(
							"Order is already dispached to company or order is deleted");
				}

			}
			if (Objects.nonNull(customer.getPantMeasurement()) && !customer.getPantMeasurement().isEmpty()) {

				List<PantMeasurement> pantMeasuerment = customer.getPantMeasurement();

				Optional<PantMeasurement> updatePant = pantMeasuerment.stream()
						.filter(pants -> Objects.equals(pants.getId(), (measurementId)))
						.collect(Collectors.reducing((a, b) -> null));

				if (!updatePant.isPresent()) {
					throw new CustomerMeasurementDoesNotExist(Constant.SHIRT_MEASUREMENT_NOT_EXIST_MSG);
				}

				if (updatePant.get().getOrderStatus().equals(OrderStatus.ORDER_INITIALIZE)
						|| updatePant.get().getOrderStatus().equals(OrderStatus.QR_SCAN_AND_ORDERED)) {
					updatePant.get().setOrderStatus(OrderStatus.ORDER_DELETED);
				} else {
					throw new CustomerMeasurementDoesNotExist("Order is already dispached to company");
				}
			}

			Customer deletedCustomerOrder = modelMapper.map(customer, Customer.class);
			return customerRepository.save(deletedCustomerOrder);

		}
		return null;
	}

	@Override
	public Customer saveOrder(String customerOrder, List<MultipartFile> files, Long productId, HttpServletRequest request)
			throws Exception {
		CustomerOrderDto customerOrderDto = getJSON(customerOrder);

		Customer customer = modelMapper.map(customerOrderDto, Customer.class);
		List<ShirtMeasurement> shirts = new ArrayList<>();
		List<PantMeasurement> pants = new ArrayList<>();
		if (null != customerOrderDto.getShirts()) {

			shirts = customerOrderDto.getShirts().stream().map(shirt -> modelMapper.map(shirt, ShirtMeasurement.class))
					.collect(Collectors.toList());			
			shirts.forEach(shirt -> {
				
				
					try {
						compareDate(Utility.convertDate(shirt.getTrialDate()));
						compareDate(Utility.convertDate(shirt.getDeliveryDate()));
					} catch (Exception e) {
						throw new RuntimeException(Constant.DATE_ERROR_MESSAGE);
					}
					
				
				shirt.setOrderStatus(OrderStatus.ORDER_INITIALIZE);
			});
			
		}
		if (null != customerOrderDto.getPants()) {

			pants = customerOrderDto.getPants().stream().map(pant -> modelMapper.map(pant, PantMeasurement.class))
					.collect(Collectors.toList());
			
			pants.forEach(pant -> {
				try {
					compareDate(Utility.convertDate(pant.getTrialDate()));
					compareDate(Utility.convertDate(pant.getDeliveryDate()));
				} catch (Exception e) {
					throw new RuntimeException(Constant.DATE_ERROR_MESSAGE);
				}
				
				pant.setOrderStatus(OrderStatus.ORDER_INITIALIZE);
				});
		}
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Product product = productService.getProductById(productId);

		Salesman salesman = salesmanService.getSalesmanById(customerOrderDto.getSalesmanId());
		if (Objects.nonNull(salesman) && Objects.nonNull(product)) {
			customer.setId(null);
			customer.setProductId(product.getId());
			customer.setSalesmanId(salesman.getId());
			boolean anyMatch = userDetails.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals(ROLE_SALESMAN));
			if (anyMatch) {
				customer.setUsername(salesman.getShopId().getUsername());
			} else {
				customer.setUsername(userDetails.getUsername());
			}

			if (ProductType.UP_WAIST.equals(product.getProductType())) {
				customer.setShirtMeasurement(uploadShirtPictures(shirts, files, request));
				if (customer.getShirtMeasurement() == null || customer.getShirtMeasurement().isEmpty()) {
					throw new ProductExistsException("Incorrect product Id");
				}
			}

			if (ProductType.DOWN_WAIST.equals(product.getProductType())) {
				customer.setPantMeasurement(uploadPantPictures(pants, files, request));
				if (customer.getPantMeasurement() == null || customer.getPantMeasurement().isEmpty()) {
					throw new ProductExistsException("Incorrect product Id");
				}
			}
		}

		return customerRepository.save(customer);
	}

	private void compareDate(LocalDateTime date) throws Exception {

		if (date.compareTo(LocalDateTime.now()) < 0) {
			throw new Exception(Constant.DATE_ERROR_MESSAGE);
		}
	}

	private CustomerOrderDto getJSON(String customerOrderDto) {
		CustomerOrderDto customer = new CustomerOrderDto();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			customer = objectMapper.readValue(customerOrderDto, CustomerOrderDto.class);
		} catch (IOException e) {
			e.getMessage();
		}
		return customer;
	}

	@Override
	public CustomerDto getOrderDetailsById(Long orderId, Long measurementId) throws CustomerMeasurementDoesNotExist {
		CustomerDto orderResponse = new CustomerDto();
		Optional<Customer> customer = customerRepository.findById(orderId);
		if (customer.isPresent()) {
			if (Objects.nonNull(customer.get().getShirtMeasurement())
					&& !customer.get().getShirtMeasurement().isEmpty()) {

				List<ShirtMeasurement> shirt = customer.get().getShirtMeasurement().stream()
						.filter(shirts -> Objects.equals(shirts.getId(), (measurementId))).collect(Collectors.toList());

				if (Objects.isNull(shirt) || shirt.isEmpty()) {
					throw new CustomerMeasurementDoesNotExist("Customer Shirt measurement does not exist");
				}
				customer.get().setShirtMeasurement(shirt);
			}

			if (Objects.nonNull(customer.get().getPantMeasurement())
					&& !customer.get().getPantMeasurement().isEmpty()) {

				List<PantMeasurement> pant = customer.get().getPantMeasurement().stream()
						.filter(pants -> Objects.equals(pants.getId(), (measurementId))).collect(Collectors.toList());

				if (Objects.isNull(pant) || pant.isEmpty()) {
					throw new CustomerMeasurementDoesNotExist("Customer Pant measurement does not exist");
				}

				customer.get().setPantMeasurement(pant);
			}
			orderResponse = getOrderResponse(customer.get());
		}

		return orderResponse;
	}

	@Override
	@Transactional
	public Customer placeOrder(Long orderId, Long measurementId, String qrCodeId)
			throws CustomerMeasurementDoesNotExist, TokenDoesNotExist, OrderDoesNotExist, InvalidQRCode {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<Customer> existingCustomer = customerRepository.findByIdAndUsername(orderId,
				userDetails.getUsername());

		if (!existingCustomer.isPresent()) {
			throw new OrderDoesNotExist(
					"Customer Order Does not exist with order id" + orderId + "and measurement id" + measurementId);
		}

		Customer customer = existingCustomer.get();
		TokenOrder accessToken = null;

		if (customer.getDeliveryIn().equals(DeliveryTime.HOURS_24)) {
			accessToken = tokenService.useToken(TokenType.Premium, customer.getProductId());
			// log

		}
		if (customer.getDeliveryIn().equals(DeliveryTime.HOURS_48)
				|| customer.getDeliveryIn().equals(DeliveryTime.HOURS_GREATER_THAN_48)) {
			accessToken = tokenService.useToken(TokenType.Regular, customer.getProductId());
		}

		if (Objects.nonNull(customer.getShirtMeasurement()) && !customer.getShirtMeasurement().isEmpty()
				&& qrCodeId.startsWith("S")) {

			List<ShirtMeasurement> shirtsMeasurement = customer.getShirtMeasurement();

			Optional<ShirtMeasurement> updateShirt = shirtsMeasurement.stream()
					.filter(shirts -> Objects.equals(shirts.getId(), (measurementId)))
					.collect(Collectors.reducing((a, b) -> null));
			if (!updateShirt.isPresent()) {
				throw new OrderDoesNotExist("Shirt is not available");
			}
			Optional<ShirtMeasurement> findByQrcodeNumberContainingAndOrderStatusNot = shirtMeasurementsRepository
					.findByQrcodeNumberContainingAndOrderStatusNot(qrCodeId + Constant.DASH,
							OrderStatus.ORDER_INITIALIZE);

			if (updateShirt.get().getOrderStatus().equals(OrderStatus.ORDER_INITIALIZE)
					&& !findByQrcodeNumberContainingAndOrderStatusNot.isPresent()) {
				updateShirt.get().setOrderStatus(OrderStatus.QR_SCAN_AND_ORDERED);
				updateShirt.get().setQrcodeNumber(
						qrCodeId + Constant.DASH + customer.getId() + Constant.DASH + updateShirt.get().getId());
				if (Objects.nonNull(accessToken)) {
					Customer updateCustomerOrder = modelMapper.map(customer, Customer.class);
					return customerRepository.save(updateCustomerOrder);
				}
			} else {
				throw new InvalidQRCode(Constant.INVALID_QR_MESSAGE);
			}
		}

		if (Objects.nonNull(customer.getPantMeasurement()) && !customer.getPantMeasurement().isEmpty()
				&& qrCodeId.startsWith("P")) {

			List<PantMeasurement> pantMeasuerment = customer.getPantMeasurement();

			Optional<PantMeasurement> updatePant = pantMeasuerment.stream()
					.filter(pants -> Objects.equals(pants.getId(), (measurementId)))
					.collect(Collectors.reducing((a, b) -> null));

			if (!updatePant.isPresent()) {
				throw new OrderDoesNotExist("Shirt is not available");
			}

			Optional<PantMeasurement> findByQrcodeNumberContainingAndOrderStatusNot = pantMeasurementRepository
					.findByQrcodeNumberContainingAndOrderStatusNot(qrCodeId + Constant.DASH,
							OrderStatus.ORDER_INITIALIZE);

			if (updatePant.get().getOrderStatus().equals(OrderStatus.ORDER_INITIALIZE)
					&& !findByQrcodeNumberContainingAndOrderStatusNot.isPresent()) {
				updatePant.get().setOrderStatus(OrderStatus.QR_SCAN_AND_ORDERED);
				updatePant.get().setQrcodeNumber(
						qrCodeId + Constant.DASH + customer.getId() + Constant.DASH + updatePant.get().getId());
				if (Objects.nonNull(accessToken)) {
					Customer updateCustomerOrder = modelMapper.map(customer, Customer.class);
					return customerRepository.save(updateCustomerOrder);
				}
			} else {
				throw new InvalidQRCode(Constant.INVALID_QR_MESSAGE);
			}
		}

		return null;
	}

	@Override
	public String collectOrder(String qrCodeId, String orderStatus) throws InvalidQRCode, OrderDoesNotExist {

		if (orderStatus.equalsIgnoreCase(OrderStatus.QR_SCAN_AND_ORDERED.name())) {
			return collectOrderFromShop(qrCodeId);
		}

		if (orderStatus.equalsIgnoreCase(OrderStatus.QUALITY_CHECKING.name())) {
			return collectOrderFromCompany(qrCodeId);
		}

		return null;

	}

	private String collectOrderFromCompany(String qrCodeId) throws InvalidQRCode, OrderDoesNotExist {
		String message = "Order collected from company to dispatch at shop.";

		if (isQRForProduct(qrCodeId).equals(Products.SHIRT)) {
			Optional<ShirtMeasurement> shirtMeasurement = shirtMeasurementsRepository
					.findByQrcodeNumberContainingAndOrderStatus(qrCodeId + Constant.DASH, OrderStatus.QUALITY_CHECKING);
			if (!shirtMeasurement.isPresent()) {
				throw new OrderDoesNotExist(Constant.ORDER_DOES_NOT_EXIST_MSG);
			}
			shirtMeasurement.get().setOrderStatus(OrderStatus.DISPATCH_FROM_COMPANY);
			shirtMeasurementsRepository.save(shirtMeasurement.get());
			return message;
		}
		if (isQRForProduct(qrCodeId).equals(Products.PANT)) {
			Optional<PantMeasurement> pantMeasurement = pantMeasurementRepository
					.findByQrcodeNumberContainingAndOrderStatus(qrCodeId + Constant.DASH, OrderStatus.QUALITY_CHECKING);
			if (!pantMeasurement.isPresent()) {
				throw new OrderDoesNotExist(Constant.ORDER_DOES_NOT_EXIST_MSG);
			}
			pantMeasurement.get().setOrderStatus(OrderStatus.DISPATCH_FROM_COMPANY);
			pantMeasurementRepository.save(pantMeasurement.get());
			return message;
		}
		throw new InvalidQRCode(Constant.CHANGE_QR_MESSAGE);

	}

	private String collectOrderFromShop(String qrCodeId) throws InvalidQRCode, OrderDoesNotExist {
		String message = "Order collected from shop.";

		if (isQRForProduct(qrCodeId).equals(Products.SHIRT)) {
			Optional<ShirtMeasurement> shirtMeasurement = shirtMeasurementsRepository
					.findByQrcodeNumberContainingAndOrderStatus(qrCodeId + Constant.DASH,
							OrderStatus.QR_SCAN_AND_ORDERED);
			if (!shirtMeasurement.isPresent()) {
				throw new OrderDoesNotExist(Constant.ORDER_DOES_NOT_EXIST_MSG);
			}
			shirtMeasurement.get().setOrderStatus(OrderStatus.ORDER_DELIVER_FROM_SHOP);
			shirtMeasurementsRepository.save(shirtMeasurement.get());
			return message;
		}
		if (isQRForProduct(qrCodeId).equals(Products.PANT)) {
			Optional<PantMeasurement> pantMeasurement = pantMeasurementRepository
					.findByQrcodeNumberContainingAndOrderStatus(qrCodeId + Constant.DASH,
							OrderStatus.QR_SCAN_AND_ORDERED);
			if (!pantMeasurement.isPresent()) {
				throw new OrderDoesNotExist(Constant.ORDER_DOES_NOT_EXIST_MSG);
			}
			pantMeasurement.get().setOrderStatus(OrderStatus.ORDER_DELIVER_FROM_SHOP);
			pantMeasurementRepository.save(pantMeasurement.get());
			return message;
		}
		throw new InvalidQRCode(Constant.CHANGE_QR_MESSAGE);
	}

	@Override
	public CustomerDto orderDeliverdToStitchx(String qrCodeId)
			throws OrderDoesNotExist, InvalidQRCode, NumberFormatException, CustomerMeasurementDoesNotExist {
		if (isQRForProduct(qrCodeId).equals(Products.SHIRT)) {
			Optional<ShirtMeasurement> shirtMeasurement = shirtMeasurementsRepository
					.findByQrcodeNumberContainingAndOrderStatus(qrCodeId + Constant.DASH,
							OrderStatus.ORDER_DELIVER_FROM_SHOP);
			if (!shirtMeasurement.isPresent()) {
				throw new OrderDoesNotExist("Order does not exist");
			}
			shirtMeasurement.get().setOrderStatus(OrderStatus.ORDER_DELIVER_AT_COMPANY);
			shirtMeasurementsRepository.save(shirtMeasurement.get());
			String[] qrcodeNumber = shirtMeasurement.get().getQrcodeNumber().split(Constant.DASH);
			return getOrderDetailsById(Long.valueOf(qrcodeNumber[1]), Long.valueOf(qrcodeNumber[2]));
		}
		if (isQRForProduct(qrCodeId).equals(Products.PANT)) {
			Optional<PantMeasurement> pantMeasurement = pantMeasurementRepository
					.findByQrcodeNumberContainingAndOrderStatus(qrCodeId + Constant.DASH,
							OrderStatus.ORDER_DELIVER_FROM_SHOP);
			if (!pantMeasurement.isPresent()) {
				throw new OrderDoesNotExist("Order does not exist");
			}
			pantMeasurement.get().setOrderStatus(OrderStatus.ORDER_DELIVER_AT_COMPANY);
			pantMeasurementRepository.save(pantMeasurement.get());
			String[] qrcodeNumber = pantMeasurement.get().getQrcodeNumber().split(Constant.DASH);
			return getOrderDetailsById(Long.valueOf(qrcodeNumber[1]), Long.valueOf(qrcodeNumber[2]));
		}
		throw new InvalidQRCode(Constant.INVALID_QR_MESSAGE);
	}

	private Products isQRForProduct(String qrCodeId) throws InvalidQRCode {
		if (qrCodeId.startsWith("S")) {
			return Products.SHIRT;
		}
		if (qrCodeId.startsWith("P")) {
			return Products.PANT;
		}
		throw new InvalidQRCode(Constant.INVALID_QR_MESSAGE);
	}

	@Override
	public List<QCResponse> checkOrderMeasurement(String qrCodeId, Measurements measurements)
			throws InvalidQRCode, OrderDoesNotExist {
		if (isQRForProduct(qrCodeId).equals(Products.SHIRT)) {
			Optional<ShirtMeasurement> shirtMeasurement = shirtMeasurementsRepository
					.findByQrcodeNumberContainingAndOrderStatus(qrCodeId + Constant.DASH,
							OrderStatus.ORDER_DELIVER_AT_COMPANY);
			if (!shirtMeasurement.isPresent()) {
				throw new OrderDoesNotExist("Order does not exist");
			}
			List<QCResponse> qcResponse = checkShirtsMeasurements(shirtMeasurement, measurements);
			Optional<QCResponse> findAny = qcResponse.stream().filter(r -> r.isCorrectMeasurement() == false).findAny();
			if (!findAny.isPresent()) {
				shirtMeasurement.get().setOrderStatus(OrderStatus.QUALITY_CHECKING);
				shirtMeasurementsRepository.save(shirtMeasurement.get());
			}
			return qcResponse;
		}
		if (isQRForProduct(qrCodeId).equals(Products.PANT)) {
			Optional<PantMeasurement> pantMeasurement = pantMeasurementRepository
					.findByQrcodeNumberContainingAndOrderStatus(qrCodeId + Constant.DASH,
							OrderStatus.ORDER_DELIVER_AT_COMPANY);
			if (!pantMeasurement.isPresent()) {
				throw new OrderDoesNotExist("Order does not exist");
			}
			List<QCResponse> qcResponse = checkPantMeasurements(pantMeasurement, measurements);
			Optional<QCResponse> findAny = qcResponse.stream().filter(r -> r.isCorrectMeasurement() == false).findAny();
			if (!findAny.isPresent()) {
				pantMeasurement.get().setOrderStatus(OrderStatus.QUALITY_CHECKING);
				pantMeasurementRepository.save(pantMeasurement.get());
			}
			return qcResponse;
		}
		return null;
	}

	private List<QCResponse> checkPantMeasurements(Optional<PantMeasurement> pantM, Measurements measurements) {

		PantMeasurement pantMeasurement = null;
		if (pantM.isPresent()) {
			pantMeasurement = pantM.get();
		}

		List<QCResponse> qcResponseList = new ArrayList<>();

		if (Objects.nonNull(pantMeasurement)) {
			if (Objects.equals(pantMeasurement.getBottom(), Double.valueOf(measurements.getBottom()))) {
				qcResponseList.add(new QCResponse(Constant.BOTTOM, measurements.getBottom(),
						String.valueOf(pantMeasurement.getBottom()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.BOTTOM, measurements.getBottom(),
						String.valueOf(pantMeasurement.getBottom()), false));
			}
			if (Objects.equals(pantMeasurement.getKambar(), Double.valueOf(measurements.getKambar()))) {
				qcResponseList.add(new QCResponse(Constant.KAMBAR, measurements.getKambar(),
						String.valueOf(pantMeasurement.getKambar()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.KAMBAR, measurements.getKambar(),
						String.valueOf(pantMeasurement.getKambar()), false));
			}
			if (Objects.equals(pantMeasurement.getKnee(), Double.valueOf(measurements.getKnee()))) {
				qcResponseList.add(new QCResponse(Constant.KNEE, measurements.getKnee(),
						String.valueOf(pantMeasurement.getKnee()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.KNEE, measurements.getKnee(),
						String.valueOf(pantMeasurement.getKnee()), false));
			}
			if (Objects.equals(pantMeasurement.getLatak(), Double.valueOf(measurements.getLatak()))) {
				qcResponseList.add(new QCResponse(Constant.LATAK, measurements.getLatak(),
						String.valueOf(pantMeasurement.getLatak()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.LATAK, measurements.getLatak(),
						String.valueOf(pantMeasurement.getLatak()), false));
			}
			if (Objects.equals(pantMeasurement.getPantLambi(), Double.valueOf(measurements.getLambi()))) {
				qcResponseList.add(new QCResponse(Constant.LENGTH, measurements.getLambi(),
						String.valueOf(pantMeasurement.getPantLambi()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.LENGTH, measurements.getLambi(),
						String.valueOf(pantMeasurement.getPantLambi()), false));
			}
			if (Objects.equals(pantMeasurement.getSeat(), Double.valueOf(measurements.getSeat()))) {
				qcResponseList.add(new QCResponse(Constant.SEAT, measurements.getSeat(),
						String.valueOf(pantMeasurement.getSeat()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.SEAT, measurements.getSeat(),
						String.valueOf(pantMeasurement.getSeat()), false));
			}
			if (Objects.equals(pantMeasurement.getThigh(), Double.valueOf(measurements.getThigh()))) {
				qcResponseList.add(new QCResponse(Constant.THIGH, measurements.getThigh(),
						String.valueOf(pantMeasurement.getThigh()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.THIGH, measurements.getThigh(),
						String.valueOf(pantMeasurement.getThigh()), false));
			}
		}
		return qcResponseList;
	}

	private List<QCResponse> checkShirtsMeasurements(Optional<ShirtMeasurement> shirtM, Measurements measurements) {
		ShirtMeasurement shirtMeasurement = null;
		if (shirtM.isPresent()) {
			shirtMeasurement = shirtM.get();
		}

		List<QCResponse> qcResponseList = new ArrayList<>();

		if (Objects.nonNull(shirtMeasurement)) {
			if (Objects.equals(shirtMeasurement.getBahi(), Double.valueOf(measurements.getBahi()))) {
				qcResponseList.add(new QCResponse(Constant.BAHI, measurements.getBahi(),
						String.valueOf(shirtMeasurement.getBahi()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.BAHI, measurements.getBahi(),
						String.valueOf(shirtMeasurement.getBahi()), false));
			}
			if (Objects.equals(shirtMeasurement.getChest(), Double.valueOf(measurements.getChest()))) {
				qcResponseList.add(new QCResponse(Constant.CHEST, measurements.getChest(),
						String.valueOf(shirtMeasurement.getChest()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.CHEST, measurements.getChest(),
						String.valueOf(shirtMeasurement.getChest()), false));
			}
			if (Objects.equals(shirtMeasurement.getNeck(), Double.valueOf(measurements.getNeck()))) {
				qcResponseList.add(new QCResponse(Constant.NECK, measurements.getNeck(),
						String.valueOf(shirtMeasurement.getNeck()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.NECK, measurements.getNeck(),
						String.valueOf(shirtMeasurement.getNeck()), false));
			}
			if (Objects.equals(shirtMeasurement.getSeat(), Double.valueOf(measurements.getSeat()))) {
				qcResponseList.add(new QCResponse(Constant.SEAT, measurements.getSeat(),
						String.valueOf(shirtMeasurement.getSeat()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.SEAT, measurements.getSeat(),
						String.valueOf(shirtMeasurement.getSeat()), false));
			}
			if (Objects.equals(shirtMeasurement.getShirtLambi(), Double.valueOf(measurements.getLambi()))) {
				qcResponseList.add(new QCResponse(Constant.LENGTH, measurements.getLambi(),
						String.valueOf(shirtMeasurement.getShirtLambi()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.LENGTH, measurements.getLambi(),
						String.valueOf(shirtMeasurement.getShirtLambi()), false));
			}
			if (Objects.equals(shirtMeasurement.getSholder(), Double.valueOf(measurements.getSholder()))) {
				qcResponseList.add(new QCResponse(Constant.SHOULDER, measurements.getSholder(),
						String.valueOf(shirtMeasurement.getSholder()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.SHOULDER, measurements.getSholder(),
						String.valueOf(shirtMeasurement.getSholder()), false));
			}
			if (Objects.equals(shirtMeasurement.getStomache(), Double.valueOf(measurements.getStomache()))) {
				qcResponseList.add(new QCResponse(Constant.STOMACHE, measurements.getStomache(),
						String.valueOf(shirtMeasurement.getStomache()), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.STOMACHE, measurements.getStomache(),
						String.valueOf(shirtMeasurement.getStomache()), false));
			}
			if (Objects.equals(shirtMeasurement.getNeckType().trim(), measurements.getNeckTypes())) {
				qcResponseList.add(new QCResponse(Constant.NECK_TYPE, measurements.getNeckTypes(),
						shirtMeasurement.getNeckType(), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.NECK_TYPE, measurements.getNeckTypes(),
						shirtMeasurement.getNeckType(), false));
			}
			if (Objects.equals(shirtMeasurement.getShirtType(), measurements.getShirtTypes())) {
				qcResponseList.add(new QCResponse(Constant.SHIRT_TYPE, measurements.getShirtTypes(),
						shirtMeasurement.getShirtType(), true));
			} else {
				qcResponseList.add(new QCResponse(Constant.SHIRT_TYPE, measurements.getShirtTypes(),
						shirtMeasurement.getShirtType(), false));
			}
		}
		return qcResponseList;

	}

	@Override
	public List<CustomerDto> getOrdesByShopNameAndOrderStatus(Pageable pageRequest, String orderStatus, String search,
			String username) {
		List<CustomerDto> customerDtoList = new ArrayList<>();
		boolean isRoleSalesman = false;
		return getCustomerOrders(username, customerDtoList, isRoleSalesman, search, pageRequest, orderStatus);
	}

}
