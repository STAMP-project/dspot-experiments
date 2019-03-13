package com.vaadin.tests.extensions;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class SetThemeAndResponsiveLayoutTest extends MultiBrowserTest {
    @Test
    public void testWidthAndHeightRanges() throws Exception {
        openTestURL();
        // IE sometimes has trouble waiting long enough.
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".v-csslayout-width-and-height")), 30);
        // set the theme programmatically
        $(ButtonElement.class).caption("Set theme").first().click();
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@width-range]")), 30);
        // Verify both width-range and height-range.
        Assert.assertEquals("600px-", $(CssLayoutElement.class).first().getAttribute("width-range"));
        Assert.assertEquals("500px-", $(CssLayoutElement.class).first().getAttribute("height-range"));
        // Resize
        testBench().resizeViewPortTo(550, 450);
        // Verify updated width-range and height-range.
        Assert.assertEquals("0-599px", $(CssLayoutElement.class).first().getAttribute("width-range"));
        Assert.assertEquals("0-499px", $(CssLayoutElement.class).first().getAttribute("height-range"));
    }
}

