/**
 * 
 */
/*
 * package com.mavericksoft.stitchx.models;
 * 
 * import javax.persistence.Column; import javax.persistence.Entity; import
 * javax.persistence.EnumType; import javax.persistence.Enumerated; import
 * javax.persistence.FetchType; import javax.persistence.GeneratedValue; import
 * javax.persistence.Id; import javax.persistence.JoinColumn; import
 * javax.persistence.ManyToOne; import javax.persistence.Table;
 * 
 * import org.hibernate.annotations.OnDelete; import
 * org.hibernate.annotations.OnDeleteAction;
 * 
 * import com.fasterxml.jackson.annotation.JsonIgnore; import
 * com.mavericksoft.stitchx.enums.TokenType;
 * 
 * import lombok.Data;
 * 
 *//**
	 * @author kaushikudavant
	 *
	 *//*
		 * @Entity
		 * 
		 * @Table
		 * 
		 * @Data public class TokenDetails {
		 * 
		 * @Id
		 * 
		 * @GeneratedValue Long id;
		 * 
		 * @Column
		 * 
		 * @Enumerated(EnumType.STRING) TokenType tokenType;
		 * 
		 * @Column Double tokenAmount;
		 * 
		 * @ManyToOne(fetch = FetchType.LAZY, optional = false)
		 * 
		 * @JoinColumn(name = "product_id", nullable = false)
		 * 
		 * @OnDelete(action = OnDeleteAction.CASCADE)
		 * 
		 * @JsonIgnore private Product product;
		 * 
		 * }
		 */