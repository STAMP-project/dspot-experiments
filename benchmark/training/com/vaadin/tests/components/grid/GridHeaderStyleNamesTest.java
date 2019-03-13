package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


@TestCategory("grid")
public class GridHeaderStyleNamesTest extends SingleBrowserTest {
    private GridElement grid;

    @Test
    public void cellStyleNamesCanBeAddedAndRemoved() {
        ButtonElement toggleStyles = $(ButtonElement.class).caption("Toggle styles").first();
        assertStylesSet(true);
        toggleStyles.click();
        assertStylesSet(false);
        toggleStyles.click();
        assertStylesSet(true);
    }

    @Test
    public void rowStyleNamesCanBeAddedAndRemoved() {
        ButtonElement toggleStyles = $(ButtonElement.class).caption("Toggle styles").first();
        assertRowStylesSet(true);
        toggleStyles.click();
        assertRowStylesSet(false);
        toggleStyles.click();
        assertRowStylesSet(true);
    }
}

