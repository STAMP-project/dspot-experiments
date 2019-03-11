package com.querydsl.core.support;


import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Assert;
import org.junit.Test;


public class EnumConversionTest {
    public enum Color {

        GREEN,
        BLUE,
        RED,
        YELLOW,
        BLACK,
        WHITE;}

    @Test
    public void name() {
        EnumPath<EnumConversionTest.Color> color = Expressions.enumPath(EnumConversionTest.Color.class, "path");
        EnumConversion<EnumConversionTest.Color> conv = new EnumConversion<EnumConversionTest.Color>(color);
        Assert.assertEquals(EnumConversionTest.Color.BLUE, conv.newInstance("BLUE"));
    }

    @Test
    public void ordinal() {
        EnumPath<EnumConversionTest.Color> color = Expressions.enumPath(EnumConversionTest.Color.class, "path");
        EnumConversion<EnumConversionTest.Color> conv = new EnumConversion<EnumConversionTest.Color>(color);
        Assert.assertEquals(EnumConversionTest.Color.RED, conv.newInstance(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal() {
        StringPath string = Expressions.stringPath("path");
        EnumConversion<String> conv = new EnumConversion<String>(string);
        Assert.fail("EnumConversion successfully created for a non-enum type");
        conv.newInstance(0);
    }
}

