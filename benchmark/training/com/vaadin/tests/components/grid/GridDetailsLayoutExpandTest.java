package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Tests the layouting of Grid's details row when it contains a HorizontalLayout
 * with expand ratios.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridDetailsLayoutExpandTest extends MultiBrowserTest {
    @Test
    public void testLabelWidths() {
        openTestURL();
        waitForElementPresent(By.className("v-grid"));
        GridElement grid = $(GridElement.class).first();
        int gridWidth = grid.getSize().width;
        grid.getRow(2).click();
        waitForElementPresent(By.id("lbl2"));
        // space left over from first label should be divided equally
        double expectedWidth = ((double) (gridWidth - 200)) / 2;
        assertLabelWidth("lbl2", expectedWidth);
        assertLabelWidth("lbl3", expectedWidth);
    }
}

