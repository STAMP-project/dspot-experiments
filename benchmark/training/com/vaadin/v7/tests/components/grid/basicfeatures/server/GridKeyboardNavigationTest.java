package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import Keys.ARROW_DOWN;
import Keys.ARROW_LEFT;
import Keys.ARROW_RIGHT;
import Keys.ARROW_UP;
import Keys.END;
import Keys.HOME;
import Keys.PAGE_DOWN;
import Keys.PAGE_UP;
import Keys.SHIFT;
import Keys.TAB;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class GridKeyboardNavigationTest extends GridBasicFeaturesTest {
    @Test
    public void testCellFocusOnClick() {
        openTestURL();
        GridElement grid = getGridElement();
        Assert.assertTrue("Body cell 0, 0 is not focused on init.", grid.getCell(0, 0).isFocused());
        grid.getCell(5, 2).click();
        Assert.assertFalse("Body cell 0, 0 was still focused after clicking", grid.getCell(0, 0).isFocused());
        Assert.assertTrue("Body cell 5, 2 is not focused after clicking", grid.getCell(5, 2).isFocused());
    }

    @Test
    public void testCellNotFocusedWhenRendererHandlesEvent() {
        openTestURL();
        GridElement grid = getGridElement();
        Assert.assertTrue("Body cell 0, 0 is not focused on init.", grid.getCell(0, 0).isFocused());
        grid.getHeaderCell(0, 3).click();
        Assert.assertFalse("Body cell 0, 0 is focused after click on header.", grid.getCell(0, 0).isFocused());
        Assert.assertTrue("Header cell 0, 3 is not focused after click on header.", grid.getHeaderCell(0, 3).isFocused());
    }

    @Test
    public void testSimpleKeyboardNavigation() {
        openTestURL();
        GridElement grid = getGridElement();
        grid.getCell(0, 0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        Assert.assertTrue("Body cell 1, 0 is not focused after keyboard navigation.", grid.getCell(1, 0).isFocused());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_RIGHT).perform();
        Assert.assertTrue("Body cell 1, 1 is not focused after keyboard navigation.", grid.getCell(1, 1).isFocused());
        int i;
        for (i = 1; i < 40; ++i) {
            new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        }
        Assert.assertFalse("Grid has not scrolled with cell focus", isElementPresent(By.xpath("//td[text() = '(0, 0)']")));
        Assert.assertTrue("Cell focus is not visible", isElementPresent(By.xpath((("//td[text() = '(" + i) + ", 0)']"))));
        Assert.assertTrue((("Body cell " + i) + ", 1 is not focused"), grid.getCell(i, 1).isFocused());
    }

    @Test
    public void testNavigateFromHeaderToBody() {
        openTestURL();
        GridElement grid = getGridElement();
        grid.scrollToRow(300);
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(grid.getHeaderCell(0, 7)).click().perform();
        grid.scrollToRow(280);
        Assert.assertTrue("Header cell is not focused.", grid.getHeaderCell(0, 7).isFocused());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_DOWN).perform();
        Assert.assertTrue("Body cell 280, 7 is not focused", grid.getCell(280, 7).isFocused());
    }

    @Test
    public void testNavigationFromFooterToBody() {
        openTestURL();
        selectMenuPath("Component", "Footer", "Visible");
        GridElement grid = getGridElement();
        grid.scrollToRow(300);
        grid.getFooterCell(0, 2).click();
        Assert.assertTrue("Footer cell does not have focus.", grid.getFooterCell(0, 2).isFocused());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_UP).perform();
        Assert.assertTrue("Body cell 300, 2 does not have focus.", grid.getCell(300, 2).isFocused());
    }

    @Test
    public void testNavigateBetweenHeaderAndBodyWithTab() {
        openTestURL();
        GridElement grid = getGridElement();
        grid.getCell(10, 2).click();
        Assert.assertTrue("Body cell 10, 2 does not have focus", grid.getCell(10, 2).isFocused());
        new org.openqa.selenium.interactions.Actions(getDriver()).keyDown(SHIFT).sendKeys(TAB).keyUp(SHIFT).perform();
        Assert.assertTrue("Header cell 0, 2 does not have focus", grid.getHeaderCell(0, 2).isFocused());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB).perform();
        Assert.assertTrue("Body cell 10, 2 does not have focus", grid.getCell(10, 2).isFocused());
        // Navigate out of the Grid and try to navigate with arrow keys.
        new org.openqa.selenium.interactions.Actions(getDriver()).keyDown(SHIFT).sendKeys(TAB).sendKeys(TAB).keyUp(SHIFT).sendKeys(ARROW_DOWN).perform();
        Assert.assertTrue("Header cell 0, 2 does not have focus", grid.getHeaderCell(0, 2).isFocused());
    }

    @Test
    public void testNavigateBetweenFooterAndBodyWithTab() {
        openTestURL();
        selectMenuPath("Component", "Footer", "Visible");
        GridElement grid = getGridElement();
        grid.getCell(10, 2).click();
        Assert.assertTrue("Body cell 10, 2 does not have focus", grid.getCell(10, 2).isFocused());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB).perform();
        Assert.assertTrue("Footer cell 0, 2 does not have focus", grid.getFooterCell(0, 2).isFocused());
        new org.openqa.selenium.interactions.Actions(getDriver()).keyDown(SHIFT).sendKeys(TAB).keyUp(SHIFT).perform();
        Assert.assertTrue("Body cell 10, 2 does not have focus", grid.getCell(10, 2).isFocused());
        // Navigate out of the Grid and try to navigate with arrow keys.
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB).sendKeys(TAB).sendKeys(ARROW_UP).perform();
        Assert.assertTrue("Footer cell 0, 2 does not have focus", grid.getFooterCell(0, 2).isFocused());
    }

    @Test
    public void testHomeEnd() throws Exception {
        openTestURL();
        getGridElement().getCell(100, 2).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(HOME).perform();
        Assert.assertTrue("First row is not visible", getGridElement().getCell(0, 2).isDisplayed());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(END).perform();
        Assert.assertTrue("Last row cell not visible", getGridElement().getCell(((GridBasicFeatures.ROWS) - 1), 2).isDisplayed());
    }

    @Test
    public void testPageUpPageDown() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Size", "HeightMode Row");
        getGridElement().getCell(9, 2).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(PAGE_DOWN).perform();
        Assert.assertTrue("Row 17 did not become visible", isElementPresent(By.xpath("//td[text() = '(17, 2)']")));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(PAGE_DOWN).perform();
        Assert.assertTrue("Row 25 did not become visible", isElementPresent(By.xpath("//td[text() = '(25, 2)']")));
        checkFocusedCell(29, 2, 4);
        getGridElement().getCell(41, 2).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(PAGE_UP).perform();
        Assert.assertTrue("Row 33 did not become visible", isElementPresent(By.xpath("//td[text() = '(33, 2)']")));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(PAGE_UP).perform();
        Assert.assertTrue("Row 25 did not become visible", isElementPresent(By.xpath("//td[text() = '(25, 2)']")));
        checkFocusedCell(21, 2, 4);
    }

    @Test
    public void testNavigateOverHiddenColumnToFrozenColumn() {
        openTestURL();
        setFrozenColumns(3);
        toggleColumnHidden(1);
        getGridElement().getCell(0, 2).click();
        assertFocusedCell(0, 2);
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_LEFT).perform();
        assertFocusedCell(0, 1);
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_LEFT).perform();
        assertFocusedCell(0, 0);
    }
}

