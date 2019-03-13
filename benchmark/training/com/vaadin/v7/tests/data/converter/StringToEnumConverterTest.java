package com.vaadin.v7.tests.data.converter;


import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.StringToEnumConverter;
import org.junit.Assert;
import org.junit.Test;


public class StringToEnumConverterTest {
    public static enum FooEnum {

        VALUE1,
        SOME_VALUE,
        FOO_BAR_BAZ,
        Bar,
        nonStandardCase,
        _HUGH;}

    public static enum EnumWithCustomToString {

        ONE,
        TWO,
        THREE;
        @Override
        public String toString() {
            return "case " + ((ordinal()) + 1);
        }
    }

    public static enum EnumWithAmbigousToString {

        FOO,
        FOOBAR,
        FOO_BAR;
        @Override
        public String toString() {
            return name().replaceAll("_", "");
        }
    }

    StringToEnumConverter converter = new StringToEnumConverter();

    Converter<Enum, String> reverseConverter = new com.vaadin.v7.data.util.converter.ReverseConverter<Enum, String>(converter);

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null, converter.convertToModel("", Enum.class, null));
    }

    @Test
    public void testInvalidEnumClassConversion() {
        try {
            converter.convertToModel("Foo", Enum.class, null);
            Assert.fail("No exception thrown");
        } catch (ConversionException e) {
            // OK
        }
    }

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null, converter.convertToModel(null, Enum.class, null));
    }

    @Test
    public void testReverseNullConversion() {
        Assert.assertEquals(null, reverseConverter.convertToModel(null, String.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals(StringToEnumConverterTest.FooEnum.VALUE1, converter.convertToModel("Value1", StringToEnumConverterTest.FooEnum.class, null));
        Assert.assertEquals(StringToEnumConverterTest.FooEnum.SOME_VALUE, converter.convertToModel("Some value", StringToEnumConverterTest.FooEnum.class, null));
        Assert.assertEquals(StringToEnumConverterTest.FooEnum.FOO_BAR_BAZ, converter.convertToModel("Foo bar baz", StringToEnumConverterTest.FooEnum.class, null));
        Assert.assertEquals(StringToEnumConverterTest.FooEnum.Bar, converter.convertToModel("Bar", StringToEnumConverterTest.FooEnum.class, null));
        Assert.assertEquals(StringToEnumConverterTest.FooEnum.nonStandardCase, converter.convertToModel("Nonstandardcase", StringToEnumConverterTest.FooEnum.class, null));
        Assert.assertEquals(StringToEnumConverterTest.FooEnum._HUGH, converter.convertToModel("_hugh", StringToEnumConverterTest.FooEnum.class, null));
    }

    @Test
    public void testReverseValueConversion() {
        Assert.assertEquals("Value1", reverseConverter.convertToModel(StringToEnumConverterTest.FooEnum.VALUE1, String.class, null));
        Assert.assertEquals("Some value", reverseConverter.convertToModel(StringToEnumConverterTest.FooEnum.SOME_VALUE, String.class, null));
        Assert.assertEquals("Foo bar baz", reverseConverter.convertToModel(StringToEnumConverterTest.FooEnum.FOO_BAR_BAZ, String.class, null));
        Assert.assertEquals("Bar", reverseConverter.convertToModel(StringToEnumConverterTest.FooEnum.Bar, String.class, null));
        Assert.assertEquals("Nonstandardcase", reverseConverter.convertToModel(StringToEnumConverterTest.FooEnum.nonStandardCase, String.class, null));
        Assert.assertEquals("_hugh", reverseConverter.convertToModel(StringToEnumConverterTest.FooEnum._HUGH, String.class, null));
    }

    @Test
    public void preserveFormattingWithCustomToString() {
        for (StringToEnumConverterTest.EnumWithCustomToString e : StringToEnumConverterTest.EnumWithCustomToString.values()) {
            Assert.assertEquals(e.toString(), convertToString(e));
        }
    }

    @Test
    public void findEnumWithCustomToString() {
        for (StringToEnumConverterTest.EnumWithCustomToString e : StringToEnumConverterTest.EnumWithCustomToString.values()) {
            Assert.assertSame(e, convertToEnum(e.toString(), StringToEnumConverterTest.EnumWithCustomToString.class));
            Assert.assertSame(e, convertToEnum(e.name(), StringToEnumConverterTest.EnumWithCustomToString.class));
        }
    }

    @Test
    public void unambigousValueInEnumWithAmbigous_succeed() {
        Assert.assertSame(StringToEnumConverterTest.EnumWithAmbigousToString.FOO, convertToEnum("foo", StringToEnumConverterTest.EnumWithAmbigousToString.class));
    }

    @Test(expected = ConversionException.class)
    public void ambigousValue_throws() {
        convertToEnum("foobar", StringToEnumConverterTest.EnumWithAmbigousToString.class);
    }
}

