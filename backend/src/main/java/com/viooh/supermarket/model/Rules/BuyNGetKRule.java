package com.viooh.supermarket.model.Rules;

/**
 * Rule that allows customers to buy N items of type X and get K items of type Y for free.
 * For example, "Buy 2 shampoos and get 1 conditioner for free."
 * Rule 4
 */
public record BuyNGetKRule(String ruleId, String description, String xItem, int n, String yItem, int k) implements RulesEngine {}

