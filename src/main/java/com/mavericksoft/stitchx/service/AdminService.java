package com.mavericksoft.stitchx.service;

import java.util.List;
import java.util.Map;

import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.models.Measurements;
import com.mavericksoft.stitchx.user.exception.ProductExistsException;

public interface AdminService {

	Measurements saveMeasurements(Measurements measurements);

	Map<String, List<String>> getProductMeasurements(Products product) throws ProductExistsException;

}
