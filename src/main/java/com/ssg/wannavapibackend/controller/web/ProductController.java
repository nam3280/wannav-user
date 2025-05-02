package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.common.Category;
import com.ssg.wannavapibackend.dto.response.ProductResponseDTO;
import com.ssg.wannavapibackend.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public String getProductList(Model model) {
        List<ProductResponseDTO> products = productService.getProductListByCategory(Category.MK);
        model.addAttribute("products", products);
        return "product/products";
    }

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProduct(id));
        return "product/product";
    }
}
