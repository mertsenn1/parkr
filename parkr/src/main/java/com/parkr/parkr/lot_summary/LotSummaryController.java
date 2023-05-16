package com.parkr.parkr.lot_summary;

import com.parkr.parkr.common.ApiResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lotSummaries")
@SecurityRequirement(name = "parkr")
public class LotSummaryController {
    private final ILotSummaryService lotSummaryService;

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('LOT_WORKER') or hasAuthority('ADMIN')")
    public ApiResponse getLotSummaryById(@PathVariable Long id) {
        return ApiResponse.ok(lotSummaryService.getLotSummaryById(id));
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse getAllLotSummaries() {
        return ApiResponse.ok(lotSummaryService.getAllLotSummarries());
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse saveLotSummaries(@RequestBody LotSummaryDto lotSummaryDto) {
        return ApiResponse.ok(lotSummaryService.saveLotSummary(lotSummaryDto));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        lotSummaryService.deleteLotSummary(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
    
    @ExceptionHandler({LotSummaryNotFoundException.class})
    public ResponseEntity<?> handleException(LotSummaryNotFoundException e) {
        return new ResponseEntity<> (e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
