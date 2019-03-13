package com.vaadin.tests.elements.listselect;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ListSelectOptionClickTest extends MultiBrowserTest {
    ListSelectElement select;

    LabelElement counterLbl;

    @Test
    public void testMultiSelectDeselectByText() {
        select.selectByText("item2");
        Assert.assertEquals("1: [item1, item2]", counterLbl.getText());
        select.selectByText("item3");
        Assert.assertEquals("2: [item1, item2, item3]", counterLbl.getText());
        select.deselectByText("item2");
        Assert.assertEquals("3: [item1, item3]", counterLbl.getText());
    }

    @Test
    public void testDeselectSelectByText() {
        select.deselectByText("item1");
        Assert.assertEquals("1: []", counterLbl.getText());
        select.selectByText("item1");
        Assert.assertEquals("2: [item1]", counterLbl.getText());
        select.selectByText("item3");
        Assert.assertEquals("3: [item1, item3]", counterLbl.getText());
        select.deselectByText("item1");
        Assert.assertEquals("4: [item3]", counterLbl.getText());
    }
}

