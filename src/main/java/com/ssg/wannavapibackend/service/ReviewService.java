package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.Review;
import com.ssg.wannavapibackend.dto.request.ReviewSaveDTO;

public interface ReviewService {

    void saveReview(Long userId, ReviewSaveDTO reviewSaveDTO, String restaurantName, String visitDate);
    void updateReview(Long reviewId, ReviewSaveDTO reviewUpdateDTO);
    void checkReviewUpdate(Long reviewId);
    Review findReview(Long reviewId);
    void deleteReview(Long reviewId);
}
