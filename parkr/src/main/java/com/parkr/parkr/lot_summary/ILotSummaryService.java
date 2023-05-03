package com.parkr.parkr.lot_summary;

import java.util.List;

import com.parkr.parkr.response.ParkingInfoModel;

public interface ILotSummaryService {
    LotSummaryDto getLotSummaryById(Long id);
    List<LotSummaryDto> getAllLotSummarries();
    LotSummary saveLotSummary(LotSummaryDto lotSummaryDto);
    void deleteLotSummary(Long id);
    LotSummaryDto convertToLotSummaryDto(LotSummary lotSummary);

    List<ParkingInfoModel> getCurrentParkingData();
    List<ParkingInfoModel> getPastParkingData();
}
