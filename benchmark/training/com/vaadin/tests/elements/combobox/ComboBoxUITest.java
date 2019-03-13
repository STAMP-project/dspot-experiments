package com.vaadin.tests.elements.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxUITest extends MultiBrowserTest {
    @Test
    public void testMultipleSelectByTextOperationsAllowingNullSelection() {
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        testMultipleSelectByTextOperationsIn(cb);
    }

    @Test
    public void testMultipleSelectByTextOperationsForbiddingNullSelection() {
        ComboBoxElement cb = $(ComboBoxElement.class).get(1);
        testMultipleSelectByTextOperationsIn(cb);
    }

    @Test
    public void testSelectByTextNotFound() {
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.selectByText("foobar");
    }

    @Test
    public void testSelectByTextWithIcons() {
        ComboBoxElement cb = $(ComboBoxElement.class).id("with-icons");
        cb.selectByText("GBP");
        Assert.assertEquals("GBP", cb.getValue());
        cb.selectByText("EUR");
        Assert.assertEquals("EUR", cb.getValue());
    }
}

