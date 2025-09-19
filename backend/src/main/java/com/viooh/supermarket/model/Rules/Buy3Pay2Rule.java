package com.viooh.supermarket.model.Rules;

/**
 * Rule that allows customers to buy 3 items and pay for only 2 of them.
 * For example, "Buy 3 soaps and pay for only 2."
 * Rule 1
 */ 
public record Buy3Pay2Rule(String ruleId, String description) implements RulesEngine {}

