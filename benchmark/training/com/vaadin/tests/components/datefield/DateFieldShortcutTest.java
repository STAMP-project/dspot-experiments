package com.vaadin.tests.components.datefield;


import Keys.DELETE;
import Keys.ENTER;
import Keys.HOME;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class DateFieldShortcutTest extends SingleBrowserTest {
    private static final String DATEFIELD_VALUE_ORIGINAL = "11/01/2018";

    private static final String DATEFIELD_VALUE_MODIFIED = "21/01/2018";

    @Test
    public void modifyValueAndPressEnter() {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        WebElement dateFieldText = dateField.findElement(By.tagName("input"));
        Assert.assertEquals((("DateField value should be \"" + (DateFieldShortcutTest.DATEFIELD_VALUE_ORIGINAL)) + "\""), DateFieldShortcutTest.DATEFIELD_VALUE_ORIGINAL, dateField.getValue());
        dateFieldText.click();
        dateFieldText.sendKeys(HOME, DELETE, "2");
        dateFieldText.sendKeys(ENTER);
        Assert.assertEquals((("DateField value should be \"" + (DateFieldShortcutTest.DATEFIELD_VALUE_MODIFIED)) + "\""), DateFieldShortcutTest.DATEFIELD_VALUE_MODIFIED, dateField.getValue());
        Assert.assertEquals(DateFieldShortcutTest.DATEFIELD_VALUE_MODIFIED, $(NotificationElement.class).first().getCaption());
    }
}

