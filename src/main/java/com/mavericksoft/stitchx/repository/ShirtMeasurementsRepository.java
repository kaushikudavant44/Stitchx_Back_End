/**
 * 
 */
package com.mavericksoft.stitchx.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.OrderStatus;
import com.mavericksoft.stitchx.models.ShirtMeasurement;

/**
 * @author kaushikudavant
 *
 */
public interface ShirtMeasurementsRepository extends JpaRepository<ShirtMeasurement, Long>{

	Optional<ShirtMeasurement> findByQrcodeNumberContainingAndOrderStatus(String string, OrderStatus qrScanAndOrdered);

	Optional<ShirtMeasurement> findByQrcodeNumberContainingAndOrderStatusNot(String qrCodeId, OrderStatus orderInitialize);

}
