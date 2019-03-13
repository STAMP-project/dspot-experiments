package com.vaadin.tests.tooltip;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class TooltipAndJavascriptTest extends MultiBrowserTest {
    @Test
    public void ensureTooltipInOverlay() throws InterruptedException {
        openTestURL();
        $(ButtonElement.class).first().showTooltip();
        WebElement tooltip = findElement(By.cssSelector(".v-overlay-container .v-tooltip"));
        WebElement overlayContainer = getParent(tooltip);
        Assert.assertTrue("v-overlay-container did not receive theme", hasClass(overlayContainer, "reindeer"));
    }
}

