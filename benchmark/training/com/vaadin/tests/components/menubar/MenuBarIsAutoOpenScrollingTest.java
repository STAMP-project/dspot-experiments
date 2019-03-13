package com.vaadin.tests.components.menubar;


import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class MenuBarIsAutoOpenScrollingTest extends MultiBrowserTest {
    @SuppressWarnings("deprecation")
    @Test
    public void testIsAutoOpenSubmenuScrolling() {
        openTestURL();
        Actions actions = new Actions(driver);
        MenuBarElement menu = $(MenuBarElement.class).get(0);
        actions.moveToElement(menu).perform();
        waitForElementPresent(By.className("v-menubar-popup"));
        WebElement subMenuPopup = driver.findElement(By.className("v-menubar-popup"));
        // here we have to use pause() because LazyCloser in VMenuBar auto
        // closes submenus popup in 750 ms.
        actions.moveToElement(subMenuPopup, ((subMenuPopup.getSize().width) / 2), 100).clickAndHold().pause(1000).moveByOffset(0, 200).release().perform();
        // subMenuPopup should still be presented
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("v-menubar-popup")));
        actions.moveByOffset(100, 0).perform();
        // subMenuPopup should disappear
        waitUntil(ExpectedConditions.not(ExpectedConditions.invisibilityOfElementLocated(By.className("v-menubar-popup"))));
    }
}

