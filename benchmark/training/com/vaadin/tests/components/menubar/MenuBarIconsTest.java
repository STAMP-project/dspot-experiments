package com.vaadin.tests.components.menubar;


import FontAwesome.AMBULANCE;
import FontAwesome.ANGELLIST;
import FontAwesome.MAIL_REPLY_ALL;
import FontAwesome.MOTORCYCLE;
import FontAwesome.SUBWAY;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class MenuBarIconsTest extends SingleBrowserTest {
    @Test
    public void fontIconsRendered() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).id("fontIcon");
        WebElement moreItem = menu.findElements(By.className("v-menubar-menuitem")).get(3);
        assertFontIcon(MAIL_REPLY_ALL, menu.findElement(By.vaadin("#Main")));
        WebElement hasSubElement = menu.findElement(By.vaadin("#Has sub"));
        assertFontIcon(SUBWAY, hasSubElement);
        assertFontIcon(ANGELLIST, menu.findElement(By.vaadin("#Filler 0")));
        hasSubElement.click();
        assertFontIcon(AMBULANCE, hasSubElement.findElement(By.vaadin("#Sub item")));
        // Close sub menu
        hasSubElement.click();
        assertFontIcon(MOTORCYCLE, moreItem);
        moreItem.click();
        WebElement filler5 = moreItem.findElement(By.vaadin("#Filler 5"));
        assertFontIcon(ANGELLIST, filler5);
    }

    @Test
    public void imageIconsRendered() {
        Assume.assumeFalse("PhantomJS uses different font which shifts index of the 'More' item", BrowserUtil.isPhantomJS(getDesiredCapabilities()));
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).id("image");
        WebElement moreItem = menu.findElements(By.className("v-menubar-menuitem")).get(4);
        String image = "/tests-valo/img/email-reply.png";
        assertImage(image, menu.findElement(By.vaadin("#Main")));
        WebElement hasSubElement = menu.findElement(By.vaadin("#Has sub"));
        assertImage(image, hasSubElement);
        assertImage(image, menu.findElement(By.vaadin("#Filler 0")));
        hasSubElement.click();
        assertImage(image, hasSubElement.findElement(By.vaadin("#Sub item")));
        // Close sub menu
        hasSubElement.click();
        assertImage(image, moreItem);
        moreItem.click();
        waitForElementPresent(By.className("v-menubar-submenu"));
        WebElement filler5 = moreItem.findElement(By.vaadin("#Filler 5"));
        assertImage(image, filler5);
    }
}

