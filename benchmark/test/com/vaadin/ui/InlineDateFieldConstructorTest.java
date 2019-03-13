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


public class InlineDateFieldConstructorTest {
    @Test
    public void initiallyEmpty() {
        InlineDateField dateField = new InlineDateField();
        Assert.assertTrue(dateField.isEmpty());
        Assert.assertEquals(DAY, dateField.getResolution());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        InlineDateField dateField = new InlineDateField(null, LocalDate.now());
        Assert.assertEquals(DAY, dateField.getResolution());
        Assert.assertFalse(dateField.isEmpty());
        dateField.clear();
        Assert.assertTrue(dateField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        InlineDateField dateField = new InlineDateField(valueChangeListener);
        Assert.assertEquals(DAY, dateField.getResolution());
        dateField.setValue(LocalDate.now());
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        InlineDateField dateField = new InlineDateField("Caption", LocalDate.now(), valueChangeListener);
        Assert.assertEquals(DAY, dateField.getResolution());
        Mockito.verify(valueChangeListener, Mockito.never()).valueChange(Mockito.any(ValueChangeEvent.class));
        dateField.setValue(LocalDate.now().plusDays(1));
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }
}

