package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class TreeGridAriaRowcountTest extends SingleBrowserTest {
    private TreeGridElement grid;

    @Test
    public void checkTreeGridAriaRowcount() {
        openTestURL();
        grid = $(TreeGridElement.class).first();
        // default with 1 header row and 2 body rows.
        Assert.assertTrue("Grid should have 3 rows", containsRows(3));
        $(ButtonElement.class).caption("addFooter").first().click();
        // 1 header row, 2 body rows and 1 footer row.
        Assert.assertTrue("Grid should have 4 rows", containsRows(4));
        $(ButtonElement.class).caption("removeFooter").first().click();
        // 1 header row and 2 body rows.
        Assert.assertTrue("Grid should have 3 rows", containsRows(3));
        $(ButtonElement.class).caption("addHeader").first().click();
        // 2 header row and 2 body rows.
        Assert.assertTrue("Grid should have 4 rows", containsRows(4));
        $(ButtonElement.class).caption("removeHeader").first().click();
        // 1 header row and 2 body rows.
        Assert.assertTrue("Grid should have 3 rows", containsRows(3));
        $(ButtonElement.class).caption("setItemsTo3").first().click();
        // 1 header row and 3 body rows.
        Assert.assertTrue("Grid should have 4 rows", containsRows(4));
        $(ButtonElement.class).caption("setItemsTo6").first().click();
        // 1 header row and 6 body rows.
        Assert.assertTrue("Grid should have 7 rows", containsRows(7));
        $(ButtonElement.class).caption("updateAll").first().click();
        // 2 header rows, 4 body rows and 1 footer row.
        Assert.assertTrue("Grid should have 7 rows", containsRows(7));
    }
}

