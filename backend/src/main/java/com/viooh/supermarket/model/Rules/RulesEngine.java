package com.viooh.supermarket.model.Rules;


public sealed interface RulesEngine permits Buy3Pay2Rule, SpecialPriceRule, CheapestFreeInGroupRule, BuyNGetKRule {
    String ruleId();
    String description();


}
