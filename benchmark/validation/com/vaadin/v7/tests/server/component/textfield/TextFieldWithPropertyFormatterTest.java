package com.vaadin.v7.tests.server.component.textfield;


import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertyFormatter;
import com.vaadin.v7.ui.TextField;
import java.util.Collections;
import org.junit.Test;


public class TextFieldWithPropertyFormatterTest {
    private static final String INPUT_VALUE = "foo";

    private static final String PARSED_VALUE = "BAR";

    private static final String FORMATTED_VALUE = "FOOBAR";

    private static final String ORIGINAL_VALUE = "Original";

    private TextField field;

    private PropertyFormatter<String> formatter;

    private ObjectProperty<String> property;

    private ValueChangeListener listener;

    private int listenerCalled;

    private int repainted;

    @Test
    public void testWithServerApi() {
        checkInitialState();
        field.setValue(TextFieldWithPropertyFormatterTest.INPUT_VALUE);
        checkEndState();
    }

    @Test
    public void testWithSimulatedClientSideChange() {
        checkInitialState();
        field.changeVariables(null, Collections.singletonMap("text", ((Object) (TextFieldWithPropertyFormatterTest.INPUT_VALUE))));
        checkEndState();
    }
}

