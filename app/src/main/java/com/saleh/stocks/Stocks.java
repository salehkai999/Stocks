package com.saleh.stocks;

import java.io.Serializable;
import java.util.Objects;

public class Stocks implements Comparable<Stocks>, Serializable {
    private String symbol;
    private String companyName;
    private double change=0.0;
    private double changePercent=0.0;
    private double price=0.0;


    public Stocks() {
    }

    public Stocks(String symbol, String companyName, double change, double changePercent, double price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.change = change;
        this.changePercent = changePercent;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stocks stocks = (Stocks) o;
        return  Objects.equals(symbol, stocks.symbol);

    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, companyName);
    }

    @Override
    public int compareTo(Stocks o) {
        return this.symbol.compareTo(o.getSymbol());
    }

    @Override
    public String toString() {
        return "Stocks{" +
                "symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", change=" + change +
                ", changePercent=" + changePercent +
                ", price=" + price +
                '}';
    }
}
