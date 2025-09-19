package com.viooh.supermarket.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.viooh.supermarket.model.Rules.Buy3Pay2Rule;
import com.viooh.supermarket.model.Rules.BuyNGetKRule;
import com.viooh.supermarket.model.Rules.CheapestFreeInGroupRule;
import com.viooh.supermarket.model.Rules.RulesEngine;
import com.viooh.supermarket.model.Rules.SpecialPriceRule;
import com.viooh.supermarket.exceptions.CheckoutException;
import com.viooh.supermarket.model.LineItem;
import com.opencsv.CSVReader;

public class SupermarketUtil {

public static List<LineItem> itemConversion(InputStream allItems) {
        List<LineItem> items = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(allItems))) {
            String[] line;
            int rowNumber = 0;

            reader.readNext(); // skip header

            while ((line = reader.readNext()) != null) {
                rowNumber++;
                items.add(LineItem.fromCsvRow(line, rowNumber));
            }

        } catch (Exception ex) {
            if(ex instanceof IllegalArgumentException) {
                throw new CheckoutException("Invalid data in Line Item CSV at row: " + ex.getMessage(), ex);
            }
            throw new CheckoutException("CSV parsing failed for LineItems: " + ex.getMessage(), ex);
        }

        return items;
    }

    public static List<RulesEngine> parseRules(MultipartFile rulesFile) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(rulesFile.getInputStream(), StandardCharsets.UTF_8))) {

            return reader.lines()
                    .skip(1)
                    .flatMap(line -> {
                        String[] parts = line.split(",", -1);
                        String[] ruleIds = parts[0].trim().split("\\|"); // multiple rules
                        return Arrays.stream(ruleIds)
                                .map(ruleId -> createRuleFromCsv(ruleId.trim(), parts));
                    })
                    .collect(Collectors.toList());
         } catch (Exception ex) {
            if(ex instanceof IllegalArgumentException) {
                throw new CheckoutException("Invalid data in Rules CSV at row: " + ex.getMessage(), ex);
            }
            throw new CheckoutException("CSV parsing failed for Rules: " + ex.getMessage(), ex);
        }
    }

    private static RulesEngine createRuleFromCsv(String ruleId, String[] parts) {
        
        String description = parts[1].trim();
        double specialPrice = parts[2].trim().isEmpty() ? 0.0 : Double.parseDouble(parts[2].trim());
        int n = parts[3].trim().isEmpty() ? 0 : Integer.parseInt(parts[3].trim());
        String specialItem = parts[4].trim().isEmpty() ? null : parts[4].trim();
        int k = parts[5].trim().isEmpty() ? 0 : Integer.parseInt(parts[5].trim());
        String offerItem = parts[6].trim().isEmpty() ? null : parts[6].trim();

        return switch (ruleId) {
            case "Rule1" -> new Buy3Pay2Rule(ruleId, description);
            case "Rule2" -> new SpecialPriceRule(ruleId, description,specialItem,specialPrice);
            case "Rule3" -> new CheapestFreeInGroupRule(ruleId, description);
            case "Rule4" -> new BuyNGetKRule(ruleId, description, specialItem, n, offerItem, k);
            default -> throw new IllegalArgumentException("Unknown rule id: " + ruleId);
        };
    }


}
