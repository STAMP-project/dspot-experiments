package com.vaadin.tests.components.window;


import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;


public class ScrollingBodyElementWithModalOpenedTest extends MultiBrowserTest {
    @Test
    public void testWindowScrollbars() throws Exception {
        openTestURL();
        WebElement bodyElement = driver.findElement(By.className("v-modal-window-open"));
        Point initial = $(WindowElement.class).first().getLocation();
        TestBenchElementCommands scrollable = testBenchElement(bodyElement);
        scrollable.scroll(1000);
        Thread.sleep(1000);
        Point current = $(WindowElement.class).first().getLocation();
        Assert.assertEquals("Window moved along X-axis", initial.getX(), current.getX());
        Assert.assertEquals("Window moved along Y-axis", initial.getY(), current.getY());
        Assert.assertEquals("Body was scrolled", 0, getScrollTop(bodyElement));
    }
}

