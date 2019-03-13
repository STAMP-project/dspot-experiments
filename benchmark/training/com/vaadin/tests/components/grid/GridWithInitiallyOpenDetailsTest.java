package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class GridWithInitiallyOpenDetailsTest extends MultiBrowserTest {
    @Test
    public void testRowPositions() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridRowElement row0 = grid.getRow(0);
        GridRowElement row1 = grid.getRow(1);
        GridRowElement row2 = grid.getRow(2);
        waitForElementPresent(By.className("v-grid-spacer"));
        Assert.assertThat("Incorrect Y-position for second row.", row1.getLocation().getY(), Matchers.greaterThan((((row0.getLocation().getY()) + (row0.getSize().height)) + 10)));
        Assert.assertThat("Incorrect Y-position for third row.", row2.getLocation().getY(), Matchers.greaterThan((((row1.getLocation().getY()) + (row1.getSize().height)) + 10)));
    }
}

