package com.vaadin.tests.server.component.gridlayout;


import GridLayout.OutOfBoundsException;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import org.junit.Assert;
import org.junit.Test;


public class GridLayoutTest {
    Component[] children = new Component[]{ new Label("A"), new Label("B"), new Label("C"), new Label("D") };

    @Test
    public void testConstructorWithComponents() {
        GridLayout grid = new GridLayout(2, 2, children);
        assertContentPositions(grid);
        assertOrder(grid, new int[]{ 0, 1, 2, 3 });
        grid = new GridLayout(1, 1, children);
        assertContentPositions(grid);
        assertOrder(grid, new int[]{ 0, 1, 2, 3 });
    }

    @Test
    public void testAddComponents() {
        GridLayout grid = new GridLayout(2, 2);
        grid.addComponents(children);
        assertContentPositions(grid);
        assertOrder(grid, new int[]{ 0, 1, 2, 3 });
        Label extra = new Label("Extra");
        Label extra2 = new Label("Extra2");
        grid.addComponents(extra, extra2);
        Assert.assertSame(grid.getComponent(0, 2), extra);
        Assert.assertSame(grid.getComponent(1, 2), extra2);
        grid.removeAllComponents();
        grid.addComponents(extra, extra2);
        Assert.assertSame(grid.getComponent(0, 0), extra);
        Assert.assertSame(grid.getComponent(1, 0), extra2);
        grid.addComponents(children);
        assertOrder(grid, new int[]{ -1, -1, 0, 1, 2, 3 });
        grid.removeComponent(extra);
        grid.removeComponent(extra2);
        assertOrder(grid, new int[]{ 0, 1, 2, 3 });
        grid.addComponents(extra2, extra);
        Assert.assertSame(grid.getComponent(0, 3), extra2);
        Assert.assertSame(grid.getComponent(1, 3), extra);
        assertOrder(grid, new int[]{ 0, 1, 2, 3, -1, -1 });
        grid.removeComponent(extra2);
        grid.removeComponent(extra);
        grid.setCursorX(0);
        grid.setCursorY(0);
        grid.addComponents(extra, extra2);
        Assert.assertSame(grid.getComponent(0, 0), extra);
        Assert.assertSame(grid.getComponent(1, 0), extra2);
        assertOrder(grid, new int[]{ -1, -1, 0, 1, 2, 3 });
        grid = new GridLayout();
        grid.addComponents(children);
        assertContentPositions(grid);
        assertOrder(grid, new int[]{ 0, 1, 2, 3 });
    }

    @Test
    public void removeRowsExpandRatiosPreserved() {
        GridLayout gl = new GridLayout(3, 3);
        gl.setRowExpandRatio(0, 0);
        gl.setRowExpandRatio(1, 1);
        gl.setRowExpandRatio(2, 2);
        gl.setRows(2);
        Assert.assertEquals(0, gl.getRowExpandRatio(0), 0);
        Assert.assertEquals(1, gl.getRowExpandRatio(1), 0);
    }

    @Test
    public void removeColsExpandRatiosPreserved() {
        GridLayout gl = new GridLayout(3, 3);
        gl.setColumnExpandRatio(0, 0);
        gl.setColumnExpandRatio(1, 1);
        gl.setColumnExpandRatio(2, 2);
        gl.setColumns(2);
        Assert.assertEquals(0, gl.getColumnExpandRatio(0), 0);
        Assert.assertEquals(1, gl.getColumnExpandRatio(1), 0);
    }

    @Test
    public void verifyOutOfBoundsExceptionContainsHelpfulMessage() {
        GridLayout grid = new GridLayout(1, 1);
        try {
            grid.addComponent(new Label(), 3, 3);
            Assert.fail("Should have failed");
        } catch (GridLayout ex) {
            Assert.assertEquals("Area{3,3 - 3,3}, layout dimension: 1x1", ex.getMessage());
        }
    }

    @Test
    public void verifyAddComponentFailsWithHelpfulMessageOnInvalidArgs() {
        GridLayout grid = new GridLayout(6, 6);
        try {
            grid.addComponent(new Label(), 3, 3, 2, 2);
            Assert.fail("Should have failed");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Illegal coordinates for the component: 3!<=2, 3!<=2", ex.getMessage());
        }
    }
}

