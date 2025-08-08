package com.example.finance.statements;

import com.example.finance.inventory.InventoryManager;
import com.example.finance.util.Money;

import java.time.LocalDate;

public class InventoryControlStatement {
    public static void print(InventoryManager inv, LocalDate startInclusive, LocalDate endInclusive) {
        InventoryManager.PeriodMetrics m = inv.computePeriodMetrics(startInclusive, endInclusive);
        Money goodsAvailable = m.beginningValue.add(m.purchasesValue);
        System.out.println("==== Inventory Control Statement for period " + startInclusive + " to " + endInclusive + " ====");
        System.out.printf("%-32s %8d %12s%n", "Beginning Inventory", m.beginningQuantity, m.beginningValue);
        System.out.printf("%-32s %8d %12s%n", "Purchases", m.purchasesQuantity, m.purchasesValue);
        System.out.printf("%-32s %8s %12s%n", "Goods Available for Sale", "-", goodsAvailable);
        System.out.printf("%-32s %8d %12s%n", "COGS (from sales & shrinkage)", m.salesQuantity, m.cogsValue);
        System.out.printf("%-32s %8d %12s%n", "Ending Inventory", m.endingQuantity, m.endingValue);
        System.out.println("===============================================================\n");
    }
}