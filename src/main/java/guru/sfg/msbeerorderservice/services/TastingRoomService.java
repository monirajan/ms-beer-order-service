package guru.sfg.msbeerorderservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.msbeerorderservice.bootstrap.BeerOrderBootstrap;
import guru.sfg.msbeerorderservice.domain.Customer;
import guru.sfg.msbeerorderservice.repositories.BeerOrderRepository;
import guru.sfg.msbeerorderservice.repositories.CustomerRepository;
import guru.sfg.msbeerorderservice.web.model.BeerOrderDto;
import guru.sfg.msbeerorderservice.web.model.BeerOrderLineDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TastingRoomService {
	
	private final CustomerRepository customerRepository;
	private final BeerOrderRepository beerOrderRepository;
	private final BeerOrderService beerOrderService;
	private final List<String> beerUpcs = new ArrayList<>(3);
	
	
	public TastingRoomService(CustomerRepository customerRepository, BeerOrderRepository beerOrderRepository,
			BeerOrderService beerOrderService) {
		this.customerRepository = customerRepository;
		this.beerOrderRepository = beerOrderRepository;
		this.beerOrderService = beerOrderService;
		beerUpcs.add(BeerOrderBootstrap.BEER_1_UPC);
		beerUpcs.add(BeerOrderBootstrap.BEER_2_UPC);
		beerUpcs.add(BeerOrderBootstrap.BEER_3_UPC);
	}
	
	@Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeTastingRoomOrder(){

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(BeerOrderBootstrap.TASTING_ROOM);

        if (customerList.size() == 1){ //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String beerToOrder = getRandomBeerUpc();

        BeerOrderLineDto beerOrderLine = BeerOrderLineDto.builder()
                .upc(beerToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<BeerOrderLineDto> beerOrderLineSet = new ArrayList<>();
        beerOrderLineSet.add(beerOrderLine);

        BeerOrderDto beerOrder = BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .beerOrderLines(beerOrderLineSet)
                .build();

        BeerOrderDto savedOrder = beerOrderService.placeOrder(customer.getId(), beerOrder);

    }

    private String getRandomBeerUpc() {
        return beerUpcs.get(new Random().nextInt(beerUpcs.size() -0));
    }
	
}
