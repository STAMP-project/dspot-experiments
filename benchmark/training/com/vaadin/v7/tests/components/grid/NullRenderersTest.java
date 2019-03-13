package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class NullRenderersTest extends MultiBrowserTest {
    @Test
    public void testDefaults() throws Exception {
        openTestURL();
        GridElement grid = findGridWithDefaults();
        Assert.assertEquals("-- No Text --", grid.getCell(0, 0).getText());
        Assert.assertEquals("-- No Jokes --", grid.getCell(0, 1).getText());
        Assert.assertEquals("-- Never --", grid.getCell(0, 2).getText());
        Assert.assertEquals("-- Nothing --", grid.getCell(0, 3).getText());
        Assert.assertEquals("-- No Control --", grid.getCell(0, 5).getText());
    }

    @Test
    public void testNoDefaults() throws Exception {
        openTestURL();
        GridElement grid = findGridNoDefaults();
        Assert.assertEquals("", grid.getCell(0, 0).getText());
        Assert.assertEquals("", grid.getCell(0, 1).getText());
        Assert.assertEquals("", grid.getCell(0, 2).getText());
        Assert.assertEquals("", grid.getCell(0, 3).getText());
        Assert.assertEquals("", grid.getCell(0, 5).getText());
    }
}

