package com.vaadin.test.osgi;


import com.vaadin.testbench.TestBenchTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class KarafIntegrationIT extends TestBenchTestCase {
    private static final String URL_PREFIX = "http://localhost:8181/";

    private static final String APP1_URL = (KarafIntegrationIT.URL_PREFIX) + "myapp1";

    private static final String APP2_URL = (KarafIntegrationIT.URL_PREFIX) + "myapp2";

    @Test
    public void testApp1() {
        runBasicTest(KarafIntegrationIT.APP1_URL, "bar");
        // App theme should make a button pink
        WebElement element = getDriver().findElement(By.className("v-button"));
        String buttonColor = element.getCssValue("color");
        Assert.assertEquals("rgba(255, 128, 128, 1)", buttonColor);
    }

    @Test
    public void testApp2() {
        runBasicTest(KarafIntegrationIT.APP2_URL, "foo");
    }
}

