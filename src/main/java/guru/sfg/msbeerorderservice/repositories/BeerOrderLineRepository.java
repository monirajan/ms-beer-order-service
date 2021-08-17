package guru.sfg.msbeerorderservice.repositories;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;

import guru.sfg.msbeerorderservice.domain.BeerOrderLine;

public interface BeerOrderLineRepository extends PagingAndSortingRepository<BeerOrderLine, UUID>{

}
