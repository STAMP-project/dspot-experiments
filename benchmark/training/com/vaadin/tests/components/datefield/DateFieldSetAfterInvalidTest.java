package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.Assert;
import org.junit.Test;


public class DateFieldSetAfterInvalidTest extends MultiBrowserTest {
    private static final By ERROR_INDICATOR_BY = By.className("v-errorindicator");

    private static String INVALID_TEXT = "abc";

    @Test
    public void setValueAfterBeingInvalid() {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now().minus(5, ChronoUnit.DAYS));
        assertNoErrorIndicator();
        dateField.setValue(((dateField.getValue()) + (DateFieldSetAfterInvalidTest.INVALID_TEXT)));
        assertErrorIndicator();
        $(ButtonElement.class).caption("Today").first().click();
        Assert.assertFalse(dateField.getValue().endsWith(DateFieldSetAfterInvalidTest.INVALID_TEXT));
        assertNoErrorIndicator();
    }

    @Test
    public void clearAfterBeingInvalid() {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now().minus(5, ChronoUnit.DAYS));
        assertNoErrorIndicator();
        dateField.setValue(((dateField.getValue()) + (DateFieldSetAfterInvalidTest.INVALID_TEXT)));
        assertErrorIndicator();
        $(ButtonElement.class).caption("Clear").first().click();
        Assert.assertTrue(dateField.getValue().isEmpty());
        assertNoErrorIndicator();
        dateField.setValue(DateFieldSetAfterInvalidTest.INVALID_TEXT);
        assertErrorIndicator();
    }

    @Test
    public void invalidTypedText() {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setValue(DateFieldSetAfterInvalidTest.INVALID_TEXT);
        assertErrorIndicator();
    }
}

