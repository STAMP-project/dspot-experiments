package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ToolTipInWindowTest extends MultiBrowserTest {
    @Test
    public void testToolTipInHeader() throws Exception {
        openTestURL();
        WebElement header = driver.findElement(By.className("v-window-outerheader"));
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(driver.findElement(By.className("v-ui")), 0, 0).perform();
        sleep(500);
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(header).perform();
        sleep(1100);
        WebElement ttip = findElement(By.className("v-tooltip"));
        Assert.assertNotNull(ttip);
        Assert.assertEquals("Tooltip", ttip.getText());
    }

    @Test
    public void testToolTipInContent() throws Exception {
        openTestURL();
        WebElement header = driver.findElement(By.className("v-window-contents"));
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(driver.findElement(By.className("v-ui")), 0, 300).perform();
        sleep(500);
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(header).perform();
        sleep(1000);
        WebElement ttip = findElement(By.className("v-tooltip"));
        Assert.assertNotNull(ttip);
        Assert.assertEquals("Tooltip", ttip.getText());
    }
}

