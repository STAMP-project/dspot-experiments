package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


@TestCategory("grid")
public class GridWithoutRendererTest extends SingleBrowserTest {
    @Test
    public void ensureNoError() {
        openTestURL();
        // WebElement errorIndicator = findElement(By
        // .cssSelector("v-error-indicator"));
        // System.out.println(errorIndicator);
        List<WebElement> errorIndicator = findElements(By.xpath("//span[@class='v-errorindicator']"));
        Assert.assertTrue("There should not be an error indicator", errorIndicator.isEmpty());
    }
}

