package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Payment;
import com.ssg.wannavapibackend.dto.response.PaymentItemResponseDTO;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByUserIdOrderByCreatedAtDesc(Long userId);

   @Query("SELECT new com.ssg.wannavapibackend.dto.response.PaymentItemResponseDTO(p.id, p.image, p.name, c.quantity, " +
       "CAST(COALESCE(p.finalPrice, 0.0) * c.quantity AS double) ) " +
       "FROM User u " +
       "JOIN Cart c ON u.id = c.user.id " +
       "JOIN Product p ON c.product.id = p.id " +
       "WHERE u.id = :userId AND c.id IN :cartIds")
    List<PaymentItemResponseDTO> findCartsForPayment(@Param("userId") Long userId, @Param("cartIds") List<Long> cartIds);

   Optional<Payment> findByOrderId(String orderId);
}