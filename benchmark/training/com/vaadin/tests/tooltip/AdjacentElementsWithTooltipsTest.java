package com.vaadin.tests.tooltip;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to see if tooltips obey quickOpenDelay when moving between directly
 * adjacent elements.
 *
 * @author Vaadin Ltd
 */
public class AdjacentElementsWithTooltipsTest extends MultiBrowserTest {
    @Test
    public void tooltipsHaveQuickOpenDelay() throws InterruptedException {
        openTestURL();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(getButton("Button 0")).perform();
        sleep(1000);
        Assert.assertThat(getTooltipElement().getLocation().getX(), Matchers.is(Matchers.greaterThan(0)));
        ButtonElement button1 = getButton("Button 1");
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(button1).perform();
        Assert.assertThat(getTooltipElement().getLocation().getX(), Matchers.is(Matchers.lessThan((-1000))));
        sleep(1000);
        Assert.assertThat(getTooltipElement().getLocation().getX(), Matchers.is(Matchers.greaterThan(button1.getLocation().getX())));
    }
}

