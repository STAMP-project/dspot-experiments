package com.vaadin.tests.elements.checkbox;


import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testcase used to validate {@link CheckBoxElement#click()} works as expected.
 * See #13763
 */
public class ClickCheckBoxUITest extends MultiBrowserTest {
    @Test
    public void testClickToggleCheckboxMark() {
        CheckBoxElement checkboxWithLabel = $(CheckBoxElement.class).first();
        CheckBoxElement checkboxWithoutLabel = $(CheckBoxElement.class).last();
        Assert.assertFalse(checkboxWithLabel.isChecked());
        Assert.assertFalse(checkboxWithoutLabel.isChecked());
        checkboxWithLabel.click();
        Assert.assertTrue(checkboxWithLabel.isChecked());
        checkboxWithoutLabel.click();
        Assert.assertTrue(checkboxWithoutLabel.isChecked());
        checkboxWithLabel.click();
        Assert.assertFalse(checkboxWithLabel.isChecked());
        checkboxWithoutLabel.click();
        Assert.assertFalse(checkboxWithoutLabel.isChecked());
    }
}

