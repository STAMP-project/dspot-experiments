package com.vaadin.tests.elements;


import ComponentElementGetValue.TEST_DATE_VALUE;
import ComponentElementGetValue.TEST_STRING_VALUE;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.testbench.elements.RichTextAreaElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static ComponentElementGetValue.TESTGET_STRING_VALUE_RICHTEXTAREA;
import static ComponentElementGetValue.TEST_STRING_VALUE;


public class ComponentElementGetValueTest extends MultiBrowserTest {
    @Test
    public void checkComboBox() {
        ComboBoxElement elem = $(ComboBoxElement.class).get(0);
        String expected = TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkListSelect() {
        ListSelectElement elem = $(ListSelectElement.class).get(0);
        String expected = TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkNativeSelect() {
        NativeSelectElement elem = $(NativeSelectElement.class).get(0);
        String expected = TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkCheckBoxGroup() {
        CheckBoxGroupElement elem = $(CheckBoxGroupElement.class).get(0);
        List<String> expected = Collections.singletonList(TEST_STRING_VALUE);
        List<String> actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkRadioButtonGroup() {
        RadioButtonGroupElement elem = $(RadioButtonGroupElement.class).get(0);
        String expected = TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkTwinColSelect() {
        TwinColSelectElement elem = $(TwinColSelectElement.class).get(0);
        String expected = TEST_STRING_VALUE;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkTextField() {
        TextFieldElement elem = $(TextFieldElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkTextArea() {
        TextAreaElement elem = $(TextAreaElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkPassword() {
        PasswordFieldElement elem = $(PasswordFieldElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkCheckBox() {
        CheckBoxElement cb = $(CheckBoxElement.class).get(0);
        String expected = "checked";
        String actual = cb.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkDateField() {
        DateFieldElement df = $(DateFieldElement.class).get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expected = formatter.format(TEST_DATE_VALUE);
        String actual = df.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkRichTextArea() {
        RichTextAreaElement elem = $(RichTextAreaElement.class).first();
        String expected = TESTGET_STRING_VALUE_RICHTEXTAREA;
        String actual = elem.getValue();
        Assert.assertEquals(expected, actual);
    }
}

