package com.vaadin.tests.components.grid;


import Keys.DOWN;
import Keys.SPACE;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basics.GridBasicsTest;
import org.junit.Assert;
import org.junit.Test;


public class GridSelectionTest extends GridBasicsTest {
    @Test
    public void testSelectOnOff() throws Exception {
        openTestURL();
        setSelectionModelMulti();
        Assert.assertFalse("row shouldn't start out as selected", getRow(0).isSelected());
        toggleFirstRowSelection();
        Assert.assertTrue("row should become selected", getRow(0).isSelected());
        toggleFirstRowSelection();
        Assert.assertFalse("row shouldn't remain selected", getRow(0).isSelected());
        toggleFirstRowSelection();
        Assert.assertTrue("row should become selected", getRow(0).isSelected());
        getGridElement().getCell(0, 0).click();
        Assert.assertFalse("row shouldn't remain selected", getRow(0).isSelected());
    }

    @Test
    public void testSelectOnScrollOffScroll() throws Exception {
        openTestURL();
        setSelectionModelMulti();
        Assert.assertFalse("row shouldn't start out as selected", getRow(0).isSelected());
        toggleFirstRowSelection();
        Assert.assertTrue("row should become selected", getRow(0).isSelected());
        scrollGridVerticallyTo(10000);// make sure the row is out of cache

        scrollGridVerticallyTo(0);// scroll it back into view

        Assert.assertTrue(("row should still be selected when scrolling " + "back into view"), getRow(0).isSelected());
    }

    @Test
    public void testSelectScrollOnScrollOff() throws Exception {
        openTestURL();
        setSelectionModelMulti();
        Assert.assertFalse("row shouldn't start out as selected", getRow(0).isSelected());
        scrollGridVerticallyTo(10000);// make sure the row is out of cache

        toggleFirstRowSelection();
        scrollGridVerticallyTo(0);// scroll it back into view

        Assert.assertTrue(("row should still be selected when scrolling " + "back into view"), getRow(0).isSelected());
        toggleFirstRowSelection();
        Assert.assertFalse("row shouldn't remain selected", getRow(0).isSelected());
    }

    @Test
    public void testSelectScrollOnOffScroll() throws Exception {
        openTestURL();
        setSelectionModelMulti();
        Assert.assertFalse("row shouldn't start out as selected", getRow(0).isSelected());
        toggleFirstRowSelection();
        Assert.assertTrue("row should be selected", getRow(0).isSelected());
        scrollGridVerticallyTo(10000);// make sure the row is out of cache

        toggleFirstRowSelection();
        scrollGridVerticallyTo(0);
        Assert.assertFalse(("row shouldn't be selected when scrolling " + "back into view"), getRow(0).isSelected());
    }

    @Test
    public void testSingleSelectionUpdatesFromServer() {
        openTestURL();
        setSelectionModelSingle();
        GridElement grid = getGridElement();
        Assert.assertFalse("First row was selected from start", grid.getRow(0).isSelected());
        toggleFirstRowSelection();
        Assert.assertTrue("First row was not selected.", getRow(0).isSelected());
        Assert.assertTrue("Selection event was not correct", logContainsText("SingleSelectionEvent: Selected: DataObject[0]"));
        grid.getCell(0, 0).click();
        Assert.assertFalse("First row was not deselected.", getRow(0).isSelected());
        Assert.assertTrue("Deselection event was not correct", logContainsText("SingleSelectionEvent: Selected: none"));
        grid.getCell(5, 0).click();
        Assert.assertTrue("Fifth row was not selected.", getRow(5).isSelected());
        Assert.assertFalse("First row was still selected.", getRow(0).isSelected());
        Assert.assertTrue("Selection event was not correct", logContainsText("SingleSelectionEvent: Selected: DataObject[5]"));
        grid.getCell(0, 6).click();
        Assert.assertTrue("Selection event was not correct", logContainsText("SingleSelectionEvent: Selected: DataObject[0]"));
        toggleFirstRowSelection();
        Assert.assertTrue("Selection event was not correct", logContainsText("SingleSelectionEvent: Selected: none"));
        Assert.assertFalse("First row was still selected.", getRow(0).isSelected());
        Assert.assertFalse("Fifth row was still selected.", getRow(5).isSelected());
        grid.scrollToRow(600);
        grid.getCell(595, 4).click();
        Assert.assertTrue("Row 595 was not selected.", getRow(595).isSelected());
        Assert.assertTrue("Selection event was not correct", logContainsText("SingleSelectionEvent: Selected: DataObject[595]"));
        toggleFirstRowSelection();
        Assert.assertFalse("Row 595 was still selected.", getRow(595).isSelected());
        Assert.assertTrue("First row was not selected.", getRow(0).isSelected());
        Assert.assertTrue("Selection event was not correct", logContainsText("SingleSelectionEvent: Selected: DataObject[0]"));
    }

    @Test
    public void testKeyboardWithMultiSelection() {
        openTestURL();
        setSelectionModelMulti();
        GridElement grid = getGridElement();
        grid.getCell(3, 1).click();
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Grid row 3 was not selected with space key.", grid.getRow(3).isSelected());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Grid row 3 was not deselected with space key.", (!(grid.getRow(3).isSelected())));
        grid.scrollToRow(500);
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Grid row 3 was not selected with space key.", grid.getRow(3).isSelected());
    }

    @Test
    public void testKeyboardWithSingleSelection() {
        openTestURL();
        setSelectionModelSingle();
        GridElement grid = getGridElement();
        grid.getCell(3, 1).click();
        Assert.assertTrue("Grid row 3 was not selected with clicking.", grid.getRow(3).isSelected());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Grid row 3 was not deselected with space key.", (!(grid.getRow(3).isSelected())));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Grid row 3 was not selected with space key.", grid.getRow(3).isSelected());
        grid.scrollToRow(500);
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE).perform();
        Assert.assertTrue("Grid row 3 was not deselected with space key.", (!(grid.getRow(3).isSelected())));
    }

    @Test
    public void testChangeSelectionModelUpdatesUI() {
        openTestURL();
        setSelectionModelMulti();
        getGridElement().getCell(5, 0).click();
        Assert.assertTrue("Row should be selected after clicking", getRow(5).isSelected());
        setSelectionModelSingle();
        Assert.assertFalse("Row should not be selected after changing selection model", getRow(5).isSelected());
    }

    @Test
    public void testSelectionCheckBoxesHaveStyleNames() {
        openTestURL();
        setSelectionModelMulti();
        Assert.assertTrue("Selection column CheckBox should have the proper style name set", getGridElement().getCell(0, 0).findElement(By.tagName("span")).getAttribute("class").contains("v-grid-selection-checkbox"));
        GridCellElement header = getGridElement().getHeaderCell(0, 0);
        Assert.assertTrue("Select all CheckBox should have the proper style name set", header.findElement(By.tagName("span")).getAttribute("class").contains("v-grid-select-all-checkbox"));
    }

    @Test
    public void testServerSideSelectTogglesSelectAllCheckBox() {
        openTestURL();
        setSelectionModelMulti();
        Assert.assertFalse("Select all CheckBox should not be selected", getSelectAllCheckbox().isSelected());
        selectAll();
        waitUntilCheckBoxValue(getSelectAllCheckbox(), true);
        Assert.assertTrue("Select all CheckBox wasn't selected as expected", getSelectAllCheckbox().isSelected());
        deselectAll();
        waitUntilCheckBoxValue(getSelectAllCheckbox(), false);
        Assert.assertFalse("Select all CheckBox was selected unexpectedly", getSelectAllCheckbox().isSelected());
        selectAll();
        waitUntilCheckBoxValue(getSelectAllCheckbox(), true);
        getGridElement().getCell(5, 0).click();
        waitUntilCheckBoxValue(getSelectAllCheckbox(), false);
        Assert.assertFalse("Select all CheckBox was selected unexpectedly", getSelectAllCheckbox().isSelected());
    }

    @Test
    public void testRemoveSelectedRow() {
        openTestURL();
        setSelectionModelSingle();
        getGridElement().getCell(0, 0).click();
        selectMenuPath("Component", "Body rows", "Deselect all");
        Assert.assertFalse("Unexpected NullPointerException when removing selected rows", logContainsText("Exception occurred, java.lang.NullPointerException: null"));
    }

    @Test
    public void singleSelectUserSelectionDisallowedSpaceSelectionNoOp() {
        openTestURL();
        setSelectionModelSingle();
        getGridElement().focus();
        getGridElement().sendKeys(DOWN, SPACE);
        Assert.assertTrue("row was selected when selection was allowed", getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().sendKeys(SPACE);
        Assert.assertTrue("deselect disallowed", getRow(1).isSelected());
        getGridElement().sendKeys(DOWN, SPACE);
        Assert.assertFalse("select disallowed", getRow(2).isSelected());
        Assert.assertTrue("old selection remains", getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().sendKeys(SPACE);
        Assert.assertTrue("select allowed again", getRow(2).isSelected());
        Assert.assertFalse("old selection removed", getRow(1).isSelected());
    }

    @Test
    public void singleSelectUserSelectionDisallowedClickSelectionNoOp() {
        openTestURL();
        setSelectionModelSingle();
        getGridElement().getCell(1, 0).click();
        Assert.assertTrue("selection allowed, should have been selected", getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().getCell(1, 0).click();
        Assert.assertTrue("deselect disallowed, should remain selected", getRow(1).isSelected());
        getGridElement().getCell(2, 0).click();
        Assert.assertFalse("select disallowed, should not have been selected", getRow(2).isSelected());
        Assert.assertTrue("select disallowed, old selection should have remained", getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().getCell(2, 0).click();
        Assert.assertTrue("select allowed again, row should have been selected", getRow(2).isSelected());
        Assert.assertFalse("old selection removed", getRow(1).isSelected());
    }

    @Test
    public void multiSelectUserSelectionDisallowedSpaceSelectionNoOp() {
        openTestURL();
        setSelectionModelMulti();
        getGridElement().focus();
        getGridElement().sendKeys(DOWN, SPACE);
        Assert.assertTrue("selection allowed, should have been selected", getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().sendKeys(SPACE);
        Assert.assertTrue("deselect disallowed, should remain selected", getRow(1).isSelected());
        getGridElement().sendKeys(DOWN, SPACE);
        Assert.assertFalse("select disallowed, should not have been selected", getRow(2).isSelected());
        Assert.assertTrue("select disallowed, old selection should have remained", getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().sendKeys(SPACE);
        Assert.assertTrue("select allowed again, row should have been selected", getRow(2).isSelected());
        Assert.assertTrue("select allowed again but old selection should have remained", getRow(1).isSelected());
    }

    @Test
    public void multiSelectUserSelectionDisallowedCheckboxSelectionNoOp() {
        openTestURL();
        setSelectionModelMulti();
        Assert.assertTrue(getSelectionCheckbox(0).isEnabled());
        toggleUserSelectionAllowed();
        Assert.assertFalse(getSelectionCheckbox(0).isEnabled());
        // Select by clicking on checkbox (should always fail as it is disabled)
        getSelectionCheckbox(0).click();
        Assert.assertFalse(getGridElement().getRow(0).isSelected());
        // Select by clicking on cell (should fail)
        getGridElement().getCell(0, 0).click();
        Assert.assertFalse(getGridElement().getRow(0).isSelected());
        toggleUserSelectionAllowed();
        Assert.assertTrue(getSelectionCheckbox(0).isEnabled());
        getSelectionCheckbox(0).click();
        Assert.assertTrue(getGridElement().getRow(0).isSelected());
    }

    @Test
    public void multiSelectUserSelectionDisallowedCheckboxSelectAllNoOp() {
        openTestURL();
        setSelectionModelMulti();
        Assert.assertTrue(getSelectAllCheckbox().isEnabled());
        toggleUserSelectionAllowed();
        Assert.assertFalse(getSelectAllCheckbox().isEnabled());
        // Select all by clicking on checkbox (should not select)
        getSelectAllCheckbox().click();
        Assert.assertFalse(getSelectAllCheckbox().isSelected());
        Assert.assertFalse(getGridElement().getRow(0).isSelected());
        Assert.assertFalse(getGridElement().getRow(10).isSelected());
        // Select all by clicking on header cell (should not select)
        getGridElement().getHeaderCell(0, 0).click();
        Assert.assertFalse(getSelectAllCheckbox().isSelected());
        Assert.assertFalse(getGridElement().getRow(0).isSelected());
        Assert.assertFalse(getGridElement().getRow(10).isSelected());
        // Select all by press SPACE on the header cell (should not select)
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(SPACE);
        Assert.assertFalse(getSelectAllCheckbox().isSelected());
        Assert.assertFalse(getGridElement().getRow(0).isSelected());
        Assert.assertFalse(getGridElement().getRow(10).isSelected());
        toggleUserSelectionAllowed();
        Assert.assertTrue(getSelectAllCheckbox().isEnabled());
        getSelectAllCheckbox().click();
        Assert.assertTrue(getGridElement().getRow(0).isSelected());
        Assert.assertTrue(getGridElement().getRow(10).isSelected());
    }

    @Test
    public void singleSelectUserSelectionDisallowedServerSelect() {
        openTestURL();
        setSelectionModelSingle();
        toggleUserSelectionAllowed();
        toggleFirstRowSelection();
        Assert.assertTrue(getGridElement().getRow(0).isSelected());
    }

    @Test
    public void multiSelectUserSelectionDisallowedServerSelect() {
        openTestURL();
        setSelectionModelMulti();
        toggleUserSelectionAllowed();
        toggleFirstRowSelection();
        Assert.assertTrue(getGridElement().getRow(0).isSelected());
    }

    @Test
    public void spaceKeyOnSelectionCheckboxShouldToggleRowSelection() {
        openTestURL();
        setSelectionModelMulti();
        getSelectionCheckbox(1).sendKeys(SPACE);
        assertSelected(1);
        getSelectionCheckbox(2).sendKeys(SPACE);
        assertSelected(1, 2);
        getSelectionCheckbox(2).sendKeys(SPACE);
        assertSelected(1);
        getSelectionCheckbox(1).sendKeys(SPACE);
        assertSelected();
    }
}

