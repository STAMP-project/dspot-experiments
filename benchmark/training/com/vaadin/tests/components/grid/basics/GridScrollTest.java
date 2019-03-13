package com.vaadin.tests.components.grid.basics;


import com.vaadin.testbench.elements.GridElement;
import org.junit.Assert;
import org.junit.Test;


public class GridScrollTest extends GridBasicsTest {
    @Test
    public void workPendingWhileScrolling() {
        openTestURL("theme=valo");
        String script = "var c = window.vaadin.clients.runcomvaadintestscomponentsgridbasicsGridBasics;\n" + // Scroll down and cause lazy loading
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

