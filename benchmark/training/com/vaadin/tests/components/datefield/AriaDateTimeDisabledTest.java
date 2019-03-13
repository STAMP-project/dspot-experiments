package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class AriaDateTimeDisabledTest extends MultiBrowserTest {
    @Test
    public void verifyAriaDisabledAttributes() {
        openTestURL();
        // Expect aria-disabled="false" on the enabled DateField.
        String ariaDisabled = driver.findElement(By.vaadin("/VVerticalLayout[0]/VPopupTimeCalendar[1]#popupButton")).getAttribute("aria-disabled");
        Assert.assertEquals("false", ariaDisabled);
        // Expect aria-disabled="true" on the disabled DateField.
        ariaDisabled = driver.findElement(By.cssSelector(".v-disabled button")).getAttribute("aria-disabled");
        Assert.assertEquals("true", ariaDisabled);
    }
}

