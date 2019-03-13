package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;


public class ComboBoxPopupPositionEmbeddedInDivTest extends MultiBrowserTest {
    @Test
    public void popupBelow() {
        driver.get(((StringUtils.strip(getBaseURL(), "/")) + "/statictestfiles/ComboBoxEmbeddingHtmlPage.html"));
        // Chrome requires document.scrollTop (<body>)
        // Firefox + IE wants document.documentElement.scrollTop (<html>)
        executeScript("document.body.scrollTop=200;document.documentElement.scrollTop=200;document.body.scrollLeft=50;document.documentElement.scrollLeft=50;");
        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.openPopup();
        WebElement popup = $(ComboBoxElement.class).first().getSuggestionPopup();
        Point comboboxLocation = combobox.getLocation();
        Point popupLocation = popup.getLocation();
        Assert.assertTrue("Popup should be below combobox", ((popupLocation.getY()) > (comboboxLocation.getY())));
        Assert.assertTrue("Popup should be left aligned with the combobox", ((popupLocation.getX()) == (comboboxLocation.getX())));
    }
}

