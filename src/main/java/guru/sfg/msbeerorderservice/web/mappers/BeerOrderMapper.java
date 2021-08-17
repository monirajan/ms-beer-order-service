package guru.sfg.msbeerorderservice.web.mappers;

import org.mapstruct.Mapper;

import guru.sfg.msbeerorderservice.domain.BeerOrder;
import guru.sfg.msbeerorderservice.web.model.BeerOrderDto;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

	BeerOrderDto beerOrderToDto(BeerOrder beerOrder);
    BeerOrder dtoToBeerOrder(BeerOrderDto dto);
}
