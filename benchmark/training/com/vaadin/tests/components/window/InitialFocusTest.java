package com.vaadin.tests.components.window;


import InitialFocus.FOCUS_GENDER_BUTTON_ID;
import InitialFocus.FOCUS_NAME_BUTTON_ID;
import InitialFocus.GENDER_FIELD_ID;
import InitialFocus.NAME_FIELD_ID;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class InitialFocusTest extends MultiBrowserTest {
    /**
     * To test an implementation of
     * {@code com.google.gwt.user.client.ui.Focusable} which is nameField of
     * TextField type
     */
    @Test
    public void window_callingFocusNameField_nameFieldShouldGetFocused() {
        openTestURL();
        WebElement openWindowButton = findElement(By.id(FOCUS_NAME_BUTTON_ID));
        openWindowButton.click();
        waitForElementPresent(By.id(NAME_FIELD_ID));
        WebElement focusedElement = getFocusedElement();
        Assert.assertNotNull("Name TextField should have focus while focusedElement is null.", focusedElement);
        String focusedElementId = focusedElement.getAttribute("id");
        Assert.assertEquals((("Name TextField should have focus while " + focusedElementId) + " has focus."), NAME_FIELD_ID, focusedElementId);
    }

    /**
     * To test an implementation of {@code com.vaadin.client.Focusable} which is
     * genderField of ComboBox type
     */
    @Test
    public void window_callingFocusGenderField_genderFieldShouldGetFocused() {
        openTestURL();
        WebElement openWindowButton = findElement(By.id(FOCUS_GENDER_BUTTON_ID));
        openWindowButton.click();
        waitForElementPresent(By.id(GENDER_FIELD_ID));
        WebElement focusedElement = getFocusedElement();
        Assert.assertNotNull("Gender ComboBox should have focus while focusedElement is null.", focusedElement);
        ComboBoxElement genderField = $(ComboBoxElement.class).first();
        Assert.assertEquals("Gender ComboBox should have focus while another element has focus.", genderField.getInputField(), focusedElement);
    }
}

