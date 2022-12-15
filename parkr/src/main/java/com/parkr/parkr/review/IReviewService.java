package com.parkr.parkr.review;

import java.util.List;

public interface IReviewService 
{
    ReviewDto getReviewById(Long id);

    List<ReviewDto> getAllReviews();

    Review saveReview(ReviewDto reviewDto, Long parkingLotId);
}
