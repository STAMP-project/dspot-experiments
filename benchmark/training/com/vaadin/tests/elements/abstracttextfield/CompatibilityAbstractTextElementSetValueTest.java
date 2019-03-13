package com.vaadin.tests.elements.abstracttextfield;


import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class CompatibilityAbstractTextElementSetValueTest extends MultiBrowserTest {
    private static final String TYPED_STRING = "this is typed string";

    @Test
    public void textFieldSetValue() {
        checkType($(TextFieldElement.class).get(0), $(LabelElement.class).get(1));
    }

    @Test
    public void passwordFieldSetValue() {
        checkType($(PasswordFieldElement.class).get(0), $(LabelElement.class).get(2));
    }

    @Test
    public void textAreaSetValue() {
        checkType($(TextAreaElement.class).get(0), $(LabelElement.class).get(3));
    }

    @Test
    public void dateFieldSetValue() {
        DateFieldElement elem = $(DateFieldElement.class).get(0);
        LabelElement eventCount = $(LabelElement.class).get(4);
        // we can type any string in date field element
        elem.setValue(CompatibilityAbstractTextElementSetValueTest.TYPED_STRING);
        Assert.assertEquals(CompatibilityAbstractTextElementSetValueTest.TYPED_STRING, elem.getValue());
        Assert.assertEquals("1", eventCount.getText());
    }
}

