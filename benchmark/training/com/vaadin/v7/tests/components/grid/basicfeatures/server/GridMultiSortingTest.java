package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import Keys.SHIFT;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridMultiSortingTest extends GridBasicFeaturesTest {
    @Test
    public void testUserMultiColumnSorting() {
        openTestURL();
        selectMenuPath("Component", "Columns", "Column 11", "Column 11 Width", "Auto");
        GridCellElement cell = getGridElement().getHeaderCell(0, 11);
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(cell, 20, 10).click().perform();
        new org.openqa.selenium.interactions.Actions(driver).keyDown(SHIFT).perform();
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(getGridElement().getHeaderCell(0, 0), 20, 10).click().perform();
        new org.openqa.selenium.interactions.Actions(driver).keyUp(SHIFT).perform();
        String prev = getGridElement().getCell(0, 11).getAttribute("innerHTML");
        for (int i = 1; i <= 6; ++i) {
            Assert.assertEquals("Column 11 should contain same values.", prev, getGridElement().getCell(i, 11).getAttribute("innerHTML"));
        }
        prev = getGridElement().getCell(0, 0).getText();
        for (int i = 1; i <= 6; ++i) {
            Assert.assertTrue("Grid is not sorted by column 0.", ((prev.compareTo(getGridElement().getCell(i, 0).getText())) < 0));
        }
    }
}

