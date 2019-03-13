package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxItemAddingWithFocusListenerTest extends MultiBrowserTest {
    @Test
    public void testPopupViewContainsAddedItem() {
        openTestURL();
        ComboBoxElement cBox = $(ComboBoxElement.class).first();
        ButtonElement focusTarget = $(ButtonElement.class).first();
        cBox.openPopup();
        int i = 0;
        while (i < 3) {
            Assert.assertTrue("No item added on focus", cBox.getPopupSuggestions().contains(("Focus" + (i++))));
            focusTarget.focus();
            focus();
        } 
        Assert.assertTrue("No item added on focus", cBox.getPopupSuggestions().contains(("Focus" + i)));
    }
}

