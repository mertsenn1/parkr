package com.parkr.parkr.lot_summary;

import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lotSummaries")
public class LotSummaryController {
    private final ILotSummaryService lotSummaryService;

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('LOT_WORKER')")
    public ApiResponse getLotSummaryById(@PathVariable Long id) {
        return ApiResponse.ok(lotSummaryService.getLotSummaryById(id));
    }

    @GetMapping()
    public ApiResponse getAllLotSummaries() {
        return ApiResponse.ok(lotSummaryService.getAllLotSummarries());
    }

    @PostMapping()
    public ApiResponse saveLotSummaries(@RequestBody LotSummaryDto lotSummaryDto) {
        return ApiResponse.ok(lotSummaryService.saveLotSummary(lotSummaryDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        lotSummaryService.deleteLotSummary(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
