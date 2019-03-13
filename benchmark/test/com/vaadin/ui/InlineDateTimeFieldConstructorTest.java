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


public class InlineDateTimeFieldConstructorTest {
    @Test
    public void initiallyEmpty() {
        InlineDateTimeField dateTimeField = new InlineDateTimeField();
        Assert.assertTrue(dateTimeField.isEmpty());
        Assert.assertEquals(MINUTE, dateTimeField.getResolution());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        InlineDateTimeField dateTimeField = new InlineDateTimeField(null, LocalDateTime.now());
        Assert.assertFalse(dateTimeField.isEmpty());
        Assert.assertEquals(MINUTE, dateTimeField.getResolution());
        dateTimeField.clear();
        Assert.assertTrue(dateTimeField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        InlineDateTimeField dateTimeField = new InlineDateTimeField(valueChangeListener);
        Assert.assertEquals(MINUTE, dateTimeField.getResolution());
        dateTimeField.setValue(LocalDateTime.now());
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        InlineDateTimeField dateTimeField = new InlineDateTimeField("Caption", LocalDateTime.now(), valueChangeListener);
        Assert.assertEquals(MINUTE, dateTimeField.getResolution());
        Mockito.verify(valueChangeListener, Mockito.never()).valueChange(Mockito.any(ValueChangeEvent.class));
        dateTimeField.setValue(LocalDateTime.now().plusDays(1));
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }
}

