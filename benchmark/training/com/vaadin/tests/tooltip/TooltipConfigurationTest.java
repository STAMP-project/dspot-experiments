package com.vaadin.tests.tooltip;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.TooltipTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class TooltipConfigurationTest extends TooltipTest {
    @Test
    public void testTooltipConfiguration() throws Exception {
        openTestURL();
        WebElement uiRoot = getDriver().findElement(By.vaadin("Root"));
        WebElement closeTimeout = vaadinElementById("Close timeout");
        WebElement shortTooltip = vaadinElementById("shortTooltip");
        WebElement longTooltip = vaadinElementById("longTooltip");
        WebElement maxWidth = vaadinElementById("Max width");
        selectAndType(closeTimeout, "0");
        checkTooltip(shortTooltip, "This is a short tooltip");
        moveToRoot();
        checkTooltipNotPresent();
        selectAndType(closeTimeout, "3000");
        checkTooltip(shortTooltip, "This is a short tooltip");
        moveToRoot();
        // The tooltip should still be there despite being "cleared", as the
        // timeout hasn't expired yet.
        checkTooltip("This is a short tooltip");
        // assert that tooltip is present
        selectAndType(closeTimeout, "0");
        selectAndType(maxWidth, "100");
        testBenchElement(longTooltip).showTooltip();
        Assert.assertThat(getDriver().findElement(By.className("popupContent")).getSize().getWidth(), Matchers.is(100));
    }
}

