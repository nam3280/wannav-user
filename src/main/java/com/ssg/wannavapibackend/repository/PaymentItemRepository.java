package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {

}