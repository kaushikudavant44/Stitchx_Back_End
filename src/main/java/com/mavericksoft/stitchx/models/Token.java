/**
 * 
 */
package com.mavericksoft.stitchx.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mavericksoft.stitchx.common.AuditModel;
import com.mavericksoft.stitchx.enums.PaymentStatus;

import lombok.Data;

/**
 * @author kaushikudavant
 *
 */
@Table
@Entity
@Data
public class Token extends AuditModel{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column
	String tokenId;

	@Column
	Boolean isUsed;
	
	@Column
	String username;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "token_order_Id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private TokenOrder tokenOrder;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	PaymentStatus paymentStatus;
}
