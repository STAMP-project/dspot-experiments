package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import GridBasicFeatures.CELL_STYLE_GENERATOR_EMPTY;
import GridBasicFeatures.CELL_STYLE_GENERATOR_NONE;
import GridBasicFeatures.CELL_STYLE_GENERATOR_NULL;
import GridBasicFeatures.CELL_STYLE_GENERATOR_PROPERTY_TO_STRING;
import GridBasicFeatures.CELL_STYLE_GENERATOR_SPECIAL;
import GridBasicFeatures.ROW_STYLE_GENERATOR_EMPTY;
import GridBasicFeatures.ROW_STYLE_GENERATOR_NONE;
import GridBasicFeatures.ROW_STYLE_GENERATOR_NULL;
import GridBasicFeatures.ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridCellStyleGeneratorTest extends GridBasicFeaturesTest {
    @Test
    public void testStyleNameGeneratorScrolling() throws Exception {
        openTestURL();
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_SPECIAL);
        GridRowElement row = getGridElement().getRow(2);
        GridCellElement cell = getGridElement().getCell(3, 2);
        Assert.assertTrue(hasCssClass(row, "row2"));
        Assert.assertTrue(hasCssClass(cell, "Column_2"));
        // Scroll down and verify that the old elements don't have the
        // stylename any more
        // Carefully chosen offset to hit an index % 4 without cell style
        row = getGridElement().getRow(352);
        cell = getGridElement().getCell(353, 2);
        Assert.assertFalse(hasCssClass(row, "row352"));
        Assert.assertFalse(hasCssClass(cell, "Column_2"));
    }

    @Test
    public void testDisableStyleNameGenerator() throws Exception {
        openTestURL();
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_SPECIAL);
        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell3_2 = getGridElement().getCell(3, 2);
        Assert.assertTrue(hasCssClass(row2, "row2"));
        Assert.assertTrue(hasCssClass(cell3_2, "Column_2"));
        // Disable the generator and check again
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_NONE);
        Assert.assertFalse(hasCssClass(row2, "row2"));
        Assert.assertFalse(hasCssClass(cell3_2, "Column_2"));
    }

    @Test
    public void testChangeStyleNameGenerator() throws Exception {
        openTestURL();
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_SPECIAL);
        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell3_2 = getGridElement().getCell(3, 2);
        Assert.assertTrue(hasCssClass(row2, "row2"));
        Assert.assertTrue(hasCssClass(cell3_2, "Column_2"));
        // Change the generator and check again
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_PROPERTY_TO_STRING);
        // Old styles removed?
        Assert.assertFalse(hasCssClass(row2, "row2"));
        Assert.assertFalse(hasCssClass(cell3_2, "Column_2"));
        // New style present?
        Assert.assertTrue(hasCssClass(cell3_2, "Column-2"));
    }

    @Test
    public void testCellStyleGeneratorWithSelectionColumn() {
        setDebug(true);
        openTestURL();
        selectMenuPath("Component", "State", "Selection mode", "multi");
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_SPECIAL);
        Assert.assertFalse("Error notification was present", isElementPresent(NotificationElement.class));
    }

    @Test
    public void testEmptyStringStyleGenerator() {
        setDebug(true);
        openTestURL();
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_EMPTY);
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_EMPTY);
        Assert.assertFalse("Error notification was present", isElementPresent(NotificationElement.class));
    }

    @Test
    public void testNullStringStyleGenerator() {
        setDebug(true);
        openTestURL();
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_NULL);
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_NULL);
        Assert.assertFalse("Error notification was present", isElementPresent(NotificationElement.class));
    }
}

