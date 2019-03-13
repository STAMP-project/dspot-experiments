package com.vaadin.tests.application;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class InitiallyDeferredLoadingTest extends SingleBrowserTest {
    @Test
    public void testInitiallyDeferredComponent() {
        openTestURL();
        WebElement deferredComponent = findElement(By.id("deferred"));
        Assert.assertEquals("DeferredConnector", deferredComponent.getText());
    }
}

