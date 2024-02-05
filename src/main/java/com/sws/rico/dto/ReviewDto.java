package com.sws.rico.dto;

import com.sws.rico.entity.Review;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter @Setter
public class ReviewDto {
    @NotNull
    private Long itemId;
    private String name;
    private LocalDateTime dateTime;
    @Max(5) @Min(1)
    @NotNull
    private Integer rating;
    @NotBlank
    @Size(min = 5, max = 1000)
    private String review;

    public static ReviewDto getReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setItemId(review.getItem().getId());
        reviewDto.setName(review.getUser().getName());
        reviewDto.setDateTime(review.getCreatedAt());
        reviewDto.setRating(review.getRating());
        reviewDto.setReview(review.getReview());
        return reviewDto;
    }
}
