package com.vaadin.tests.components;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static TooltipPosition.NUMBER_OF_BUTTONS;


/**
 * Tests that the tooltip is positioned so that it fits in the displayed area.
 *
 * @author Vaadin Ltd
 */
public class TooltipPositionTest extends MultiBrowserTest {
    @Test
    public void testRegression_EmptyTooltipShouldNotBeAppearedDuringInitialization() throws Exception {
        openTestURL();
        waitForElementPresent(By.cssSelector(".v-tooltip"));
        WebElement tooltip = driver.findElement(By.cssSelector(".v-tooltip"));
        Assert.assertTrue("This init tooltip with text ' ' is present in the DOM and should be entirely outside the browser window", isOutsideOfWindow(tooltip));
    }

    @Test
    public void testTooltipPosition() throws Exception {
        openTestURL();
        for (int i = 0; i < (NUMBER_OF_BUTTONS); i++) {
            ButtonElement button = $(ButtonElement.class).get(i);
            // Move the mouse to display the tooltip.
            Actions actions = new Actions(driver);
            actions.moveToElement(button, 10, 10);
            actions.build().perform();
            waitUntil(tooltipToBeInsideWindow(By.cssSelector(".v-tooltip"), driver.manage().window()));
            if (i < ((NUMBER_OF_BUTTONS) - 1)) {
                // Remove the tooltip by moving the mouse.
                actions = new Actions(driver);
                actions.moveByOffset(500, (-50));
                actions.build().perform();
                waitUntil(tooltipNotToBeShown(By.cssSelector(".v-tooltip"), driver.manage().window()));
            }
        }
    }
}

