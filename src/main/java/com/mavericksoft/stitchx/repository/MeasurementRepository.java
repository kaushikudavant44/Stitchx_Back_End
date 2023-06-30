package com.mavericksoft.stitchx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.Products;
import com.mavericksoft.stitchx.models.Measurements;

public interface MeasurementRepository extends JpaRepository<Measurements, Long>{

	List<Measurements> findByProduct(Products product);

}
