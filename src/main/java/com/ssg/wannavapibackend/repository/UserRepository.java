package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByCode(String code);


    Optional<User> findUserByEmail(String email);

    @Query("UPDATE User u " +
            "SET u.point = u.point + :point " +
            "WHERE u.id = :userId")
    void updateUserPoint(Long userId, Long point);
}
