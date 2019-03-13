package com.vaadin.ui;


import HasValue.ValueChangeEvent;
import HasValue.ValueChangeListener;
import com.vaadin.data.HasValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RichTextAreaConstructorTest {
    @Test
    public void initiallyEmpty() {
        RichTextArea richTextArea = new RichTextArea();
        Assert.assertTrue(richTextArea.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        RichTextArea richTextArea = new RichTextArea(null, "foobar");
        Assert.assertFalse(richTextArea.isEmpty());
        richTextArea.clear();
        Assert.assertTrue(richTextArea.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        RichTextArea richTextArea = new RichTextArea(valueChangeListener);
        richTextArea.setValue("value change");
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito.mock(ValueChangeListener.class);
        RichTextArea richTextArea = new RichTextArea("Caption", "Initial value", valueChangeListener);
        Mockito.verify(valueChangeListener, Mockito.never()).valueChange(Mockito.any(ValueChangeEvent.class));
        richTextArea.setValue("value change");
        Mockito.verify(valueChangeListener).valueChange(Mockito.any(ValueChangeEvent.class));
    }
}

