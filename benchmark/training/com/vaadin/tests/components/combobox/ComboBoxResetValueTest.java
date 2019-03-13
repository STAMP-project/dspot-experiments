package com.vaadin.tests.components.combobox;


import Keys.BACK_SPACE;
import Keys.TAB;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxResetValueTest extends MultiBrowserTest {
    private ComboBoxElement comboBoxWithNullSelectionItemId;

    private ComboBoxElement comboBoxWithoutNullSelectionItemId;

    private ComboBoxElement comboBoxWithNullNotAllowed;

    @Test
    public void testNullSelectionAllowedAndSetNullSelectionItemId() {
        comboBoxWithNullSelectionItemId.openPopup();
        assertThatNullSelectionItemSelected(comboBoxWithNullSelectionItemId);
    }

    @Test
    public void testFilterNullSelectionAllowedAndSetNullSelectionItemId() {
        comboBoxWithNullSelectionItemId.sendKeys("foo", TAB);
        assertThatNullSelectionItemSelected(comboBoxWithNullSelectionItemId);
    }

    @Test
    public void testNullSelectionAllowedWithoutNullSelectionItemId() {
        comboBoxWithoutNullSelectionItemId.openPopup();
        assertThatSelectionIsEmpty(comboBoxWithoutNullSelectionItemId);
    }

    @Test
    public void testFilterNullSelectionAllowedWithoutNullSelectionItemId() {
        comboBoxWithoutNullSelectionItemId.sendKeys("foo", TAB);
        assertThatSelectionIsEmpty(comboBoxWithoutNullSelectionItemId);
    }

    @Test
    public void testNullSelectionNotAllowed() {
        comboBoxWithNullNotAllowed.openPopup();
        assertThatSelectionIsEmpty(comboBoxWithNullNotAllowed);
    }

    @Test
    public void testFilterNullSelectionNotAllowed() {
        comboBoxWithNullNotAllowed.sendKeys("1", TAB);
        comboBoxWithNullNotAllowed.sendKeys(BACK_SPACE, TAB);
        Assert.assertThat("Selection changed when it shouldn't have.", comboBoxWithNullNotAllowed.getText(), CoreMatchers.is("1"));
    }
}

