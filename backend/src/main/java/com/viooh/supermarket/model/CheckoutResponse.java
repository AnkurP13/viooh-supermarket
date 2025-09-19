package com.viooh.supermarket.model;

import java.util.List;

public class CheckoutResponse {
    private List<Receipt> receipts;
    private String originalTotalPrice;
    private String discountedTotalPrice;

    public CheckoutResponse(List<Receipt> receipts, String originalTotalPrice, String discountedTotalPrice   ) {
        this.receipts = receipts;
        this.originalTotalPrice = originalTotalPrice;
        this.discountedTotalPrice = discountedTotalPrice;
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
    }

    public String getDiscountedTotalPrice() {
        return discountedTotalPrice;
    }

    public void setDiscountedTotalPrice(String discountedTotalPrice) {
        this.discountedTotalPrice = discountedTotalPrice;
    }

    public String getOriginalTotalPrice() {
        return originalTotalPrice;
    }

    public void setOriginalTotalPrice(String originalTotalPrice) {
        this.originalTotalPrice = originalTotalPrice;
    }
}
