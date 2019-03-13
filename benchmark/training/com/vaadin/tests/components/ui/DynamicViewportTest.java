package com.vaadin.tests.components.ui;


import DynamicViewport.VIEWPORT_DISABLE_PARAMETER;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class DynamicViewportTest extends SingleBrowserTest {
    @Test
    public void testGeneratedViewport() {
        openTestURL();
        WebElement viewportElement = findElement(By.cssSelector("meta[name=viewport]"));
        String viewportContent = viewportElement.getAttribute("content").toLowerCase(Locale.ROOT);
        String browserName = getDesiredCapabilities().getBrowserName().toLowerCase(Locale.ROOT);
        Assert.assertTrue(viewportContent.contains(browserName));
    }

    @Test
    public void testGeneratedEmptyViewport() {
        openTestURL(VIEWPORT_DISABLE_PARAMETER);
        List<WebElement> viewportElements = findElements(By.cssSelector("meta[name=viewport]"));
        Assert.assertTrue("There should be no viewport tags", viewportElements.isEmpty());
    }
}

