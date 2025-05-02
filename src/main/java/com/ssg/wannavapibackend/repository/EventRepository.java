package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

}
