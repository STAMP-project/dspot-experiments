package com.vaadin.tests.components.treegrid;


import Keys.DOWN;
import Keys.HOME;
import Keys.LEFT;
import Keys.RIGHT;
import Keys.UP;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


public class TreeGridHugeTreeNavigationTest extends MultiBrowserTest {
    private TreeGridElement grid;

    @Test
    public void keyboard_navigation() {
        grid.getRow(0).getCell(0).click();
        // Should navigate to "Granddad 1" and expand it
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(DOWN, RIGHT).perform();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1", "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2");
        checkRowFocused(1);
        // Should navigate to and expand "Dad 1/1"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(DOWN, DOWN, RIGHT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1", "Dad 1/0", "Dad 1/1", "Son 1/1/0", "Son 1/1/1", "Son 1/1/2", "Son 1/1/3");
        checkRowFocused(3);
        // Should navigate 100 items down
        Keys[] downKeyArr = new Keys[100];
        for (int i = 0; i < 100; i++) {
            downKeyArr[i] = Keys.DOWN;
        }
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(downKeyArr).perform();
        WebElement son1_1_99 = findFocusedRow();
        Assert.assertEquals("Son 1/1/99 --", son1_1_99.getText());
        // Should navigate to "Dad 1/1" back
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(HOME, DOWN, DOWN, DOWN).perform();
        WebElement dad1_1 = findFocusedRow();
        Assert.assertEquals("Dad 1/1 --", dad1_1.getText());
        // Should collapse "Dad 1/1"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1", "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2");
        checkRowFocused(3);
        // Should navigate to "Granddad 1"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1", "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2");
        checkRowFocused(1);
        // Should collapse "Granddad 1"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1", "Granddad 2");
        checkRowFocused(1);
        // Nothing should happen
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1", "Granddad 2");
        checkRowFocused(1);
        assertNoErrorNotifications();
    }

    @Test
    public void no_exception_when_calling_expand_or_collapse_twice() {
        // Currently the collapsed state is updated in a round trip to the
        // server, thus it is possible to trigger an expand on the same row
        // multiple times through the UI. This should not cause exceptions, but
        // rather ignore the redundant calls.
        grid.getRow(0).getCell(0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(RIGHT, RIGHT).perform();
        assertNoErrorNotifications();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT, LEFT).perform();
        assertNoErrorNotifications();
    }

    @Test
    public void uncollapsible_item() {
        grid.getRow(0).getCell(0).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(DOWN, DOWN, RIGHT).perform();
        grid.waitForVaadin();
        // expand Dad 2/1
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(DOWN, DOWN, RIGHT).perform();
        grid.waitForVaadin();
        assertNoErrorNotifications();
        assertCellTexts(5, 0, "Son 2/1/0");
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        grid.waitForVaadin();
        assertNoErrorNotifications();
        assertCellTexts(5, 0, "Son 2/1/0");
    }

    @Test
    public void can_toggle_collapse_on_row_that_is_no_longer_in_cache() {
        grid.getRow(0).getCell(0).click();
        // Expand 2 levels
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(RIGHT).perform();
        grid.waitForVaadin();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(DOWN, RIGHT).perform();
        grid.waitForVaadin();
        grid.scrollToRow(200);
        grid.waitForVaadin();
        // Jump into view
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        grid.waitForVaadin();
        // Collapse
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        grid.waitForVaadin();
        Assert.assertEquals(6, grid.getRowCount());
        // Expand
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(RIGHT, UP).perform();
        grid.waitForVaadin();
        grid.scrollToRow(200);
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(RIGHT).perform();
        grid.waitForVaadin();
        Assert.assertEquals(306, grid.getRowCount());
    }
}

