package com.vaadin.ui;


import DateResolution.DAY;
import HasValue.ValueChangeEvent;
import HasValue.ValueChangeListener;
import com.vaadin.data.HasValue;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DateFieldConstructorTest {
    @Test
    public void initiallyEmpty() {
        DateField dateField = new DateField();
        Assert.assertTrue(dateField.isEmpty());
        Assert.assertEquals(DAY, dateField.getResolution());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        DateField dateField = new DateField(null, LocalDate.now());
        Assert.assertEquals(DAY, dateField.getResolution());
        Assert.assertFalse(dateField.isEmpty());
        dateField.clear();
        Assert.assertTrue(dateField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        DateField dateField = new DateField(valueChangeListener);
        Assert.assertEquals(DAY, dateField.getResolution());
        dateField.setValue(LocalDate.now());
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        DateField dateField = new DateField("Caption", LocalDate.now(), valueChangeListener);
        Assert.assertEquals(DAY, dateField.getResolution());
        Mockito.verify(valueChangeListener, Mockito.never()).valueChange(Mockito.any(ValueChangeEvent.class));
        dateField.setValue(LocalDate.now().plusDays(1));
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }
}

