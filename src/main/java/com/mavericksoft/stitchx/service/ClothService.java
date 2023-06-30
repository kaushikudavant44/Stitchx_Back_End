/**
 * 
 */
package com.mavericksoft.stitchx.service;

import java.util.List;

import com.mavericksoft.stitchx.models.ClothType;

/**
 * @author kaushikudavant
 *
 */
public interface ClothService {

	ClothType addClothType(ClothType clothType);

	List<ClothType> getClothTypes();

}
