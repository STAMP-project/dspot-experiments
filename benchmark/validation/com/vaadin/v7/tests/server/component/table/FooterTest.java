package com.vaadin.v7.tests.server.component.table;


import com.vaadin.v7.ui.Table;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for testing the footer API
 */
public class FooterTest {
    /**
     * Tests if setting the footer visibility works properly
     */
    @Test
    public void testFooterVisibility() {
        Table table = new Table("Test table", FooterTest.createContainer());
        // The footer should by default be hidden
        Assert.assertFalse(table.isFooterVisible());
        // Set footer visibility to tru should be reflected in the
        // isFooterVisible() method
        table.setFooterVisible(true);
        Assert.assertTrue(table.isFooterVisible());
    }

    /**
     * Tests adding footers to the columns
     */
    @Test
    public void testAddingFooters() {
        Table table = new Table("Test table", FooterTest.createContainer());
        // Table should not contain any footers at initialization
        Assert.assertNull(table.getColumnFooter("col1"));
        Assert.assertNull(table.getColumnFooter("col2"));
        Assert.assertNull(table.getColumnFooter("col3"));
        // Adding column footer
        table.setColumnFooter("col1", "Footer1");
        Assert.assertEquals("Footer1", table.getColumnFooter("col1"));
        // Add another footer
        table.setColumnFooter("col2", "Footer2");
        Assert.assertEquals("Footer2", table.getColumnFooter("col2"));
        // Add footer for a non-existing column
        table.setColumnFooter("fail", "FooterFail");
    }

    /**
     * Test removing footers
     */
    @Test
    public void testRemovingFooters() {
        Table table = new Table("Test table", FooterTest.createContainer());
        table.setColumnFooter("col1", "Footer1");
        table.setColumnFooter("col2", "Footer2");
        // Test removing footer
        Assert.assertNotNull(table.getColumnFooter("col1"));
        table.setColumnFooter("col1", null);
        Assert.assertNull(table.getColumnFooter("col1"));
        // The other footer should still be there
        Assert.assertNotNull(table.getColumnFooter("col2"));
        // Remove non-existing footer
        table.setColumnFooter("fail", null);
    }
}

