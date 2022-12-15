package com.parkr.parkr.review;

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
public class ReviewService implements IReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ParkingLotRepository parkingLotRepository;

    @Override
    public ReviewDto getReviewById(Long id) {
        Optional<Review> review = reviewRepository.findById(id);

        if (!review.isPresent()) return null;

        log.info("Review with the id: {} is requested", id);

        return convertToReviewDto(review.get());
    }

    @Override
    public List<ReviewDto> getAllReviews() {
        List<ReviewDto> reviews = reviewRepository.findAll().stream().map(this::convertToReviewDto).toList();

        log.info("All reviews are requested with the size: {}", reviews.size());

        return reviews;
    }

    @Override
    public Review saveReview(ReviewDto reviewDto, Long parkingLotId) {
        Review review;
        ParkingLot parkingLot;

        try
        {
            parkingLot = parkingLotRepository.findById(parkingLotId).get();

            review = reviewRepository.save(convertToReview(reviewDto, parkingLot));
            log.info("Review is saved with id: {}", review.getId());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the review, error: {}", ex.getMessage());
            return null;
        }
        return review;
    }

    private ReviewDto convertToReviewDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .parkingLotId(review.getParkingLot().getId())
                .build();
    }

    private Review convertToReview(ReviewDto reviewDto, ParkingLot parkingLot) {
        return new Review(null, reviewDto.getComment(), reviewDto.getRating(), parkingLot);
    }
    
}
