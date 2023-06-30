package com.mavericksoft.stitchx.serviceimpl;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mavericksoft.stitchx.common.Constant;
import com.mavericksoft.stitchx.enums.BlazerTypes;
import com.mavericksoft.stitchx.enums.ButtonTypes;
import com.mavericksoft.stitchx.enums.IndoTypes;
import com.mavericksoft.stitchx.enums.JodhpuriTypes;
import com.mavericksoft.stitchx.enums.NeckTypes;
import com.mavericksoft.stitchx.enums.ProductType;
import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.enums.ShirtTypes;
import com.mavericksoft.stitchx.models.Measurements;
import com.mavericksoft.stitchx.models.Product;
import com.mavericksoft.stitchx.repository.MeasurementRepository;
import com.mavericksoft.stitchx.service.AdminService;
import com.mavericksoft.stitchx.service.ProductService;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	MeasurementRepository meausurementRepository;

	@Autowired
	ProductService productService;

	@Override
	public Measurements saveMeasurements(Measurements measurements) {
		return meausurementRepository.save(measurements);
	}

	@Override
	public Map<String, List<String>> getProductMeasurements(Products product) throws ProductExistsException {

		String LOWER_CHEST="lowerChest";
		
		String TYPES = "types";
		List<Product> getActiveProduct = productService.getProductByName(product);
		if (getActiveProduct.isEmpty() || Objects.isNull(getActiveProduct)) {
			throw new ProductExistsException(Constant.PRODUCT_DOES_NOT_EXISTS);
		}
		Map<String, List<String>> map = new LinkedHashMap<>();
		if (ProductType.UP_WAIST.equals(getActiveProduct.get(0).getProductType())) {
			map.put("lambi", Arrays.asList(Constant.TRUE));
			map.put("shoulder", Arrays.asList(Constant.TRUE));
			map.put("bahi", Arrays.asList(Constant.TRUE));
			map.put("chest", Arrays.asList(Constant.TRUE));
			map.put("stomache", Arrays.asList(Constant.TRUE));
			map.put("seat", Arrays.asList(Constant.TRUE));
			map.put("neck", Arrays.asList(Constant.TRUE));
			map.put("neckTypes", Stream.of(NeckTypes.values()).map(NeckTypes::name).collect(Collectors.toList()));
			
			if (Products.BLAZER.name().equalsIgnoreCase(getActiveProduct.get(0).getProductName())) {
				map.put(TYPES,
						Stream.of(BlazerTypes.values()).map(BlazerTypes::name).collect(Collectors.toList()));
				map.put(LOWER_CHEST, Arrays.asList(Constant.TRUE));
				map.put("buttonType",
						Stream.of(ButtonTypes.values()).map(ButtonTypes::name).collect(Collectors.toList()));
			} else if (Products.JODHPURI.name().equalsIgnoreCase(getActiveProduct.get(0).getProductName())) {
				map.put(LOWER_CHEST, Arrays.asList(Constant.TRUE));
				map.put(TYPES,
						Stream.of(JodhpuriTypes.values()).map(JodhpuriTypes::name).collect(Collectors.toList()));
			} else if (Products.INDO_WESTERN.name().equalsIgnoreCase(getActiveProduct.get(0).getProductName())
					|| Products.SHERWANI.name().equalsIgnoreCase(getActiveProduct.get(0).getProductName())
					|| Products.NAWABI.name().equalsIgnoreCase(getActiveProduct.get(0).getProductName())) {
				map.put(LOWER_CHEST, Arrays.asList(Constant.TRUE));
				map.put(TYPES, Stream.of(IndoTypes.values()).map(IndoTypes::name).collect(Collectors.toList()));
			} else {
				map.put(TYPES,
						Stream.of(ShirtTypes.values()).map(ShirtTypes::name).collect(Collectors.toList()));
			}

			return map;
		}
		if (ProductType.DOWN_WAIST.equals(getActiveProduct.get(0).getProductType())) {
			map.put("seat", Arrays.asList(Constant.TRUE));
			map.put("lambi", Arrays.asList(Constant.TRUE));
			map.put("kambar", Arrays.asList(Constant.TRUE));
			map.put("thigh", Arrays.asList(Constant.TRUE));
			map.put("knee", Arrays.asList(Constant.TRUE));
			map.put("bottom", Arrays.asList(Constant.TRUE));
			map.put("latak", Arrays.asList(Constant.TRUE));
			return map;
		}

		throw new ProductExistsException(Constant.PRODUCT_DOES_NOT_EXISTS);
	}

}
