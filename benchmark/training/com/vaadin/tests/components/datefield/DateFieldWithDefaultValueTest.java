package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateFieldWithDefaultValueTest extends MultiBrowserTest {
    @Test
    public void testDateFieldDefaultValue() {
        openTestURL();
        String datePickerId = DateFieldWithDefaultValue.DATEFIELD_HAS_DEFAULT;
        getDateFieldElement(datePickerId).openPopup();
        WebElement monthSpanElement = getMonthSpanFromVisibleCalendarPanel();
        // Can't check for "October 2010", since IE11 translates October ->
        // lokakuu
        assert monthSpanElement.getText().contains("2010");
    }

    @Test
    public void testDateFieldWithNoDefault() {
        openTestURL();
        String datePickerId = DateFieldWithDefaultValue.DATEFIELD_REGULAR;
        getDateFieldElement(datePickerId).openPopup();
        WebElement monthSpanElement = getMonthSpanFromVisibleCalendarPanel();
        assert !(monthSpanElement.getText().contains("2010"));
    }
}

