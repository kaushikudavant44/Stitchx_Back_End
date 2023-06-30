/**
 * 
 */
package com.mavericksoft.stitchx.common;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.Data;

/**
 * @author kaushikudavant
 *
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EnableJpaAuditing
@Data
public abstract class AuditModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private Timestamp updatedAt;
	
}
