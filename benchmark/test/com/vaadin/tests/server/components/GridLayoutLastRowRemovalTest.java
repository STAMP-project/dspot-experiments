package com.vaadin.tests.server.components;


import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import org.junit.Assert;
import org.junit.Test;


public class GridLayoutLastRowRemovalTest {
    @Test
    public void testRemovingLastRow() {
        GridLayout grid = new GridLayout(2, 1);
        grid.addComponent(new Label("Col1"));
        grid.addComponent(new Label("Col2"));
        try {
            // Removing the last row in the grid
            grid.removeRow(0);
        } catch (IllegalArgumentException iae) {
            // Removing the last row should not throw an
            // IllegalArgumentException
            Assert.fail("removeRow(0) threw an IllegalArgumentExcetion when removing the last row");
        }
        // The column amount should be preserved
        Assert.assertEquals(2, grid.getColumns());
        // There should be one row left
        Assert.assertEquals(1, grid.getRows());
        // There should be no component left in the grid layout
        Assert.assertNull("A component should not be left in the layout", grid.getComponent(0, 0));
        Assert.assertNull("A component should not be left in the layout", grid.getComponent(1, 0));
        // The cursor should be in the first cell
        Assert.assertEquals(0, grid.getCursorX());
        Assert.assertEquals(0, grid.getCursorY());
    }
}

