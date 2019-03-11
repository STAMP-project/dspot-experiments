package com.querydsl.sql.types;


import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class LocaleTypeTest {
    @Test
    public void lang() {
        Locale l = new Locale("en");
        Assert.assertEquals(l, LocaleType.toLocale(l.toString()));
    }

    @Test
    public void lang_country() {
        Locale l = new Locale("en", "US");
        Assert.assertEquals(l, LocaleType.toLocale(l.toString()));
    }

    @Test
    public void lang_country_variant() {
        Locale l = new Locale("en", "US", "X");
        Assert.assertEquals(l, LocaleType.toLocale(l.toString()));
    }
}

