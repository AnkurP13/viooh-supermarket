package com.viooh.supermarket.model;

import java.math.BigDecimal;

public record LineItem(
        String itemId,
        String groupId,
        int quantity,
        BigDecimal unitPrice
) {
    // Compact constructor for validation
    public LineItem {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId cannot be blank");
        }
        if (groupId == null || groupId.isBlank()) {
            throw new IllegalArgumentException("groupId cannot be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("unitPrice must be non-negative");
        }
    }

    // Factory method to parse from CSV row
    public static LineItem fromCsvRow(String[] row, int rowNumber) {
        try {
            return new LineItem(
                    row[0].trim(),
                    row[1].trim(),
                    Integer.parseInt(row[2].trim()),
                    new BigDecimal(row[3].trim())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Row " + rowNumber + " has invalid number format", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Row " + rowNumber + " has missing columns", e);
        }
    }
}