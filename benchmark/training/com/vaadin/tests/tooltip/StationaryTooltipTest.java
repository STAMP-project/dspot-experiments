package com.vaadin.tests.tooltip;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StationaryTooltipTest extends MultiBrowserTest {
    @Test
    public void tooltipShouldBeStationary() throws InterruptedException {
        openTestURL();
        ButtonElement button = getButtonElement();
        // Top left corner
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(button, 2, 2).perform();
        sleep(3000);// wait for the tooltip to become visible

        int originalTooltipLocationX = getTooltipLocationX();
        Assert.assertThat("Tooltip not displayed", originalTooltipLocationX, Matchers.is(Matchers.greaterThan(0)));
        // Bottom right corner
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(button, ((button.getSize().width) - 2), ((button.getSize().height) - 2)).perform();
        int actualTooltipLocationX = getTooltipLocationX();
        Assert.assertThat("Tooltip should not move", actualTooltipLocationX, Matchers.is(originalTooltipLocationX));
    }
}

