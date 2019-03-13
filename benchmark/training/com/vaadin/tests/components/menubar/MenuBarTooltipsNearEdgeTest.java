package com.vaadin.tests.components.menubar;


import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test to see if tooltips will render in the correct locations near the edges.
 *
 * @author Vaadin Ltd
 */
public class MenuBarTooltipsNearEdgeTest extends MultiBrowserTest {
    @Test
    public void testTooltipLocation() {
        openTestURL();
        final MenuBarElement menuBar = $(MenuBarElement.class).first();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(menuBar).click().moveByOffset(0, (-40)).perform();
        WebElement tooltip = getTooltipElement();
        Assert.assertTrue("Tooltip outside of the screen.", (((tooltip.getLocation().getX()) > 0) && ((tooltip.getLocation().getY()) > 0)));
        Assert.assertThat("Tooltip too far to the right", ((tooltip.getLocation().getX()) + (tooltip.getSize().getWidth())), Matchers.is(Matchers.lessThan(((menuBar.getLocation().getX()) + ((menuBar.getSize().getWidth()) / 2)))));
        Assert.assertThat("Tooltip too low on the screen", tooltip.getLocation().getY(), Matchers.is(Matchers.lessThan(menuBar.getLocation().getY())));
    }
}

