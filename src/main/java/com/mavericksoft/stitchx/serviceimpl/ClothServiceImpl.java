/**
 * 
 */
package com.mavericksoft.stitchx.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.ClothType;
import com.mavericksoft.stitchx.repository.ClothRepository;
import com.mavericksoft.stitchx.service.ClothService;

/**
 * @author kaushikudavant
 *
 */
@Service
public class ClothServiceImpl implements ClothService{
	
	@Autowired
	ClothRepository clothRepository;

	@Override
	public ClothType addClothType(ClothType clothType) {
		// TODO Auto-generated method stub
		
		return clothRepository.save(clothType); 
	}

	@Override
	public List<ClothType> getClothTypes() {
		// TODO Auto-generated method stub
		return clothRepository.findAllByClothStatus(Status.ACTIVE);
	}

}
