package com.baeldung.string.interview;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class LocaleUnitTest {
    @Test
    public void whenUsingLocal_thenCorrectResultsForDifferentLocale() {
        Locale usLocale = Locale.US;
        BigDecimal number = new BigDecimal(102300.456);
        NumberFormat usNumberFormat = NumberFormat.getCurrencyInstance(usLocale);
        Assert.assertEquals(usNumberFormat.format(number), "$102,300.46");
    }
}

