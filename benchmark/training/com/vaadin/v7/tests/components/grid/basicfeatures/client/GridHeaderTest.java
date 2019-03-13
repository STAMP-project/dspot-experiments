package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeatures;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class GridHeaderTest extends GridStaticSectionTest {
    @Test
    public void testDefaultHeader() throws Exception {
        openTestURL();
        assertHeaderCount(1);
        assertHeaderTexts(0, 0);
    }

    @Test
    public void testHeaderVisibility() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Header", "Visible");
        assertHeaderCount(0);
        selectMenuPath("Component", "Header", "Append row");
        assertHeaderCount(0);
        selectMenuPath("Component", "Header", "Visible");
        assertHeaderCount(2);
    }

    @Test
    public void testHeaderCaptions() throws Exception {
        openTestURL();
        assertHeaderTexts(0, 0);
    }

    @Test
    public void testAddRows() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Header", "Append row");
        assertHeaderCount(2);
        assertHeaderTexts(0, 0);
        assertHeaderTexts(1, 1);
        selectMenuPath("Component", "Header", "Prepend row");
        assertHeaderCount(3);
        assertHeaderTexts(2, 0);
        assertHeaderTexts(0, 1);
        assertHeaderTexts(1, 2);
        selectMenuPath("Component", "Header", "Append row");
        assertHeaderCount(4);
        assertHeaderTexts(2, 0);
        assertHeaderTexts(0, 1);
        assertHeaderTexts(1, 2);
        assertHeaderTexts(3, 3);
    }

    @Test
    public void testRemoveRows() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Remove top row");
        assertHeaderCount(2);
        assertHeaderTexts(0, 0);
        assertHeaderTexts(2, 1);
        selectMenuPath("Component", "Header", "Remove bottom row");
        assertHeaderCount(1);
        assertHeaderTexts(0, 0);
    }

    @Test
    public void testDefaultRow() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Columns", "Column 0", "Sortable");
        GridCellElement headerCell = getGridElement().getHeaderCell(0, 0);
        headerCell.click();
        Assert.assertTrue(hasClassName(headerCell, "sort-asc"));
        headerCell.click();
        Assert.assertFalse(hasClassName(headerCell, "sort-asc"));
        Assert.assertTrue(hasClassName(headerCell, "sort-desc"));
        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Default row", "Top");
        Assert.assertFalse(hasClassName(headerCell, "sort-desc"));
        headerCell = getGridElement().getHeaderCell(0, 0);
        Assert.assertTrue(hasClassName(headerCell, "sort-desc"));
        selectMenuPath("Component", "Header", "Default row", "Unset");
        Assert.assertFalse(hasClassName(headerCell, "sort-desc"));
    }

    @Test
    public void joinHeaderColumnsByCells() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join column cells 0, 1");
        GridCellElement spannedCell = getGridElement().getHeaderCell(1, 0);
        Assert.assertTrue(spannedCell.isDisplayed());
        Assert.assertEquals("2", spannedCell.getAttribute("colspan"));
        // TestBench returns the spanned cell for all spanned columns
        GridCellElement hiddenCell = getGridElement().getHeaderCell(1, 1);
        Assert.assertEquals(spannedCell.getText(), hiddenCell.getText());
    }

    @Test
    public void joinHeaderColumnsByColumns() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");
        GridCellElement spannedCell = getGridElement().getHeaderCell(1, 1);
        Assert.assertTrue(spannedCell.isDisplayed());
        Assert.assertEquals("2", spannedCell.getAttribute("colspan"));
        // TestBench returns the spanned cell for all spanned columns
        GridCellElement hiddenCell = getGridElement().getHeaderCell(1, 2);
        Assert.assertEquals(spannedCell.getText(), hiddenCell.getText());
    }

    @Test
    public void joinAllColumnsInHeaderRow() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "Header", "Row 2", "Join all columns");
        GridCellElement spannedCell = getGridElement().getHeaderCell(1, 0);
        Assert.assertTrue(spannedCell.isDisplayed());
        Assert.assertEquals(("" + (GridBasicFeatures.COLUMNS)), spannedCell.getAttribute("colspan"));
        for (int columnIndex = 1; columnIndex < (GridBasicFeatures.COLUMNS); columnIndex++) {
            // TestBench returns the spanned cell for all spanned columns
            GridCellElement hiddenCell = getGridElement().getHeaderCell(1, columnIndex);
            Assert.assertEquals(spannedCell.getText(), hiddenCell.getText());
        }
    }

    @Test
    public void testInitialCellTypes() throws Exception {
        openTestURL();
        GridCellElement textCell = getGridElement().getHeaderCell(0, 0);
        /* Reindeer has a CSS text transformation that changes the casing so
        that we can't rely on it being what we set
         */
        Assert.assertEquals("header (0,0)", textCell.getText().toLowerCase(Locale.ROOT));
        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 1);
        Assert.assertTrue(widgetCell.isElementPresent(By.className("gwt-HTML")));
        GridCellElement htmlCell = getGridElement().getHeaderCell(0, 2);
        GridStaticSectionTest.assertHTML("<b>Header (0,2)</b>", htmlCell);
    }

    @Test
    public void testDynamicallyChangingCellType() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Columns", "Column 0", "Header Type", "Widget Header");
        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 0);
        Assert.assertTrue(widgetCell.isElementPresent(By.className("gwt-Button")));
        selectMenuPath("Component", "Columns", "Column 1", "Header Type", "HTML Header");
        GridCellElement htmlCell = getGridElement().getHeaderCell(0, 1);
        GridStaticSectionTest.assertHTML("<b>HTML Header</b>", htmlCell);
        selectMenuPath("Component", "Columns", "Column 2", "Header Type", "Text Header");
        GridCellElement textCell = getGridElement().getHeaderCell(0, 2);
        /* Reindeer has a CSS text transformation that changes the casing so
        that we can't rely on it being what we set
         */
        Assert.assertEquals("text header", textCell.getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void testCellWidgetInteraction() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Columns", "Column 0", "Header Type", "Widget Header");
        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 0);
        WebElement button = widgetCell.findElement(By.className("gwt-Button"));
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(button, 5, 5).click().perform();
        Assert.assertEquals("clicked", button.getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void widgetInSortableCellInteraction() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Columns", "Column 0", "Header Type", "Widget Header");
        selectMenuPath("Component", "Columns", "Column 0", "Sortable");
        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 0);
        WebElement button = widgetCell.findElement(By.className("gwt-Button"));
        Assert.assertNotEquals("clicked", button.getText().toLowerCase(Locale.ROOT));
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(button, 5, 5).click().perform();
        Assert.assertEquals("clicked", button.getText().toLowerCase(Locale.ROOT));
    }
}

