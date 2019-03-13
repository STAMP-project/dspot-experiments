package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class InvalidViewportTest extends SingleBrowserTest {
    @Test
    public void testInvalidViewport() {
        openTestURL();
        WebElement heading = findElement(By.tagName("h2"));
        Assert.assertEquals("HTTP ERROR 500", heading.getText());
    }
}

