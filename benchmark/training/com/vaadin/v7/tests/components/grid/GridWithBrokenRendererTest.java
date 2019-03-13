package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class GridWithBrokenRendererTest extends SingleBrowserTest {
    @Test
    public void ensureRendered() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertRow(grid, 0, "FI", "", "Finland");
        assertRow(grid, 1, "SE", "", "Sweden");
    }
}

