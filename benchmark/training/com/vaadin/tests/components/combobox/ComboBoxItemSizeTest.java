package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class ComboBoxItemSizeTest extends SingleBrowserTest {
    ComboBoxElement comboBoxElement;

    @Test
    public void comboBoxItemSizeDisplayCorrectly() {
        openTestURL();
        comboBoxElement = $(ComboBoxElement.class).id("combobox");
        // initial item size include the empty option
        assertItemSizeInPopup(7);
        comboBoxElement.clear();
        sendKeysToInput("black");
        assertItemSizeInPopup(8);
    }
}

