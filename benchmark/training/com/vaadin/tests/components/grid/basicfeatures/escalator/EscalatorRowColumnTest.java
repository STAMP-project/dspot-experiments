package com.vaadin.tests.components.grid.basicfeatures.escalator;


import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class EscalatorRowColumnTest extends EscalatorBasicClientFeaturesTest {
    /**
     * The scroll position of the Escalator when scrolled all the way down, to
     * reveal the 100:th row.
     */
    private static final int BOTTOM_SCROLL_POSITION = 1857;

    @Test
    public void testInit() {
        Assert.assertNotNull(getEscalator());
        Assert.assertNull(getHeaderRow(0));
        Assert.assertNull(getBodyRow(0));
        Assert.assertNull(getFooterRow(0));
        assertLogContains("Columns: 0");
        assertLogContains("Header rows: 0");
        assertLogContains("Body rows: 0");
        assertLogContains("Footer rows: 0");
    }

    @Test
    public void testInsertAColumn() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        Assert.assertNull(getHeaderRow(0));
        Assert.assertNull(getBodyRow(0));
        Assert.assertNull(getFooterRow(0));
        assertLogContains("Columns: 1");
    }

    @Test
    public void testInsertAHeaderRow() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.HEADER_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        Assert.assertNull(getHeaderCell(0, 0));
        Assert.assertNull(getBodyCell(0, 0));
        Assert.assertNull(getFooterCell(0, 0));
        assertLogContains("Header rows: 1");
    }

    @Test
    public void testInsertABodyRow() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        Assert.assertNull(getHeaderCell(0, 0));
        Assert.assertNull(getBodyCell(0, 0));
        Assert.assertNull(getFooterCell(0, 0));
        assertLogContains("Body rows: 1");
    }

    @Test
    public void testInsertAFooterRow() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.FOOTER_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        Assert.assertNull(getHeaderCell(0, 0));
        Assert.assertNull(getBodyCell(0, 0));
        Assert.assertNull(getFooterCell(0, 0));
        assertLogContains("Footer rows: 1");
    }

    @Test
    public void testInsertAColumnAndAHeaderRow() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.HEADER_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        Assert.assertNotNull(getHeaderCell(0, 0));
        Assert.assertNull(getBodyCell(0, 0));
        Assert.assertNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Header rows: 1");
    }

    @Test
    public void testInsertAColumnAndABodyRow() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        Assert.assertNull(getHeaderCell(0, 0));
        Assert.assertNotNull(getBodyCell(0, 0));
        Assert.assertNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Body rows: 1");
    }

    @Test
    public void testInsertAColumnAndAFooterRow() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.FOOTER_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        Assert.assertNull(getHeaderCell(0, 0));
        Assert.assertNull(getBodyCell(0, 0));
        Assert.assertNotNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Footer rows: 1");
    }

    @Test
    public void testInsertAHeaderRowAndAColumn() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.HEADER_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        Assert.assertNotNull(getHeaderCell(0, 0));
        Assert.assertNull(getBodyCell(0, 0));
        Assert.assertNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Header rows: 1");
    }

    @Test
    public void testInsertABodyRowAndAColumn() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        Assert.assertNull(getHeaderCell(0, 0));
        Assert.assertNotNull(getBodyCell(0, 0));
        Assert.assertNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Body rows: 1");
    }

    @Test
    public void testInsertAFooterRowAndAColumn() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.FOOTER_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        Assert.assertNull(getHeaderCell(0, 0));
        Assert.assertNull(getBodyCell(0, 0));
        Assert.assertNotNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Footer rows: 1");
    }

    @Test
    public void testFillColRow() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        scrollVerticallyTo(2000);// more like 1857, but this should be enough.

        // if not found, an exception is thrown here
        Assert.assertTrue("Wanted cell was not visible", isElementPresent(By.xpath("//td[text()='Cell: 9,99']")));
    }

    @Test
    public void testFillRowCol() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_ROW_COLUMN);
        scrollVerticallyTo(2000);// more like 1857, but this should be enough.

        // if not found, an exception is thrown here
        Assert.assertTrue("Wanted cell was not visible", isElementPresent(By.xpath("//td[text()='Cell: 9,99']")));
    }

    @Test
    public void testClearColRow() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.CLEAR_COLUMN_ROW);
        Assert.assertNull(getBodyCell(0, 0));
    }

    @Test
    public void testClearRowCol() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.CLEAR_ROW_COLUMN);
        Assert.assertNull(getBodyCell(0, 0));
    }

    @Test
    public void testResizeColToFit() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.RESIZE_FIRST_COLUMN_TO_100PX);
        int originalWidth = getBodyCell(0, 0).getSize().getWidth();
        Assert.assertEquals(100, originalWidth);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.RESIZE_FIRST_COLUMN_TO_MAX_WIDTH);
        int newWidth = getBodyCell(0, 0).getSize().getWidth();
        Assert.assertNotEquals("Column width should've changed", originalWidth, newWidth);
    }

    @Test
    public void testRemoveMoreThanPagefulAtBottomWhileScrolledToBottom() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        scrollVerticallyTo(EscalatorRowColumnTest.BOTTOM_SCROLL_POSITION);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_50_ROWS_FROM_BOTTOM);
        Assert.assertEquals("Row 49: 0,49", getBodyCell((-1), 0).getText());
        scrollVerticallyTo(0);
        // let the DOM organize itself
        Thread.sleep(500);
        // if something goes wrong, it'll explode before this.
        Assert.assertEquals("Row 0: 0,0", getBodyCell(0, 0).getText());
    }

    @Test
    public void testRemoveMoreThanPagefulAtBottomWhileScrolledAlmostToBottom() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        // bottom minus 15 rows.
        scrollVerticallyTo(((EscalatorRowColumnTest.BOTTOM_SCROLL_POSITION) - (15 * 20)));
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_50_ROWS_FROM_BOTTOM);
        Assert.assertEquals("Row 49: 0,49", getBodyCell((-1), 0).getText());
        scrollVerticallyTo(0);
        // let the DOM organize itself
        Thread.sleep(500);
        // if something goes wrong, it'll explode before this.
        Assert.assertEquals("Row 0: 0,0", getBodyCell(0, 0).getText());
    }

    @Test
    public void testRemoveMoreThanPagefulNearBottomWhileScrolledToBottom() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        scrollVerticallyTo(EscalatorRowColumnTest.BOTTOM_SCROLL_POSITION);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_50_ROWS_FROM_ALMOST_BOTTOM);
        Assert.assertEquals("Row 49: 0,99", getBodyCell((-1), 0).getText());
        scrollVerticallyTo(0);
        // let the DOM organize itself
        Thread.sleep(500);
        // if something goes wrong, it'll explode before this.
        Assert.assertEquals("Row 0: 0,0", getBodyCell(0, 0).getText());
    }

    @Test
    public void testRemoveMoreThanPagefulNearBottomWhileScrolledAlmostToBottom() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        // bottom minus 15 rows.
        scrollVerticallyTo(((EscalatorRowColumnTest.BOTTOM_SCROLL_POSITION) - (15 * 20)));
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_50_ROWS_FROM_ALMOST_BOTTOM);
        // let the DOM organize itself
        Thread.sleep(500);
        Assert.assertEquals("Row 49: 0,99", getBodyCell((-1), 0).getText());
        scrollVerticallyTo(0);
        // let the DOM organize itself
        Thread.sleep(500);
        // if something goes wrong, it'll explode before this.
        Assert.assertEquals("Row 0: 0,0", getBodyCell(0, 0).getText());
    }
}

