package com.vaadin.tests.components.menubar;


import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to see if tooltips on menu items obscure other items on the menu.
 *
 * @author Vaadin Ltd
 */
public class MenuTooltipTest extends MultiBrowserTest {
    @Test
    public void testToolTipDelay() throws InterruptedException {
        openTestURL();
        final MenuBarElement menuBar = $(MenuBarElement.class).first();
        // Open menu bar and move on top of the first menu item
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(menuBar).click().moveByOffset(0, menuBar.getSize().getHeight()).perform();
        // Make sure tooltip is outside of the screen
        Assert.assertThat(getTooltipElement().getLocation().getX(), Matchers.is(Matchers.lessThan((-1000))));
        // Wait for tooltip to open up
        sleep(3000);
        // Make sure it's the correct tooltip
        Assert.assertThat(getTooltipElement().getLocation().getX(), Matchers.is(Matchers.greaterThan(menuBar.getLocation().getX())));
        Assert.assertThat(getTooltipElement().getText(), Matchers.is("TOOLTIP 1"));
    }
}

