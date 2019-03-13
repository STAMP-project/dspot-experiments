package com.vaadin.tests.components;


import Keys.LEFT_ALT;
import Keys.LEFT_CONTROL;
import Keys.LEFT_SHIFT;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


public class FocusShortcutsTest extends SingleBrowserTest {
    @Test
    public void triggerShortCuts() {
        openTestURL();
        WebElement body = findElement(By.xpath("//body"));
        Actions actions = new Actions(getDriver());
        actions.keyDown(body, LEFT_ALT).sendKeys("a").keyUp(LEFT_ALT).build().perform();
        Assert.assertEquals("1. Alt+A", getLogRow(0));
        body.click();
        actions = new Actions(getDriver());
        actions.keyDown(body, LEFT_ALT).sendKeys("n").keyUp(LEFT_ALT).build().perform();
        Assert.assertEquals("2. Alt+N", getLogRow(0));
        body.click();
        actions = new Actions(getDriver());
        actions.keyDown(body, LEFT_CONTROL).keyDown(body, LEFT_SHIFT).sendKeys("d").keyUp(LEFT_CONTROL).keyUp(LEFT_SHIFT).build().perform();
        Assert.assertEquals("3. Ctrl+Shift+D", getLogRow(0));
    }
}

