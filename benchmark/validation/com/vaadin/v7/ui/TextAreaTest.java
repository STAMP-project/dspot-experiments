package com.vaadin.v7.ui;


import org.junit.Assert;
import org.junit.Test;


public class TextAreaTest {
    @Test
    public void initiallyEmpty() {
        TextArea tf = new TextArea();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        TextArea tf = new TextArea(new com.vaadin.v7.data.util.ObjectProperty<String>("foo"));
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextArea tf = new TextArea();
        tf.setValue("foobar");
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }
}

