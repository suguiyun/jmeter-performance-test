package com.cryptoex.response;

public class BusinessOrder {
    private String amount;
    private String type;
    private String price;
    private String symbol;

    public BusinessOrder(String amount, String type, String price, String symbol) {
        this.amount = amount;
        this.type = type;
        this.price = price;
        this.symbol = symbol;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
