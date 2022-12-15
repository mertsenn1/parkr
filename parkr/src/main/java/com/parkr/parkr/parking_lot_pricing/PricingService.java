package com.parkr.parkr.parking_lot_pricing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.parkr.parkr.parking_lot.ParkingLot;
import com.parkr.parkr.parking_lot.ParkingLotRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService implements IPricingService {

    private final PricingRepository pricingRepository;
    private final ParkingLotRepository parkingLotRepository;

    @Override
    public PricingDto getPricingById(Long id) {
        Optional<Pricing> pricing = pricingRepository.findById(id);

        if (!pricing.isPresent()) return null;

        log.info("Pricing with the id: {} is requested", id);

        return convertToPricingDto(pricing.get());
    }

    @Override
    public List<PricingDto> getAllPricings() {
        List<PricingDto> pricings = pricingRepository.findAll().stream().map(this::convertToPricingDto).toList();

        log.info("All pricings are requested with the size: {}", pricings.size());

        return pricings;
    }

    @Override
    public Pricing savePricing(PricingDto pricingDto, Long parkingLotId) {
        Pricing pricing;
        ParkingLot parkingLot;

        try
        {
            parkingLot = parkingLotRepository.findById(parkingLotId).get();

            pricing = pricingRepository.save(convertToPricing(pricingDto, parkingLot));
            log.info("Pricing is saved with id: {}", pricingDto.getId());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the Pricing, error: {}", ex.getMessage());
            return null;
        }
        return pricing;
    }

    private PricingDto convertToPricingDto(Pricing pricing) {
        return PricingDto.builder()
                .id(pricing.getId())
                .hourType(pricing.getHourType())
                .price(pricing.getPrice())
                .currency(pricing.getCurrency())
                .carType(pricing.getCarType())
                .parkingLotId(pricing.getParkingLot().getId())
                .build();
    }

    private Pricing convertToPricing(PricingDto pricingDto, ParkingLot parkingLot) {
        return new Pricing(null, pricingDto.getHourType(), pricingDto.getPrice(), pricingDto.getCurrency(), pricingDto.getCarType(), parkingLot);
    }
    
}
