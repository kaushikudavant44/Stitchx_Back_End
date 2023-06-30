/**
 * 
 */
package com.mavericksoft.stitchx.repository;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.PaymentStatus;
import com.mavericksoft.stitchx.enums.TokenOrderStatus;
import com.mavericksoft.stitchx.enums.TokenType;
import com.mavericksoft.stitchx.models.TokenOrder;

/**
 * @author kaushikudavant
 *
 */
public interface TokenOrderRepository extends JpaRepository<TokenOrder, Long>{


	List<TokenOrder> findByUsername(String username);

	TokenOrder findTopByUsernameAndTokenTypeAndProductIdAndTokenOrderStatusAndPaymentStatus(String username,
			TokenType tokenType, Long productId, TokenOrderStatus active, PaymentStatus paid);

	List<TokenOrder> findByUsernameAndPaymentStatus(String username, PaymentStatus paid);

	List<TokenOrder> findByUsernameAndPaymentStatusAndTokenType(String username, PaymentStatus paid, TokenType tokenType);

	List<TokenOrder> findByUsernameInAndPaymentStatusOrderById(List<@NotNull String> shopUsernames,
			PaymentStatus pending);

}
