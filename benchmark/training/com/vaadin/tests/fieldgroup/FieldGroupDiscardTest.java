package com.vaadin.tests.fieldgroup;


import Keys.ENTER;
import org.junit.Test;


public class FieldGroupDiscardTest extends BasicPersonFormTest {
    @Test
    public void testFieldGroupDiscard() throws Exception {
        openTestURL();
        assertDefaults();
        /* make some changes */
        getFirstNameField().sendKeys("John123", ENTER);
        getLastNameArea().sendKeys("Doe123", ENTER);
        getEmailField().sendKeys("john@doe.com123", ENTER);
        getAgeField().sendKeys("64123", ENTER);
        getGenderTable().getCell(2, 0);
        getDeceasedField().click();
        getDeceasedField().click();
        getDeceasedField().sendKeys("YAY!", ENTER);
        assertBeanValuesUnchanged();
        assertDiscardResetsFields();
        assertBeanValuesUnchanged();
        /* we should still be at the state we started from */
        assertDefaults();
    }
}

