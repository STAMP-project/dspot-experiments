package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TableWithPollingTest extends MultiBrowserTest {
    @Test
    public void testColumnResizing() throws Exception {
        openTestURL();
        int offset = -20;
        int headerCellWidth = getHeaderCell(0).getSize().width;
        int bodyCellWidth = getBodyCell(0).getSize().width;
        resizeColumn(0, offset);
        assertHeaderCellWidth(0, (headerCellWidth + offset));
        assertBodyCellWidth(0, (bodyCellWidth + offset));
        offset = 50;
        headerCellWidth = getHeaderCell(1).getSize().width;
        bodyCellWidth = getBodyCell(1).getSize().width;
        resizeColumn(1, offset);
        assertHeaderCellWidth(1, (headerCellWidth + offset));
        assertBodyCellWidth(1, (bodyCellWidth + offset));
    }
}

