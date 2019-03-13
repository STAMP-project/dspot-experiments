package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;


public class WindowMoveListenerTest extends MultiBrowserTest {
    @Test
    public void testWindowRepositioning() throws Exception {
        openTestURL();
        final WebElement window = getDriver().findElement(By.id("testwindow"));
        WebElement button = getDriver().findElement(By.id("testbutton"));
        // I'd loved to use the header, but that doesn't work. Footer works
        // fine, though :)
        WebElement windowFooter = getDriver().findElement(By.className("v-window-footer"));
        final Point winPos = window.getLocation();
        // move window
        Action a = new org.openqa.selenium.interactions.Actions(driver).clickAndHold(windowFooter).moveByOffset(100, 100).release().build();
        a.perform();
        Assert.assertNotEquals("Window was not dragged correctly.", winPos.x, window.getLocation().x);
        Assert.assertNotEquals("Window was not dragged correctly.", winPos.y, window.getLocation().y);
        // re-set window
        button.click();
        waitUntilWindowHasReseted(window, winPos);
    }
}

