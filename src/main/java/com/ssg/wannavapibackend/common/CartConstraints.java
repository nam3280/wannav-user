package com.ssg.wannavapibackend.common;

public enum CartConstraints {
    CART_MAX_ITEMS(15, "Maximum items allowed in the cart"),
    MIN_PRODUCT_QUANTITY(1, "Minimum quantity allowed per product"),
    MAX_PRODUCT_QUANTITY(99, "Maximum quantity allowed per product");

    private final int value;
    private final String description;

    CartConstraints(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
