package com.vaadin.tests.components.menubar;


import Keys.ARROW_RIGHT;
import Keys.DOWN;
import Keys.SPACE;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class MenuBarSubmenusClosingValoTest extends MultiBrowserTest {
    @Test
    public void testEnableParentLayoutControlByKeyboard() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).get(0);
        menu.focus();
        menu.sendKeys(SPACE);
        menu.sendKeys(DOWN);
        waitForElementPresent(By.className("v-menubar-popup"));
        menu.sendKeys(ARROW_RIGHT);
        menu.sendKeys(ARROW_RIGHT);
        int count = driver.findElements(By.className("v-menubar-popup")).size();
        Assert.assertTrue("The count of open popups should be one", (count == 1));
    }

    @Test
    public void testEnableParentLayoutControlByMouse() throws InterruptedException {
        openTestURL();
        List<WebElement> menuItemList = driver.findElements(By.className("v-menubar-menuitem"));
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(menuItemList.get(1)).click().perform();
        waitForElementPresent(By.className("v-menubar-popup"));
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(menuItemList.get(1)).perform();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(menuItemList.get(2)).perform();
        waitForElementPresent(By.className("v-menubar-popup"));
        int count = driver.findElements(By.className("v-menubar-popup")).size();
        Assert.assertEquals("The count of open popups should be one", 1, count);
    }
}

