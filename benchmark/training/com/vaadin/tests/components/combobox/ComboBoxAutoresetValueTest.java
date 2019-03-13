package com.vaadin.tests.components.combobox;


import ComboBoxAutoresetValue.CHANGE;
import ComboBoxAutoresetValue.RESET;
import ComboBoxAutoresetValue.SOMETHING;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxAutoresetValueTest extends SingleBrowserTest {
    @Test
    public void testValueChanges() {
        openTestURL();
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        Assert.assertEquals("", comboBox.getValue());
        comboBox.selectByText(RESET);
        assertLogChange(1, RESET, 1);
        assertLogChange(2, null, 0);
        Assert.assertEquals("", comboBox.getValue());
        comboBox.selectByText(CHANGE);
        assertLogChange(3, CHANGE, 1);
        assertLogChange(4, SOMETHING, 0);
        Assert.assertEquals(SOMETHING, comboBox.getValue());
        comboBox.selectByText(SOMETHING);
        // No new log items
        assertLogChange(4, SOMETHING, 0);
        Assert.assertEquals(SOMETHING, comboBox.getValue());
    }
}

