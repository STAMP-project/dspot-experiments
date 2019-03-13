package com.vaadin.tests.serialization;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class LegacySerializerUITest extends SingleBrowserTest {
    @Test
    public void testInfinity() {
        openTestURL();
        WebElement html = findElement(By.className("gwt-HTML"));
        Assert.assertEquals("doubleInfinity: Infinity", html.getText());
        // Can't send infinity back, never have been able to
        Assert.assertEquals("1. doubleInfinity: null", getLogRow(0));
    }
}

