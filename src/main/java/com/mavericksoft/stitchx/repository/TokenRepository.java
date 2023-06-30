package com.mavericksoft.stitchx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.PaymentStatus;
import com.mavericksoft.stitchx.models.Token;
import com.mavericksoft.stitchx.models.TokenOrder;

public interface TokenRepository extends JpaRepository<Token, Long>{

	List<Token> findByTokenOrder(TokenOrder tokenOrder);

	Token findTopByTokenOrderAndIsUsedAndPaymentStatus(TokenOrder tokenOrders, boolean b, PaymentStatus paid);

	List<Token> findByTokenOrderIdInAndIsUsed(List<Long> tokenIds, boolean b);

}
