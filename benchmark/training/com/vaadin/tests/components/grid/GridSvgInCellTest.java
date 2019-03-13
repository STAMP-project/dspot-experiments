package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class GridSvgInCellTest extends SingleBrowserTest {
    @Test
    public void moveMouseOverSvgInCell() {
        GridElement grid = $(GridElement.class).first();
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(grid.getCell(0, 0)).perform();
        assertNoErrorNotifications();
    }
}

