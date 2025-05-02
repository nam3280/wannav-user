package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository  extends JpaRepository<Admin, Long> {

}
