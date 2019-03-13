package com.vaadin.tests.components.datefield;


import Keys.ARROW_LEFT;
import Keys.ENTER;
import Keys.TAB;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.time.LocalDate;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class DateFieldValidationErrorTest extends MultiBrowserTest {
    @Test
    public void testComponentErrorShouldBeShownWhenEnteringInvalidDate() throws InterruptedException {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.getInputElement().click();
        dateField.getInputElement().sendKeys("01/01/01", TAB);
        assertHasErrorMessage(dateField);
    }

    @Test
    public void testComponentErrorShouldBeShownWhenSelectingInvalidDate() throws InterruptedException {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now());
        dateField.openPopup();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("v-datefield-popup")));
        WebElement popup = findElement(com.vaadin.testbench.By.className("v-datefield-popup"));
        // select day before today
        WebElement popupBody = popup.findElement(By.className("v-datefield-calendarpanel"));
        popupBody.sendKeys(ARROW_LEFT, ENTER);
        // move focus away otherwise tooltip is not shown
        WebElement inputElement = dateField.getInputElement();
        inputElement.click();
        inputElement.sendKeys(TAB);
        assertHasErrorMessage(dateField);
    }
}

