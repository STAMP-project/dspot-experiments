package com.vaadin.tests.components.treegrid;


import Keys.DOWN;
import Keys.LEFT;
import Keys.RIGHT;
import Keys.SPACE;
import Keys.UP;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ParameterizedTB3Runner.class)
public class TreeGridBasicFeaturesTest extends MultiBrowserTest {
    private TreeGridElement grid;

    @Test
    public void toggle_collapse_server_side() {
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        selectMenuPath("Component", "Features", "Server-side expand", "Expand 0 | 0");
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
        // expanding already expanded item should have no effect
        selectMenuPath("Component", "Features", "Server-side expand", "Expand 0 | 0");
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
        selectMenuPath("Component", "Features", "Server-side collapse", "Collapse 0 | 0");
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        // collapsing the same item twice should have no effect
        selectMenuPath("Component", "Features", "Server-side collapse", "Collapse 0 | 0");
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        selectMenuPath("Component", "Features", "Server-side expand", "Expand 1 | 1");
        // 1 | 1 not yet visible, shouldn't immediately expand anything
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        selectMenuPath("Component", "Features", "Server-side expand", "Expand 0 | 0");
        // 1 | 1 becomes visible and is also expanded
        Assert.assertEquals(9, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "2 | 0", "2 | 1", "2 | 2", "1 | 2" });
        // collapsing a leaf should have no effect
        selectMenuPath("Component", "Features", "Server-side collapse", "Collapse 2 | 1");
        Assert.assertEquals(9, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "2 | 0", "2 | 1", "2 | 2", "1 | 2" });
        // collapsing 0 | 0 should collapse the expanded 1 | 1
        selectMenuPath("Component", "Features", "Server-side collapse", "Collapse 0 | 0");
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        // expand 0 | 0 recursively
        selectMenuPath("Component", "Features", "Server-side expand", "Expand 0 | 0 recursively");
        Assert.assertEquals(15, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "1 | 0", "2 | 0" });
        // collapse 0 | 0 recursively
        selectMenuPath("Component", "Features", "Server-side collapse", "Collapse 0 | 0 recursively");
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        // expanding 0 | 0 should result in 3 additional nodes after recursive
        // collapse
        selectMenuPath("Component", "Features", "Server-side expand", "Expand 0 | 0");
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
        assertNoSystemNotifications();
        assertNoErrorNotifications();
    }

    @Test
    public void pending_expands_cleared_when_data_provider_set() {
        selectMenuPath("Component", "Features", "Server-side expand", "Expand 1 | 1");
        selectMenuPath("Component", "Features", "Set data provider", "LazyHierarchicalDataProvider");
        grid.expandWithClick(0);
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
    }

    @Test
    public void non_leaf_collapse_on_click() {
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        // Should expand "0 | 0"
        grid.getRow(0).getCell(0).findElement(By.className("v-treegrid-expander")).click();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
        // Should collapse "0 | 0"
        grid.getRow(0).getCell(0).findElement(By.className("v-treegrid-expander")).click();
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
    }

    @Test
    public void keyboard_navigation() {
        grid.getRow(0).getCell(0).click();
        // Should expand "0 | 0" without moving focus
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(RIGHT).perform();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
        Assert.assertTrue(grid.getRow(0).hasClassName("v-treegrid-row-focused"));
        Assert.assertFalse(grid.getRow(1).hasClassName("v-treegrid-row-focused"));
        // Should navigate 2 times down to "1 | 1"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(DOWN, DOWN).perform();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
        Assert.assertFalse(grid.getRow(0).hasClassName("v-treegrid-row-focused"));
        Assert.assertFalse(grid.getRow(1).hasClassName("v-treegrid-row-focused"));
        Assert.assertTrue(grid.getRow(2).hasClassName("v-treegrid-row-focused"));
        // Should expand "1 | 1" without moving focus
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(RIGHT).perform();
        Assert.assertEquals(9, grid.getRowCount());
        assertCellTexts(2, 0, new String[]{ "1 | 1", "2 | 0", "2 | 1", "2 | 2", "1 | 2" });
        Assert.assertTrue(grid.getRow(2).hasClassName("v-treegrid-row-focused"));
        // Should collapse "1 | 1"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(2, 0, new String[]{ "1 | 1", "1 | 2", "0 | 1" });
        Assert.assertTrue(grid.getRow(2).hasClassName("v-treegrid-row-focused"));
        // Should navigate to "0 | 0"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "1 | 0", "1 | 1", "1 | 2", "0 | 1" });
        Assert.assertTrue(grid.getRow(0).hasClassName("v-treegrid-row-focused"));
        // Should collapse "0 | 0"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        Assert.assertTrue(grid.getRow(0).hasClassName("v-treegrid-row-focused"));
        // Nothing should happen
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(LEFT).perform();
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "0 | 2" });
        Assert.assertTrue(grid.getRow(0).hasClassName("v-treegrid-row-focused"));
        assertNoErrorNotifications();
    }

    @Test
    public void keyboard_selection() {
        grid.getRow(0).getCell(0).click();
        // Should expand "0 | 0" without moving focus
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(RIGHT).perform();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
        // Should navigate 2 times down to "1 | 1"
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(DOWN, DOWN).perform();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[]{ "1 | 0", "1 | 1", "1 | 2" });
        Assert.assertFalse(grid.getRow(0).hasClassName("v-treegrid-row-focused"));
        Assert.assertFalse(grid.getRow(1).hasClassName("v-treegrid-row-focused"));
        Assert.assertTrue(grid.getRow(2).hasClassName("v-treegrid-row-focused"));
        // Should select "1 | 1" without moving focus
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue(grid.getRow(2).hasClassName("v-treegrid-row-selected"));
        // Should move focus but not selection
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(UP).perform();
        Assert.assertTrue(grid.getRow(1).hasClassName("v-treegrid-row-focused"));
        Assert.assertFalse(grid.getRow(2).hasClassName("v-treegrid-row-focused"));
        Assert.assertFalse(grid.getRow(1).hasClassName("v-treegrid-row-selected"));
        Assert.assertTrue(grid.getRow(2).hasClassName("v-treegrid-row-selected"));
        // Should select "1 | 0" without moving focus
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue(grid.getRow(1).hasClassName("v-treegrid-row-focused"));
        Assert.assertFalse(grid.getRow(2).hasClassName("v-treegrid-row-focused"));
        Assert.assertTrue(grid.getRow(1).hasClassName("v-treegrid-row-selected"));
        Assert.assertFalse(grid.getRow(2).hasClassName("v-treegrid-row-selected"));
        assertNoErrorNotifications();
    }

    @Test
    public void changing_hierarchy_column() {
        Assert.assertTrue(grid.getRow(0).getCell(0).isElementPresent(By.className("v-treegrid-expander")));
        Assert.assertFalse(grid.getRow(0).getCell(1).isElementPresent(By.className("v-treegrid-expander")));
        selectMenuPath("Component", "Features", "Set hierarchy column", "depth");
        Assert.assertFalse(grid.getRow(0).getCell(0).isElementPresent(By.className("v-treegrid-expander")));
        Assert.assertTrue(grid.getRow(0).getCell(1).isElementPresent(By.className("v-treegrid-expander")));
        selectMenuPath("Component", "Features", "Set hierarchy column", "string");
        Assert.assertTrue(grid.getRow(0).getCell(0).isElementPresent(By.className("v-treegrid-expander")));
        Assert.assertFalse(grid.getRow(0).getCell(1).isElementPresent(By.className("v-treegrid-expander")));
    }

    @Test
    public void expand_and_collapse_listeners() {
        selectMenuPath("Component", "State", "Expand listener");
        selectMenuPath("Component", "State", "Collapse listener");
        Assert.assertFalse(logContainsText("Item expanded (user originated: true): 0 | 0"));
        Assert.assertFalse(logContainsText("Item collapsed (user originated: true): 0 | 0"));
        grid.expandWithClick(0);
        Assert.assertTrue(logContainsText("Item expanded (user originated: true): 0 | 0"));
        Assert.assertFalse(logContainsText("Item collapsed (user originated: true): 0 | 0"));
        grid.collapseWithClick(0);
        Assert.assertTrue(logContainsText("Item expanded (user originated: true): 0 | 0"));
        Assert.assertTrue(logContainsText("Item collapsed (user originated: true): 0 | 0"));
        selectMenuPath("Component", "Features", "Server-side expand", "Expand 0 | 0");
        Assert.assertTrue(logContainsText("Item expanded (user originated: false): 0 | 0"));
        Assert.assertFalse(logContainsText("Item collapsed (user originated: false): 0 | 0"));
        selectMenuPath("Component", "Features", "Server-side collapse", "Collapse 0 | 0");
        Assert.assertTrue(logContainsText("Item expanded (user originated: false): 0 | 0"));
        Assert.assertTrue(logContainsText("Item collapsed (user originated: false): 0 | 0"));
        selectMenuPath("Component", "State", "Expand listener");
        selectMenuPath("Component", "State", "Collapse listener");
        grid.expandWithClick(1);
        grid.collapseWithClick(1);
        Assert.assertFalse(logContainsText("Item expanded (user originated: true): 0 | 1"));
        Assert.assertFalse(logContainsText("Item collapsed (user originated: true): 0 | 1"));
    }

    @Test
    public void expanded_nodes_stay_expanded_when_parent_expand_state_is_toggled() {
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        grid.collapseWithClick(0);
        grid.expandWithClick(0);
        assertCellTexts(0, 0, new String[]{ "0 | 0", "1 | 0", "2 | 0", "2 | 1", "2 | 2", "1 | 1", "1 | 2", "0 | 1", "0 | 2" });
        Assert.assertEquals(9, grid.getRowCount());
        grid.expandWithClick(7);
        grid.expandWithClick(8);
        grid.collapseWithClick(7);
        grid.collapseWithClick(0);
        grid.expandWithClick(1);
        assertCellTexts(0, 0, new String[]{ "0 | 0", "0 | 1", "1 | 0", "2 | 0", "2 | 1", "2 | 2", "1 | 1", "1 | 2", "0 | 2" });
        Assert.assertEquals(9, grid.getRowCount());
    }

    @Test
    public void change_renderer_of_hierarchy_column() {
        Assert.assertTrue("Cell style names should contain renderer name", grid.getCell(0, 0).getAttribute("class").contains("TextRenderer"));
        selectMenuPath("Component", "Features", "Hierarchy column renderer", "html");
        Assert.assertTrue("Cell style names should contain renderer name", grid.getCell(0, 0).getAttribute("class").contains("HtmlRenderer"));
        grid.expandWithClick(0);
        Assert.assertEquals("Not expanded", "1 | 0", grid.getCell(1, 0).getText());
    }
}

