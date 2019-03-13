package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.util.Currency;
import junit.framework.TestCase;


public class CurrencyTest3 extends TestCase {
    public static class Money {
        public Currency currency;

        public BigDecimal amount;

        @Override
        public String toString() {
            return ((("Money{currency=" + (currency)) + ", amount=") + (amount)) + '}';
        }
    }

    public void testJson() throws Exception {
        CurrencyTest3.Money money = new CurrencyTest3.Money();
        money.currency = Currency.getInstance("CNY");
        money.amount = new BigDecimal("10.03");
        String json = JSON.toJSONString(money);
        System.out.println(("json = " + json));
        CurrencyTest3.Money moneyBack = JSON.parseObject(json, CurrencyTest3.Money.class);
        System.out.println(("money = " + moneyBack));
        JSONObject jsonObject = JSON.parseObject(json);
        CurrencyTest3.Money moneyCast = JSON.toJavaObject(jsonObject, CurrencyTest3.Money.class);
        System.out.printf(("money = " + moneyCast));
    }
}

