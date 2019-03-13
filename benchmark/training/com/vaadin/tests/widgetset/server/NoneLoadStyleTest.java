package com.vaadin.tests.widgetset.server;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class NoneLoadStyleTest extends SingleBrowserTest {
    @Test
    public void connectorNotLoaded() {
        openTestURL();
        String componentText = findElement(By.id("component")).getText();
        Assert.assertTrue(componentText.contains("does not contain"));
    }
}

