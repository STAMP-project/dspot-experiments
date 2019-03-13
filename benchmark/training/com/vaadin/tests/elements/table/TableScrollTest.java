package com.vaadin.tests.elements.table;


import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TableScrollTest extends MultiBrowserTest {
    private static final int SCROLL_VALUE = 200;

    @Test
    public void testScrollLeft() {
        openTestURL();
        TableElement table = $(TableElement.class).get(0);
        table.scrollLeft(TableScrollTest.SCROLL_VALUE);
        Assert.assertEquals(TableScrollTest.SCROLL_VALUE, getScrollLeftValue(table));
    }

    @Test
    public void testScrollTop() {
        openTestURL();
        TableElement table = $(TableElement.class).get(0);
        table.scroll(TableScrollTest.SCROLL_VALUE);
        Assert.assertEquals(TableScrollTest.SCROLL_VALUE, getScrollTopValue(table));
    }
}

