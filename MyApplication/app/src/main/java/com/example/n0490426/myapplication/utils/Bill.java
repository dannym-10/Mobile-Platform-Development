package com.example.n0490426.myapplication.utils;

/**
 * Created by Danny on 14/03/2018.
 */

public class Bill {
    protected String billName;
    protected int dueDate;
    protected int daysBefore;
    protected int amount;
    protected boolean splitBill;

    public String getBillName() {
        return billName;
    }

    public void setBillName(String value) {
        this.billName = value;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int value) {
        this.dueDate = value;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(int value) {
        this.daysBefore = value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int value) {
        this.amount = value;
    }

    public boolean getSplitBill() { return splitBill; }

    public void setSplitBill(boolean value) { this.splitBill = value; }
}
