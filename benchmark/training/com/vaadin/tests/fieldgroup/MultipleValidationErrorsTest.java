package com.vaadin.tests.fieldgroup;


import MultipleValidationErrors.FIRST_NAME_NOT_EMPTY_VALIDATION_MESSAGE;
import MultipleValidationErrors.LAST_NAME_NOT_EMPTY_VALIDATION_MESSAGE;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class MultipleValidationErrorsTest extends MultiBrowserTest {
    @Test
    public void validationErrorsIncludeBothErrors() {
        openTestURL();
        clearTextField("First Name");
        clearTextField("Last Name");
        commitTextFields();
        String validationErrors = $(LabelElement.class).id("validationErrors").getText();
        MatcherAssert.assertThat(validationErrors, CoreMatchers.containsString(FIRST_NAME_NOT_EMPTY_VALIDATION_MESSAGE));
        MatcherAssert.assertThat(validationErrors, CoreMatchers.containsString(LAST_NAME_NOT_EMPTY_VALIDATION_MESSAGE));
    }
}

