package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridFrozenColumnNPETest extends MultiBrowserTest {
    @Test
    public void testFrozenColumnNPE() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        Assert.assertTrue(grid.getCell(0, 0).isFrozen());
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        Assert.assertTrue(grid.getCell(0, 1).isFrozen());
    }
}

