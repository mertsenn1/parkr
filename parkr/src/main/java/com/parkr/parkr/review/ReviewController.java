package com.parkr.parkr.review;

import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    @GetMapping("{id}")
    public ApiResponse getReviewById(@PathVariable Long id) {
        return ApiResponse.ok(reviewService.getReviewById(id));
    }

    @GetMapping
    public ApiResponse getAllReviews() {
        return ApiResponse.ok(reviewService.getAllReviews());
    }

    @PostMapping()
    public ApiResponse saveReview(@RequestBody ReviewDto reviewDto) {
        return ApiResponse.ok(reviewService.saveReview(reviewDto, reviewDto.getParkingLotId()));
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
