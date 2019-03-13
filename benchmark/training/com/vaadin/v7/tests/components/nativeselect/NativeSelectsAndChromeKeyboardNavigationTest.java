package com.vaadin.v7.tests.components.nativeselect;


import Keys.ARROW_DOWN;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class NativeSelectsAndChromeKeyboardNavigationTest extends MultiBrowserTest {
    @Test
    public void testValueChangeListenerWithKeyboardNavigation() throws InterruptedException {
        setDebug(true);
        openTestURL();
        Thread.sleep(1000);
        menu("Component");
        menuSub("Listeners");
        menuSub("Value change listener");
        getDriver().findElement(By.tagName("body")).click();
        WebElement select = getDriver().findElement(By.tagName("select"));
        select.sendKeys(ARROW_DOWN);
        select.sendKeys(ARROW_DOWN);
        select.sendKeys(ARROW_DOWN);
        String bodytext = getDriver().findElement(By.tagName("body")).getText();
        Assert.assertTrue(bodytext.contains("new value: 'Item 1'"));
        Assert.assertTrue(bodytext.contains("new value: 'Item 2'"));
        Assert.assertTrue(bodytext.contains("new value: 'Item 3'"));
    }
}

