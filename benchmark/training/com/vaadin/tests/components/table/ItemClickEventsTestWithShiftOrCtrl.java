package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ItemClickEventsTestWithShiftOrCtrl extends MultiBrowserTest {
    @Test
    public void testMultiSelectNotSelectable() throws Exception {
        // Activate table multi-selection mode
        clickElement($(CheckBoxElement.class).caption("multi").get(1));
        // Remove the 'selectable' mode from Table
        $(CheckBoxElement.class).caption("selectable").get(1).click();
        // Get table element
        TableElement table = $(TableElement.class).id("table");
        // Click some items and check that clicks go through
        clickElement(table.getCell(4, 0));
        assertLog("left click on table/Item 4");
        clickElement(table.getCell(2, 0));
        assertLog("left click on table/Item 2");
        ctrlClickElement(table.getCell(5, 0));
        assertLog("left click on table/Item 5 (ctrl)");
        shiftClickElement(table.getCell(3, 0));
        assertLog("left click on table/Item 3 (shift)");
    }

    @Test
    public void testSingleNonImmediateNonSelectable() throws Exception {
        // Disable table immediate mode
        clickElement($(CheckBoxElement.class).caption("immediate").get(1));
        // Disable the 'selectable' mode from Table
        $(CheckBoxElement.class).caption("selectable").get(1).click();
        // Get table element
        TableElement table = $(TableElement.class).id("table");
        // Click items and verify that click event went through
        clickElement(table.getCell(0, 0));
        assertLog("left click on table/Item 0");
        clickElement(table.getCell(1, 0));
        assertLog("left click on table/Item 1");
        ctrlClickElement(table.getCell(2, 0));
        assertLog("left click on table/Item 2 (ctrl)");
        shiftClickElement(table.getCell(3, 0));
        assertLog("left click on table/Item 3 (shift)");
    }

    @Test
    public void testMultiSelectNotNull() throws Exception {
        // Activate table multi-selection mode
        clickElement($(CheckBoxElement.class).caption("multi").get(1));
        // Get table element
        TableElement table = $(TableElement.class).id("table");
        // Click item 10, verify log output
        clickElement(table.getCell(9, 0));
        assertLog("left click on table/Item 9");
        // Click it again, should not deselect
        ctrlClickElement(table.getCell(9, 0));
        assertLog("left click on table/Item 9 (ctrl)");
        // Click item 4 to select it
        ctrlClickElement(table.getCell(3, 0));
        assertLog("left click on table/Item 3 (ctrl)");
        // Unselect item 10
        ctrlClickElement(table.getCell(9, 0));
        assertLog("left click on table/Item 9 (ctrl)");
        // Try to click item 4, should not deselect
        ctrlClickElement(table.getCell(3, 0));
        assertLog("left click on table/Item 3 (ctrl)");
        // Check that row 4 remains selected
        assertSelected(table.getRow(3));
    }

    @Test
    public void testMultiSelectNull() throws Exception {
        // Activate table multi-selection mode
        clickElement($(CheckBoxElement.class).caption("multi").get(1));
        // Activate table null selection mode
        clickElement($(CheckBoxElement.class).caption("nullsel").get(1));
        // Get table element
        TableElement table = $(TableElement.class).id("table");
        // Select first item
        clickElement(table.getCell(0, 0));
        assertLog("left click on table/Item 0");
        // Shift-click to select range between first and fifth element
        shiftClickElement(table.getCell(4, 0));
        assertLog("left click on table/Item 4 (shift)");
        // Pick element 7
        ctrlClickElement(table.getCell(6, 0));
        assertLog("left click on table/Item 6 (ctrl)");
        // Un-pick element 3
        ctrlClickElement(table.getCell(2, 0));
        assertLog("left click on table/Item 2 (ctrl)");
        // Check selection
        assertSelected(table.getRow(0));
        assertSelected(table.getRow(1));
        assertNotSelected(table.getRow(2));
        assertSelected(table.getRow(3));
        assertSelected(table.getRow(4));
        assertSelected(table.getRow(6));
    }
}

