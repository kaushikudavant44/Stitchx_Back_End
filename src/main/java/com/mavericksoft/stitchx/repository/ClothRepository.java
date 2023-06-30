/**
 * 
 */
package com.mavericksoft.stitchx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.ClothType;

/**
 * @author kaushikudavant
 *
 */
public interface ClothRepository extends JpaRepository<ClothType, Long>{

	List<ClothType> findAllByClothStatus(Status active);

}
