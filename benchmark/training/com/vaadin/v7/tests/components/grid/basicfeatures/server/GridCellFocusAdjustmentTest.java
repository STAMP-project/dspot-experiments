package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import Keys.UP;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridCellFocusAdjustmentTest extends GridBasicFeaturesTest {
    @Test
    public void testCellFocusWithAddAndRemoveRows() {
        openTestURL();
        GridElement grid = getGridElement();
        grid.getCell(0, 0).click();
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertTrue("Cell focus was not moved when adding a row", grid.getCell(1, 0).isFocused());
        selectMenuPath("Component", "Body rows", "Add 18 rows");
        Assert.assertTrue("Cell focus was not moved when adding multiple rows", grid.getCell(19, 0).isFocused());
        for (int i = 18; i <= 0; --i) {
            selectMenuPath("Component", "Body rows", "Remove first row");
            Assert.assertTrue("Cell focus was not moved when removing a row", grid.getCell(i, 0).isFocused());
        }
    }

    @Test
    public void testCellFocusOffsetWhileInDifferentSection() {
        openTestURL();
        getGridElement().getCell(0, 0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(UP).perform();
        Assert.assertTrue("Header 0,0 should've become focused", getGridElement().getHeaderCell(0, 0).isFocused());
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertTrue("Header 0,0 should've remained focused", getGridElement().getHeaderCell(0, 0).isFocused());
    }

    @Test
    public void testCellFocusOffsetWhileInSameSectionAndInsertedAbove() {
        openTestURL();
        Assert.assertTrue("Body 0,0 should've gotten focus", getGridElement().getCell(0, 0).isFocused());
        selectMenuPath("Component", "Body rows", "Add first row");
        Assert.assertTrue("Body 1,0 should've gotten focus", getGridElement().getCell(1, 0).isFocused());
    }

    @Test
    public void testCellFocusOffsetWhileInSameSectionAndInsertedBelow() {
        openTestURL();
        Assert.assertTrue("Body 0,0 should've gotten focus", getGridElement().getCell(0, 0).isFocused());
        selectMenuPath("Component", "Body rows", "Add third row");
        Assert.assertTrue("Body 0,0 should've remained focused", getGridElement().getCell(0, 0).isFocused());
    }
}

