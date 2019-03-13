package com.vaadin.tests.components.table;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class ReloadWidgetsTest extends MultiBrowserTest {
    private int rowHeight = -1;

    private WebElement wrapper;

    @Test
    public void testScrollingThenUpdatingContents() throws Exception {
        // Scroll down to row 44 so that we get the cut-off point where the
        // problem becomes apparent
        testBenchElement(wrapper).scroll((44 * (rowHeight)));
        waitForScrollToFinish();
        // Assert that we have the button widget.
        Assert.assertTrue("Button widget was not found after scrolling for the first time", (!(findElements(By.id("46")).isEmpty())));
        // Now refresh the container contents
        WebElement refreshButton = findElement(By.id("refresh"));
        refreshButton.click();
        // Again scroll down to row 44 so we get the cut-off point visible
        testBenchElement(wrapper).scroll((44 * (rowHeight)));
        waitForScrollToFinish();
        // Assert that we still get the button
        Assert.assertTrue("Button widget was not found after refreshing container items.", (!(findElements(By.id("46")).isEmpty())));
    }
}

