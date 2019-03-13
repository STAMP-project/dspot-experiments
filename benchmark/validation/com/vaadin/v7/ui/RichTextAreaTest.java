package com.vaadin.v7.ui;


import org.junit.Assert;
import org.junit.Test;


public class RichTextAreaTest {
    @Test
    public void initiallyEmpty() {
        RichTextArea tf = new RichTextArea();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        RichTextArea tf = new RichTextArea(new com.vaadin.v7.data.util.ObjectProperty<String>("foo"));
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        RichTextArea tf = new RichTextArea();
        tf.setValue("foobar");
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }
}

