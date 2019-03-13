package com.vaadin.tests.components.combobox;


import Keys.ARROW_DOWN;
import Keys.ENTER;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class ComboBoxVaadinIconsTest extends MultiBrowserTest {
    @Test
    public void testComboBoxIconRendering() throws IOException {
        openTestURL();
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        comboBox.openPopup();
        compareScreen(comboBox.getSuggestionPopup(), "popup");
        comboBox.sendKeys(ARROW_DOWN, ARROW_DOWN, ENTER);
        compareScreen(comboBox, "paperplane");
    }
}

