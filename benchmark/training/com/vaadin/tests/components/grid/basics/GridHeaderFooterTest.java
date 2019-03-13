package com.vaadin.tests.components.grid.basics;


import GridBasics.COLUMN_CAPTIONS;
import GridBasics.COLUMN_CAPTIONS.length;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import java.util.Locale;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class GridHeaderFooterTest extends GridBasicsTest {
    protected static final String[] HEADER_TEXTS = IntStream.range(0, length).mapToObj(( i) -> "Header cell " + i).toArray(String[]::new);

    protected static final String[] FOOTER_TEXTS = IntStream.range(0, length).mapToObj(( i) -> "Footer cell " + i).toArray(String[]::new);

    @Test
    public void initialState_defaultHeaderPresent() {
        Assert.assertEquals(1, getGridElement().getHeaderCount());
        assertHeaderTexts(0, COLUMN_CAPTIONS);
    }

    @Test
    public void appendHeaderRow_addedToBottom() {
        selectMenuPath("Component", "Header", "Append header row");
        Assert.assertEquals(2, getGridElement().getHeaderCount());
        assertHeaderTexts(0, COLUMN_CAPTIONS);
        assertHeaderTexts(1, GridHeaderFooterTest.HEADER_TEXTS);
    }

    @Test
    public void prependHeaderRow_addedToTop() {
        selectMenuPath("Component", "Header", "Prepend header row");
        Assert.assertEquals(2, getGridElement().getHeaderCount());
        assertHeaderTexts(0, GridHeaderFooterTest.HEADER_TEXTS);
        assertHeaderTexts(1, COLUMN_CAPTIONS);
    }

    @Test
    public void removeDefaultHeaderRow_noHeaderRows() {
        selectMenuPath("Component", "Header", "Remove first header row");
        Assert.assertEquals(0, getGridElement().getHeaderCount());
    }

    @Test
    public void setDefaultRow_headerCaptionsUpdated() {
        selectMenuPath("Component", "Header", "Prepend header row");
        selectMenuPath("Component", "Header", "Set first row as default");
        assertHeaderTexts(0, GridHeaderFooterTest.HEADER_TEXTS);
    }

    @Test
    public void clickDefaultHeaderCell_sortIndicatorPresent() {
        GridCellElement headerCell = getGridElement().getHeaderCell(0, 2);
        headerCell.click();
        assertSortIndicator(headerCell, "sort-asc");
        headerCell.click();
        assertNoSortIndicator(headerCell, "sort-asc");
        assertSortIndicator(headerCell, "sort-desc");
        GridCellElement anotherCell = getGridElement().getHeaderCell(0, 3);
        anotherCell.click();
        assertNoSortIndicator(headerCell, "sort-asc");
        assertNoSortIndicator(headerCell, "sort-desc");
        assertSortIndicator(anotherCell, "sort-asc");
    }

    @Test
    public void noDefaultRow_clickHeaderCell_sortIndicatorsNotPresent() {
        selectMenuPath("Component", "Header", "Set no default row");
        GridCellElement headerCell = getGridElement().getHeaderCell(0, 2);
        headerCell.click();
        assertNoSortIndicator(headerCell, "sort-asc");
        assertNoSortIndicator(headerCell, "sort-desc");
    }

    @Test
    public void initialState_defaultFooterPresent() {
        Assert.assertEquals(1, getGridElement().getFooterCount());
        assertFooterTexts(0, COLUMN_CAPTIONS);
    }

    @Test
    public void appendFooterRow_addedToBottom() {
        selectMenuPath("Component", "Footer", "Append footer row");
        Assert.assertEquals(2, getGridElement().getFooterCount());
        assertFooterTexts(0, COLUMN_CAPTIONS);
        assertFooterTexts(1, GridHeaderFooterTest.FOOTER_TEXTS);
    }

    @Test
    public void prependFooterRow_addedToTop() {
        selectMenuPath("Component", "Footer", "Prepend footer row");
        Assert.assertEquals(2, getGridElement().getFooterCount());
        assertFooterTexts(0, GridHeaderFooterTest.FOOTER_TEXTS);
        assertFooterTexts(1, COLUMN_CAPTIONS);
    }

    @Test
    public void testDynamicallyChangingHeaderCellType() throws Exception {
        selectMenuPath("Component", "Columns", "Column 0", "Header Type", "Widget Header");
        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 0);
        Assert.assertTrue(widgetCell.isElementPresent(By.className("v-button")));
        selectMenuPath("Component", "Columns", "Column 1", "Header Type", "HTML Header");
        GridCellElement htmlCell = getGridElement().getHeaderCell(0, 1);
        Assert.assertEquals("<b>HTML Header</b>", htmlCell.findElement(By.className("v-grid-column-header-content")).getAttribute("innerHTML"));
        selectMenuPath("Component", "Columns", "Column 2", "Header Type", "Text Header");
        GridCellElement textCell = getGridElement().getHeaderCell(0, 2);
        Assert.assertEquals("text header", textCell.getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void testButtonInHeader() throws Exception {
        selectMenuPath("Component", "Columns", "Column 1", "Header Type", "Widget Header");
        getGridElement().findElements(By.className("v-button")).get(0).click();
        Assert.assertTrue("Button click should be logged", logContainsText("Button clicked!"));
    }

    @Test
    public void testRemoveComponentFromHeader() throws Exception {
        selectMenuPath("Component", "Columns", "Column 1", "Header Type", "Widget Header");
        selectMenuPath("Component", "Columns", "Column 1", "Header Type", "Text Header");
        Assert.assertTrue("No notifications should've been shown", (!($(NotificationElement.class).exists())));
        Assert.assertEquals("Header should've been reverted back to text header", "text header", getGridElement().getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void testColumnHidingToggleCaption_settingWidgetToHeader_toggleCaptionStays() {
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        Assert.assertEquals("column 1", getGridElement().getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        Assert.assertEquals("Column 1", getColumnHidingToggle(1).getText());
        selectMenuPath("Component", "Columns", "Column 1", "Header Type", "Widget Header");
        getSidebarOpenButton().click();
        Assert.assertEquals("Column 1", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testDynamicallyChangingFooterCellType() throws Exception {
        selectMenuPath("Component", "Columns", "Column 0", "Footer Type", "Widget Footer");
        GridCellElement widgetCell = getGridElement().getFooterCell(0, 0);
        Assert.assertTrue(widgetCell.isElementPresent(By.className("v-button")));
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type", "HTML Footer");
        GridCellElement htmlCell = getGridElement().getFooterCell(0, 1);
        Assert.assertEquals("<b>HTML Footer</b>", htmlCell.findElement(By.className("v-grid-column-footer-content")).getAttribute("innerHTML"));
        selectMenuPath("Component", "Columns", "Column 2", "Footer Type", "Text Footer");
        GridCellElement textCell = getGridElement().getFooterCell(0, 2);
        Assert.assertEquals("text footer", textCell.getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void testButtonInFooter() throws Exception {
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type", "Widget Footer");
        getGridElement().findElements(By.className("v-button")).get(0).click();
        Assert.assertTrue("Button click should be logged", logContainsText("Button clicked!"));
    }

    @Test
    public void testRemoveComponentFromFooter() throws Exception {
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type", "Widget Footer");
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type", "Text Footer");
        Assert.assertTrue("No notifications should've been shown", (!($(NotificationElement.class).exists())));
        Assert.assertEquals("Footer should've been reverted back to text footer", "text footer", getGridElement().getFooterCell(0, 1).getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void testColumnHidingToggleCaption_settingWidgetToFooter_toggleCaptionStays() {
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        Assert.assertEquals("column 1", getGridElement().getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        Assert.assertEquals("Column 1", getColumnHidingToggle(1).getText());
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type", "Widget Footer");
        getSidebarOpenButton().click();
        Assert.assertEquals("Column 1", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testHeaderMergedRemoveColumn() {
        selectMenuPath("Component", "Header", "Append header row");
        selectMenuPath("Component", "Header", "Merge Header Cells [0,0..1]");
        selectMenuPath("Component", "Footer", "Append footer row");
        selectMenuPath("Component", "Footer", "Merge Footer Cells [0,0..1]");
        checkMergedHeaderFooter();
        selectMenuPath("Component", "Columns", "Column 1", "Remove");
        selectMenuPath("Component", "Header", "Append header row");
        selectMenuPath("Component", "Footer", "Append footer row");
        checkHeaderAfterDelete();
        checkFooterAfterDelete();
    }

    @Test
    public void testHeaderMerge() {
        selectMenuPath("Component", "Header", "Append header row");
        selectMenuPath("Component", "Header", "Merge Header Cells [0,0..1]");
        selectMenuPath("Component", "Header", "Merge Header Cells [1,1..3]");
        selectMenuPath("Component", "Header", "Merge Header Cells [0,6..7]");
        GridCellElement mergedCell1 = getGridElement().getHeaderCell(0, 0);
        Assert.assertEquals("0+1", mergedCell1.getText());
        Assert.assertEquals("Colspan, cell [0,0]", "2", mergedCell1.getAttribute("colspan"));
        GridCellElement mergedCell2 = getGridElement().getHeaderCell(1, 1);
        Assert.assertEquals("1+2+3", mergedCell2.getText());
        Assert.assertEquals("Colspan of cell [1,1]", "3", mergedCell2.getAttribute("colspan"));
        GridCellElement mergedCell3 = getGridElement().getHeaderCell(0, 6);
        Assert.assertEquals("6+7", mergedCell3.getText());
        Assert.assertEquals("Colspan of cell [0,6]", "2", mergedCell3.getAttribute("colspan"));
    }

    @Test
    public void testFooterMerge() {
        selectMenuPath("Component", "Footer", "Append footer row");
        selectMenuPath("Component", "Footer", "Merge Footer Cells [0,0..1]");
        selectMenuPath("Component", "Footer", "Merge Footer Cells [1,1..3]");
        selectMenuPath("Component", "Footer", "Merge Footer Cells [0,6..7]");
        GridCellElement mergedCell1 = getGridElement().getFooterCell(0, 0);
        Assert.assertEquals("0+1", mergedCell1.getText());
        Assert.assertEquals("Colspan, cell [0,0]", "2", mergedCell1.getAttribute("colspan"));
        GridCellElement mergedCell2 = getGridElement().getFooterCell(1, 1);
        Assert.assertEquals("1+2+3", mergedCell2.getText());
        Assert.assertEquals("Colspan of cell [1,1]", "3", mergedCell2.getAttribute("colspan"));
        GridCellElement mergedCell3 = getGridElement().getFooterCell(0, 6);
        Assert.assertEquals("6+7", mergedCell3.getText());
        Assert.assertEquals("Colspan of cell [0,6]", "2", mergedCell3.getAttribute("colspan"));
    }

    @Test
    public void testHideAndShowHeader() {
        Assert.assertEquals("There should be one header row", 1, getGridElement().getHeaderCount());
        selectMenuPath("Component", "Header", "Toggle header visibility");
        Assert.assertEquals("There should be no header rows", 0, getGridElement().getHeaderCount());
        selectMenuPath("Component", "Header", "Toggle header visibility");
        Assert.assertEquals("There should be one header row again", 1, getGridElement().getHeaderCount());
    }

    @Test
    public void testHideAndShowFooter() {
        Assert.assertEquals("There should be one footer row", 1, getGridElement().getFooterCount());
        selectMenuPath("Component", "Footer", "Toggle footer visibility");
        Assert.assertEquals("There should be no footer rows", 0, getGridElement().getFooterCount());
        selectMenuPath("Component", "Footer", "Toggle footer visibility");
        Assert.assertEquals("There should be one footer row again", 1, getGridElement().getFooterCount());
    }
}

