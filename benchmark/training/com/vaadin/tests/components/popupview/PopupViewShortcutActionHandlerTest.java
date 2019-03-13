package com.vaadin.tests.components.popupview;


import Keys.ENTER;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * Check availability of shortcut action listener in the popup view.
 *
 * @author Vaadin Ltd
 */
public class PopupViewShortcutActionHandlerTest extends MultiBrowserTest {
    @Test
    public void testShortcutHandling() {
        openTestURL();
        getDriver().findElement(By.className("v-popupview")).click();
        WebElement textField = getDriver().findElement(By.className("v-textfield"));
        textField.sendKeys("a", ENTER);
        Assert.assertTrue(("Unable to find label component which is the result of" + " shortcut action handling."), isElementPresent(By.className("shortcut-result")));
    }
}

