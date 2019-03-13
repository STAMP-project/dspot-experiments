package com.vaadin.tests.components.grid;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Tests for ensuring that the furthest away visible rows don't get emptied when
 * editRow is called, and that the editor doesn't open beyond the lower border
 * of the Grid.
 */
@TestCategory("grid")
public class GridEditRowTest extends MultiBrowserTest {
    private GridElement grid;

    private ButtonElement addButton;

    private ButtonElement editButton;

    @Test
    public void testEditWhenAllRowsVisible() {
        addRows(7);
        assertRowContents(0);
        editLastRow();
        assertRowContents(0);
        waitForElementVisible(By.className("v-grid-editor"));
        assertEditorWithinGrid();
    }

    @Test
    public void testEditWhenSomeRowsNotVisible() {
        addRows(11);
        assertRowContents(3);
        editLastRow();
        waitForElementVisible(By.className("v-grid-editor"));
        assertRowContents(3);
        assertEditorWithinGrid();
    }

    @Test
    public void testEditWhenSomeRowsOutsideOfCache() {
        addRows(100);
        assertRowContents(91);
        editLastRow();
        waitForElementVisible(By.className("v-grid-editor"));
        assertRowContents(91);
        assertEditorWithinGrid();
    }
}

