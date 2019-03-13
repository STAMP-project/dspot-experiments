package com.vaadin.tests.components.menubar;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class MenuItemStyleRemovedTest extends MultiBrowserTest {
    @Test
    public void testCustomStyleShouldStayAfterMenuSelect() {
        openTestURL();
        $(ButtonElement.class).caption("Add styles").first().click();
        MenuBarElement menu = $(MenuBarElement.class).first();
        List<WebElement> elements = menu.findElements(By.className("custom-menu-item"));
        Assert.assertEquals(2, elements.size());
        menu.clickItem("first");
        menu.clickItem("second");
        elements = menu.findElements(By.className("custom-menu-item"));
        Assert.assertEquals(2, elements.size());
    }
}

