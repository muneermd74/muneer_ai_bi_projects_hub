package com.example.finance.inventory;

import com.example.finance.util.Money;

import java.time.LocalDate;

public class InventoryEvent implements Comparable<InventoryEvent> {
    public enum Type { PURCHASE, SALE, ADJUSTMENT }

    private final LocalDate date;
    private final String sku;
    private final String itemName;
    private final Type type;
    private final int quantity; // positive for purchase, positive for sale quantity (outflow handled by type)
    private final Money unitCost; // for purchases/adjustments; for sale, this can be null
    private final Money saleUnitPrice; // for sales

    private InventoryEvent(LocalDate date, String sku, String itemName, Type type, int quantity, Money unitCost, Money saleUnitPrice) {
        this.date = date;
        this.sku = sku;
        this.itemName = itemName;
        this.type = type;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.saleUnitPrice = saleUnitPrice;
    }

    public static InventoryEvent purchase(LocalDate date, String sku, String itemName, int quantity, Money unitCost) {
        return new InventoryEvent(date, sku, itemName, Type.PURCHASE, quantity, unitCost, null);
    }

    public static InventoryEvent sale(LocalDate date, String sku, String itemName, int quantity, Money saleUnitPrice) {
        return new InventoryEvent(date, sku, itemName, Type.SALE, quantity, null, saleUnitPrice);
    }

    public static InventoryEvent adjust(LocalDate date, String sku, String itemName, int quantity, Money unitCost) {
        return new InventoryEvent(date, sku, itemName, Type.ADJUSTMENT, quantity, unitCost, null);
    }

    public LocalDate getDate() { return date; }
    public String getSku() { return sku; }
    public String getItemName() { return itemName; }
    public Type getType() { return type; }
    public int getQuantity() { return quantity; }
    public Money getUnitCost() { return unitCost; }
    public Money getSaleUnitPrice() { return saleUnitPrice; }

    @Override
    public int compareTo(InventoryEvent o) {
        int cmp = this.date.compareTo(o.date);
        if (cmp != 0) return cmp;
        return this.sku.compareTo(o.sku);
    }
}