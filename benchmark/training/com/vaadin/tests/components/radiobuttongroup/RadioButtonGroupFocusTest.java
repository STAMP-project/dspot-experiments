package com.vaadin.tests.components.radiobuttongroup;


import Keys.TAB;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.components.FocusTest;
import org.junit.Assert;
import org.junit.Test;


public class RadioButtonGroupFocusTest extends FocusTest {
    @Test
    public void focusOnInit() {
        openTestURL();
        RadioButtonGroupElement radioButtonGroup = $(RadioButtonGroupElement.class).first();
        Assert.assertTrue(isFocusInsideElement(radioButtonGroup));
    }

    @Test
    public void moveFocusAfterClick() {
        openTestURL();
        $(ButtonElement.class).first().click();
        RadioButtonGroupElement radioButtonGroup2 = $(RadioButtonGroupElement.class).last();
        Assert.assertTrue(isFocusInsideElement(radioButtonGroup2));
    }

    @Test
    public void focusDoesNotGoIntoWrapperElement() {
        openTestURL();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB).perform();
        Assert.assertTrue("Focus not in the second radio button group.", isFocusInsideElement($(RadioButtonGroupElement.class).last()));
        Assert.assertEquals("Focus should not be in the wrapping div.", "input", getFocusedElement().getTagName());
    }
}

