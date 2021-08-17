package guru.sfg.msbeerorderservice.repositories;

import java.util.List;
import java.util.UUID;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import guru.sfg.msbeerorderservice.domain.BeerOrder;
import guru.sfg.msbeerorderservice.domain.Customer;
import guru.sfg.msbeerorderservice.domain.OrderStatusEnum;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID>{

	Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);

	List<BeerOrder> findAllByOrderStatus(OrderStatusEnum orderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    BeerOrder findOneById(UUID id);

}
