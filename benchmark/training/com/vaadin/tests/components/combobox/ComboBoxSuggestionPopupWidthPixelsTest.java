package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class ComboBoxSuggestionPopupWidthPixelsTest extends MultiBrowserTest {
    @Test
    public void suggestionPopupFixedWidthTest() throws Exception {
        openTestURL();
        waitForElementVisible(By.className("pixels"));
        WebElement selectTextbox = $(ComboBoxElement.class).first().findElement(By.vaadin("#textbox"));
        selectTextbox.click();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();
        int width = popup.getSize().getWidth();
        Assert.assertTrue((width == 300));
    }
}

