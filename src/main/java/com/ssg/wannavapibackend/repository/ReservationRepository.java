package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {
    @Query("""
            SELECT CASE WHEN COUNT(r) > 0
                        THEN true ELSE false END
                        FROM Reservation r
                        WHERE r.user.id = :userId
                        AND r.restaurant.id = :restaurantId
                        AND r.reservationDate = :selectDate
            """)
    Boolean existsByMyReservaion(Long userId, Long restaurantId, LocalDate selectDate);
}