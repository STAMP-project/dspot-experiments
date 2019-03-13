package com.vaadin.tests.components.combobox;


import Keys.TAB;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxMixedUpdateTest extends MultiBrowserTest {
    private ComboBoxElement comboBox;

    private ButtonElement reset;

    private ButtonElement show;

    @Test
    public void testMixedUpdateWorks() {
        comboBox.focus();
        sendKeysToInput("2", TAB);
        show.click();
        Assert.assertEquals("1. Bean value = 2 - ComboBox value = 2", getLogRow(0));
        reset.click();
        show.click();
        Assert.assertEquals("2. Bean value = 0 - ComboBox value = 0", getLogRow(0));
        sendKeysToInput("2", TAB);
        show.click();
        Assert.assertEquals("3. Bean value = 2 - ComboBox value = 2", getLogRow(0));
    }
}

