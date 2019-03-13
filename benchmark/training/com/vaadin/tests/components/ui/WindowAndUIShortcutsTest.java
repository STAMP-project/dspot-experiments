package com.vaadin.tests.components.ui;


import Keys.ESCAPE;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class WindowAndUIShortcutsTest extends SingleBrowserTest {
    @Test
    public void windowShortcutShouldNotReachUI() {
        openTestURL();
        $(ButtonElement.class).caption("Show page").first().click();
        $(ButtonElement.class).caption("Open dialog window").first().click();
        $(ButtonElement.class).first().sendKeys(ESCAPE);
        // Window should have been closed
        Assert.assertTrue($(WindowElement.class).all().isEmpty());
        // "Close page" should not have been clicked
        Assert.assertTrue($(ButtonElement.class).caption("Close page").exists());
    }

    @Test
    public void modalCurtainShouldNotTriggerShortcuts() {
        openTestURL();
        $(ButtonElement.class).caption("Show page").first().click();
        $(ButtonElement.class).caption("Open dialog window").first().click();
        WebElement curtain = findElement(By.className("v-window-modalitycurtain"));
        // Click in the curtain next to the window and send escape
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(curtain, (($(WindowElement.class).first().getSize().getWidth()) * 2), 0).click().sendKeys(ESCAPE).perform();
        // "Close page" should not have been clicked
        Assert.assertTrue($(ButtonElement.class).caption("Close page").exists());
    }
}

