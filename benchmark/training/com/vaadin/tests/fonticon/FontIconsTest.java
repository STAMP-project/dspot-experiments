package com.vaadin.tests.fonticon;


import Keys.ARROW_DOWN;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class FontIconsTest extends MultiBrowserTest {
    @Test
    public void checkScreenshot() throws IOException {
        openTestURL();
        compareScreen("allVaadinIcons");
    }

    @Test
    public void comboBoxItemIconsOnKeyboardNavigation() throws Exception {
        openTestURL();
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        // No initial value.
        Assert.assertEquals("", comboBox.getText());
        // Navigate to the first item with keyboard navigation.
        comboBox.sendKeys(400, ARROW_DOWN, ARROW_DOWN);
        // Value must be "One" without any extra characters.
        // See ticket #14660
        Assert.assertEquals("One", comboBox.getText());
        // Check also the second item.
        comboBox.sendKeys(ARROW_DOWN);
        Assert.assertEquals("Two", comboBox.getText());
    }
}

