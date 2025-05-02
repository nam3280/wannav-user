package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.PointLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointLogRepository extends JpaRepository<PointLog, Long> {

    List<PointLog> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<PointLog> findFirstByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
