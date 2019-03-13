package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class DateTimeFieldWeekDaysTest extends SingleBrowserTest {
    @Test
    public void testFiLocale_weekNumbersVisible() {
        openTestURL();
        openPopupAndValidateWeekNumbers();
    }

    @Test
    public void testToggleWeekNumbers_renderedCorrectly() {
        openTestURL();
        openPopupAndValidateWeekNumbers();
        $(CheckBoxElement.class).first().click();
        Assert.assertFalse("Checkbox is selected even though should be unselected.", $(CheckBoxElement.class).first().isChecked());
        openPopupAndValidateNoWeeknumbers();
    }

    @Test
    public void testLocaleChangeToEnglish_removesWeekNumbers() {
        openTestURL();
        openPopupAndValidateWeekNumbers();
        $(ButtonElement.class).id("english").click();
        openPopupAndValidateNoWeeknumbers();
    }

    @Test
    public void testChangeBackToFinnish_weekNumbersVisible() {
        openTestURL();
        $(ButtonElement.class).id("english").click();
        openPopupAndValidateNoWeeknumbers();
        $(ButtonElement.class).id("finnish").click();
        openPopupAndValidateWeekNumbers();
    }
}

