package com.viooh.supermarket.model.Rules;


/**
 * Rule that offers the cheapest item for free within a specified group of items.
 * For example, "Buy any 3 items from group 'A' and get the cheapest one for free."
 * Rule 3
 */
public record CheapestFreeInGroupRule(String ruleId, String description) implements RulesEngine {}

