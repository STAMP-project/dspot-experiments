package com.vaadin.tests.components.checkboxgroup;


import Keys.TAB;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.components.FocusTest;
import org.junit.Assert;
import org.junit.Test;


public class CheckBoxGroupFocusTest extends FocusTest {
    @Test
    public void focusOnInit() {
        openTestURL();
        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class).first();
        Assert.assertTrue(isFocusInsideElement(checkBoxGroup));
    }

    @Test
    public void moveFocusAfterClick() {
        openTestURL();
        $(ButtonElement.class).first().click();
        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class).last();
        Assert.assertTrue(isFocusInsideElement(checkBoxGroup));
    }

    @Test
    public void focusDoesNotGoIntoWrapperElement() {
        openTestURL();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB, TAB, TAB).perform();
        Assert.assertTrue("Focus not in the second check box group.", isFocusInsideElement($(CheckBoxGroupElement.class).last()));
        Assert.assertEquals("Focus should not be in the wrapping div.", "input", getFocusedElement().getTagName());
    }
}

