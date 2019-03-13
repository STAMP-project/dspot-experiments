package com.vaadin.v7.tests.components.grid.basicfeatures;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridDefaultTextRendererTest extends MultiBrowserTest {
    private GridElement grid;

    @ServerClass("com.vaadin.tests.widgetset.server.TestWidgetComponent")
    public static class MyGridElement extends GridElement {}

    @Test
    public void testNullIsRenderedAsEmptyStringByDefaultTextRenderer() {
        Assert.assertTrue("First cell should've been empty", grid.getCell(0, 0).getText().isEmpty());
    }

    @Test
    public void testStringIsRenderedAsStringByDefaultTextRenderer() {
        Assert.assertEquals("Second cell should've been populated ", "string", grid.getCell(1, 0).getText());
    }

    @Test
    public void testWarningShouldNotBeInDebugLog() {
        Assert.assertFalse("Warning visible with string content.", isElementPresent(By.xpath("//span[contains(.,'attached:#1')]")));
    }
}

