package com.vaadin.tests.components.datefield;


import Keys.TAB;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateTextHandlingTest extends SingleBrowserTest {
    public static final String Y2K_GB_LOCALE = "01-Jan-2000";

    @Test
    public void testSpecialValue() throws InterruptedException {
        openTestURL();
        DateFieldElement dateFieldElement = $(DateFieldElement.class).first();
        ButtonElement validate = $(ButtonElement.class).first();
        WebElement dateTextbox = dateFieldElement.getInputElement();
        dateTextbox.sendKeys("Y2K", TAB);
        validate.click();
        assertNotification(("Y2K Should be converted to " + (DateTextHandlingTest.Y2K_GB_LOCALE)), DateTextHandlingTest.Y2K_GB_LOCALE);
        dateTextbox.clear();
        validate.click();
        assertNotification("Null for empty string", "NULL");
    }
}

