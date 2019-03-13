package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import Keys.ARROW_DOWN;
import Keys.ARROW_RIGHT;
import Keys.ARROW_UP;
import Keys.ENTER;
import Keys.LEFT;
import Keys.RIGHT;
import Keys.SHIFT;
import SortDirection.ASCENDING;
import SortDirection.DESCENDING;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridSortingTest extends GridBasicFeaturesTest {
    private static class SortInfo {
        public final int sortOrder;

        public final SortDirection sortDirection;

        private SortInfo(int sortOrder, SortDirection sortDirection) {
            this.sortOrder = sortOrder;
            this.sortDirection = sortDirection;
        }
    }

    private static class SortInfoWithColumn extends GridSortingTest.SortInfo {
        public final int columnIndex;

        private SortInfoWithColumn(int columnIndex, int sortOrder, SortDirection sortDirection) {
            super(sortOrder, sortDirection);
            this.columnIndex = columnIndex;
        }
    }

    @Test
    public void testProgrammaticSorting() throws Exception {
        openTestURL();
        // Sorting by column 9 is sorting by row index that is represented as a
        // String.
        // First cells for first 3 rows are (9, 0), (99, 0) and (999, 0)
        sortBy("Column 9, DESC");
        assertLastSortIsUserOriginated(false);
        // Verify that programmatic sorting calls are identified as originating
        // from API
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(9, 1, DESCENDING));
        String row = "";
        for (int i = 0; i < 3; ++i) {
            row += "9";
            String expected = ("(" + row) + ", 0)";
            String cellValue = getGridElement().getCell(i, 0).getText();
            Assert.assertEquals(("Grid is not sorted by Column 9 " + "using descending direction."), expected, cellValue);
        }
        // Column 10 is random numbers from Random with seed 13334
        sortBy("Column 10, ASC");
        Assert.assertFalse("Column 9 should no longer have the sort-desc stylename", getGridElement().getHeaderCell(0, 9).getAttribute("class").contains("sort-desc"));
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(10, 1, ASCENDING));
        for (int i = 0; i < 5; ++i) {
            Integer firstRow = Integer.valueOf(getGridElement().getCell((i + 1), 10).getText());
            Integer secondRow = Integer.valueOf(getGridElement().getCell(i, 10).getText());
            AbstractTB3Test.assertGreater(("Grid is not sorted by Column 10 using" + " ascending direction"), firstRow, secondRow);
        }
        // Column 7 is row index as a number. Last three row are original rows
        // 2, 1 and 0.
        sortBy("Column 7, DESC");
        for (int i = 0; i < 3; ++i) {
            String expected = ("(" + i) + ", 0)";
            String cellContent = getGridElement().getCell(((GridBasicFeatures.ROWS) - (i + 1)), 0).getText();
            Assert.assertEquals(("Grid is not sorted by Column 7 using " + "descending direction"), expected, cellContent);
        }
        Assert.assertFalse("Column 10 should no longer have the sort-asc stylename", getGridElement().getHeaderCell(0, 10).getAttribute("class").contains("sort-asc"));
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(7, 1, DESCENDING));
    }

    @Test
    public void testMouseSorting() throws Exception {
        setDebug(true);
        openTestURL();
        GridElement grid = getGridElement();
        selectMenuPath("Component", "Columns", "Column 9", "Column 9 Width", "Auto");
        // Sorting by column 9 is sorting by row index that is represented as a
        // String.
        // Click header twice to sort descending
        clickHeader(grid.getHeaderCell(0, 9));
        assertLastSortIsUserOriginated(true);
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(9, 1, ASCENDING));
        clickHeader(grid.getHeaderCell(0, 9));
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(9, 1, DESCENDING));
        // First cells for first 3 rows are (9, 0), (99, 0) and (999, 0)
        String row = "";
        for (int i = 0; i < 3; ++i) {
            row += "9";
            String expected = ("(" + row) + ", 0)";
            String actual = grid.getCell(i, 0).getText();
            Assert.assertEquals(("Grid is not sorted by Column 9" + " using descending direction."), expected, actual);
        }
        selectMenuPath("Component", "Columns", "Column 10", "Column 10 Width", "Auto");
        // Column 10 is random numbers from Random with seed 13334
        // Click header to sort ascending
        clickHeader(grid.getHeaderCell(0, 10));
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(10, 1, ASCENDING));
        for (int i = 0; i < 5; ++i) {
            Integer firstRow = Integer.valueOf(grid.getCell((i + 1), 10).getText());
            Integer secondRow = Integer.valueOf(grid.getCell(i, 10).getText());
            AbstractTB3Test.assertGreater("Grid is not sorted by Column 10 using ascending direction", firstRow, secondRow);
        }
        selectMenuPath("Component", "Columns", "Column 7", "Column 7 Width", "Auto");
        // Column 7 is row index as a number. Last three row are original rows
        // 2, 1 and 0.
        // Click header twice to sort descending
        clickHeader(grid.getHeaderCell(0, 7));
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(7, 1, ASCENDING));
        clickHeader(grid.getHeaderCell(0, 7));
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(7, 1, DESCENDING));
        for (int i = 0; i < 3; ++i) {
            Assert.assertEquals("Grid is not sorted by Column 7 using descending direction", (("(" + i) + ", 0)"), grid.getCell(((GridBasicFeatures.ROWS) - (i + 1)), 0).getText());
        }
    }

    @Test
    public void testKeyboardSortingMultipleHeaders() {
        openTestURL();
        selectMenuPath("Component", "Header", "Append row");
        // Sort according to first column by clicking
        getGridElement().getHeaderCell(0, 0).click();
        assertColumnIsSorted(0);
        // Try to sort according to second column by pressing enter on the new
        // header
        sendKey(ARROW_RIGHT);
        sendKey(ARROW_DOWN);
        sendKey(ENTER);
        // Should not have sorted
        assertColumnIsSorted(0);
        // Sort using default header
        sendKey(ARROW_UP);
        sendKey(ENTER);
        // Should have sorted
        assertColumnIsSorted(1);
    }

    @Test
    public void testKeyboardSorting() {
        openTestURL();
        /* We can't click on the header directly, since it will sort the header
        immediately. We need to focus some other column first, and only then
        navigate there.
         */
        getGridElement().getCell(0, 0).click();
        sendKey(ARROW_UP);
        // Sort ASCENDING on first column
        sendKey(ENTER);
        assertLastSortIsUserOriginated(true);
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(1, ASCENDING));
        // Move to next column
        sendKey(RIGHT);
        // Add this column to the existing sorting group
        holdKey(SHIFT);
        sendKey(ENTER);
        releaseKey(SHIFT);
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(1, ASCENDING), GridSortingTest.getSortInfo(2, ASCENDING));
        // Move to next column
        sendKey(RIGHT);
        // Add a third column to the sorting group
        holdKey(SHIFT);
        sendKey(ENTER);
        releaseKey(SHIFT);
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(1, ASCENDING), GridSortingTest.getSortInfo(2, ASCENDING), GridSortingTest.getSortInfo(3, ASCENDING));
        // Move back to the second column
        sendKey(LEFT);
        // Change sort direction of the second column to DESCENDING
        holdKey(SHIFT);
        sendKey(ENTER);
        releaseKey(SHIFT);
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(1, ASCENDING), GridSortingTest.getSortInfo(2, DESCENDING), GridSortingTest.getSortInfo(3, ASCENDING));
        // Move back to the third column
        sendKey(RIGHT);
        // Set sorting to third column, ASCENDING
        sendKey(ENTER);
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(2, 1, ASCENDING));
        // Move to the fourth column
        sendKey(RIGHT);
        // Make sure that single-column sorting also works as expected
        sendKey(ENTER);
        assertColumnsAreSortedAs(GridSortingTest.getSortInfo(3, 1, ASCENDING));
    }
}

