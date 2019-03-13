package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;


/**
 * Test for issue #12726, IE's make text selection when sub windows are
 * dragged(moved).
 *
 * @author Vaadin Ltd
 */
public class SubWindowsTextSelectionTest extends MultiBrowserTest {
    @Test
    public void verifyNoTextSelectionOnMove() throws Exception {
        openTestURL();
        WebElement element = driver.findElement(By.className("v-window-outerheader"));
        Point location = element.getLocation();
        element.click();
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(element).perform();
        sleep(100);
        // move pointer bit right from the caption text
        moveByOffset(10, 0).release().perform();
        String selection = ((JavascriptExecutor) (getDriver())).executeScript("return document.getSelection().toString();").toString();
        Assert.assertTrue(("Text selection was not empty:" + selection), selection.isEmpty());
        // Verify also that window was really moved
        Point location2 = element.getLocation();
        Assert.assertEquals(((location.getX()) + (4 * 10)), location2.getX());
    }
}

