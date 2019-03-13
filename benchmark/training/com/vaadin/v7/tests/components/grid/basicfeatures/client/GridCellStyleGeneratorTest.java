package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_COL_INDEX;
import GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_NONE;
import GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_SIMPLE;
import GridBasicClientFeaturesWidget.ROW_STYLE_GENERATOR_NONE;
import GridBasicClientFeaturesWidget.ROW_STYLE_GENERATOR_ROW_INDEX;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridCellStyleGeneratorTest extends GridBasicClientFeaturesTest {
    @Test
    public void testStyleNameGeneratorScrolling() throws Exception {
        openTestURL();
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_COL_INDEX);
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_ROW_INDEX);
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell4_2 = getGridElement().getCell(4, 2);
        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));
        // Scroll down and verify that the old elements don't have the
        // stylename any more
        getGridElement().getRow(350);
        Assert.assertFalse(hasCssClass(row2, "2"));
        Assert.assertFalse(hasCssClass(cell4_2, "4_2"));
    }

    @Test
    public void testDisableStyleNameGenerator() throws Exception {
        openTestURL();
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_COL_INDEX);
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_ROW_INDEX);
        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell4_2 = getGridElement().getCell(4, 2);
        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));
        // Disable the generator and check again
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_NONE);
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_NONE);
        Assert.assertFalse(hasCssClass(row2, "2"));
        Assert.assertFalse(hasCssClass(cell4_2, "4_2"));
    }

    @Test
    public void testChangeStyleNameGenerator() throws Exception {
        openTestURL();
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_COL_INDEX);
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_ROW_INDEX);
        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell4_2 = getGridElement().getCell(4, 2);
        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));
        // Change the generator and check again
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_SIMPLE);
        // Old styles removed?
        Assert.assertFalse(hasCssClass(row2, "2"));
        Assert.assertFalse(hasCssClass(cell4_2, "4_2"));
        // New style present?
        Assert.assertTrue(hasCssClass(cell4_2, "two"));
    }

    @Test
    public void testStyleNameGeneratorChangePrimary() throws Exception {
        openTestURL();
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_COL_INDEX);
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_ROW_INDEX);
        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell4_2 = getGridElement().getCell(4, 2);
        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));
        // Change primary stylename
        selectMenuPath("Component", "State", "Primary Stylename", "v-escalator");
        // Styles still present
        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));
        // New styles present?
        Assert.assertFalse(hasCssClass(row2, "v-escalator-row-2"));
        Assert.assertFalse(hasCssClass(cell4_2, "v-escalator-cell-content-4_2"));
    }
}

