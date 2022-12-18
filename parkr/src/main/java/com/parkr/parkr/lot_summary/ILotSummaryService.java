package com.parkr.parkr.lot_summary;

import java.util.List;

public interface ILotSummaryService {
    LotSummaryDto getLotSummaryById(Long id);
    List<LotSummaryDto> getAllLotSummarries();
    LotSummary saveLotSummary(LotSummaryDto lotSummaryDto);
    void deleteLotSummary(Long id);
    LotSummaryDto convertToLotSummaryDto(LotSummary lotSummary);
}
