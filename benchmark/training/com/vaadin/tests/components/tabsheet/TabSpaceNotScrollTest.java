package com.vaadin.tests.components.tabsheet;


import Keys.SPACE;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;


/**
 * Test if the page scroll when press space on a tabsheet's tab.
 *
 * @author Vaadin Ltd
 */
public class TabSpaceNotScrollTest extends MultiBrowserTest {
    @Test
    public void testScroll() throws IOException, InterruptedException {
        openTestURL();
        TestBenchElement tab = ((TestBenchElement) (getDriver().findElement(By.className("v-tabsheet-tabitemcell"))));
        tab.click(10, 10);
        Point oldLocation = tab.getLocation();
        tab.sendKeys(SPACE);
        Point newLocation = tab.getLocation();
        Assert.assertEquals(oldLocation, newLocation);
    }
}

