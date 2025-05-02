package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.common.Category;
import com.ssg.wannavapibackend.domain.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByCategoryOrderById(Category category);
}
