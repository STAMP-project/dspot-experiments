package com.vaadin.v7.tests.server.component.textfield;


import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.tests.data.converter.ConverterFactoryTest;
import com.vaadin.v7.ui.TextField;
import org.junit.Test;


// TODO test converter changing value to null with validator
public class TextFieldWithConverterAndValidatorTest {
    private TextField field;

    private ObjectProperty<Integer> property;

    @Test
    public void testConvert42AndValidator() {
        property = new ObjectProperty<Integer>(123);
        field.setConverter(new ConverterFactoryTest.ConvertTo42());
        field.setPropertyDataSource(property);
        field.addValidator(new com.vaadin.v7.data.validator.RangeValidator<Integer>("Incorrect value", Integer.class, 42, 42));
        // succeeds
        field.setValue("a");
        // succeeds
        field.setValue("42");
        // succeeds - no validation
        property.setValue(42);
        // nulls
        // succeeds - validate() converts field value back to property type
        // before validation
        property.setValue(null);
        field.validate();
        // succeeds
        field.setValue(null);
    }
}

