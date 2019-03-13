package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class CustomDateFormatTest extends MultiBrowserTest {
    @Test
    public void checkCustomDateFormat() {
        openTestURL();
        String text = findElement(By.tagName("input")).getAttribute("value");
        Assert.assertEquals("1. tammikuuta 2010 klo", text);
    }
}

