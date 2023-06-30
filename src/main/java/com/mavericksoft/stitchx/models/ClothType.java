package com.mavericksoft.stitchx.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.mavericksoft.stitchx.enums.Status;

import lombok.Data;

/**
 * @author kaushikudavant
 *
 */
@Entity
@Table
@Data
public class ClothType {

	@Id
	@GeneratedValue
	Long id;

	@Column
	String clothName;

	@Column
	Status clothStatus;

}
