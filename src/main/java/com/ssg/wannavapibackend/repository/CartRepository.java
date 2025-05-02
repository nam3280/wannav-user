package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Cart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Long countByUserId(Long userId);

    Optional<Cart> findByUserIdAndProductId(Long userId, Long productId);

    List<Cart> findAllByUserId(Long userId, Sort id);
}
