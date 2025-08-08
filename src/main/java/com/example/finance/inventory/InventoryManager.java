package com.example.finance.inventory;

import com.example.finance.util.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class InventoryManager {
    private final Map<String, List<InventoryEvent>> eventsBySku = new LinkedHashMap<>();

    public void recordPurchase(LocalDate date, String sku, String itemName, int quantity, Money unitCost) {
        addEvent(InventoryEvent.purchase(date, sku, itemName, quantity, unitCost));
    }

    public void recordSale(LocalDate date, String sku, String itemName, int quantity, Money saleUnitPrice) {
        addEvent(InventoryEvent.sale(date, sku, itemName, quantity, saleUnitPrice));
    }

    public void recordAdjustment(LocalDate date, String sku, String itemName, int quantity, Money unitCost) {
        addEvent(InventoryEvent.adjust(date, sku, itemName, quantity, unitCost));
    }

    private void addEvent(InventoryEvent event) {
        eventsBySku.computeIfAbsent(event.getSku(), k -> new ArrayList<>()).add(event);
    }

    public PeriodMetrics computePeriodMetrics(LocalDate startInclusive, LocalDate endInclusive) {
        PeriodMetrics total = new PeriodMetrics();
        for (Map.Entry<String, List<InventoryEvent>> e : eventsBySku.entrySet()) {
            List<InventoryEvent> sorted = new ArrayList<>(e.getValue());
            sorted.sort(Comparator.naturalOrder());
            total = total.add(computeForSku(sorted, startInclusive, endInclusive));
        }
        return total;
    }

    private PeriodMetrics computeForSku(List<InventoryEvent> events, LocalDate start, LocalDate end) {
        int qtyOnHand = 0;
        Money valueOnHand = Money.zero();
        Money avgUnitCost = Money.zero();

        PeriodMetrics m = new PeriodMetrics();

        // Pre-period to establish beginning balances
        for (InventoryEvent ev : events) {
            if (ev.getDate().isBefore(start)) {
                if (ev.getType() == InventoryEvent.Type.PURCHASE || ev.getType() == InventoryEvent.Type.ADJUSTMENT) {
                    // treat adjustments as purchases for positive quantity, write-offs for negative handled below
                    if (ev.getQuantity() >= 0) {
                        Money addValue = ev.getUnitCost().multiply(ev.getQuantity());
                        valueOnHand = valueOnHand.add(addValue);
                        qtyOnHand += ev.getQuantity();
                        avgUnitCost = qtyOnHand == 0 ? Money.zero() : valueOnHand.divide(new BigDecimal(qtyOnHand));
                    } else {
                        int outQty = -ev.getQuantity();
                        Money cogs = avgUnitCost.multiply(outQty);
                        valueOnHand = valueOnHand.subtract(cogs);
                        qtyOnHand -= outQty;
                        avgUnitCost = qtyOnHand == 0 ? Money.zero() : valueOnHand.divide(new BigDecimal(qtyOnHand));
                    }
                } else if (ev.getType() == InventoryEvent.Type.SALE) {
                    int outQty = ev.getQuantity();
                    Money cogs = avgUnitCost.multiply(outQty);
                    valueOnHand = valueOnHand.subtract(cogs);
                    qtyOnHand -= outQty;
                    avgUnitCost = qtyOnHand == 0 ? Money.zero() : valueOnHand.divide(new BigDecimal(qtyOnHand));
                }
            }
        }
        m.beginningQuantity += qtyOnHand;
        m.beginningValue = m.beginningValue.add(valueOnHand);

        // In-period
        for (InventoryEvent ev : events) {
            if (!ev.getDate().isBefore(start) && !ev.getDate().isAfter(end)) {
                if (ev.getType() == InventoryEvent.Type.PURCHASE || ev.getType() == InventoryEvent.Type.ADJUSTMENT) {
                    if (ev.getQuantity() >= 0) {
                        Money addValue = ev.getUnitCost().multiply(ev.getQuantity());
                        m.purchasesQuantity += ev.getQuantity();
                        m.purchasesValue = m.purchasesValue.add(addValue);
                        valueOnHand = valueOnHand.add(addValue);
                        qtyOnHand += ev.getQuantity();
                        avgUnitCost = qtyOnHand == 0 ? Money.zero() : valueOnHand.divide(new BigDecimal(qtyOnHand));
                    } else {
                        int outQty = -ev.getQuantity();
                        Money cogs = avgUnitCost.multiply(outQty);
                        m.cogsValue = m.cogsValue.add(cogs);
                        m.salesQuantity += outQty; // negative adjustment treated as shrinkage like sale for COGS
                        valueOnHand = valueOnHand.subtract(cogs);
                        qtyOnHand -= outQty;
                        avgUnitCost = qtyOnHand == 0 ? Money.zero() : valueOnHand.divide(new BigDecimal(qtyOnHand));
                    }
                } else if (ev.getType() == InventoryEvent.Type.SALE) {
                    int outQty = ev.getQuantity();
                    Money cogs = avgUnitCost.multiply(outQty);
                    m.cogsValue = m.cogsValue.add(cogs);
                    m.salesQuantity += outQty;
                    valueOnHand = valueOnHand.subtract(cogs);
                    qtyOnHand -= outQty;
                    avgUnitCost = qtyOnHand == 0 ? Money.zero() : valueOnHand.divide(new BigDecimal(qtyOnHand));
                    // Sales revenue is not tracked here; it is handled in the ledger
                }
            }
        }

        m.endingQuantity += qtyOnHand;
        m.endingValue = m.endingValue.add(valueOnHand);
        return m;
    }

    public static class PeriodMetrics {
        public int beginningQuantity = 0;
        public Money beginningValue = Money.zero();
        public int purchasesQuantity = 0;
        public Money purchasesValue = Money.zero();
        public int salesQuantity = 0;
        public Money cogsValue = Money.zero();
        public int endingQuantity = 0;
        public Money endingValue = Money.zero();

        public PeriodMetrics add(PeriodMetrics other) {
            PeriodMetrics m = new PeriodMetrics();
            m.beginningQuantity = this.beginningQuantity + other.beginningQuantity;
            m.beginningValue = this.beginningValue.add(other.beginningValue);
            m.purchasesQuantity = this.purchasesQuantity + other.purchasesQuantity;
            m.purchasesValue = this.purchasesValue.add(other.purchasesValue);
            m.salesQuantity = this.salesQuantity + other.salesQuantity;
            m.cogsValue = this.cogsValue.add(other.cogsValue);
            m.endingQuantity = this.endingQuantity + other.endingQuantity;
            m.endingValue = this.endingValue.add(other.endingValue);
            return m;
        }
    }
}