package com.example.finance.accounting;

import com.example.finance.util.Money;

import java.time.LocalDate;
import java.util.*;

public class Ledger {
    private final Map<String, AccountType> accountTypes = new LinkedHashMap<>();
    private final List<JournalEntry> journalEntries = new ArrayList<>();

    public void registerAccount(String accountName, AccountType type) {
        if (accountTypes.containsKey(accountName)) return;
        accountTypes.put(accountName, type);
    }

    public void post(JournalEntry entry) {
        entry.validateBalanced();
        for (Entry line : entry.getLines()) {
            if (!accountTypes.containsKey(line.getAccountName())) {
                throw new IllegalArgumentException("Account not registered: " + line.getAccountName());
            }
        }
        journalEntries.add(entry);
    }

    public Map<String, Money> calculateBalances(LocalDate startInclusive, LocalDate endInclusive) {
        Map<String, Money> balances = new LinkedHashMap<>();
        for (String account : accountTypes.keySet()) {
            balances.put(account, Money.zero());
        }
        for (JournalEntry je : journalEntries) {
            LocalDate d = je.getDate();
            boolean inRange = (startInclusive == null || !d.isBefore(startInclusive)) && (endInclusive == null || !d.isAfter(endInclusive));
            if (!inRange) continue;
            for (Entry e : je.getLines()) {
                String acc = e.getAccountName();
                AccountType type = accountTypes.get(acc);
                Money current = balances.getOrDefault(acc, Money.zero());
                Money next;
                if (isDebitNormal(type)) {
                    next = e.isDebit() ? current.add(e.getAmount()) : current.subtract(e.getAmount());
                } else {
                    next = e.isDebit() ? current.subtract(e.getAmount()) : current.add(e.getAmount());
                }
                balances.put(acc, next);
            }
        }
        return balances;
    }

    public Money sumByTypes(Map<String, Money> balances, AccountType... types) {
        Set<AccountType> typeSet = new HashSet<>(Arrays.asList(types));
        Money total = Money.zero();
        for (Map.Entry<String, Money> e : balances.entrySet()) {
            if (typeSet.contains(accountTypes.get(e.getKey()))) {
                total = total.add(e.getValue());
            }
        }
        return total;
    }

    public AccountType getAccountType(String accountName) {
        return accountTypes.get(accountName);
    }

    public Set<String> getAccounts() {
        return accountTypes.keySet();
    }

    public static boolean isDebitNormal(AccountType type) {
        return type == AccountType.ASSET || type == AccountType.EXPENSE || type == AccountType.COGS;
    }
}