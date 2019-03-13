package com.vaadin.tests.components.combobox;


import Keys.DOWN;
import Keys.ENTER;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxCombinedWithEnterShortcutTest extends MultiBrowserTest {
    @Test
    public void testKeyboardSelection() throws InterruptedException {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.click();
        cb.sendKeys(500, DOWN, DOWN, DOWN, ENTER);
        Assert.assertEquals("", getLogRow(0).trim());
        cb.sendKeys(ENTER);
        Assert.assertEquals("1. Button clicked. ComboBox value: Berlin", getLogRow(0));
    }
}

