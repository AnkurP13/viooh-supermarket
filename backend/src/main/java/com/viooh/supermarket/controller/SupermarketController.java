package com.viooh.supermarket.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.viooh.supermarket.model.CheckoutResponse;
import com.viooh.supermarket.model.Receipt;
import com.viooh.supermarket.service.SupermarketService;

@RestController
@RequestMapping("/supermarket")
public class SupermarketController {


    private final SupermarketService supermarketService;
    public SupermarketController(SupermarketService supermarketService) {
        this.supermarketService = supermarketService;
    }

    // Endpoint 1: upload CSVs via multipart
    @PostMapping(path = "/itemcheckout",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public CheckoutResponse checkoutFiles(
            @RequestPart("itemsFile") MultipartFile itemsFile,
            @RequestPart("rulesFile") MultipartFile rulesFile) throws IOException {
                System.out.println("Items File: " + itemsFile.getOriginalFilename());
                List<Receipt> receipts = new ArrayList<>();
                try{
                    receipts = supermarketService.process(itemsFile, rulesFile);
                } catch (Exception e) {
                    System.err.println("Error during processing: " + e.getMessage());
                }
                // Calculate overall total price
                double discountTotal = receipts.stream()
                    .mapToDouble(r -> Double.parseDouble(r.totalPrice()))
                    .sum();

                double originalTotal = receipts.stream()
                    .mapToDouble(r -> Double.parseDouble(r.price()))
                    .sum();
                return new CheckoutResponse(
                    receipts,
                    "£" + String.format("%.2f", discountTotal),
                    "£" + String.format("%.2f", originalTotal)
                );
    }
}
