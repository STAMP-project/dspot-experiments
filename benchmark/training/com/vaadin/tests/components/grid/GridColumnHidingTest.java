package com.vaadin.tests.components.grid;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class GridColumnHidingTest extends MultiBrowserTest {
    @Test
    public void serverHideColumns() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        ButtonElement toggleNameColumn = $(ButtonElement.class).get(0);
        ButtonElement toggleAgeColumn = $(ButtonElement.class).get(1);
        ButtonElement toggleEmailColumn = $(ButtonElement.class).get(2);
        Assert.assertEquals("Foo", grid.getCell(0, 0).getText());
        Assert.assertEquals("Maya", grid.getCell(1, 0).getText());
        Assert.assertEquals("46", grid.getCell(0, 1).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 2).getText());
        toggleAgeColumn.click();
        Assert.assertEquals("Foo", grid.getCell(0, 0).getText());
        Assert.assertEquals("Maya", grid.getCell(1, 0).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 1).getText());
        toggleNameColumn.click();
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 0).getText());
        toggleEmailColumn.click();
        Assert.assertFalse(isElementPresent(By.className("v-grid-cell")));
        toggleAgeColumn.click();
        toggleNameColumn.click();
        toggleEmailColumn.click();
        Assert.assertEquals("Foo", grid.getCell(0, 0).getText());
        Assert.assertEquals("46", grid.getCell(0, 1).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 2).getText());
    }

    @Test
    public void clientHideColumns() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        getSidebarOpenButton(grid).click();
        getColumnHidingToggle(grid, "custom age column caption").click();
        Assert.assertEquals("Foo", grid.getCell(0, 0).getText());
        Assert.assertEquals("Maya", grid.getCell(1, 0).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 1).getText());
        Assert.assertEquals("maya@foo.bar", grid.getCell(1, 1).getText());
        getColumnHidingToggle(grid, "Name").click();
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 0).getText());
        getColumnHidingToggle(grid, "custom age column caption").click();
        Assert.assertEquals("46", grid.getCell(0, 0).getText());
        Assert.assertEquals("18", grid.getCell(1, 0).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 1).getText());
        Assert.assertEquals("maya@foo.bar", grid.getCell(1, 1).getText());
    }

    @Test
    public void clientHideServerShowColumns() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        getSidebarOpenButton(grid).click();
        // Assuming client-side hiding works. See clientHideColumns()
        getColumnHidingToggle(grid, "custom age column caption").click();
        getColumnHidingToggle(grid, "Name").click();
        // Show from server
        $(ButtonElement.class).caption("server side toggle age column").first().click();
        Assert.assertEquals("46", grid.getCell(0, 0).getText());
        Assert.assertEquals("18", grid.getCell(1, 0).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 1).getText());
        Assert.assertEquals("maya@foo.bar", grid.getCell(1, 1).getText());
    }

    @Test
    public void columnVisibilityChangeListener() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        LabelElement isHiddenLabel = $(LabelElement.class).get(1);
        ButtonElement toggleNameColumn = $(ButtonElement.class).get(0);
        ButtonElement toggleAgeColumn = $(ButtonElement.class).get(1);
        Assert.assertEquals("visibility change label", isHiddenLabel.getText());
        toggleNameColumn.click();
        Assert.assertEquals("true", isHiddenLabel.getText());
        toggleAgeColumn.click();
        Assert.assertEquals("true", isHiddenLabel.getText());
        toggleAgeColumn.click();
        Assert.assertEquals("false", isHiddenLabel.getText());
        getSidebarOpenButton(grid).click();
        getColumnHidingToggle(grid, "Name").click();
        Assert.assertEquals("false", isHiddenLabel.getText());
        getColumnHidingToggle(grid, "custom age column caption").click();
        Assert.assertEquals("true", isHiddenLabel.getText());
        getSidebarOpenButton(grid).click();
    }

    @Test
    public void columnTogglesVisibility() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        getSidebarOpenButton(grid).click();
        List<WebElement> elements = getColumnHidingToggles(grid);
        Assert.assertEquals(2, elements.size());
        Assert.assertTrue("Name".equals(elements.get(0).getText()));
        Assert.assertTrue("custom age column caption".equals(elements.get(1).getText()));
    }

    @Test
    public void testShrinkColumnToZeroWithHiddenColumn() {
        openTestURL();
        // hide all
        $(ButtonElement.class).get(3).click();
        ButtonElement toggleNameColumn = $(ButtonElement.class).get(0);
        ButtonElement toggleEmailColumn = $(ButtonElement.class).get(2);
        // Show
        toggleNameColumn.click();
        toggleEmailColumn.click();
        GridElement gridElement = $(GridElement.class).first();
        GridCellElement cell = gridElement.getCell(0, 1);
        dragResizeColumn(1, 0, (-(cell.getSize().getWidth())));
        AbstractTB3Test.assertGreaterOrEqual("Cell got too small.", cell.getSize().getWidth(), 10);
        Assert.assertEquals(gridElement.getCell(0, 0).getLocation().getY(), gridElement.getCell(0, 1).getLocation().getY());
    }
}

