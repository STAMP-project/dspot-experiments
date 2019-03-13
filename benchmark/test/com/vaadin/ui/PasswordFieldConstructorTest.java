package com.vaadin.ui;


import HasValue.ValueChangeEvent;
import HasValue.ValueChangeListener;
import com.vaadin.data.HasValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PasswordFieldConstructorTest {
    @Test
    public void initiallyEmpty() {
        PasswordField passwordField = new PasswordField();
        Assert.assertTrue(passwordField.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        PasswordField passwordField = new PasswordField(null, "foobar");
        Assert.assertFalse(passwordField.isEmpty());
        passwordField.clear();
        Assert.assertTrue(passwordField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        PasswordField passwordField = new PasswordField(valueChangeListener);
        passwordField.setValue("value change");
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        PasswordField passwordField = new PasswordField("Caption", "Initial value", valueChangeListener);
        Mockito.verify(valueChangeListener, Mockito.never()).valueChange(Mockito.any(ValueChangeEvent.class));
        passwordField.setValue("value change");
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }
}

