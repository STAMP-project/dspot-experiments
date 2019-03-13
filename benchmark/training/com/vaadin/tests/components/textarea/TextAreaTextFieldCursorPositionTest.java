package com.vaadin.tests.components.textarea;


import TextAreaTextFieldCursorPosition.GET_POSITION;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TextAreaTextFieldCursorPositionTest extends SingleBrowserTest {
    @Test
    public void positionUpdatedWithoutTextChanges() {
        openTestURL();
        $(ButtonElement.class).id(GET_POSITION).click();
        Assert.assertEquals("2. TextField position: -1", getLogRow(0));
        Assert.assertEquals("1. TextArea position: -1", getLogRow(1));
        $(TextFieldElement.class).first().focus();
        $(TextAreaElement.class).first().focus();
        $(ButtonElement.class).id(GET_POSITION).click();
        Assert.assertTrue(getLogRow(0).startsWith("4. TextField position:"));
        Assert.assertNotEquals("4. TextField position: -1", getLogRow(0));
        Assert.assertNotEquals("3. TextArea position: -1", getLogRow(1));
    }
}

