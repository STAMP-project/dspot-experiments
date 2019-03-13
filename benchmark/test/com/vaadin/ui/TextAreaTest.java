package com.vaadin.ui;


import org.junit.Assert;
import org.junit.Test;


public class TextAreaTest {
    @Test
    public void initiallyEmpty() {
        TextArea textArea = new TextArea();
        Assert.assertTrue(textArea.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextArea textArea = new TextArea();
        textArea.setValue("foobar");
        Assert.assertFalse(textArea.isEmpty());
        textArea.clear();
        Assert.assertTrue(textArea.isEmpty());
    }
}

