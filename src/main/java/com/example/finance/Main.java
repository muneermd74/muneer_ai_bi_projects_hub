package com.example.finance;

import com.example.finance.accounting.*;
import com.example.finance.inventory.InventoryManager;
import com.example.finance.statements.BalanceSheet;
import com.example.finance.statements.IncomeStatement;
import com.example.finance.statements.InventoryControlStatement;
import com.example.finance.util.Money;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 3, 31);

        Ledger ledger = new Ledger();
        ledger.registerAccount("Cash", AccountType.ASSET);
        ledger.registerAccount("Accounts Receivable", AccountType.ASSET);
        ledger.registerAccount("Inventory", AccountType.ASSET);
        ledger.registerAccount("Accounts Payable", AccountType.LIABILITY);
        ledger.registerAccount("Owner's Equity", AccountType.EQUITY);
        ledger.registerAccount("Sales Revenue", AccountType.REVENUE);
        ledger.registerAccount("Cost of Goods Sold", AccountType.COGS);
        ledger.registerAccount("Operating Expenses", AccountType.EXPENSE);

        InventoryManager inventoryManager = new InventoryManager();

        // Opening equity injection
        ledger.post(new JournalEntry(start, "Owner investment")
                .debit("Cash", Money.of(50000))
                .credit("Owner's Equity", Money.of(50000))
        );

        // Inventory purchases
        inventoryManager.recordPurchase(LocalDate.of(2025, 1, 5), "SKU-100", "Widget", 100, Money.of(50.00));
        ledger.post(new JournalEntry(LocalDate.of(2025, 1, 5), "Purchase inventory")
                .debit("Inventory", Money.of(5000.00))
                .credit("Cash", Money.of(5000.00))
        );

        inventoryManager.recordPurchase(LocalDate.of(2025, 2, 10), "SKU-100", "Widget", 150, Money.of(55.00));
        ledger.post(new JournalEntry(LocalDate.of(2025, 2, 10), "Purchase inventory")
                .debit("Inventory", Money.of(8250.00))
                .credit("Cash", Money.of(8250.00))
        );

        // Sales
        // First sale: 120 units at selling price 90 each
        inventoryManager.recordSale(LocalDate.of(2025, 2, 20), "SKU-100", "Widget", 120, Money.of(90.00));
        // Weighted-average COGS for the sale will be reflected in statement via InventoryControlStatement.
        // We also post financial entries for revenue and COGS using an estimated average cost for demo purposes.
        Money sale1Revenue = Money.of(90.00).multiply(120);
        // Estimate average cost at time of sale: (5000 + 8250) / (100 + 150) = 53.00 approx
        Money sale1Cogs = Money.of(53.00).multiply(120);
        ledger.post(new JournalEntry(LocalDate.of(2025, 2, 20), "Sales - invoice 1001")
                .debit("Cash", sale1Revenue)
                .credit("Sales Revenue", sale1Revenue)
        );
        ledger.post(new JournalEntry(LocalDate.of(2025, 2, 20), "COGS for invoice 1001")
                .debit("Cost of Goods Sold", sale1Cogs)
                .credit("Inventory", sale1Cogs)
        );

        // Another sale: 60 units at price 95
        inventoryManager.recordSale(LocalDate.of(2025, 3, 15), "SKU-100", "Widget", 60, Money.of(95.00));
        Money sale2Revenue = Money.of(95.00).multiply(60);
        Money sale2Cogs = Money.of(54.00).multiply(60); // simple estimate for demo
        ledger.post(new JournalEntry(LocalDate.of(2025, 3, 15), "Sales - invoice 1002")
                .debit("Cash", sale2Revenue)
                .credit("Sales Revenue", sale2Revenue)
        );
        ledger.post(new JournalEntry(LocalDate.of(2025, 3, 15), "COGS for invoice 1002")
                .debit("Cost of Goods Sold", sale2Cogs)
                .credit("Inventory", sale2Cogs)
        );

        // Operating expenses
        ledger.post(new JournalEntry(LocalDate.of(2025, 3, 20), "Rent expense")
                .debit("Operating Expenses", Money.of(2000.00))
                .credit("Cash", Money.of(2000.00))
        );

        // Print statements
        IncomeStatement.print(ledger, start, end);
        BalanceSheet.print(ledger, end);
        InventoryControlStatement.print(inventoryManager, start, end);
    }
}