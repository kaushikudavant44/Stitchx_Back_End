/**
 * 
 */
package com.mavericksoft.stitchx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.mavericksoft.stitchx.models.Customer;

/**
 * @author kaushikudavant
 *
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>{

	List<Customer> findByUsername(String username);

	List<Customer> findByUsername(String username, Pageable pageRequest);

	List<Customer> findBySalesmanId(Long id,Pageable pageRequest);

	Optional<Customer> findByIdAndUsername(Long orderId, String username);

	@Query("select c from Customer c where c.salesmanId = :salesmanId AND c.customerName LIKE "
			+ "CONCAT('%',:searchBy, '%') OR c.mobileNumber LIKE CONCAT('%',:searchBy, '%') OR c.updatedAt LIKE CONCAT('%',:searchBy, '%')")
	List<Customer> findBySalesmanIdAndCustomerNameORMobileNumberORUpdatedAt(Long salesmanId, String searchBy);

	@Query("select c from Customer c where c.username = :username AND c.customerName LIKE "
			+ "CONCAT('%',:searchBy, '%') OR c.mobileNumber LIKE CONCAT('%',:searchBy, '%') OR c.updatedAt LIKE CONCAT('%',:searchBy, '%')")
	List<Customer> findByUsernameAndCustomerNameORMobileNumberORUpdatedAt(String username, String searchBy);

}
