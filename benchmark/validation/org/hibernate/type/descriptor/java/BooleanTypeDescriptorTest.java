package org.hibernate.type.descriptor.java;


import org.junit.Assert;
import org.junit.Test;


public class BooleanTypeDescriptorTest {
    private BooleanTypeDescriptor underTest = new BooleanTypeDescriptor();

    @Test
    public void testWrapShouldReturnTrueWhenYStringGiven() {
        // given
        // when
        Boolean result = underTest.wrap("Y", null);
        // then
        Assert.assertTrue(result);
    }

    @Test
    public void testWrapShouldReturnFalseWhenFStringGiven() {
        // given
        // when
        Boolean result = underTest.wrap("N", null);
        // then
        Assert.assertFalse(result);
    }

    @Test
    public void testWrapShouldReturnFalseWhenRandomStringGiven() {
        // given
        // when
        Boolean result = underTest.wrap("k", null);
        // then
        Assert.assertFalse(result);
    }

    @Test
    public void testWrapShouldReturnNullWhenNullStringGiven() {
        // given
        // when
        Boolean result = underTest.wrap(null, null);
        // then
        Assert.assertNull(result);
    }

    @Test
    public void testWrapShouldReturnFalseWhenEmptyStringGiven() {
        // given
        // when
        Boolean result = underTest.wrap("", null);
        // then
        Assert.assertFalse(result);
    }
}

