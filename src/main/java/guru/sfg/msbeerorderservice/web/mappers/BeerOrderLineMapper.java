package guru.sfg.msbeerorderservice.web.mappers;

import org.mapstruct.Mapper;

import guru.sfg.msbeerorderservice.domain.BeerOrderLine;
import guru.sfg.msbeerorderservice.web.model.BeerOrderLineDto;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper {
	
	BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);
    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);

}
