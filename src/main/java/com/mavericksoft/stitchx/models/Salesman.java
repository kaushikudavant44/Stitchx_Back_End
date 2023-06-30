/**
 * 
 */
package com.mavericksoft.stitchx.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mavericksoft.stitchx.common.AuditModel;
import com.mavericksoft.stitchx.enums.Status;

import lombok.Data;

/**
 * @author kaushikudavant
 *
 */
@Entity
@Table
@Data
public class Salesman extends AuditModel{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@Column
	String name;
	
	@Column
	Status salesmanStatus;

	@Column
	String profilePic;
	
	@Column
	String address;
	
	@Column
	String adharNumber;
	
	@Column(unique = true)
	String username;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private ShopOwner shopId;
	
}
