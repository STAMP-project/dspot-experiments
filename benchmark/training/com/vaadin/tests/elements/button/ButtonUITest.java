package com.vaadin.tests.elements.button;


import ButtonUI.LABEL_ID;
import ButtonUI.NORMAL_BUTTON_ID;
import ButtonUI.QUIET_BUTTON_ID;
import ButtonUI.QUIET_BUTTON_NO_CAPTION_ID;
import ButtonUI.TEXT_FIELD_ID;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ButtonUITest extends MultiBrowserTest {
    @Test
    public void testButtonWithQUIETStyle() {
        ButtonElement button = $(ButtonElement.class).id(QUIET_BUTTON_ID);
        TextFieldElement field = $(TextFieldElement.class).first();
        button.click();
        Assert.assertEquals("Clicked", field.getValue());
    }

    @Test
    public void testButtonWithQUIETStyleNoCaption() {
        ButtonElement button = $(ButtonElement.class).id(QUIET_BUTTON_NO_CAPTION_ID);
        TextFieldElement field = $(TextFieldElement.class).first();
        button.click();
        Assert.assertEquals("Clicked", field.getValue());
    }

    @Test
    public void testButton_clickButtonWithSleep_TextFieldWorkAsExpected() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).id(NORMAL_BUTTON_ID);
        TextFieldElement field = $(TextFieldElement.class).id(TEXT_FIELD_ID);
        button.click();
        Assert.assertEquals("Clicked", field.getValue());
    }

    @Test
    public void testButton_clickButtonWithSleep_LabelWorkAsExpected() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).id(NORMAL_BUTTON_ID);
        LabelElement label = $(LabelElement.class).id(LABEL_ID);
        button.click();
        Assert.assertEquals("Clicked", label.getText());
    }
}

