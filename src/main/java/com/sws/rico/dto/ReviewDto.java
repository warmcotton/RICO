package com.sws.rico.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sws.rico.entity.Review;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter @Setter
public class ReviewDto {
    @NotNull
    private Long itemId;
    private String name;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    @Max(5) @Min(1)
    @NotNull
    private Double rating;
    @NotBlank
    @Size(min = 5, max = 1000)
    private String review;
}
