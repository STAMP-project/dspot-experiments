package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class RemoveHiddenColumnTest extends SingleBrowserTest {
    @Test
    public void removeHiddenColumnInEmptyGrid() {
        openTestURL("debug");
        removeColumns();
    }

    @Test
    public void removeHiddenColumnInPopulatedGrid() {
        openTestURL("debug");
        ButtonElement add = $(ButtonElement.class).id("add");
        add.click();
        removeColumns();
    }
}

