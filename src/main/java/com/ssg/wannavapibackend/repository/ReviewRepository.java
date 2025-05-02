package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN r.reviewTags rt " +
            "WHERE r.user.id = :userId " +
            "ORDER BY r.createdAt DESC")
    List<Review> findAllByUserIdDesc(Long userId);
}
