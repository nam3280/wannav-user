package com.ssg.wannavapibackend.repository;


import com.ssg.wannavapibackend.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {

}
