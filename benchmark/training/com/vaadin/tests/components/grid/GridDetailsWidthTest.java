package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


@TestCategory("grid")
public class GridDetailsWidthTest extends SingleBrowserTest {
    @Test
    public void testSpacerTDsHaveNoWidth() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        // Open all details rows
        grid.getCell(0, 0).click();
        checkSpacersHaveNoWidths(1);
        grid.getCell(1, 0).click();
        checkSpacersHaveNoWidths(2);
        grid.getCell(2, 0).click();
        checkSpacersHaveNoWidths(3);
        // Close all details rows
        grid.getCell(2, 0).click();
        checkSpacersHaveNoWidths(2);
        grid.getCell(1, 0).click();
        checkSpacersHaveNoWidths(1);
        grid.getCell(0, 0).click();
        checkSpacersHaveNoWidths(0);
    }

    @Test
    public void testDetailsOnSort() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        // Open a details rows
        grid.getCell(0, 0).click();
        GridCellElement cell = grid.getHeaderCell(0, 0);
        cell.click();
        cell.click();
        cell = grid.getCell(2, 0);
        WebElement spacer = findElement(By.className("v-grid-spacer"));
        Assert.assertEquals("Grid was not sorted correctly", "Hello 0", cell.getText());
        Assert.assertEquals("Details row was not in correct location", ((cell.getLocation().getY()) + (cell.getSize().getHeight())), spacer.getLocation().getY(), 2);
    }
}

