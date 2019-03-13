package com.vaadin.tests.components.window;


import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;


/**
 * Automatic test for fix for #12943.
 *
 * While testing without the fix, the test failed on both Chrome and PhantomJS.
 *
 * @author Vaadin Ltd
 */
public class BottomComponentScrollsUpTest extends MultiBrowserTest {
    @Test
    public void windowScrollTest() throws IOException, InterruptedException {
        TestBenchElement panelScrollable = ((TestBenchElement) (getDriver().findElement(By.className("v-panel-content"))));
        Dimension panelScrollableSize = panelScrollable.getSize();
        WebElement verticalLayout = panelScrollable.findElement(By.className("v-verticallayout"));
        Dimension verticalLayoutSize = verticalLayout.getSize();
        panelScrollable.scroll(verticalLayoutSize.height);
        int beforeClick = getScrollTop(panelScrollable);
        WebElement button = verticalLayout.findElement(By.className("v-button"));
        button.click();
        // Loose the focus from the button.
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(panelScrollable, ((panelScrollableSize.width) / 2), ((panelScrollableSize.height) / 2)).click().build().perform();
        Assert.assertEquals("Clicking a button or the panel should not cause scrolling.", beforeClick, getScrollTop(panelScrollable));
    }
}

