package com.vaadin.tests.components.menubar;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class MenuItemStyleAddTest extends MultiBrowserTest {
    @Test
    public void testCustomStyleShouldStayAfterMenuSelect() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).first();
        List<WebElement> elements = menu.findElements(By.className("v-menubar-menuitem"));
        WebElement more = elements.get(0);
        Assert.assertEquals(true, more.getAttribute("class").replaceAll("v-menubar-menuitem-styleNameMore", "").contains("styleNameMore"));
        Assert.assertEquals(2, elements.size());
        menu.clickItem("Drinks");
        WebElement popup = getDriver().findElement(By.className("v-menubar-popup"));
        List<WebElement> popupElCon = popup.findElements(By.className("styleTest"));
        Assert.assertEquals(1, popupElCon.size());
        Assert.assertEquals(true, popupElCon.get(0).getAttribute("class").replaceAll("v-menubar-menuitem-styleTest", "").contains("styleTest"));
    }
}

