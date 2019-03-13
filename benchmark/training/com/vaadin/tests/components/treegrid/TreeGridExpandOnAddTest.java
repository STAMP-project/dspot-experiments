package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TreeGridExpandOnAddTest extends SingleBrowserTest {
    @Test
    public void testNoException() {
        setDebug(true);
        openTestURL();
        $(ButtonElement.class).first().click();
        TreeGridElement treeGrid = $(TreeGridElement.class).first();
        Assert.assertEquals("Parent node not added", "Parent", treeGrid.getCell(0, 0).getText());
        Assert.assertEquals("Child node not added", "Child", treeGrid.getCell(1, 0).getText());
        assertNoErrorNotifications();
    }
}

