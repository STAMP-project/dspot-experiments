package com.vaadin.tests.components.grid.basics;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static GridBasics.COLUMN_CAPTIONS;
import static com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest.CellSide.LEFT;
import static com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest.CellSide.RIGHT;


@TestCategory("grid")
public class GridColumnReorderTest extends GridBasicsTest {
    @Test
    public void testColumnReorder_onReorder_columnReorderEventTriggered() {
        selectMenuPath("Component", "Header", "Prepend header row");
        selectMenuPath("Component", "State", "Column reorder listener");
        selectMenuPath("Component", "Columns", COLUMN_CAPTIONS[3], "Move left");
        Assert.assertEquals("1. Registered a column reorder listener.", getLogRow(2));
        Assert.assertEquals("2. Columns reordered, userOriginated: false", getLogRow(1));
        assertColumnHeaderOrder(0, 1, 3, 2);
        // trigger another event
        selectMenuPath("Component", "Columns", COLUMN_CAPTIONS[3], "Move right");
        assertColumnHeaderOrder(0, 1, 2, 3);
        // test drag and drop is user originated
        toggleColumnReorder();
        dragAndDropColumnHeader(0, 0, 1, RIGHT);
        Assert.assertEquals("6. Columns reordered, userOriginated: true", getLogRow(1));
        assertColumnHeaderOrder(1, 0, 2, 3);
    }

    @Test
    public void testColumnReorder_draggingSortedColumn_sortIndicatorShownOnDraggedElement() {
        // given
        toggleColumnReorder();
        toggleSortableColumn(0);
        sortColumn(0);
        // when
        startDragButDontDropOnDefaultColumnHeader(0);
        // then
        WebElement draggedElement = getDraggedHeaderElement();
        Assert.assertTrue(draggedElement.getAttribute("class").contains("sort"));
    }

    @Test
    public void testColumnReorder_draggingSortedColumn_sortStays() {
        // given
        toggleColumnReorder();
        toggleSortableColumn(0);
        sortColumn(0);
        // when
        dragAndDropDefaultColumnHeader(0, 2, LEFT);
        // then
        assertColumnIsSorted(1);
    }

    @Test
    public void testColumnReorder_draggingFocusedHeader_focusShownOnDraggedElement() {
        // given
        toggleColumnReorder();
        focusDefaultHeader(0);
        // when
        startDragButDontDropOnDefaultColumnHeader(0);
        // then
        WebElement draggedElement = getDraggedHeaderElement();
        Assert.assertTrue(draggedElement.getAttribute("class").contains("focused"));
    }

    @Test
    public void testColumnReorder_draggingFocusedHeader_focusIsKeptOnHeader() {
        // given
        toggleColumnReorder();
        focusDefaultHeader(0);
        // when
        dragAndDropDefaultColumnHeader(0, 3, LEFT);
        // then
        WebElement defaultColumnHeader = getDefaultColumnHeader(2);
        String attribute = defaultColumnHeader.getAttribute("class");
        Assert.assertTrue(attribute.contains("focused"));
    }

    @Test
    public void testColumnReorder_draggingFocusedCellColumn_focusIsKeptOnCell() {
        // given
        toggleColumnReorder();
        focusCell(2, 2);
        // when
        dragAndDropDefaultColumnHeader(2, 0, LEFT);
        // then
        assertFocusedCell(2, 0);
    }

    @Test
    public void testColumnReorderWithHiddenColumn_draggingFocusedCellColumnOverHiddenColumn_focusIsKeptOnCell() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Columns", "Column 1", "Hidden");
        focusCell(2, 2);
        assertFocusedCell(2, 2);
        // when
        dragAndDropDefaultColumnHeader(1, 0, LEFT);
        // then
        assertFocusedCell(2, 2);
        // when
        dragAndDropDefaultColumnHeader(0, 2, LEFT);
        // then
        assertFocusedCell(2, 2);
    }

    @Test
    public void testColumnReorder_dragColumnFromRightToLeftOfFocusedCellColumn_focusIsKept() {
        // given
        toggleColumnReorder();
        focusCell(1, 3);
        // when
        dragAndDropDefaultColumnHeader(4, 1, LEFT);
        // then
        assertFocusedCell(1, 4);
    }

    @Test
    public void testColumnReorder_dragColumnFromLeftToRightOfFocusedCellColumn_focusIsKept() {
        // given
        toggleColumnReorder();
        focusCell(4, 2);
        // when
        dragAndDropDefaultColumnHeader(0, 4, LEFT);
        // then
        assertFocusedCell(4, 1);
    }
}

