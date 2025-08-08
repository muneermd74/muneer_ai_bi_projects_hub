package com.example.finance.accounting;

import com.example.finance.util.Money;

public class Entry {
    private final String accountName;
    private final Money amount;
    private final boolean debit;

    public Entry(String accountName, Money amount, boolean debit) {
        this.accountName = accountName;
        this.amount = amount;
        this.debit = debit;
    }

    public String getAccountName() {
        return accountName;
    }

    public Money getAmount() {
        return amount;
    }

    public boolean isDebit() {
        return debit;
    }
}