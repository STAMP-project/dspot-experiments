package com.vaadin.tests.smoke;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.FormElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.TooltipTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class FormSmokeTest extends TooltipTest {
    @Test
    public void testTooltipConfiguration() throws Exception {
        openTestURL();
        // first name tooltip
        WebElement fieldElement = $(TextFieldElement.class).first();
        checkTooltip(fieldElement, "Fields own tooltip");
        clearTooltip();
        checkTooltipNotPresent();
        // first name caption tooltip
        checkTooltip($(FormElement.class).first().findElement(By.className("v-caption")), "Fields own tooltip");
        clearTooltip();
        checkTooltipNotPresent();
        // Form should not have a description tooltip
        checkTooltip($(FormElement.class).first(), null);
        // Form error message should not have a tooltip
        checkTooltip(By.className("v-form-errormessage"), null);
        // last name should have no tooltip
        checkTooltip($(TextFieldElement.class).get(1), null);
        // last name caption should have no tooltip
        checkTooltip($(FormElement.class).first().findElements(By.className("v-caption")).get(1), null);
    }
}

