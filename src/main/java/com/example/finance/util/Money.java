package com.example.finance.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;
    public static final MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL64;

    private final BigDecimal amount;

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public static Money of(long value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money of(String value) {
        return new Money(new BigDecimal(value));
    }

    public Money(BigDecimal amount) {
        this.amount = amount.setScale(2, DEFAULT_ROUNDING);
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount, DEFAULT_MATH_CONTEXT));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount, DEFAULT_MATH_CONTEXT));
    }

    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier), DEFAULT_MATH_CONTEXT));
    }

    public Money multiply(double multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier), DEFAULT_MATH_CONTEXT));
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier, DEFAULT_MATH_CONTEXT));
    }

    public Money divide(int divisor) {
        return new Money(this.amount.divide(BigDecimal.valueOf(divisor), 2, DEFAULT_ROUNDING));
    }

    public Money divide(BigDecimal divisor) {
        return new Money(this.amount.divide(divisor, 2, DEFAULT_ROUNDING));
    }

    public Money negate() {
        return new Money(this.amount.negate(DEFAULT_MATH_CONTEXT));
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }

    @Override
    public int compareTo(Money o) {
        return this.amount.compareTo(o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}