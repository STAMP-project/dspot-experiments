package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridDefaultSelectionModeTest extends MultiBrowserTest {
    @Test
    public void testSelectionFromServer() {
        setDebug(true);
        openTestURL();
        $(ButtonElement.class).caption("Select on server").first().click();
        Assert.assertTrue("Row should be selected.", $(GridElement.class).first().getRow(0).isSelected());
        $(ButtonElement.class).caption("Deselect on server").first().click();
        Assert.assertFalse("Row should not be selected.", $(GridElement.class).first().getRow(0).isSelected());
        assertNoErrorNotifications();
    }

    @Test
    public void testSelectionWithSort() {
        setDebug(true);
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.getCell(0, 0).click();
        GridCellElement header = grid.getHeaderCell(0, 1);
        header.click();
        header.click();
        Assert.assertTrue("Row should be selected.", grid.getRow(1).isSelected());
        assertNoErrorNotifications();
    }

    @Test
    public void testReselectDeselectedRow() {
        setDebug(true);
        openTestURL();
        $(ButtonElement.class).caption("Select on server").first().click();
        GridElement grid = $(GridElement.class).first();
        Assert.assertTrue("Row should be selected.", grid.getRow(0).isSelected());
        $(ButtonElement.class).caption("Deselect on server").first().click();
        Assert.assertFalse("Row should not be selected.", grid.getRow(0).isSelected());
        grid.getCell(0, 0).click();
        Assert.assertTrue("Row should be selected.", grid.getRow(0).isSelected());
        assertNoErrorNotifications();
    }
}

