package com.parkr.parkr.lot_summary;


import com.parkr.parkr.location.Location;
import com.parkr.parkr.location.LocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotSummaryService implements  ILotSummaryService{
    private final LotSummaryRepository lotSummaryRepository;
    private final ModelMapper modelMapper;

    public LotSummaryDto getLotSummaryById(Long id){
        Optional<LotSummary> lotSummary = lotSummaryRepository.findById(id);
        if (!lotSummary.isPresent()) throw new LotSummaryNotFoundException("LotSummary couldn't found by id: " + id);
        log.info("Location with the id: {} is requested", id);
        return convertToLotSummaryDto(lotSummary.get());
    }
    public List<LotSummaryDto> getAllLotSummarries(){
        List<LotSummaryDto> lotSummaryDtoList = lotSummaryRepository.findAll().stream().map(this::convertToLotSummaryDto).toList();
        log.info("All lotSummaries are requested with the size: {}", lotSummaryDtoList.size());
        return lotSummaryDtoList;
    }
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
}
