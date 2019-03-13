package com.vaadin.ui;


import Unit.PERCENTAGE;
import org.junit.Assert;
import org.junit.Test;


public class GridLayoutExpandRatioTest {
    private GridLayout gridLayout;

    @Test
    public void testColExpandRatioIsForgotten() {
        gridLayout = new GridLayout(4, 1);
        gridLayout.setWidth(100, PERCENTAGE);
        gridLayout.setSizeFull();
        gridLayout.setSpacing(true);
        addComponents(true);
        gridLayout.setColumnExpandRatio(1, 1);
        gridLayout.setColumnExpandRatio(3, 1);
        Assert.assertTrue(((gridLayout.getColumnExpandRatio(0)) == 0));
        Assert.assertTrue(((gridLayout.getColumnExpandRatio(1)) == 1));
        Assert.assertTrue(((gridLayout.getColumnExpandRatio(2)) == 0));
        Assert.assertTrue(((gridLayout.getColumnExpandRatio(3)) == 1));
        Assert.assertFalse(gridLayout.getState().explicitColRatios.contains(0));
        Assert.assertTrue(gridLayout.getState().explicitColRatios.contains(1));
        Assert.assertFalse(gridLayout.getState().explicitColRatios.contains(2));
        Assert.assertTrue(gridLayout.getState().explicitColRatios.contains(3));
        gridLayout.removeAllComponents();
        gridLayout.setColumns(3);
        addComponents(false);
        Assert.assertTrue(((gridLayout.getColumnExpandRatio(0)) == 0));
        Assert.assertTrue(((gridLayout.getColumnExpandRatio(1)) == 1));
        Assert.assertTrue(((gridLayout.getColumnExpandRatio(2)) == 0));
        Assert.assertTrue(((gridLayout.getColumnExpandRatio(3)) == 0));
        Assert.assertFalse(gridLayout.getState().explicitColRatios.contains(0));
        Assert.assertTrue(gridLayout.getState().explicitColRatios.contains(1));
        Assert.assertFalse(gridLayout.getState().explicitColRatios.contains(2));
        Assert.assertFalse(gridLayout.getState().explicitColRatios.contains(3));
    }

    @Test
    public void testRowExpandRatioIsForgotten() {
        gridLayout = new GridLayout(1, 4);
        gridLayout.setWidth(100, PERCENTAGE);
        gridLayout.setSizeFull();
        gridLayout.setSpacing(true);
        addComponents(true);
        gridLayout.setRowExpandRatio(1, 1);
        gridLayout.setRowExpandRatio(3, 1);
        Assert.assertTrue(((gridLayout.getRowExpandRatio(0)) == 0));
        Assert.assertTrue(((gridLayout.getRowExpandRatio(1)) == 1));
        Assert.assertTrue(((gridLayout.getRowExpandRatio(2)) == 0));
        Assert.assertTrue(((gridLayout.getRowExpandRatio(3)) == 1));
        Assert.assertFalse(gridLayout.getState().explicitRowRatios.contains(0));
        Assert.assertTrue(gridLayout.getState().explicitRowRatios.contains(1));
        Assert.assertFalse(gridLayout.getState().explicitRowRatios.contains(2));
        Assert.assertTrue(gridLayout.getState().explicitRowRatios.contains(3));
        gridLayout.removeAllComponents();
        gridLayout.setRows(3);
        addComponents(false);
        Assert.assertTrue(((gridLayout.getRowExpandRatio(0)) == 0));
        Assert.assertTrue(((gridLayout.getRowExpandRatio(1)) == 1));
        Assert.assertTrue(((gridLayout.getRowExpandRatio(2)) == 0));
        Assert.assertTrue(((gridLayout.getRowExpandRatio(3)) == 0));
        Assert.assertFalse(gridLayout.getState().explicitRowRatios.contains(0));
        Assert.assertTrue(gridLayout.getState().explicitRowRatios.contains(1));
        Assert.assertFalse(gridLayout.getState().explicitRowRatios.contains(2));
        Assert.assertFalse(gridLayout.getState().explicitRowRatios.contains(3));
    }
}

