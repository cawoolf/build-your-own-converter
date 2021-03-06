package com.rayadev.byoc.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName= "currency_table")
public class Currency {


    @PrimaryKey
    @NonNull
    public String currencyPair;

    public Double currencyValue;

    public Currency(String currencyPair, Double currencyValue) {

        this.currencyPair = currencyPair;
        this.currencyValue = currencyValue;

    }

    @NonNull
    public String getCurrencyPair() {
        return currencyPair;
    }

    public Double getCurrencyValue() {
        return currencyValue;
    }


}
