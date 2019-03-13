package com.vaadin.v7.data.util.sqlcontainer.filters;


import com.vaadin.v7.data.util.filter.Compare;
import org.junit.Assert;
import org.junit.Test;


public class CompareTest {
    @Test
    public void testEquals() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        Compare c2 = new Compare.Equal("prop1", "val1");
        Assert.assertTrue(c1.equals(c2));
    }

    @Test
    public void testDifferentTypeEquals() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        Compare c2 = new Compare.Greater("prop1", "val1");
        Assert.assertFalse(c1.equals(c2));
    }

    @Test
    public void testEqualsNull() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        Assert.assertFalse(c1.equals(null));
    }
}

