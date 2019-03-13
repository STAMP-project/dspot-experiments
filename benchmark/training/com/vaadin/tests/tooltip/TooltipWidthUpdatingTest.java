package com.vaadin.tests.tooltip;


import TooltipWidthUpdating.MAX_WIDTH;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.TooltipTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class TooltipWidthUpdatingTest extends TooltipTest {
    @Test
    public void testTooltipWidthUpdating() {
        openTestURL();
        WebElement btnLongTooltip = vaadinElementById("longTooltip");
        WebElement btnShortTooltip = vaadinElementById("shortTooltip");
        moveMouseToTopLeft(btnLongTooltip);
        testBenchElement(btnLongTooltip).showTooltip();
        moveMouseToTopLeft(btnShortTooltip);
        testBenchElement(btnShortTooltip).showTooltip();
        Assert.assertThat(getDriver().findElement(By.className("popupContent")).getSize().getWidth(), Matchers.lessThan(MAX_WIDTH));
    }
}

