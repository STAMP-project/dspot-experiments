package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class StaticViewportTest extends SingleBrowserTest {
    @Test
    public void testStaticViewport() {
        openTestURL();
        WebElement viewportElement = findElement(By.cssSelector("meta[name=viewport]"));
        Assert.assertEquals("myViewport", viewportElement.getAttribute("content"));
    }
}

