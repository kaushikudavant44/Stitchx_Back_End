package com.mavericksoft.stitchx.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mavericksoft.stitchx.enums.Status;
import com.mavericksoft.stitchx.models.Salesman;
import com.mavericksoft.stitchx.models.ShopOwner;

public interface SalesmanRepository extends PagingAndSortingRepository<Salesman, Long>{

	Salesman findByUsername(String username);

	List<Salesman> findByShopId(ShopOwner shopId);

	Salesman findByUsernameAndSalesmanStatus(String username, Status active);

	List<Salesman> findByShopIdAndSalesmanStatus(ShopOwner shopOwner, Status active);

}
