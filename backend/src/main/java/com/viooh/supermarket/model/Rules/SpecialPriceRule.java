package com.viooh.supermarket.model.Rules;


/**
 * Rule that offers a special price for a specific item.
 * For example, "Buy soap for $2.00 instead of the regular price of $3.00."
 * Rule 2
 */ 
public record SpecialPriceRule(String ruleId, String description, String itemId, double specialPrice) implements RulesEngine {}


