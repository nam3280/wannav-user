package com.ssg.wannavapibackend.repository.mypage.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssg.wannavapibackend.domain.QReview;
import com.ssg.wannavapibackend.domain.QUser;
import com.ssg.wannavapibackend.domain.QUserGradeLog;
import com.ssg.wannavapibackend.dto.request.PointDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointDTORepository {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;
    private final QUser user = QUser.user;
    private final QUserGradeLog userGradeLog = QUserGradeLog.userGradeLog;

    public List<PointDTO> findAllWithUserGrade() {
        return queryFactory
                .select(Projections.fields(PointDTO.class,
                        review, user, userGradeLog))
                .from(review)
                .join(user).on(user.id.eq(review.user.id))
                .join(userGradeLog).on(userGradeLog.user.id.eq(user.id))
                .where(userGradeLog.createdAt.eq(
                        JPAExpressions.select(userGradeLog.createdAt.max())
                                .from(userGradeLog)
                                .where(userGradeLog.user.id.eq(user.id))))
                .orderBy(review.id.asc()).fetch();
    }
}
