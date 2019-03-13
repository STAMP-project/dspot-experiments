package com.vaadin.tests.elements.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxPopupTest extends MultiBrowserTest {
    private ComboBoxElement comboBoxElement;

    @Test
    public void comboBoxPopup_popupOpen_popupFetchedSuccessfully() {
        comboBoxElement.openPopup();
        Assert.assertNotNull(comboBoxElement.getSuggestionPopup());
    }

    @Test
    public void comboBoxPopup_popupClosed_popupFetchedSuccessfully() {
        Assert.assertNotNull(comboBoxElement.getSuggestionPopup());
    }
}

