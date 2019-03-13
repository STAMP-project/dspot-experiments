package com.vaadin.tests.contextclick;


import com.vaadin.testbench.elements.TreeElement;
import org.junit.Assert;
import org.junit.Test;


public class TreeV8ContextClickTest extends AbstractContextClickTest {
    @Test
    public void testBodyContextClickWithTypedListener() {
        addOrRemoveTypedListener();
        TreeElement tree = $(TreeElement.class).first();
        contextClick(tree.getItem(0));
        Assert.assertEquals("1. ContextClickEvent value: Granddad 0", getLogRow(0));
        tree.expand(0);
        tree.expand(2);
        contextClick(tree.getItem(6));
        Assert.assertEquals("2. ContextClickEvent value: Son 0/1/3", getLogRow(0));
    }
}

