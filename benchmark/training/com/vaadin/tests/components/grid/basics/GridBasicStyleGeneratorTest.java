package com.vaadin.tests.components.grid.basics;


import GridBasics.CELL_STYLE_GENERATOR_EMPTY;
import GridBasics.CELL_STYLE_GENERATOR_NONE;
import GridBasics.CELL_STYLE_GENERATOR_NULL;
import GridBasics.CELL_STYLE_GENERATOR_PROPERTY_TO_STRING;
import GridBasics.CELL_STYLE_GENERATOR_SPECIAL;
import GridBasics.ROW_STYLE_GENERATOR_EMPTY;
import GridBasics.ROW_STYLE_GENERATOR_NONE;
import GridBasics.ROW_STYLE_GENERATOR_NULL;
import GridBasics.ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.testbench.elements.NotificationElement;
import org.junit.Assert;
import org.junit.Test;


public class GridBasicStyleGeneratorTest extends GridBasicsTest {
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
        GridRowElement row = getGridElement().getRow(2);
        GridCellElement cell = getGridElement().getCell(3, 2);
        Assert.assertTrue(hasCssClass(row, "row2"));
        Assert.assertTrue(hasCssClass(cell, "Column_2"));
        // Disable the generator and check again
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_NONE);
        row = getGridElement().getRow(2);
        cell = getGridElement().getCell(3, 2);
        Assert.assertFalse(hasCssClass(row, "row2"));
        Assert.assertFalse(hasCssClass(cell, "Column_2"));
    }

    @Test
    public void testChangeStyleNameGenerator() throws Exception {
        openTestURL();
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_SPECIAL);
        // Just verify that change was effective
        GridRowElement row = getGridElement().getRow(2);
        GridCellElement cell = getGridElement().getCell(3, 2);
        Assert.assertTrue(hasCssClass(row, "row2"));
        Assert.assertTrue(hasCssClass(cell, "Column_2"));
        // Change the generator and check again
        selectRowStyleNameGenerator(ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(CELL_STYLE_GENERATOR_PROPERTY_TO_STRING);
        row = getGridElement().getRow(2);
        cell = getGridElement().getCell(3, 2);
        // Old styles removed?
        Assert.assertFalse(hasCssClass(row, "row2"));
        Assert.assertFalse(hasCssClass(cell, "Column_2"));
        // New style present?
        Assert.assertTrue(hasCssClass(cell, "Column-2"));
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

