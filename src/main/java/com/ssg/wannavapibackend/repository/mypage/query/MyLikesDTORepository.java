package com.ssg.wannavapibackend.repository.mypage.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssg.wannavapibackend.domain.QLikes;
import com.ssg.wannavapibackend.domain.QRestaurant;
import com.ssg.wannavapibackend.domain.QReview;
import com.ssg.wannavapibackend.dto.response.MyLikesResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyLikesDTORepository {

    private final JPAQueryFactory queryFactory;
    private final QRestaurant restaurant = QRestaurant.restaurant;
    private final QReview review = QReview.review;
    private final QLikes likes = QLikes.likes;

    public List<MyLikesResponseDTO> findMyLikesById(Long userId) {
        return queryFactory
                .select(Projections.fields(MyLikesResponseDTO.class,
                        restaurant,
                        review.rating.avg().coalesce(0.0).as("ratingAvg"),              //식당별 평균 별점
                        review.id.count().coalesce(0L).intValue().as("reviewCount"),    //식당별 리뷰 개수
                        likes.id.count().coalesce(0L).intValue().as("likesCount")))     //식당별 찜 개수
                .from(restaurant)
                .join(likes).on(restaurant.id.eq(likes.restaurant.id).and(likes.user.id.eq(userId)))
                .leftJoin(review).on(restaurant.id.eq(review.restaurant.id))
                .groupBy(restaurant.id).fetch();
    }
}
