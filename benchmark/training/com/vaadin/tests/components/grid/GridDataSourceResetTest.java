package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridDataSourceResetTest extends SingleBrowserTest {
    @Test
    public void testRemoveWithSelectUpdatesRowsCorrectly() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        Assert.assertTrue("First row was not selected", grid.getRow(0).isSelected());
        for (int i = 1; i < 10; ++i) {
            Assert.assertFalse("Only first row should be selected", grid.getRow(i).isSelected());
        }
        $(ButtonElement.class).first().click();
        Assert.assertTrue("First row was not selected after remove", grid.getRow(0).isSelected());
        for (int i = 1; i < 9; ++i) {
            Assert.assertFalse("Only first row should be selected after remove", grid.getRow(i).isSelected());
        }
    }
}

