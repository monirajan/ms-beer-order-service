package guru.sfg.msbeerorderservice.services;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import guru.sfg.msbeerorderservice.domain.BeerOrder;
import guru.sfg.msbeerorderservice.domain.Customer;
import guru.sfg.msbeerorderservice.domain.OrderStatusEnum;
import guru.sfg.msbeerorderservice.repositories.BeerOrderRepository;
import guru.sfg.msbeerorderservice.repositories.CustomerRepository;
import guru.sfg.msbeerorderservice.web.mappers.BeerOrderMapper;
import guru.sfg.msbeerorderservice.web.model.BeerOrderDto;
import guru.sfg.msbeerorderservice.web.model.BeerOrderPagedList;

@Service
public class BeerOrderServiceImpl implements BeerOrderService{
	
	private final BeerOrderRepository beerOrderRepository;
	private final CustomerRepository customerRepository;
	private final BeerOrderMapper beerOrderMapper;
	private final ApplicationEventPublisher eventPublisher;
	
	public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository, CustomerRepository customerRepository,
			BeerOrderMapper beerOrderMapper, ApplicationEventPublisher eventPublisher) {
		this.beerOrderRepository = beerOrderRepository;
		this.customerRepository = customerRepository;
		this.beerOrderMapper = beerOrderMapper;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
		
		Optional<Customer> customerOptional = customerRepository.findById(customerId);
		if(customerOptional.isPresent()) {
			Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(customerOptional.get(), pageable);
			
			return new BeerOrderPagedList(beerOrderPage
					.stream()
					.map(beerOrderMapper::beerOrderToDto)
					.collect(Collectors.toList()), 
					PageRequest.of(beerOrderPage.getPageable().getPageNumber(), beerOrderPage.getPageable().getPageSize()), 
					beerOrderPage.getTotalElements());
		}
		return null;
	}

	@Override
	@Transactional
	public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
		
		Optional<Customer> customerOptional = customerRepository.findById(customerId);
		if(customerOptional.isPresent())
		{
			BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
			beerOrder.setId(null);
			beerOrder.setCustomer(customerOptional.get());
			beerOrder.setOrderStatus(OrderStatusEnum.NEW);
			if(beerOrder.getBeerOrderLines()!=null) {
			beerOrder.getBeerOrderLines().forEach(line->line.setBeerOrder(beerOrder));		// cross reference
			}
			BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
			
			//todo impl
	          //  publisher.publishEvent(new NewBeerOrderEvent(savedBeerOrder));

	        return beerOrderMapper.beerOrderToDto(savedBeerOrder);
		}
		throw new RuntimeException("Customer Not Found");
	}

	@Override
	public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
		return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
	}

	@Override
	public void pickupOrder(UUID customerId, UUID orderId) {
		BeerOrder beerOrder = getOrder(customerId, orderId);
		beerOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);
		beerOrderRepository.save(beerOrder);
	}
	
	private BeerOrder getOrder(UUID customerId, UUID orderId){
		
		Optional<Customer> customerOptional = customerRepository.findById(customerId);
		if(customerOptional.isPresent()) {
			Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);
			if(beerOrderOptional.isPresent()) {
				BeerOrder beerOrder = beerOrderOptional.get();
				if(beerOrder.getCustomer().getId().equals(customerId)) {
					return beerOrder;
				}
			}
			throw new RuntimeException("Beer Order Not Found");
		}
		throw new RuntimeException("Customer Not Found");
	}

}
