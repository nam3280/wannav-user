package com.ssg.wannavapibackend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CartItemQuantityUpdateDTO {

    private Long cartId;

    @Positive(message = "Quantity must be a positive number.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    @Max(value = 99, message = "Quantity must be no greater than 99.")
    private Integer quantity;

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
