package com.vaadin.ui;


import HasValue.ValueChangeEvent;
import HasValue.ValueChangeListener;
import com.vaadin.data.HasValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TextFieldConstructorTest {
    @Test
    public void initiallyEmpty() {
        TextField textField = new TextField();
        Assert.assertTrue(textField.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        TextField textField = new TextField(null, "foobar");
        Assert.assertFalse(textField.isEmpty());
        textField.clear();
        Assert.assertTrue(textField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        TextField textField = new TextField(valueChangeListener);
        textField.setValue("value change");
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        TextField textField = new TextField("Caption", "Initial value", valueChangeListener);
        Mockito.verify(valueChangeListener, Mockito.never()).valueChange(Mockito.any(ValueChangeEvent.class));
        textField.setValue("value change");
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }
}

