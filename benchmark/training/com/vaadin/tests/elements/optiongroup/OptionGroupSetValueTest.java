package com.vaadin.tests.elements.optiongroup;


import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class OptionGroupSetValueTest extends MultiBrowserTest {
    private static final String NEW_VALUE = "item2";

    private OptionGroupElement group;

    @Test
    public void testSetValue() {
        group.setValue(OptionGroupSetValueTest.NEW_VALUE);
        Assert.assertEquals(OptionGroupSetValueTest.NEW_VALUE, group.getValue());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(OptionGroupSetValueTest.NEW_VALUE);
        Assert.assertEquals(OptionGroupSetValueTest.NEW_VALUE, group.getValue());
    }
}

