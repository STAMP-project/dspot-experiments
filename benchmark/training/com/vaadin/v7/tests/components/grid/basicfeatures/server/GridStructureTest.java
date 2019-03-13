package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import GridBasicFeatures.ROWS;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import java.util.List;
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class GridStructureTest extends GridBasicFeaturesTest {
    @Test
    public void testRemovingAllColumns() {
        setDebug(true);
        openTestURL();
        for (int i = 0; i < (GridBasicFeatures.COLUMNS); ++i) {
            selectMenuPath("Component", "Columns", ("Column " + i), "Add / Remove");
            Assert.assertFalse(isElementPresent(NotificationElement.class));
        }
        Assert.assertEquals("Headers still visible.", 0, getGridHeaderRowCells().size());
    }

    @Test
    public void testRemoveAndAddColumn() {
        setDebug(true);
        openTestURL();
        Assert.assertEquals("column 0", getGridElement().getHeaderCell(0, 0).getText().toLowerCase(Locale.ROOT));
        selectMenuPath("Component", "Columns", "Column 0", "Add / Remove");
        Assert.assertEquals("column 1", getGridElement().getHeaderCell(0, 0).getText().toLowerCase(Locale.ROOT));
        selectMenuPath("Component", "Columns", "Column 0", "Add / Remove");
        // Column 0 is now the last column in Grid.
        Assert.assertEquals("Unexpected column content", "(0, 0)", getGridElement().getCell(0, 11).getText());
    }

    @Test
    public void testRemovingColumn() throws Exception {
        openTestURL();
        // Column 0 should be visible
        List<TestBenchElement> cells = getGridHeaderRowCells();
        Assert.assertEquals("column 0", cells.get(0).getText().toLowerCase(Locale.ROOT));
        // Hide column 0
        selectMenuPath("Component", "Columns", "Column 0", "Add / Remove");
        // Column 1 should now be the first cell
        cells = getGridHeaderRowCells();
        Assert.assertEquals("column 1", cells.get(0).getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void testDataLoadingAfterRowRemoval() throws Exception {
        openTestURL();
        // Remove columns 2,3,4
        selectMenuPath("Component", "Columns", "Column 2", "Add / Remove");
        selectMenuPath("Component", "Columns", "Column 3", "Add / Remove");
        selectMenuPath("Component", "Columns", "Column 4", "Add / Remove");
        // Scroll so new data is lazy loaded
        scrollGridVerticallyTo(1000);
        // Let lazy loading do its job
        sleep(1000);
        // Check that row is loaded
        MatcherAssert.assertThat(getGridElement().getCell(11, 0).getText(), IsNot.not("..."));
    }

    @Test
    public void testFreezingColumn() throws Exception {
        openTestURL();
        // Freeze column 1
        selectMenuPath("Component", "State", "Frozen column count", "1");
        WebElement cell = getGridElement().getCell(0, 0);
        Assert.assertTrue(cell.getAttribute("class").contains("frozen"));
        cell = getGridElement().getCell(0, 1);
        Assert.assertFalse(cell.getAttribute("class").contains("frozen"));
    }

    @Test
    public void testInitialColumnWidths() throws Exception {
        openTestURL();
        WebElement cell = getGridElement().getCell(0, 0);
        Assert.assertEquals(100, cell.getSize().getWidth());
        cell = getGridElement().getCell(0, 1);
        Assert.assertEquals(150, cell.getSize().getWidth());
        cell = getGridElement().getCell(0, 2);
        Assert.assertEquals(200, cell.getSize().getWidth());
    }

    @Test
    public void testColumnWidths() throws Exception {
        openTestURL();
        // Default column width is 100px
        WebElement cell = getGridElement().getCell(0, 0);
        Assert.assertEquals(100, cell.getSize().getWidth());
        // Set first column to be 200px wide
        selectMenuPath("Component", "Columns", "Column 0", "Column 0 Width", "200px");
        cell = getGridElement().getCell(0, 0);
        Assert.assertEquals(200, cell.getSize().getWidth());
        // Set second column to be 150px wide
        selectMenuPath("Component", "Columns", "Column 1", "Column 1 Width", "150px");
        cell = getGridElement().getCell(0, 1);
        Assert.assertEquals(150, cell.getSize().getWidth());
        selectMenuPath("Component", "Columns", "Column 0", "Column 0 Width", "Auto");
        // since the column 0 was previously 200, it should've shrunk when
        // autoresizing.
        cell = getGridElement().getCell(0, 0);
        AbstractTB3Test.assertLessThan("", cell.getSize().getWidth(), 200);
    }

    @Test
    public void testPrimaryStyleNames() throws Exception {
        openTestURL();
        // v-grid is default primary style namea
        assertPrimaryStylename("v-grid");
        selectMenuPath("Component", "State", "Primary style name", "v-escalator");
        assertPrimaryStylename("v-escalator");
        selectMenuPath("Component", "State", "Primary style name", "my-grid");
        assertPrimaryStylename("my-grid");
        selectMenuPath("Component", "State", "Primary style name", "v-grid");
        assertPrimaryStylename("v-grid");
    }

    /**
     * Test that the current view is updated when a server-side container change
     * occurs (without scrolling back and forth)
     */
    @Test
    public void testItemSetChangeEvent() throws Exception {
        openTestURL();
        final By newRow = com.vaadin.testbench.By.xpath("//td[text()='newcell: 0']");
        Assert.assertTrue("Unexpected initial state", (!(isElementPresent(newRow))));
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertTrue("Add row failed", isElementPresent(newRow));
        selectMenuPath("Component", "Body rows", "Remove first row");
        Assert.assertTrue("Remove row failed", (!(isElementPresent(newRow))));
    }

    /**
     * Test that the current view is updated when a property's value is reflect
     * to the client, when the value is modified server-side.
     */
    @Test
    public void testPropertyValueChangeEvent() throws Exception {
        openTestURL();
        Assert.assertEquals("Unexpected cell initial state", "(0, 0)", getGridElement().getCell(0, 0).getText());
        selectMenuPath("Component", "Body rows", "Modify first row (getItemProperty)");
        Assert.assertEquals("(First) modification with getItemProperty failed", "modified: 0", getGridElement().getCell(0, 0).getText());
        selectMenuPath("Component", "Body rows", "Modify first row (getContainerProperty)");
        Assert.assertEquals("(Second) modification with getItemProperty failed", "modified: Column 0", getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testRemovingAllItems() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Body rows", "Remove all rows");
        Assert.assertEquals(0, getGridElement().findElement(com.vaadin.testbench.By.tagName("tbody")).findElements(com.vaadin.testbench.By.tagName("tr")).size());
    }

    @Test
    public void testRemoveFirstRowTwice() {
        openTestURL();
        selectMenuPath("Component", "Body rows", "Remove first row");
        selectMenuPath("Component", "Body rows", "Remove first row");
        getGridElement().scrollToRow(50);
        Assert.assertFalse("Listener setup problem occurred.", logContainsText("AssertionError: Value change listeners"));
    }

    @Test
    public void testVerticalScrollBarVisibilityWhenEnoughRows() throws Exception {
        openTestURL();
        Assert.assertTrue(verticalScrollbarIsPresent());
        selectMenuPath("Component", "Body rows", "Remove all rows");
        Assert.assertFalse(verticalScrollbarIsPresent());
        selectMenuPath("Component", "Size", "HeightMode Row");
        selectMenuPath("Component", "Size", "Height by Rows", "2.33 rows");
        selectMenuPath("Component", "Body rows", "Add first row");
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertFalse(verticalScrollbarIsPresent());
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertTrue(verticalScrollbarIsPresent());
    }

    @Test
    public void testBareItemSetChange() throws Exception {
        openTestURL();
        filterSomeAndAssert();
    }

    @Test
    public void testBareItemSetChangeRemovingAllRows() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Filter", "Impassable filter");
        Assert.assertFalse("A notification shouldn't have been displayed", $(NotificationElement.class).exists());
        Assert.assertTrue("No body cells should've been found", getGridElement().getBody().findElements(com.vaadin.testbench.By.tagName("td")).isEmpty());
    }

    @Test
    public void testBareItemSetChangeWithMidScroll() throws Exception {
        openTestURL();
        getGridElement().scrollToRow(((GridBasicFeatures.ROWS) / 2));
        filterSomeAndAssert();
    }

    @Test
    public void testBareItemSetChangeWithBottomScroll() throws Exception {
        openTestURL();
        getGridElement().scrollToRow(ROWS);
        filterSomeAndAssert();
    }

    @Test
    public void testBareItemSetChangeWithBottomScrollAndSmallViewport() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Size", "HeightMode Row");
        getGridElement().getRow(((GridBasicFeatures.ROWS) - 1));
        // filter
        selectMenuPath("Component", "Filter", "Column 1 starts with \"(23\"");
        String text = getGridElement().getCell(10, 0).getText();
        Assert.assertFalse(text.isEmpty());
    }

    @Test
    public void testRemoveLastColumn() {
        setDebug(true);
        openTestURL();
        int col = GridBasicFeatures.COLUMNS;
        String columnName = "Column " + ((GridBasicFeatures.COLUMNS) - 1);
        Assert.assertTrue((columnName + " was not present in DOM"), isElementPresent(com.vaadin.testbench.By.xpath((("//th[" + col) + "]/div[1]"))));
        selectMenuPath("Component", "Columns", columnName, "Add / Remove");
        Assert.assertFalse(isElementPresent(NotificationElement.class));
        Assert.assertFalse((columnName + " was still present in DOM"), isElementPresent(com.vaadin.testbench.By.xpath((("//th[" + col) + "]/div[1]"))));
    }

    @Test
    public void testReverseColumns() {
        openTestURL();
        String[] gridData = new String[GridBasicFeatures.COLUMNS];
        GridElement grid = getGridElement();
        for (int i = 0; i < (gridData.length); ++i) {
            gridData[i] = grid.getCell(0, i).getAttribute("innerHTML");
        }
        selectMenuPath("Component", "State", "Reverse Grid Columns");
        // Compare with reversed order
        for (int i = 0; i < (gridData.length); ++i) {
            final int column = ((gridData.length) - 1) - i;
            final String newText = grid.getCell(0, column).getAttribute("innerHTML");
            Assert.assertEquals((("Grid contained unexpected values. (0, " + column) + ")"), gridData[i], newText);
        }
    }

    @Test
    public void testAddingProperty() {
        setDebug(true);
        openTestURL();
        Assert.assertNotEquals("property value", getGridElement().getCell(0, 0).getText());
        selectMenuPath("Component", "Properties", "Prepend property");
        Assert.assertEquals("property value", getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testRemovingAddedProperty() {
        openTestURL();
        Assert.assertEquals("(0, 0)", getGridElement().getCell(0, 0).getText());
        Assert.assertNotEquals("property value", getGridElement().getCell(0, 0).getText());
        selectMenuPath("Component", "Properties", "Prepend property");
        selectMenuPath("Component", "Properties", "Prepend property");
        Assert.assertNotEquals("property value", getGridElement().getCell(0, 0).getText());
        Assert.assertEquals("(0, 0)", getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testAddRowAboveViewport() {
        setDebug(true);
        openTestURL();
        GridCellElement cell = getGridElement().getCell(500, 1);
        String cellContent = cell.getText();
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertFalse("Error notification was present", isElementPresent(NotificationElement.class));
        Assert.assertEquals("Grid scrolled unexpectedly", cellContent, cell.getText());
    }

    @Test
    public void testRemoveAndAddRowAboveViewport() {
        setDebug(true);
        openTestURL();
        GridCellElement cell = getGridElement().getCell(500, 1);
        String cellContent = cell.getText();
        selectMenuPath("Component", "Body rows", "Remove first row");
        Assert.assertFalse("Error notification was present after removing row", isElementPresent(NotificationElement.class));
        Assert.assertEquals("Grid scrolled unexpectedly", cellContent, cell.getText());
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertFalse("Error notification was present after adding row", isElementPresent(NotificationElement.class));
        Assert.assertEquals("Grid scrolled unexpectedly", cellContent, cell.getText());
    }

    @Test
    public void testScrollAndRemoveAll() {
        setDebug(true);
        openTestURL();
        getGridElement().scrollToRow(500);
        selectMenuPath("Component", "Body rows", "Remove all rows");
        Assert.assertFalse("Error notification was present after removing all rows", isElementPresent(NotificationElement.class));
        Assert.assertFalse(getGridElement().isElementPresent(com.vaadin.testbench.By.vaadin("#cell[0][0]")));
    }

    @Test
    public void testScrollPosDoesNotChangeAfterStateChange() {
        openTestURL();
        scrollGridVerticallyTo(1000);
        int scrollPos = getGridVerticalScrollPos();
        selectMenuPath("Component", "Editor", "Enabled");
        Assert.assertEquals("Scroll position should've not have changed", scrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testReloadPage() throws InterruptedException {
        setDebug(true);
        openTestURL();
        reopenTestURL();
        // After opening the URL Grid can be stuck in a state where it thinks it
        // should wait for something that's not going to happen.
        testBench().disableWaitForVaadin();
        // Wait until page is loaded completely.
        int count = 0;
        while (!($(GridElement.class).exists())) {
            if (count == 100) {
                Assert.fail("Reloading page failed");
            }
            sleep(100);
            ++count;
        } 
        // Wait a bit more for notification to occur.
        sleep(1000);
        Assert.assertFalse("Exception occurred when reloading page", isElementPresent(NotificationElement.class));
    }

    @Test
    public void testAddThirdRowToGrid() {
        openTestURL();
        selectMenuPath("Component", "Body rows", "Add third row");
        Assert.assertFalse(logContainsText("Exception occurred"));
    }

    @Test
    public void getBodyRowCountJS() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals(1000L, executeScript("return arguments[0].getBodyRowCount()", grid));
        selectMenuPath("Component", "Body rows", "Remove all rows");
        Assert.assertEquals(0L, executeScript("return arguments[0].getBodyRowCount()", grid));
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertEquals(1L, executeScript("return arguments[0].getBodyRowCount()", grid));
    }
}

