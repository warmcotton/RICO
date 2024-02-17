package com.sws.rico.mapper;

import com.sws.rico.dto.ReviewDto;
import com.sws.rico.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public static ReviewDto toReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setItemId(review.getItem().getId());
        reviewDto.setName(review.getUser().getName());
        reviewDto.setDateTime(review.getCreatedAt());
        reviewDto.setRating(review.getRating());
        reviewDto.setReview(review.getReview());
        return reviewDto;
    }
}
