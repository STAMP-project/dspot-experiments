package com.vaadin.tests.elements.twincolselect;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TwinColSelectUITest extends MultiBrowserTest {
    TwinColSelectElement multiSelect;

    LabelElement multiCounterLbl;

    @Test
    public void testSelectDeselectByText() {
        multiSelect.selectByText("item2");
        Assert.assertEquals("1: [item1, item2]", multiCounterLbl.getText());
        multiSelect.selectByText("item3");
        Assert.assertEquals("2: [item1, item2, item3]", multiCounterLbl.getText());
        multiSelect.deselectByText("item2");
        Assert.assertEquals("3: [item1, item3]", multiCounterLbl.getText());
    }

    @Test
    public void testDeselectSelectByText() {
        multiSelect.deselectByText("item1");
        Assert.assertEquals("1: []", multiCounterLbl.getText());
        multiSelect.selectByText("item1");
        Assert.assertEquals("2: [item1]", multiCounterLbl.getText());
    }

    @Test
    public void testGetAvailableOptions() {
        assertAvailableOptions("item2", "item3");
        multiSelect.selectByText("item2");
        assertAvailableOptions("item3");
        multiSelect.deselectByText("item1");
        assertAvailableOptions("item1", "item3");
    }
}

