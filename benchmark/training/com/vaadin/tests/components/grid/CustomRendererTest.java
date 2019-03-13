package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class CustomRendererTest extends MultiBrowserTest {
    @Test
    public void testIntArrayIsRendered() throws Exception {
        openTestURL();
        GridElement grid = findGrid();
        Assert.assertEquals("1 :: 1 :: 2 :: 3 :: 5 :: 8 :: 13", grid.getCell(0, 0).getText());
    }

    @Test
    public void testRowAwareRenderer() throws Exception {
        openTestURL();
        GridElement grid = findGrid();
        Assert.assertEquals("Click me!", grid.getCell(0, 1).getText());
        Assert.assertEquals("Debug label placeholder", findDebugLabel().getText());
        grid.getCell(0, 1).click();
        Assert.assertEquals("row: 0, key: 1", grid.getCell(0, 1).getText());
        Assert.assertEquals("key: 1, itemId: test-data", findDebugLabel().getText());
    }

    @Test
    public void testBeanRenderer() throws Exception {
        openTestURL();
        Assert.assertEquals("SimpleTestBean(42)", findGrid().getCell(0, 2).getText());
    }
}

