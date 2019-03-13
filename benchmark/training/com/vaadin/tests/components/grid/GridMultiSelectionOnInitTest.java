package com.vaadin.tests.components.grid;


import GridElement.GridCellElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


@TestCategory("grid")
public class GridMultiSelectionOnInitTest extends MultiBrowserTest {
    @Test
    public void testSelectAllCheckBoxExists() {
        openTestURL();
        Assert.assertTrue("The select all checkbox was missing.", $(GridElement.class).first().getHeaderCell(0, 0).isElementPresent(By.tagName("input")));
    }

    @Test
    public void selectAllCellCanBeClicked() throws IOException {
        openTestURL();
        GridElement.GridCellElement selectAllCell = $(GridElement.class).first().getHeaderCell(0, 0);
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(selectAllCell, 2, 2).click().perform();
        WebElement selectAllCheckbox = selectAllCell.findElement(By.cssSelector("input"));
        MatcherAssert.assertThat(selectAllCheckbox.isSelected(), Is.is(true));
    }

    @Test
    public void testSetSelectedUpdatesClient() {
        openTestURL();
        Assert.assertFalse("Rows should not be selected initially.", $(GridElement.class).first().getRow(0).isSelected());
        $(ButtonElement.class).first().click();
        Assert.assertTrue("Rows should be selected after button click.", $(GridElement.class).first().getRow(0).isSelected());
    }

    @Test
    public void testInitialSelection() {
        openTestURL("initialSelection=yes");
        Assert.assertTrue("Initial selection should be visible", $(GridElement.class).first().getRow(1).isSelected());
    }
}

