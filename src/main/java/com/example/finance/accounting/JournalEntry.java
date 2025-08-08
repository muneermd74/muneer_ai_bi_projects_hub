package com.example.finance.accounting;

import com.example.finance.util.Money;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JournalEntry {
    private final LocalDate date;
    private final String description;
    private final List<Entry> lines = new ArrayList<>();

    public JournalEntry(LocalDate date, String description) {
        this.date = date;
        this.description = description;
    }

    public JournalEntry debit(String accountName, Money amount) {
        lines.add(new Entry(accountName, amount, true));
        return this;
    }

    public JournalEntry credit(String accountName, Money amount) {
        lines.add(new Entry(accountName, amount, false));
        return this;
    }

    public void validateBalanced() {
        Money debits = Money.zero();
        Money credits = Money.zero();
        for (Entry e : lines) {
            if (e.isDebit()) debits = debits.add(e.getAmount());
            else credits = credits.add(e.getAmount());
        }
        if (!debits.equals(credits)) {
            throw new IllegalStateException("Unbalanced journal entry '" + description + "' on " + date + ": debits=" + debits + ", credits=" + credits);
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public List<Entry> getLines() {
        return lines;
    }
}