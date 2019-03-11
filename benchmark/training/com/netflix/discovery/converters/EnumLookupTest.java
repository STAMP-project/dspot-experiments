package com.netflix.discovery.converters;


import org.junit.Assert;
import org.junit.Test;


public class EnumLookupTest {
    enum TestEnum {

        VAL_ONE("one"),
        VAL_TWO("two"),
        VAL_THREE("three");
        private final String name;

        private TestEnum(String name) {
            this.name = name;
        }
    }

    @Test
    public void testLookup() {
        EnumLookup<EnumLookupTest.TestEnum> lookup = new EnumLookup(EnumLookupTest.TestEnum.class, ( v) -> v.name.toCharArray());
        char[] buffer = "zeroonetwothreefour".toCharArray();
        Assert.assertSame(EnumLookupTest.TestEnum.VAL_ONE, lookup.find(buffer, 4, 3));
        Assert.assertSame(EnumLookupTest.TestEnum.VAL_TWO, lookup.find(buffer, 7, 3));
        Assert.assertSame(EnumLookupTest.TestEnum.VAL_THREE, lookup.find(buffer, 10, 5));
    }
}

