package com.parkr.parkr.review;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {
    private Long id;

    private String comment;

    private double rating;

    private Long parkingLotId;
}
