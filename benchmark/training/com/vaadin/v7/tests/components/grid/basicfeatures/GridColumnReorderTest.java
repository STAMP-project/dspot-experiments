package com.vaadin.v7.tests.components.grid.basicfeatures;


import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest.CellSide.LEFT;
import static com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest.CellSide.RIGHT;


/**
 *
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridColumnReorderTest extends GridBasicClientFeaturesTest {
    @Test
    public void columnReorderEventTriggered() {
        final int firstIndex = 3;
        final int secondIndex = 4;
        final String firstHeaderText = getGridElement().getHeaderCell(0, firstIndex).getText();
        final String secondHeaderText = getGridElement().getHeaderCell(0, secondIndex).getText();
        selectMenuPath("Component", "Internals", "Listeners", "Add ColumnReorder listener");
        selectMenuPath("Component", "Columns", ("Column " + secondIndex), "Move column left");
        // columns 3 and 4 should have swapped to 4 and 3
        GridCellElement headerCell = getGridElement().getHeaderCell(0, firstIndex);
        Assert.assertEquals(secondHeaderText, headerCell.getText());
        headerCell = getGridElement().getHeaderCell(0, secondIndex);
        Assert.assertEquals(firstHeaderText, headerCell.getText());
        // the reorder event should have typed the order to this label
        WebElement columnReorderElement = findElement(By.id("columnreorder"));
        int eventIndex = Integer.parseInt(columnReorderElement.getAttribute("columns"));
        Assert.assertEquals(1, eventIndex);
        // trigger another event
        selectMenuPath("Component", "Columns", ("Column " + secondIndex), "Move column left");
        columnReorderElement = findElement(By.id("columnreorder"));
        eventIndex = Integer.parseInt(columnReorderElement.getAttribute("columns"));
        Assert.assertEquals(2, eventIndex);
    }

    @Test
    public void testColumnReorder_onReorder_columnReorderEventTriggered() {
        final int firstIndex = 3;
        final int secondIndex = 4;
        final String firstHeaderText = getGridElement().getHeaderCell(0, firstIndex).getText();
        final String secondHeaderText = getGridElement().getHeaderCell(0, secondIndex).getText();
        selectMenuPath("Component", "Internals", "Listeners", "Add ColumnReorder listener");
        selectMenuPath("Component", "Columns", ("Column " + secondIndex), "Move column left");
        // columns 3 and 4 should have swapped to 4 and 3
        GridCellElement headerCell = getGridElement().getHeaderCell(0, firstIndex);
        Assert.assertEquals(secondHeaderText, headerCell.getText());
        headerCell = getGridElement().getHeaderCell(0, secondIndex);
        Assert.assertEquals(firstHeaderText, headerCell.getText());
        // the reorder event should have typed the order to this label
        WebElement columnReorderElement = findElement(By.id("columnreorder"));
        int eventIndex = Integer.parseInt(columnReorderElement.getAttribute("columns"));
        Assert.assertEquals(1, eventIndex);
        // trigger another event
        selectMenuPath("Component", "Columns", ("Column " + secondIndex), "Move column left");
        columnReorderElement = findElement(By.id("columnreorder"));
        eventIndex = Integer.parseInt(columnReorderElement.getAttribute("columns"));
        Assert.assertEquals(2, eventIndex);
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

    @Test
    public void testColumnReorder_draggingHeaderRowThatHasColumnHeadersSpanned_cantDropInsideSpannedHeaderFromOutside() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        int horizontalOffset = ((getGridElement().getHeaderCell(1, 1).getSize().getWidth()) / 2) - 10;
        dragAndDropColumnHeader(1, 3, 1, horizontalOffset);
        // then
        assertColumnHeaderOrder(0, 3, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_anotherRowHasColumnHeadersSpanned_cantDropInsideSpannedHeaderFromOutside() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        int horizontalOffset = ((getGridElement().getHeaderCell(1, 1).getSize().getWidth()) / 2) + 10;
        dragAndDropColumnHeader(0, 0, 2, horizontalOffset);
        // then
        assertColumnHeaderOrder(1, 2, 0, 3, 4);
    }

    @Test
    public void testColumnReorder_cellInsideSpannedHeader_cantBeDroppedOutsideSpannedArea() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(0, 2, 0, LEFT);
        // then
        assertColumnHeaderOrder(0, 2, 1, 3, 4);
    }

    @Test
    public void testColumnReorder_cellInsideTwoCrossingSpanningHeaders_cantTouchThis() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join column cells 0, 1");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        dragAndDropColumnHeader(0, 3, 0, LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
        // when
        dragAndDropColumnHeader(0, 2, 0, LEFT);
        // then
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_cellsInsideSpannedHeaderAndBlockedByOtherSpannedCells_cantTouchThose() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join column cells 0, 1");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        dragAndDropColumnHeader(0, 3, 0, LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
        // when then
        dragAndDropColumnHeader(0, 1, 3, LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
        dragAndDropColumnHeader(1, 2, 1, LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
        dragAndDropColumnHeader(2, 1, 2, RIGHT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_cellsInsideSpannedHeaderAndBlockedByOtherSpannedCells_reorderingLimited() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(0, 0, 4, RIGHT);
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5);
        // when then
        dragAndDropColumnHeader(0, 1, 4, LEFT);
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5);
        dragAndDropColumnHeader(0, 2, 4, LEFT);
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5);
        dragAndDropColumnHeader(0, 3, 4, RIGHT);
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 2, 3, 5, 4);
        dragAndDropColumnHeader(0, 4, 2, RIGHT);
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5);
        dragAndDropColumnHeader(2, 3, 4, RIGHT);
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 2, 3, 5, 4);
        dragAndDropColumnHeader(2, 4, 2, RIGHT);
        scrollGridHorizontallyTo(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5);
    }

    @Test
    public void testColumnReorder_cellsInsideTwoAdjacentSpannedHeaders_reorderingLimited() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(0, 0, 4, RIGHT);
        scrollGridHorizontallyTo(0);
        dragAndDropColumnHeader(0, 1, 4, RIGHT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(1, 3, 4, 5, 2);
        // when then
        dragAndDropColumnHeader(0, 1, 4, LEFT);
        assertColumnHeaderOrder(1, 4, 3, 5, 2);
        dragAndDropColumnHeader(0, 2, 4, LEFT);
        assertColumnHeaderOrder(1, 4, 3, 5, 2);
        dragAndDropColumnHeader(0, 2, 0, LEFT);
        assertColumnHeaderOrder(1, 3, 4, 5, 2);
    }

    @Test
    public void testColumnReorder_footerHasSpannedCells_cantDropInside() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Footer", "Row 1", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(0, 3, 1, RIGHT);
        // then
        assertColumnHeaderOrder(0, 3, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_cellInsideASpannedFooter_cantBeDroppedOutsideSpannedArea() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Footer", "Row 1", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(0, 2, 0, LEFT);
        // then
        assertColumnHeaderOrder(0, 2, 1, 3, 4);
    }

    @Test
    public void testColumnReorder_cellInsideTwoCrossingSpanningFooters_cantTouchThis() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Footer", "Row 1", "Join column cells 0, 1");
        selectMenuPath("Component", "Footer", "Row 2", "Join columns 1, 2");
        dragAndDropColumnHeader(0, 3, 0, LEFT);
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
        // when
        dragAndDropColumnHeader(0, 2, 0, LEFT);
        // then
        assertColumnHeaderOrder(3, 0, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_cellsInsideTwoAdjacentSpannedHeaderAndFooter_reorderingLimited() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Footer", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(0, 0, 5, LEFT);
        scrollGridHorizontallyTo(0);
        dragAndDropColumnHeader(0, 1, 5, LEFT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Footer", "Row 1", "Join columns 1, 2");
        assertColumnHeaderOrder(1, 3, 4, 5, 2);
        // when then
        dragAndDropColumnHeader(0, 1, 3, RIGHT);
        assertColumnHeaderOrder(1, 4, 3, 5, 2);
        dragAndDropColumnHeader(0, 2, 4, RIGHT);
        assertColumnHeaderOrder(1, 4, 3, 5, 2);
        dragAndDropColumnHeader(0, 2, 0, RIGHT);
        assertColumnHeaderOrder(1, 3, 4, 5, 2);
    }

    @Test
    public void testColumnReorder_draggingASpannedCell_dragWorksNormally() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(1, 1, 4, LEFT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(0, 3, 1, 2, 4);
    }

    @Test
    public void testColumnReorder_twoEqualSpannedCells_bothCanBeDragged() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(1, 1, 4, LEFT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(0, 3, 1, 2, 4);
        // when
        dragAndDropColumnHeader(2, 3, 0, LEFT);
        // then
        assertColumnHeaderOrder(1, 2, 0, 3, 4);
    }

    @Test
    public void testColumReorder_twoCrossingSpanningHeaders_neitherCanBeDragged() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join column cells 0, 1");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(1, 1, 4, LEFT);
        // then
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(2, 0, 3, RIGHT);
        // then
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
    }

    @Test
    public void testColumnReorder_spannedCellHasAnotherSpannedCellInside_canBeDraggedNormally() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(1, 3, 1, LEFT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 3, 4, 5, 1);
        // when
        dragAndDropColumnHeader(1, 1, 0, LEFT);
        // then
        assertColumnHeaderOrder(3, 4, 5, 0, 1);
    }

    @Test
    public void testColumnReorder_spannedCellInsideAnotherSpanned_canBeDraggedWithBoundaries() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(1, 3, 1, LEFT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 3, 4, 5, 1);
        // when
        dragAndDropColumnHeader(2, 1, 3, RIGHT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(0, 5, 3, 4, 1);
        // when
        dragAndDropColumnHeader(2, 2, 0, LEFT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(0, 3, 4, 5, 1);
    }

    @Test
    public void testColumnReorder_cellInsideAndNextToSpannedCells_canBeDraggedWithBoundaries() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        dragAndDropColumnHeader(1, 3, 1, LEFT);
        scrollGridHorizontallyTo(0);
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 3, 4, 5, 1);
        // when
        dragAndDropColumnHeader(2, 3, 0, LEFT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(0, 5, 3, 4, 1);
        // when
        dragAndDropColumnHeader(2, 1, 4, LEFT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(0, 3, 4, 5, 1);
    }

    @Test
    public void testColumnReorder_multipleSpannedCells_dragWorksNormally() {
        toggleColumnReorder();
        selectMenuPath("Component", "State", "Width", "750px");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 3, 4, 5");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 3", "Join columns 1, 2");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(1, 3, 1, RIGHT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(0, 3, 4, 5, 1);
        // when
        scrollGridHorizontallyTo(100);
        dragAndDropColumnHeader(2, 4, 2, LEFT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        // when
        dragAndDropColumnHeader(0, 0, 3, LEFT);
        scrollGridHorizontallyTo(0);
        // then
        assertColumnHeaderOrder(1, 2, 0, 3, 4);
    }
}

