package com.viooh.supermarket.model;

public record Item(
    String itemId,
    String groupId,
    int quantity,
    double price
) {}
