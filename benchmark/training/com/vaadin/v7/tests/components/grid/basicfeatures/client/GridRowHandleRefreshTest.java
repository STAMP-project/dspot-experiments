package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridRowHandleRefreshTest extends GridBasicClientFeaturesTest {
    @Test
    public void testRefreshingThroughRowHandle() {
        openTestURL();
        Assert.assertEquals("Unexpected initial state", "(0, 0)", getGridElement().getCell(0, 0).getText());
        selectMenuPath("Component", "State", "Edit and refresh Row 0");
        Assert.assertEquals("Cell contents did not update correctly", "Foo", getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testDelayedRefreshingThroughRowHandle() throws InterruptedException {
        openTestURL();
        Assert.assertEquals("Unexpected initial state", "(0, 0)", getGridElement().getCell(0, 0).getText());
        selectMenuPath("Component", "State", "Delayed edit of Row 0");
        // Still the same data
        Assert.assertEquals("Cell contents did not update correctly", "(0, 0)", getGridElement().getCell(0, 0).getText());
        sleep(5000);
        // Data should be updated
        Assert.assertEquals("Cell contents did not update correctly", "Bar", getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testRefreshingWhenNotInViewThroughRowHandle() {
        openTestURL();
        Assert.assertEquals("Unexpected initial state", "(0, 0)", getGridElement().getCell(0, 0).getText());
        getGridElement().scrollToRow(100);
        selectMenuPath("Component", "State", "Edit and refresh Row 0");
        Assert.assertEquals("Cell contents did not update correctly", "Foo", getGridElement().getCell(0, 0).getText());
    }
}

