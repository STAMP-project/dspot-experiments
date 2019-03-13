package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateTimeFieldPopupTest extends MultiBrowserTest {
    @Test
    public void testOpenCloseOpen_popupShouldBeOpen() throws Exception {
        openTestURL();
        WebElement toggleButton = $(DateTimeFieldElement.class).first().findElement(By.className("v-datefield-button"));
        toggleButton.click();
        assertThatPopupIsVisible();
        toggleButton.click();
        assertThatPopupIsInvisible();
        // We should be able to immediately open the popup from the popup after
        // clicking the button to close it. (#8446)
        toggleButton.click();
        assertThatPopupIsVisible();
    }
}

