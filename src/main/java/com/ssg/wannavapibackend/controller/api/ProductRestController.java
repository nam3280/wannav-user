package com.ssg.wannavapibackend.controller.api;

import com.ssg.wannavapibackend.common.Category;
import com.ssg.wannavapibackend.dto.response.ProductResponseDTO;
import com.ssg.wannavapibackend.service.ProductService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductRestController {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getProductListByCategory(@RequestParam Category category) {

        List<ProductResponseDTO> products = productService.getProductListByCategory(category);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", products);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
