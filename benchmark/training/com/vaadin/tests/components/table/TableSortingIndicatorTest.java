package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests if the sort indicator is visible after the table has been sorted from
 * the serverside.
 *
 * @author Vaadin Ltd
 */
public class TableSortingIndicatorTest extends MultiBrowserTest {
    private static final String TABLE_HEADER_DESC_INDICATOR = "v-table-header-cell-desc";

    private static final String TABLE_HEADER_ASC_INDICATOR = "v-table-header-cell-asc";

    @Test
    public void testTableSortingIndicatorIsVisibleAfterServersideSort() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).caption("Sort").first();
        TableElement table = $(TableElement.class).first();
        Assert.assertFalse("Descending indicator was prematurely visible", getHeaderClasses(table).contains(TableSortingIndicatorTest.TABLE_HEADER_DESC_INDICATOR));
        Assert.assertFalse("Ascending indicator was prematurely visible", getHeaderClasses(table).contains(TableSortingIndicatorTest.TABLE_HEADER_ASC_INDICATOR));
        button.click();
        Assert.assertTrue("Indicator did not become visible", getHeaderClasses(table).contains(TableSortingIndicatorTest.TABLE_HEADER_DESC_INDICATOR));
        Assert.assertFalse("Ascending sort indicator was wrongly visible", getHeaderClasses(table).contains(TableSortingIndicatorTest.TABLE_HEADER_ASC_INDICATOR));
        table.getHeaderCell(0).click();
        Assert.assertFalse("Table sort indicator didn't change", getHeaderClasses(table).contains(TableSortingIndicatorTest.TABLE_HEADER_DESC_INDICATOR));
        Assert.assertTrue("Ascending sort indicator didn't become visible", getHeaderClasses(table).contains(TableSortingIndicatorTest.TABLE_HEADER_ASC_INDICATOR));
        button.click();
        Assert.assertTrue("Descending sort indicator didn't appear on the second serverside sort.", getHeaderClasses(table).contains(TableSortingIndicatorTest.TABLE_HEADER_DESC_INDICATOR));
        Assert.assertFalse("Ascending sort indicator didn't disappear", getHeaderClasses(table).contains(TableSortingIndicatorTest.TABLE_HEADER_ASC_INDICATOR));
    }
}

