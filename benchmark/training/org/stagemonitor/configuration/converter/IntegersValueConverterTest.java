package org.stagemonitor.configuration.converter;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;

import static SetValueConverter.INTEGERS;


public class IntegersValueConverterTest {
    private final ValueConverter<Collection<Integer>> converter = INTEGERS;

    @Test
    public void testConvertSingleValue() throws Exception {
        Assert.assertEquals(SetValueConverter.immutableSet(1), converter.convert("1"));
    }

    @Test
    public void testConvertMultipleValues() throws Exception {
        Assert.assertEquals(SetValueConverter.immutableSet(1, 2, 3, 4), converter.convert("1, 2,3  ,  4 "));
    }

    @Test
    public void testConvertNull() throws Exception {
        Assert.assertEquals(Collections.<Integer>emptySet(), converter.convert(null));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("1, 2, 3, 4", converter.toString(converter.convert("1, 2,3  ,  4 ")));
    }

    @Test
    public void testToStringNull() throws Exception {
        Assert.assertNull(converter.toString(null));
    }

    @Test
    public void testFail() throws Exception {
        try {
            converter.convert("a,2,c");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Can't convert 'a' to Integer.", e.getMessage());
        }
    }
}

