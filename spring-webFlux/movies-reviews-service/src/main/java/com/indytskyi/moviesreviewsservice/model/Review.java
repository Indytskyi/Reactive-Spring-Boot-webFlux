package com.indytskyi.moviesreviewsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {

    @Id
    private String reviewId;

    @NotNull(message = "rating.movieInfoId : must not be null")
    private Long movieInfoId;

    @NotBlank(message = "rating.comment : must not be blank")
    private String comment;
    @Min(value = 0L, message = "rating.negative : rating is negative and please pass a non-negative value")
    private Double rating;
}
