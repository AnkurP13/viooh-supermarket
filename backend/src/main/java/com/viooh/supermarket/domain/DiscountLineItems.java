package com.viooh.supermarket.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.viooh.supermarket.exceptions.CheckoutException;
import com.viooh.supermarket.model.LineItem;
import com.viooh.supermarket.model.Receipt;
import com.viooh.supermarket.model.Rules.Buy3Pay2Rule;
import com.viooh.supermarket.model.Rules.BuyNGetKRule;
import com.viooh.supermarket.model.Rules.CheapestFreeInGroupRule;
import com.viooh.supermarket.model.Rules.RulesEngine;
import com.viooh.supermarket.model.Rules.SpecialPriceRule;

@Component
public class DiscountLineItems {

    public List<Receipt> applyDiscounts(List<LineItem> items, List<RulesEngine> rules) {
        List<Receipt> finalReceipts = new ArrayList<>();
            
        for (LineItem item : items) {
            if (item.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero for item " + item.itemId());
            }
             Map<RulesEngine, Double> discountRules = new HashMap<>();
            rules.stream()
                .filter(r -> r instanceof Buy3Pay2Rule)
                .findFirst()
                .ifPresent(r -> discountRules.put(r, applyRuleOne(item)));
            
            rules.stream()
                .filter(r -> r instanceof SpecialPriceRule spr && spr.itemId().equals(item.itemId()))
                .findFirst()
                .ifPresent(r -> discountRules.put(r, applyRuleTwo(item, (SpecialPriceRule) r)));
        
            rules.stream()
                .filter(r -> r instanceof CheapestFreeInGroupRule)
                .findFirst()
                .ifPresent(r -> {
                    double discountedPrice = applyRuleThree(items, item.groupId());
                    discountRules.put(r, discountedPrice);
                });

            rules.stream()
                .filter(r -> r instanceof BuyNGetKRule)
                .map(r -> (BuyNGetKRule) r)
                .forEach(rule -> {
                    double discountedTotal = applyRuleFour(items, rule);
                    discountRules.put(rule, discountedTotal);
                });

        double originalPrice = item.unitPrice().doubleValue() * item.quantity();

        // Determine the best rule
        Map.Entry<RulesEngine, Double> bestEntry = discountRules.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);

        double discountedPrice = bestEntry != null ? bestEntry.getValue() : originalPrice;
        String appliedRule = bestEntry != null ? bestEntry.getKey().ruleId() + ": " + bestEntry.getKey().description() : "No discount";
        double discount = originalPrice - discountedPrice;

        // Add receipt
        finalReceipts.add(new Receipt(
            item.itemId(),
            item.quantity(),
            String.format("%.2f", originalPrice),
            String.format("%.2f", discount),
            appliedRule,
            String.format("%.2f", discountedPrice)
        ));
    }
        return finalReceipts;
    }

    private double applyRuleOne(LineItem item) {
        double total = 0.0;
        try{
            int groupsOfThree = item.quantity() / 3;
            int remainder = item.quantity() % 3;
            total = (groupsOfThree * 2 + remainder) * item.unitPrice().doubleValue();
        }   catch (Exception e) {
            throw new CheckoutException("Error applying Rule One: " + e.getMessage());   
        }
        return total;
    }

    private double applyRuleTwo(LineItem item, SpecialPriceRule rule) {
        double total = 0.0;
        try{
            int pairs = item.quantity() / 2;
            int remainder = item.quantity() % 2;
            total = pairs * rule.specialPrice() + remainder * item.unitPrice().doubleValue();
        } catch (Exception e) {
            throw new CheckoutException("Error applying Rule two: " + e.getMessage());   
        }
        return total;
    }

    private double applyRuleThree(List<LineItem> items, String groupId) {
        double total = 0.0;
        
        try{
        // Filter items belonging to this group
        List<BigDecimal> prices = items.stream()
                .filter(item -> groupId.equals(item.groupId()))
                .flatMap(item -> 
                    java.util.stream.IntStream.range(0, item.quantity())
                        .mapToObj(i -> item.unitPrice())
                )
                .toList();

        // Sort all unit prices ascending
        List<BigDecimal> sorted = new ArrayList<>(prices);
        sorted.sort(BigDecimal::compareTo);

        
        for (int i = 0; i < sorted.size(); i++) {
            // Every 3rd item → skip (cheapest free)
            if ((i + 1) % 3 == 0) continue;
            total += sorted.get(i).doubleValue();
        }
        } catch (Exception e) {
            throw new CheckoutException("Error applying Rule three: " + e.getMessage());   
        }
        return total;
    }

    private double applyRuleFour(List<LineItem> items, BuyNGetKRule rule) {
    double total = 0.0;
    
    try{
    // Step 1: Find counts of X and Y
    LineItem itemX = items.stream()
            .filter(i -> i.itemId().equals(rule.xItem()))
            .findFirst()
            .orElse(null);

    LineItem itemY = items.stream()
            .filter(i -> i.itemId().equals(rule.yItem()))
            .findFirst()
            .orElse(null);

    if (itemX == null || itemY == null) {
        return items.stream()
                .mapToDouble(i -> i.unitPrice().doubleValue() * i.quantity())
                .sum(); // no discount applied
    }

    // Step 2: How many times does the offer apply?
    int eligibleGroups = itemX.quantity() / rule.n();
    int freeY = eligibleGroups * rule.k();

    // Step 3: Compute discounted total
    
    for (LineItem item : items) {
        if (item.itemId().equals(rule.yItem())) {
            int payableQuantity = Math.max(0, item.quantity() - freeY);
            total += payableQuantity * item.unitPrice().doubleValue();
        } else {
            total += item.quantity() * item.unitPrice().doubleValue();
        }
    }
        } catch (Exception e) {
            throw new CheckoutException("Error applying Rule four: " + e.getMessage());   
        }

    return total;
}


}
