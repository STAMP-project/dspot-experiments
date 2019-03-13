package com.vaadin.tests.components.combobox;


import Keys.TAB;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class ComboBoxFilterClearTest extends MultiBrowserTest {
    ComboBoxElement comboBox;

    @Test
    public void testFilterCleared() {
        openTestURL();
        comboBox = $(ComboBoxElement.class).first();
        ButtonElement toggleVisibility = $(ButtonElement.class).id("toggleVisibility");
        ButtonElement setNull = $(ButtonElement.class).id("setNull");
        sendKeysToInput("b0", TAB);
        Assert.assertEquals("b0", comboBox.getText());
        toggleVisibility.click();
        waitForElementNotPresent(By.className("v-filterselect"));
        setNull.click();
        toggleVisibility.click();
        waitForElementPresent(By.className("v-filterselect"));
        comboBox = $(ComboBoxElement.class).first();
        WebElement suggestionPopup = comboBox.getSuggestionPopup();
        List<WebElement> menuItems = suggestionPopup.findElements(By.className("gwt-MenuItem"));
        Assert.assertEquals("a0", menuItems.get(1).getText());
    }
}

