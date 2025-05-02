package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query("SELECT c.active FROM Coupon c WHERE c.id = :couponId")
    Boolean findIsActiveById(Long couponId);

    Coupon findByEventId(Long eventId);
}
