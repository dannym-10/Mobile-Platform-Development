package com.example.n0490426.myapplication.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 21/03/2018.
 */

public class EditedBill implements Serializable{
    protected String editBill;

    public String getEditBills() {
        return editBill;
    }

    public void setEditBill(String theBill) {
            editBill = theBill;
    }
}
