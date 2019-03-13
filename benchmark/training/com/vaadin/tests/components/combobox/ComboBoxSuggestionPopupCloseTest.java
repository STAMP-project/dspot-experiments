package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class ComboBoxSuggestionPopupCloseTest extends MultiBrowserTest {
    private WebElement selectTextbox;

    @Test
    public void closeSuggestionPopupTest() throws Exception {
        openTestURL();
        waitForElementVisible(By.className("v-filterselect"));
        selectTextbox = $(ComboBoxElement.class).first().findElement(By.vaadin("#textbox"));
        selectTextbox.click();
        // open popup and select first element
        sendKeys(new Keys[]{ Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ENTER });
        // open popup and hit enter to close it
        sendKeys(new Keys[]{ Keys.ARROW_DOWN, Keys.ENTER });
        Assert.assertFalse(isElementPresent(By.className("v-filterselect-suggestmenu")));
    }
}

