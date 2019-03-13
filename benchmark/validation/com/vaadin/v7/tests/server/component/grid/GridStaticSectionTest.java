package com.vaadin.v7.tests.server.component.grid;


import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;
import org.junit.Assert;
import org.junit.Test;


public class GridStaticSectionTest extends Grid {
    private Indexed dataSource = new IndexedContainer();

    @Test
    public void testAddAndRemoveHeaders() {
        Assert.assertEquals(1, getHeaderRowCount());
        prependHeaderRow();
        Assert.assertEquals(2, getHeaderRowCount());
        removeHeaderRow(0);
        Assert.assertEquals(1, getHeaderRowCount());
        removeHeaderRow(0);
        Assert.assertEquals(0, getHeaderRowCount());
        Assert.assertEquals(null, getDefaultHeaderRow());
        HeaderRow row = appendHeaderRow();
        Assert.assertEquals(1, getHeaderRowCount());
        Assert.assertEquals(null, getDefaultHeaderRow());
        setDefaultHeaderRow(row);
        Assert.assertEquals(row, getDefaultHeaderRow());
    }

    @Test
    public void testAddAndRemoveFooters() {
        // By default there are no footer rows
        Assert.assertEquals(0, getFooterRowCount());
        FooterRow row = appendFooterRow();
        Assert.assertEquals(1, getFooterRowCount());
        prependFooterRow();
        Assert.assertEquals(2, getFooterRowCount());
        Assert.assertEquals(row, getFooterRow(1));
        removeFooterRow(0);
        Assert.assertEquals(1, getFooterRowCount());
        removeFooterRow(0);
        Assert.assertEquals(0, getFooterRowCount());
    }

    @Test
    public void testUnusedPropertyNotInCells() {
        removeColumn("firstName");
        Assert.assertNull("firstName cell was not removed from existing row", getDefaultHeaderRow().getCell("firstName"));
        HeaderRow newRow = appendHeaderRow();
        Assert.assertNull("firstName cell was created when it should not.", newRow.getCell("firstName"));
        addColumn("firstName");
        Assert.assertNotNull("firstName cell was not created for default row when added again", getDefaultHeaderRow().getCell("firstName"));
        Assert.assertNotNull("firstName cell was not created for new row when added again", newRow.getCell("firstName"));
    }

    @Test
    public void testJoinHeaderCells() {
        HeaderRow mergeRow = prependHeaderRow();
        mergeRow.join("firstName", "lastName").setText("Name");
        mergeRow.join(mergeRow.getCell("streetAddress"), mergeRow.getCell("zipCode"));
    }

    @Test(expected = IllegalStateException.class)
    public void testJoinHeaderCellsIncorrectly() throws Throwable {
        HeaderRow mergeRow = prependHeaderRow();
        mergeRow.join("firstName", "zipCode").setText("Name");
        sanityCheck();
    }

    @Test
    public void testJoinAllFooterCells() {
        FooterRow mergeRow = prependFooterRow();
        mergeRow.join(dataSource.getContainerPropertyIds().toArray()).setText("All the stuff.");
    }
}

