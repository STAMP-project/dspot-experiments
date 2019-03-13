package com.vaadin.tests.fieldgroup;


import DateForm.DATE;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.text.SimpleDateFormat;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateFormTest extends MultiBrowserTest {
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");

    @Test
    public void testCorrectDateFormat() throws Exception {
        openTestURL();
        Assert.assertEquals("Unexpected DateField value,", "1/20/84", getDateFieldValue());
        Assert.assertEquals("Unexpected PopupDateField value,", "1/21/84", getPopupDateFieldValue());
        WebElement day20 = getInlineDateFieldCalendarPanel().findElement(By.vaadin("#day20"));
        Assert.assertTrue("Unexpected InlineDateField state, 20th not selected.", hasCssClass(day20, "v-inline-datefield-calendarpanel-day-selected"));
        // Depends on the TZ offset on the server
        Assert.assertEquals("Unexpected TextField contents,", FORMAT.format(DATE), $(TextFieldElement.class).first().getValue());
    }
}

