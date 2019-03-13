package com.vaadin.tests.components.datefield;


import Keys.TAB;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class DateTimeFieldIsValidTest extends MultiBrowserTest {
    @Test
    public void testInvalidText() throws Exception {
        openTestURL();
        waitForElementVisible(By.id("Log"));
        waitForElementVisible(By.className("v-datefield"));
        WebElement dateTextbox = $(AbstractDateFieldElement.class).first().findElement(By.className("v-textfield"));
        ButtonElement button = $(ButtonElement.class).first();
        dateTextbox.sendKeys("01/01/01 1.12", TAB);
        assertLogText("1. valueChange: value: 01/01/01 1.12, is valid: true");
        button.click();
        assertLogText("2. buttonClick: value: 01/01/01 1.12, is valid: true");
        dateTextbox.sendKeys("lala", TAB);
        assertLogText("3. valueChange: value: null, is valid: false");
        button.click();
        assertLogText("4. buttonClick: value: null, is valid: false");
        dateTextbox.clear();
        dateTextbox.sendKeys("02/02/02 2.34", TAB);
        assertLogText("5. valueChange: value: 02/02/02 2.34, is valid: true");
        button.click();
        assertLogText("6. buttonClick: value: 02/02/02 2.34, is valid: true");
    }
}

