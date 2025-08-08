# Finance Statements (Java)

Minimal Java implementation to generate:
- Balance Sheet (as of a date)
- Income Statement (for a period)
- Inventory Control Statement (weighted-average costing, for a period)

No external build tools required.

## Run

```bash
bash scripts/run.sh
```

## Structure

- `com.example.finance.accounting`: Accounts, journal entries, ledger, and statement calculations
- `com.example.finance.inventory`: Inventory events and manager (weighted-average)
- `com.example.finance.statements`: Statement printers
- `com.example.finance.util.Money`: BigDecimal wrapper for money-safe math

## Notes
- Inventory COGS in the income statement comes from posted journal entries. The Inventory Control Statement independently computes COGS and ending inventory using weighted-average costing, which you can use to reconcile against the ledger postings.
- Sample data is in `Main` and can be modified to fit your scenario.
