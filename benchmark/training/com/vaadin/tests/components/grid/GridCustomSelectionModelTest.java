package com.vaadin.tests.components.grid;


import Keys.SPACE;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


@TestCategory("grid")
public class GridCustomSelectionModelTest extends MultiBrowserTest {
    @Test
    public void testCustomSelectionModel() {
        setDebug(true);
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridCellElement cell = grid.getCell(0, 0);
        Assert.assertTrue("First column of Grid should not have an input element", cell.findElements(By.tagName("input")).isEmpty());
        Assert.assertFalse("Row should not be selected initially", grid.getRow(0).isSelected());
        cell.click(5, 5);
        Assert.assertTrue("Click should select row", grid.getRow(0).isSelected());
        cell.click(5, 5);
        Assert.assertFalse("Click should deselect row", grid.getRow(0).isSelected());
        grid.sendKeys(SPACE);
        Assert.assertTrue("Space should select row", grid.getRow(0).isSelected());
        grid.sendKeys(SPACE);
        Assert.assertFalse("Space should deselect row", grid.getRow(0).isSelected());
        assertNoErrorNotifications();
    }
}

