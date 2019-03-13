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
public class ComboBoxSuggestionPopupWidthPercentageTest extends MultiBrowserTest {
    @Test
    public void suggestionPopupPersentageWidthTest() throws Exception {
        openTestURL();
        waitForElementVisible(By.className("percentage"));
        WebElement selectTextbox = $(ComboBoxElement.class).first().findElement(By.vaadin("#textbox"));
        selectTextbox.click();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();
        int width = popup.getSize().getWidth();
        Assert.assertTrue((width == 400));
    }
}

