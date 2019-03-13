package com.vaadin.tests.extensions;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class ResponsiveUITest extends MultiBrowserTest {
    @Test
    public void testResizingSplitPanelReflowsLayout() throws Exception {
        openTestURL();
        // IE sometimes has trouble waiting long enough.
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".v-csslayout-grid.first")), 30);
        Assert.assertEquals("401px-600px", $(".v-csslayout-grid.first").getAttribute("width-range"));
        Assert.assertEquals("501px-", $(".v-csslayout-grid.second").getAttribute("width-range"));
        moveSplitter(200);
        Assert.assertEquals("601-800", $(".v-csslayout-grid.first").getAttribute("width-range"));
        Assert.assertEquals("501px-", $(".v-csslayout-grid.second").getAttribute("width-range"));
        moveSplitter((-350));
        Assert.assertEquals("201px-400px", $(".v-csslayout-grid.first").getAttribute("width-range"));
        Assert.assertEquals("301px-400px", $(".v-csslayout-grid.second").getAttribute("width-range"));
        compareScreen("responsive");
        moveSplitter((-200));
        Assert.assertEquals("-200px", $(".v-csslayout-grid.first").getAttribute("width-range"));
        moveSplitter((-100));
        Assert.assertEquals("0-100px", $(".v-csslayout-grid.second").getAttribute("width-range"));
    }

    @Test
    public void testResizingWindowReflowsLayout() throws Exception {
        openTestURL();
        Assert.assertEquals("401px-600px", $(".v-csslayout-grid.first").getAttribute("width-range"));
        Assert.assertEquals("501px-", $(".v-csslayout-grid.second").getAttribute("width-range"));
        testBench().resizeViewPortTo(1224, 768);
        Assert.assertEquals("601-800", $(".v-csslayout-grid.first").getAttribute("width-range"));
        Assert.assertEquals("501px-", $(".v-csslayout-grid.second").getAttribute("width-range"));
        testBench().resizeViewPortTo(674, 768);
        Assert.assertEquals("201px-400px", $(".v-csslayout-grid.first").getAttribute("width-range"));
        Assert.assertEquals("301px-400px", $(".v-csslayout-grid.second").getAttribute("width-range"));
    }
}

