package com.mavericksoft.stitchx.service;

import java.util.List;

import com.mavericksoft.stitchx.models.Salesman;
import com.mavericksoft.stitchx.user.exception.UserExistsException;

public interface SalesmanService {
	
	Salesman saveSalesmanDetails(Salesman salesman);

	List<Salesman> getShopSalesmans();

	Salesman getSalesmanById(Long salesmanId);

	Salesman getSalesmanByUsername(String username);

	Salesman deactivateSalesman(String username) throws UserExistsException;

}
