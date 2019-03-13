package com.vaadin.tests.components.accordion;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test for Accordion: tabs should stay selectable after remove tab.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class AccordionRemoveTabTest extends MultiBrowserTest {
    @Test
    public void testRemoveTab() {
        openTestURL();
        WebElement button = driver.findElement(By.className("v-button"));
        button.click();
        checkFirstItemHeight("On second tab");
        button.click();
        checkFirstItemHeight("On third tab");
    }

    @Test
    public void testConsoleErrorOnSwitch() {
        setDebug(true);
        openTestURL();
        WebElement firstItem = driver.findElement(By.className("v-accordion-item-first"));
        WebElement caption = firstItem.findElement(By.className("v-accordion-item-caption"));
        caption.click();
        Assert.assertEquals("Errors present in console", 0, findElements(By.className("SEVERE")).size());
    }
}

