package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridItemSetChangeTest extends SingleBrowserTest {
    @Test
    public void testValueChangeListenersWorkAfterItemSetChange() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals("Last name initially wrong", "Bar", grid.getCell(0, 1).getText());
        $(ButtonElement.class).caption("Modify").first().click();
        Assert.assertEquals("Last name was not updated", "Spam", grid.getCell(0, 1).getText());
        $(ButtonElement.class).caption("Reset").first().click();
        Assert.assertEquals("Last name was not updated on reset", "Baz", grid.getCell(0, 1).getText());
        $(ButtonElement.class).caption("Modify").first().click();
        Assert.assertEquals("Last name was not updated after reset modification", "Spam", grid.getCell(0, 1).getText());
    }
}

