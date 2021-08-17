package guru.sfg.msbeerorderservice.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.sfg.msbeerorderservice.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID>{
	List<Customer> findAllByCustomerNameLike(String customerName);
}
