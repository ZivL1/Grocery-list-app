package com.example.grocerylistapp;

public class Grocery {
    private String name;
    private  int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ", amount: "+ amount;
    }
}
