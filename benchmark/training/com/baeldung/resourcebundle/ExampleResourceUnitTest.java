package com.baeldung.resourcebundle;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;


public class ExampleResourceUnitTest {
    @Test
    public void whenGetBundleExampleResourceForLocalePlPl_thenItShouldInheritPropertiesGreetingAndLanguage() {
        Locale plLocale = new Locale("pl", "PL");
        ResourceBundle exampleBundle = ResourceBundle.getBundle("com.baeldung.resourcebundle.ExampleResource", plLocale);
        Assert.assertTrue(exampleBundle.keySet().containsAll(Arrays.asList("toUsdRate", "cities", "greeting", "currency", "language")));
        Assert.assertEquals(exampleBundle.getString("greeting"), "cze??");
        Assert.assertEquals(exampleBundle.getObject("toUsdRate"), new BigDecimal("3.401"));
        Assert.assertArrayEquals(exampleBundle.getStringArray("cities"), new String[]{ "Warsaw", "Cracow" });
    }

    @Test
    public void whenGetBundleExampleResourceForLocaleUs_thenItShouldContainOnlyGreeting() {
        Locale usLocale = Locale.US;
        ResourceBundle exampleBundle = ResourceBundle.getBundle("com.baeldung.resourcebundle.ExampleResource", usLocale);
        Assert.assertFalse(exampleBundle.keySet().containsAll(Arrays.asList("toUsdRate", "cities", "currency", "language")));
        Assert.assertTrue(exampleBundle.keySet().containsAll(Arrays.asList("greeting")));
    }
}

