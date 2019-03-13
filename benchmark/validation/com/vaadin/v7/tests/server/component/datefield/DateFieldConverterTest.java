package com.vaadin.v7.tests.server.component.datefield;


import Resolution.MINUTE;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.DateField;
import java.util.Date;
import org.junit.Test;


public class DateFieldConverterTest {
    private Property<Long> date;

    private DateField datefield;

    /* See #12193. */
    @Test
    public void testResolution() {
        datefield.setValue(new Date(110, 0, 1));
        datefield.setResolution(MINUTE);
        datefield.validate();
    }
}

