package com.vaadin.tests.validation;


import Keys.SHIFT;
import Keys.TAB;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ValidationOfRequiredEmptyFieldsTest extends MultiBrowserTest {
    @Test
    public void requiredErrorMessage() throws Exception {
        openTestURL();
        getRequiredCheckbox().click();
        getRequiredMessageField().click();
        getRequiredMessageField().sendKeys("The field is required", TAB);
        assertTooltipError("The field is required");
    }

    @Test
    public void integerValidatorErrorMessage() {
        openTestURL();
        getRequiredCheckbox().click();
        getIntegerValidatorCheckbox().click();
        getTargetTextField().sendKeys("a", SHIFT, TAB);
        assertTooltipError("Must be an integer");
    }

    @Test
    public void requiredWithIntegerAndLengthValidatorErrorMessage() {
        openTestURL();
        getRequiredCheckbox().click();
        getIntegerValidatorCheckbox().click();
        getLengthValidatorCheckbox().click();
        getTargetTextField().sendKeys("a", SHIFT, TAB);
        assertTooltipError("Must be an integer\nMust be 5-10 chars");
    }

    @Test
    public void integerAndLengthValidatorErrorMessage() {
        openTestURL();
        getIntegerValidatorCheckbox().click();
        getLengthValidatorCheckbox().click();
        getTargetTextField().sendKeys("a", SHIFT, TAB);
        assertTooltipError("Must be an integer\nMust be 5-10 chars");
    }
}

