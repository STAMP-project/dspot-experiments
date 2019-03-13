package com.vaadin.tests.components.orderedlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


/**
 * Tests hovering over caption in nested layout
 */
public class NestedLayoutCaptionHoverTest extends MultiBrowserTest {
    @Test
    public void testTooltipInNestedLayout() throws Exception {
        openTestURL();
        WebElement caption = getDriver().findElement(By.className("v-captiontext"));
        Assert.assertEquals("inner layout", caption.getText());
        // Hover over the caption
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(caption).perform();
        sleep(1000);
        String selector = "Root/VNotification[0]";
        try {
            // Verify that there's no error notification
            vaadinElement(selector);
            Assert.fail("No error notification should be found");
        } catch (NoSuchElementException e) {
            // Exception caught. Verify it's the right one.
            Assert.assertTrue(e.getMessage().contains(selector));
        }
    }
}

