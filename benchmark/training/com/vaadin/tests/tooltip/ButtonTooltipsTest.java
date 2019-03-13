package com.vaadin.tests.tooltip;


import ButtonTooltips.longDescription;
import ButtonTooltips.shortDescription;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.TooltipTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests that tooltip sizes do not change when moving between adjacent elements
 *
 * @author Vaadin Ltd
 */
public class ButtonTooltipsTest extends TooltipTest {
    @Test
    public void tooltipSizeWhenMovingBetweenElements() throws Exception {
        openTestURL();
        WebElement buttonOne = $(ButtonElement.class).caption("One").first();
        WebElement buttonTwo = $(ButtonElement.class).caption("Two").first();
        checkTooltip(buttonOne, longDescription);
        int originalWidth = getTooltipElement().getSize().getWidth();
        int originalHeight = getTooltipElement().getSize().getHeight();
        clearTooltip();
        checkTooltip(buttonTwo, shortDescription);
        moveMouseTo(buttonOne, 5, 5);
        sleep(100);
        Assert.assertThat(getTooltipElement().getSize().getWidth(), Matchers.is(originalWidth));
        Assert.assertThat(getTooltipElement().getSize().getHeight(), Matchers.is(originalHeight));
    }
}

