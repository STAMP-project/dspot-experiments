package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TableColumnWidthsAndSortingTest extends MultiBrowserTest {
    @Test
    public void testHeaderHeight() {
        openTestURL();
        TableElement t = $(TableElement.class).first();
        assertHeaderCellHeight(t);
        // Sort according to age
        t.getHeaderCell(2).click();
        assertHeaderCellHeight(t);
        // Sort again according to age
        t.getHeaderCell(2).click();
        assertHeaderCellHeight(t);
    }
}

