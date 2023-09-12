package com.unitech.api.service;

public interface MoneyService {

    boolean transfer(String fromPin, String toPin, double amount) throws Exception;

    double getCurrencyRate(String fromCurrency, String toCurrency);

}
