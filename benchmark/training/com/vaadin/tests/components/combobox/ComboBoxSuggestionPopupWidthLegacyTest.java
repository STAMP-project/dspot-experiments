package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class ComboBoxSuggestionPopupWidthLegacyTest extends MultiBrowserTest {
    @Test
    public void suggestionPopupLegacyWidthTest() throws Exception {
        openTestURL();
        waitForElementVisible(By.className("legacy"));
        WebElement selectTextbox = $(ComboBoxElement.class).first().findElement(By.vaadin("#textbox"));
        selectTextbox.click();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();
        int width = popup.getSize().getWidth();
        AbstractTB3Test.assertGreater("Legacy mode popup should be quite wide", width, 600);
        AbstractTB3Test.assertLessThan("Even legacy mode popup should not be over 1000px wide with the set item captions ", width, 1000);
    }
}

