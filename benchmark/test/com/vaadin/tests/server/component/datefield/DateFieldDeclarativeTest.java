package com.vaadin.tests.server.component.datefield;


import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateFieldDeclarativeTest;
import com.vaadin.ui.DateField;
import org.junit.Test;


/**
 * Tests the declarative support for implementations of {@link DateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DateFieldDeclarativeTest extends AbstractLocalDateFieldDeclarativeTest<DateField> {
    @Test
    public void remainingAttributes() throws IllegalAccessException, InstantiationException {
        String placeholder = "foo";
        String assistiveText = "at";
        boolean textFieldEnabled = false;
        String design = String.format(("<%s placeholder='%s' " + "assistive-text='%s' text-field-enabled='%s'/>"), getComponentTag(), placeholder, assistiveText, textFieldEnabled);
        DateField component = getComponentClass().newInstance();
        component.setPlaceholder(placeholder);
        component.setTextFieldEnabled(textFieldEnabled);
        component.setAssistiveText(assistiveText);
        testRead(design, component);
        testWrite(design, component);
    }
}

