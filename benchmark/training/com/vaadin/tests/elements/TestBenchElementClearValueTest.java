package com.vaadin.tests.elements;


import ComponentElementGetValue.CHECKBOX_VALUE_CHANGE;
import ComponentElementGetValue.DATEFIELD_VALUE_CHANGE;
import ComponentElementGetValue.MULTI_SELECT_VALUE_CHANGE;
import ComponentElementGetValue.TEST_DATE_VALUE;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import static ComponentElementGetValue.FIELD_VALUES;


/**
 * Test clear method. Checks that value of the component was changed both on
 * client and server side Testing of the client side done by comparing first
 * with initial value then calling clear and comparing with empty value. Testing
 * of the server side by checking that changeValue even was raised on the server
 * side. Each element has changeValue listener added in the UI class. Compare
 * labelChangeValue value with the value used in the listener of the UI class.
 *
 * @author Vaadin Ltd
 */
public class TestBenchElementClearValueTest extends MultiBrowserTest {
    // The label text is changed on element component ValueChange event
    // Used to test that element.clear() method has actually triggered the
    // server side code
    private LabelElement labelChangeValue;

    @Test
    public void clearTextField() {
        TextFieldElement elem = $(TextFieldElement.class).get(0);
        checkElementValue(elem);
        Assert.assertEquals(FIELD_VALUES[0], labelChangeValue.getText());
    }

    @Test
    public void clearTextArea() {
        TextAreaElement elem = $(TextAreaElement.class).get(0);
        checkElementValue(elem);
        Assert.assertEquals(FIELD_VALUES[1], labelChangeValue.getText());
    }

    @Test
    public void clearPasswordField() {
        PasswordFieldElement elem = $(PasswordFieldElement.class).get(0);
        checkElementValue(elem);
        Assert.assertEquals(FIELD_VALUES[2], labelChangeValue.getText());
    }

    @Test
    public void clearDateField() {
        DateFieldElement df = $(DateFieldElement.class).get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expected = formatter.format(TEST_DATE_VALUE);
        String initial = df.getValue();
        Assert.assertEquals(expected, initial);
        df.clear();
        Assert.assertEquals("", df.getValue());
        Assert.assertEquals(DATEFIELD_VALUE_CHANGE, labelChangeValue.getText());
    }

    @Test
    public void clearCheckBox() {
        CheckBoxElement elem = $(CheckBoxElement.class).get(0);
        elem.clear();
        Assert.assertTrue(elem.getValue().equals("unchecked"));
        Assert.assertEquals(CHECKBOX_VALUE_CHANGE, labelChangeValue.getText());
    }

    @Test
    public void clearTwinCol() {
        TwinColSelectElement elem = $(TwinColSelectElement.class).get(0);
        elem.clear();
        Assert.assertEquals("", elem.getValue());
        Assert.assertEquals(MULTI_SELECT_VALUE_CHANGE, labelChangeValue.getText());
    }
}

