package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TableScrollAfterAddRowTest extends MultiBrowserTest {
    @Test
    public void testJumpToFirstRow() throws InterruptedException {
        jumpToFifteenthRow();
        sleep(300);
        jumpToFirstRow();
        Assert.assertEquals("0", getCurrentPageFirstItemIndex());
    }

    @Test
    public void testAddRowAfterJumpToLastRow() throws InterruptedException {
        jumpToLastRow();
        addRow();
        sleep(200);
        Assert.assertEquals("85", getCurrentPageFirstItemIndex());
    }

    @Test
    public void testAddRowAfterJumpingToLastRowAndScrollingUp() throws InterruptedException {
        jumpToLastRow();
        scrollUp();
        addRow();
        sleep(200);
        Assert.assertNotEquals("86", getCurrentPageFirstItemIndex());
    }
}

