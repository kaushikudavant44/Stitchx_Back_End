package com.mavericksoft.stitchx.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.OrderStatus;
import com.mavericksoft.stitchx.models.PantMeasurement;

public interface PantMeasurementRepository extends JpaRepository<PantMeasurement, Long>{

	Optional<PantMeasurement> findByQrcodeNumberContainingAndOrderStatus(String string, OrderStatus qrScanAndOrdered);

	Optional<PantMeasurement> findByQrcodeNumberContainingAndOrderStatusNot(String qrCodeId,
			OrderStatus orderInitialize);

}
