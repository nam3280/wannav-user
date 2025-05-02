package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);
}
