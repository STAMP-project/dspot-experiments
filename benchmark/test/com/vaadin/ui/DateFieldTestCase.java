package com.vaadin.ui;


import java.time.LocalDate;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class DateFieldTestCase {
    private AbstractLocalDateField dateField;

    private LocalDate date;

    @Test
    public void rangeStartIsSetToNull() {
        dateField.setRangeStart(null);
        MatcherAssert.assertThat(dateField.getRangeStart(), CoreMatchers.is(IsNull.nullValue()));
    }

    @Test
    public void rangeStartIsAcceptedAsValue() {
        dateField.setRangeStart(date);
        dateField.setValue(date);
        Assert.assertNull(dateField.getComponentError());
    }

    @Test
    public void belowRangeStartIsNotAcceptedAsValue() {
        dateField.setRangeStart(date);
        dateField.setValue(date.minusDays(1));
        Assert.assertNotNull(dateField.getComponentError());
    }

    @Test
    public void rangeEndIsSetToNull() {
        dateField.setRangeEnd(null);
        MatcherAssert.assertThat(dateField.getRangeEnd(), CoreMatchers.is(IsNull.nullValue()));
    }

    @Test
    public void rangeEndIsAcceptedAsValue() {
        dateField.setRangeEnd(date);
        dateField.setValue(date);
        Assert.assertNull(dateField.getComponentError());
    }

    @Test
    public void aboveRangeEndIsNotAcceptedAsValue() {
        dateField.setRangeEnd(date);
        dateField.setValue(date.plusDays(1));
        Assert.assertNotNull(dateField.getComponentError());
    }
}

