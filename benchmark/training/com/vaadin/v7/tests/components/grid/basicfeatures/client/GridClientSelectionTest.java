package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import Keys.ARROW_DOWN;
import Keys.SPACE;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridClientSelectionTest extends GridBasicClientFeaturesTest {
    @Test
    public void testChangeSelectionMode() {
        openTestURL();
        setSelectionModelNone();
        Assert.assertTrue("First column was selection column", getGridElement().getCell(0, 0).getText().equals("(0, 0)"));
        setSelectionModelMulti();
        Assert.assertTrue("First column was not selection column", getGridElement().getCell(0, 1).getText().equals("(0, 0)"));
    }

    @Test
    public void testSelectAllCheckbox() {
        openTestURL();
        setSelectionModelMulti();
        selectMenuPath("Component", "DataSource", "Reset with 100 rows of Data");
        GridCellElement header = getGridElement().getHeaderCell(0, 0);
        Assert.assertTrue("No checkbox", header.isElementPresent(By.tagName("input")));
        header.findElement(By.tagName("input")).click();
        for (int i = 0; i < 100; i += 10) {
            Assert.assertTrue((("Row " + i) + " was not selected."), getGridElement().getRow(i).isSelected());
        }
        header.findElement(By.tagName("input")).click();
        Assert.assertFalse("Row 52 was still selected", getGridElement().getRow(52).isSelected());
    }

    @Test
    public void testSelectAllCheckboxWhenChangingModels() {
        openTestURL();
        GridCellElement header;
        header = getGridElement().getHeaderCell(0, 0);
        Assert.assertFalse("Check box shouldn't have been in header for None Selection Model", header.isElementPresent(By.tagName("input")));
        setSelectionModelMulti();
        header = getGridElement().getHeaderCell(0, 0);
        Assert.assertTrue("Multi Selection Model should have select all checkbox", header.isElementPresent(By.tagName("input")));
        setSelectionModelSingle(true);
        header = getGridElement().getHeaderCell(0, 0);
        Assert.assertFalse("Check box shouldn't have been in header for Single Selection Model", header.isElementPresent(By.tagName("input")));
        setSelectionModelNone();
        header = getGridElement().getHeaderCell(0, 0);
        Assert.assertFalse("Check box shouldn't have been in header for None Selection Model", header.isElementPresent(By.tagName("input")));
    }

    @Test
    public void testDeselectAllowedMouseInput() {
        openTestURL();
        setSelectionModelSingle(true);
        getGridElement().getCell(5, 1).click();
        Assert.assertTrue("Row 5 should be selected after clicking", isRowSelected(5));
        getGridElement().getCell(7, 1).click();
        Assert.assertFalse("Row 5 should be deselected after clicking another row", isRowSelected(5));
        Assert.assertTrue("Row 7 should be selected after clicking", isRowSelected(7));
        getGridElement().getCell(7, 1).click();
        Assert.assertFalse("Row should be deselected after clicking again", isRowSelected(7));
    }

    @Test
    public void testDeselectAllowedKeyboardInput() {
        openTestURL();
        setSelectionModelSingle(true);
        getGridElement().getHeaderCell(0, 0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Row 0 should be selected after pressing space", isRowSelected(0));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertFalse("Row 0 should be deselected after pressing space another row", isRowSelected(0));
        Assert.assertTrue("Row 1 should be selected after pressing space", isRowSelected(1));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertFalse("Row should be deselected after pressing space again", isRowSelected(1));
    }

    @Test
    public void testDeselectNotAllowedMouseInput() {
        openTestURL();
        setSelectionModelSingle(false);
        getGridElement().getCell(5, 1).click();
        Assert.assertTrue("Row 5 should be selected after clicking", isRowSelected(5));
        getGridElement().getCell(7, 1).click();
        Assert.assertFalse("Row 5 should be deselected after clicking another row", isRowSelected(5));
        Assert.assertTrue("Row 7 should be selected after clicking", isRowSelected(7));
        getGridElement().getCell(7, 1).click();
        Assert.assertTrue("Row should remain selected after clicking again", isRowSelected(7));
    }

    @Test
    public void testDeselectNotAllowedKeyboardInput() {
        openTestURL();
        setSelectionModelSingle(false);
        getGridElement().getHeaderCell(0, 0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Row 0 should be selected after pressing space", isRowSelected(0));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertFalse("Row 0 should be deselected after pressing space another row", isRowSelected(0));
        Assert.assertTrue("Row 1 should be selected after pressing space", isRowSelected(1));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Row should remain selected after pressing space again", isRowSelected(1));
    }

    @Test
    public void testChangeSelectionModelUpdatesUI() {
        openTestURL();
        setSelectionModelSingle(true);
        getGridElement().getCell(5, 1).click();
        Assert.assertTrue("Row 5 should be selected after clicking", isRowSelected(5));
        setSelectionModelNone();
        Assert.assertFalse("Row 5 should not be selected after changing selection model", isRowSelected(5));
    }
}

