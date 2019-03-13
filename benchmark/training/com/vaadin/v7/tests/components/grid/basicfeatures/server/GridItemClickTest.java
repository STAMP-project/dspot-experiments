package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridItemClickTest extends GridBasicFeaturesTest {
    @Test
    public void testItemClick() {
        openTestURL();
        selectMenuPath("Component", "State", "ItemClickListener");
        GridCellElement cell = getGridElement().getCell(3, 2);
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(cell).click().perform();
        Assert.assertTrue("No click in log", logContainsText(itemClickOn(3, 2, false)));
    }

    @Test
    public void testItemDoubleClick() {
        openTestURL();
        selectMenuPath("Component", "State", "ItemClickListener");
        GridCellElement cell = getGridElement().getCell(3, 2);
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(cell).doubleClick().perform();
        Assert.assertTrue("No double click in log", logContainsText(itemClickOn(3, 2, true)));
    }
}

