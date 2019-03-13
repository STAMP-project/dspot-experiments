package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class ComboBoxPopupWhenBodyScrollsTest extends MultiBrowserTest {
    @Test
    public void popupBelow() {
        openTestURL();
        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.openPopup();
        WebElement popup = $(ComboBoxElement.class).first().getSuggestionPopup();
        int comboboxTop = combobox.getLocation().getY();
        int popupTop = popup.getLocation().getY();
        Assert.assertTrue("Popup should be below combobox", (popupTop > comboboxTop));
    }
}

