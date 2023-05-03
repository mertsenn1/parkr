package com.parkr.parkr.lot_summary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.parkr.parkr.parking_lot.ParkingLotRepository;
import com.parkr.parkr.response.ParkingInfoModel;
import com.parkr.parkr.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotSummaryService implements  ILotSummaryService{
    private final LotSummaryRepository lotSummaryRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ModelMapper modelMapper;

    @Override
    public LotSummaryDto getLotSummaryById(Long id){
        Optional<LotSummary> lotSummary = lotSummaryRepository.findById(id);
        if (!lotSummary.isPresent()) throw new LotSummaryNotFoundException("LotSummary couldn't found by id: " + id);
        log.info("Location with the id: {} is requested", id);
        return convertToLotSummaryDto(lotSummary.get());
    }

    @Override
    public List<LotSummaryDto> getAllLotSummarries(){
        List<LotSummaryDto> lotSummaryDtoList = lotSummaryRepository.findAll().stream().map(this::convertToLotSummaryDto).toList();
        log.info("All lotSummaries are requested with the size: {}", lotSummaryDtoList.size());
        return lotSummaryDtoList;
    }

    @Override
    public LotSummary saveLotSummary(LotSummaryDto lotSummaryDto){
        LotSummary lotSummary;
        try{
            lotSummary = lotSummaryRepository.save(convertToLotSummary(lotSummaryDto));
            log.info("LotSummary is saved with id: {}", lotSummary.getId());
        }
        catch (Exception ex){
            log.info("Error occurred while saving the lotSummary, error: {}", ex.getMessage());
            return null;
        }
        return lotSummary;
    }

    @Override
    public void deleteLotSummary(Long id){
        Optional<LotSummary> lotSummary = lotSummaryRepository.findById(id);
        if (!lotSummary.isPresent()) throw new LotSummaryNotFoundException("LotSummary couldn't found by id: " + id);
        try{
            lotSummaryRepository.delete(lotSummary.get());
            log.info("LotSummary with id is deleted: {}", id);
        }
        catch (Exception ex){
            log.info("Error occurred while deleting the lotSummary, error: {}", ex.getMessage());
        }
    }

    public LotSummaryDto convertToLotSummaryDto(LotSummary lotSummary){
        LotSummaryDto lotSummaryDto = modelMapper.map(lotSummary, LotSummaryDto.class);
        return lotSummaryDto;
    }

    private LotSummary convertToLotSummary(LotSummaryDto lotSummaryDto){
        LotSummary lotSummary = modelMapper.map(lotSummaryDto, LotSummary.class);
        return lotSummary;
    }

    @Override
    public List<ParkingInfoModel> getCurrentParkingData() {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(currentUser.getId());

        List<LotSummary> summaryList = lotSummaryRepository.getCurrentLotSummariesOfUser(currentUser.getId());

        ArrayList<ParkingInfoModel> responseList = new ArrayList<>();
        summaryList.forEach(summary -> {
            ParkingInfoModel responseModel = new ParkingInfoModel();
            responseModel.setId(summary.getId());
            responseModel.setName(summary.getParkingLot().getName());
            responseModel.setFee(summary.getFee());
            responseModel.setStartTime(summary.getStartTime());

            responseList.add(responseModel);
        });
        return responseList;
    }

    @Override
    public List<ParkingInfoModel> getPastParkingData() {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(currentUser.getId());

        List<LotSummary> summaryList = lotSummaryRepository.getPastLotSummariesOfUser(currentUser.getId());

        ArrayList<ParkingInfoModel> responseList = new ArrayList<>();
        summaryList.forEach(summary -> {
            ParkingInfoModel responseModel = new ParkingInfoModel();
            responseModel.setId(summary.getId());
            responseModel.setName(summary.getParkingLot().getName());
            responseModel.setFee(null);
            responseModel.setStartTime(summary.getStartTime());

            responseList.add(responseModel);
        });
        return responseList;
    }
}
