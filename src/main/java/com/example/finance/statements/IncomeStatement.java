package com.example.finance.statements;

import com.example.finance.accounting.AccountType;
import com.example.finance.accounting.Ledger;
import com.example.finance.util.Money;

import java.time.LocalDate;
import java.util.Map;

public class IncomeStatement {
    public static void print(Ledger ledger, LocalDate startInclusive, LocalDate endInclusive) {
        Map<String, Money> periodBalances = ledger.calculateBalances(startInclusive, endInclusive);

        Money totalRevenue = ledger.sumByTypes(periodBalances, AccountType.REVENUE);
        Money totalCOGS = ledger.sumByTypes(periodBalances, AccountType.COGS);
        Money totalExpenses = ledger.sumByTypes(periodBalances, AccountType.EXPENSE);

        Money grossProfit = totalRevenue.subtract(totalCOGS);
        Money netIncome = grossProfit.subtract(totalExpenses);

        System.out.println("==== Income Statement for period " + startInclusive + " to " + endInclusive + " ====");
        System.out.printf("%-27s %12s%n", "Revenue", totalRevenue);
        System.out.printf("%-27s %12s%n", "Cost of Goods Sold", totalCOGS);
        System.out.printf("%-27s %12s%n", "Gross Profit", grossProfit);
        System.out.println();
        System.out.printf("%-27s %12s%n", "Operating Expenses", totalExpenses);
        System.out.println();
        System.out.printf("%-27s %12s%n", "Net Income", netIncome);
        System.out.println("==============================================\n");
    }
}