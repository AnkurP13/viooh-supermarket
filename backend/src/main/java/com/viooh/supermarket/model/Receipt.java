package com.viooh.supermarket.model;

public record Receipt(
    String itemId,
    int quantity,
    String price,
    String discount,
    String ruleDiscount,
    String totalPrice
) {}