package com.vaadin.ui;


import HasValue.ValueChangeEvent;
import HasValue.ValueChangeListener;
import com.vaadin.data.HasValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TextAreaConstructorTest {
    @Test
    public void initiallyEmpty() {
        TextArea textArea = new TextArea();
        Assert.assertTrue(textArea.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        TextArea textArea = new TextArea(null, "foobar");
        Assert.assertFalse(textArea.isEmpty());
        textArea.clear();
        Assert.assertTrue(textArea.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        TextArea textArea = new TextArea(valueChangeListener);
        textArea.setValue("value change");
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        TextArea textArea = new TextArea("Caption", "Initial value", valueChangeListener);
        Mockito.verify(valueChangeListener, Mockito.never()).valueChange(Mockito.any(ValueChangeEvent.class));
        textArea.setValue("value change");
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }
}

