package com.vaadin.v7.tests.server.component.grid;


import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.AbstractGridExtension;
import org.junit.Assert;
import org.junit.Test;


public class GridExtensionTest {
    public static class DummyGridExtension extends AbstractGridExtension {
        public DummyGridExtension(Grid grid) {
            super(grid);
        }
    }

    @Test
    public void testCreateExtension() {
        Grid grid = new Grid();
        GridExtensionTest.DummyGridExtension dummy = new GridExtensionTest.DummyGridExtension(grid);
        Assert.assertTrue("DummyGridExtension never made it to Grid", grid.getExtensions().contains(dummy));
    }
}

