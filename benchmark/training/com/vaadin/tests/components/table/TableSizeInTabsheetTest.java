package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TableSizeInTabsheetTest extends MultiBrowserTest {
    private static final String TABSHEET_CONTENT_STYLENAME = "v-tabsheet-content";

    @Test
    public void testTabsheetContentHasTheSameHeightAsTable() {
        openTestURL();
        int tableHeight = getTableHeigth();
        int tabSheetContentHeight = getTableSheetContentHeight();
        Assert.assertEquals(tableHeight, tabSheetContentHeight);
    }
}

