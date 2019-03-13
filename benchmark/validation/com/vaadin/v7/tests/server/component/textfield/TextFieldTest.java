package com.vaadin.v7.tests.server.component.textfield;


import com.vaadin.v7.ui.TextField;
import org.junit.Assert;
import org.junit.Test;


public class TextFieldTest {
    @Test
    public void initiallyEmpty() {
        TextField tf = new TextField();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        TextField tf = new TextField(new com.vaadin.v7.data.util.ObjectProperty<String>("foo"));
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextField tf = new TextField();
        tf.setValue("foobar");
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }
}

