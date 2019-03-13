package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridScrollTest extends GridBasicFeaturesTest {
    @Test
    public void testCorrectItemRequestsOnScroll() {
        openTestURL();
        Assert.assertTrue("Initial push from server not found", getLogRow(1).equals("0. Requested items 0 - 40"));
        // Client response varies a bit between browsers as different amount of
        // rows is cached.
        Assert.assertTrue("First row request from client not found", getLogRow(0).startsWith("1. Requested items 0 - "));
        selectMenuPath("Component", "Size", "HeightMode Row");
        selectMenuPath("Settings", "Clear log");
        $(GridElement.class).first().scrollToRow(40);
        Assert.assertEquals("Log row did not contain expected item request", "0. Requested items 0 - 86", getLogRow(0));
        Assert.assertEquals("There should be only one log row", " ", getLogRow(1));
        selectMenuPath("Settings", "Clear log");
        $(GridElement.class).first().scrollToRow(100);
        Assert.assertEquals("Log row did not contain expected item request", "0. Requested items 47 - 146", getLogRow(0));
        Assert.assertEquals("There should be only one log row", " ", getLogRow(1));
        selectMenuPath("Settings", "Clear log");
        $(GridElement.class).first().scrollToRow(300);
        Assert.assertEquals("Log row did not contain expected item request", "0. Requested items 247 - 346", getLogRow(0));
        Assert.assertEquals("There should be only one log row", " ", getLogRow(1));
    }

    @Test
    public void workPendingWhileScrolling() {
        openTestURL("theme=valo");
        String script = "var c = window.vaadin.clients.runcomvaadintestscomponentsgridbasicfeaturesGridBasicFeatures;\n" + // Scroll down and cause lazy loading
        ("c.getElementByPath(\"//Grid[0]#cell[21]\"); \n" + "return c.isActive();");
        Boolean active = ((Boolean) (executeScript(script)));
        Assert.assertTrue("Grid should be marked to have workPending while scrolling", active);
    }

    @Test
    public void scrollIntoViewThroughSubPart() {
        openTestURL("theme=valo");
        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals("(10, 0)", grid.getCell(10, 0).getText());
    }
}

