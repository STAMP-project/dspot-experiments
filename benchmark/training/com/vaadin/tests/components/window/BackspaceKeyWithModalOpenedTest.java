package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;


public class BackspaceKeyWithModalOpenedTest extends MultiBrowserTest {
    /**
     * Tests that backspace in textfield does work
     */
    @Test
    public void testWithFocusOnInput() throws Exception {
        TextFieldElement textField = getTextField();
        // Try to delete characters in a text field.
        textField.sendKeys("textt");
        textField.sendKeys(Keys.BACK_SPACE);
        Assert.assertEquals("text", textField.getValue());
        checkButtonsCount();
    }

    /**
     * Tests that backspace action outside textfield is prevented
     */
    @Test
    public void testWithFocusOnModal() throws Exception {
        // Try to send back actions to the browser.
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(Keys.BACK_SPACE).perform();
        checkButtonsCount();
    }
}

