package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class TreeGridDetailsExpandLastTest extends MultiBrowserTest {
    private TreeGridElement treeGrid;

    @Test
    public void expandLastRow() {
        openTestURL();
        waitForElementPresent(By.className("v-treegrid"));
        treeGrid = $(TreeGridElement.class).first();
        waitUntil(expectedConditionDetails(1));
        treeGrid.scrollToRow(297);
        waitUntil(expectedConditionDetails(99));
        treeGrid.expandWithClick(297);
        Assert.assertEquals("Error notification detected.", 0, treeGrid.findElements(By.className("v-Notification-error")).size());
        GridCellElement cell98_1 = treeGrid.getCell(296, 0);
        GridCellElement cell99 = treeGrid.getCell(297, 0);
        WebElement spacer99 = getDetails(99);
        Assert.assertThat("Unexpected row location.", (((double) (cell98_1.getLocation().getY())) + (cell98_1.getSize().getHeight())), closeTo(cell99.getLocation().getY(), 2.0));
        Assert.assertThat("Unexpected spacer location.", (((double) (cell99.getLocation().getY())) + (cell99.getSize().getHeight())), closeTo(spacer99.getLocation().getY(), 2.0));
    }
}

