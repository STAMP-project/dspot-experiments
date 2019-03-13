package com.vaadin.tests.elements.radiobuttongroup;


import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class RadioButtonGroupSetValueTest extends MultiBrowserTest {
    private static final String NEW_VALUE = "item2";

    private RadioButtonGroupElement group;

    @Test
    public void testSetValue() {
        group.setValue(RadioButtonGroupSetValueTest.NEW_VALUE);
        Assert.assertEquals(RadioButtonGroupSetValueTest.NEW_VALUE, group.getValue());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(RadioButtonGroupSetValueTest.NEW_VALUE);
        Assert.assertEquals(RadioButtonGroupSetValueTest.NEW_VALUE, group.getValue());
    }
}

