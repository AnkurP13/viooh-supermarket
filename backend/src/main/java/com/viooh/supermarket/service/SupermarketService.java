package com.viooh.supermarket.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.viooh.supermarket.domain.DiscountLineItems;
import com.viooh.supermarket.model.Rules.RulesEngine;
import com.viooh.supermarket.model.LineItem;
import com.viooh.supermarket.model.Receipt;
import com.viooh.supermarket.util.SupermarketUtil;

@Service
public class SupermarketService {

    private final DiscountLineItems discountLineItems;

    public SupermarketService(DiscountLineItems discountLineItems) {
        this.discountLineItems = discountLineItems;
    }

    public List<Receipt> process(MultipartFile itemsFile, MultipartFile rulesFile) throws Exception {

            List<LineItem> items = retrieveItemsFromCSV(itemsFile);
            List<RulesEngine> rules = retrieveRulesFromCSV(rulesFile);
            List<Receipt> receipts = retrieveDiscountedLineItems(items, rules);

        return receipts;
    }

    private List<LineItem> retrieveItemsFromCSV(MultipartFile itemsFile) throws IOException {
        // Implement CSV parsing logic here
        List<LineItem> itemsList = null;
        itemsList = SupermarketUtil.itemConversion(itemsFile.getInputStream());
        return itemsList;
    }

    private List<RulesEngine> retrieveRulesFromCSV(MultipartFile rulesFile) throws Exception {
        // Implement CSV parsing logic here
        List<RulesEngine> rulesList = null;
        rulesList = SupermarketUtil.parseRules(rulesFile);
        return rulesList;
    }

        private List<Receipt> retrieveDiscountedLineItems(List<LineItem> items, List<RulesEngine> rules) {
        List<Receipt> receipts = null;
        try {
            // Implement discount application logic here
            receipts = discountLineItems.applyDiscounts(items, rules);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receipts;
    }
}
