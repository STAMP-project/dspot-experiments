package com.vaadin.ui;


import DateTimeResolution.MINUTE;
import HasValue.ValueChangeEvent;
import HasValue.ValueChangeListener;
import com.vaadin.data.HasValue;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DateTimeFieldConstructorTest {
    @Test
    public void initiallyEmpty() {
        DateTimeField dateTimeField = new DateTimeField();
        Assert.assertTrue(dateTimeField.isEmpty());
        Assert.assertEquals(MINUTE, dateTimeField.getResolution());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        DateTimeField dateTimeField = new DateTimeField(null, LocalDateTime.now());
        Assert.assertFalse(dateTimeField.isEmpty());
        Assert.assertEquals(MINUTE, dateTimeField.getResolution());
        dateTimeField.clear();
        Assert.assertTrue(dateTimeField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        DateTimeField dateTimeField = new DateTimeField(valueChangeListener);
        Assert.assertEquals(MINUTE, dateTimeField.getResolution());
        dateTimeField.setValue(LocalDateTime.now());
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        DateTimeField dateTimeField = new DateTimeField("Caption", LocalDateTime.now(), valueChangeListener);
        Assert.assertEquals(MINUTE, dateTimeField.getResolution());
        Mockito.verify(valueChangeListener, Mockito.never()).valueChange(Mockito.any(ValueChangeEvent.class));
        dateTimeField.setValue(LocalDateTime.now().plusDays(1));
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }
}

