package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateFieldCloseTest extends MultiBrowserTest {
    private WebElement dateField;

    @Test
    public void closeByClickingCalendarButton() throws Exception {
        openTestURL();
        dateField = driver.findElement(By.id(DateFieldClose.DATEFIELD_ID));
        clickButton();
        checkForCalendarHeader(true);
        closePopup();
        checkForCalendarHeader(false);
    }
}

