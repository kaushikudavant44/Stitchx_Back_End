package com.mavericksoft.stitchx.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mavericksoft.stitchx.models.ClothType;
import com.mavericksoft.stitchx.service.ClothService;

@RestController
@RequestMapping("/v1/")
public class ClothsController {
	
	@Autowired
	ClothService clothService;
	
	
	@PostMapping("cloth/type")
	public ResponseEntity<ClothType> insertClothDetails(@RequestBody ClothType clothType){
		try {
		return new ResponseEntity<ClothType>(clothService.addClothType(clothType), HttpStatus.OK);
		}catch(Exception e) {
			return null;
		}
	}
	
	@GetMapping("cloth/type")
	public ResponseEntity<List<ClothType>> getClothDetails(){
		try {
		return new ResponseEntity<List<ClothType>>(clothService.getClothTypes(), HttpStatus.OK);
		}catch(Exception e) {
			return null;
		}
	}

}
