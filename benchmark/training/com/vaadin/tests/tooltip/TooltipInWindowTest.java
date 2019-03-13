package com.vaadin.tests.tooltip;


import com.vaadin.tests.tb3.TooltipTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test if tooltips in subwindows behave correctly
 *
 * @author Vaadin Ltd
 */
public class TooltipInWindowTest extends TooltipTest {
    @Test
    public void testTooltipsInSubWindow() throws Exception {
        openTestURL();
        WebElement textfield = vaadinElementById("tf1");
        checkTooltip(textfield, "My tooltip");
        ensureVisibleTooltipPositionedCorrectly(textfield);
        clearTooltip();
        checkTooltip(textfield, "My tooltip");
        clearTooltip();
    }
}

