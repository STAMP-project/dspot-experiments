package com.vaadin.tests.server.component.textfield;


import com.vaadin.tests.server.component.abstracttextfield.AbstractTextFieldDeclarativeTest;
import com.vaadin.ui.TextField;
import org.junit.Test;


/**
 * Tests declarative support for implementations of {@link TextField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextFieldDeclarativeTest extends AbstractTextFieldDeclarativeTest<TextField> {
    @Override
    @Test
    public void valueDeserialization() throws IllegalAccessException, InstantiationException {
        String value = "foo";
        String design = String.format("<%s value='%s'/>", getComponentTag(), value);
        TextField component = getComponentClass().newInstance();
        component.setValue(value);
        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    @Test
    public void readOnlyValue() throws IllegalAccessException, InstantiationException {
        String value = "foo";
        String design = String.format("<%s readonly value='%s'/>", getComponentTag(), value);
        TextField component = getComponentClass().newInstance();
        component.setValue(value);
        component.setReadOnly(true);
        testRead(design, component);
        testWrite(design, component);
    }
}

