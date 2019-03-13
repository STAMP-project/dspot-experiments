package com.vaadin.tests.components.menubar;


import Keys.ARROW_RIGHT;
import Keys.ENTER;
import Keys.SPACE;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class SpaceMenuBarNavigationTest extends MultiBrowserTest {
    @Test
    public void testEnableParentLayout() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).get(0);
        menu.focus();
        menu.sendKeys(ARROW_RIGHT);
        menu.sendKeys(ENTER);
        List<WebElement> captions = driver.findElements(By.className("v-menubar-menuitem-caption"));
        boolean found = false;
        for (WebElement caption : captions) {
            if ("subitem".equals(caption.getText())) {
                found = true;
            }
        }
        Assert.assertTrue("Sub menu is not opened on ENTER key", found);
        menu.sendKeys(SPACE);
        Assert.assertTrue("No result of action triggered by SPACE key", isElementPresent(By.className("action-result")));
    }
}

