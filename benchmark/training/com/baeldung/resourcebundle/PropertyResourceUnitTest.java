package com.baeldung.resourcebundle;


import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;


public class PropertyResourceUnitTest {
    @Test
    public void givenLocaleUsAsDefualt_whenGetBundleForLocalePlPl_thenItShouldContain3ButtonsAnd1Label() {
        Locale.setDefault(Locale.US);
        ResourceBundle bundle = ResourceBundle.getBundle("resourcebundle.resource", new Locale("pl", "PL"));
        Assert.assertTrue(bundle.keySet().containsAll(Arrays.asList("backButton", "helloLabel", "cancelButton", "continueButton", "helloLabelNoEncoding")));
    }

    @Test
    public void givenLocaleUsAsDefualt_whenGetBundleForLocaleFrFr_thenItShouldContainKeys1To3AndKey4() {
        Locale.setDefault(Locale.US);
        ResourceBundle bundle = ResourceBundle.getBundle("resourcebundle.resource", new Locale("fr", "FR"));
        Assert.assertTrue(bundle.keySet().containsAll(Arrays.asList("deleteButton", "helloLabel", "cancelButton", "continueButton")));
    }

    @Test
    public void givenLocaleChinaAsDefualt_whenGetBundleForLocaleFrFr_thenItShouldOnlyContainKeys1To3() {
        Locale.setDefault(Locale.CHINA);
        ResourceBundle bundle = ResourceBundle.getBundle("resourcebundle.resource", new Locale("fr", "FR"));
        Assert.assertTrue(bundle.keySet().containsAll(Arrays.asList("continueButton", "helloLabel", "cancelButton")));
    }

    @Test
    public void givenLocaleChinaAsDefualt_whenGetBundleForLocaleFrFrAndExampleControl_thenItShouldOnlyContainKey5() {
        Locale.setDefault(Locale.CHINA);
        ResourceBundle bundle = ResourceBundle.getBundle("resourcebundle.resource", new Locale("fr", "FR"), new ExampleControl());
        Assert.assertTrue(bundle.keySet().containsAll(Arrays.asList("backButton", "helloLabel")));
    }

    @Test
    public void givenValuesDifferentlyEncoded_whenGetBundleForLocalePlPl_thenItShouldContain3ButtonsAnd1Label() {
        ResourceBundle bundle = ResourceBundle.getBundle("resourcebundle.resource", new Locale("pl", "PL"));
        Assert.assertEquals(bundle.getString("helloLabel"), "cze??");
        Assert.assertEquals(bundle.getString("helloLabelNoEncoding"), "cze\u00c5\u009b\u00c4\u0087");
    }
}

