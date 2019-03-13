package org.stagemonitor.configuration.converter;


import org.junit.Assert;
import org.junit.Test;


public class EnumValueConverterTest {
    private static enum TestEnum {

        TEST_ENUM;}

    private final EnumValueConverter<EnumValueConverterTest.TestEnum> converter = new EnumValueConverter<EnumValueConverterTest.TestEnum>(EnumValueConverterTest.TestEnum.class);

    @Test
    public void testConvert() throws Exception {
        Assert.assertEquals(EnumValueConverterTest.TestEnum.TEST_ENUM, converter.convert("TEST_ENUM"));
        Assert.assertEquals(EnumValueConverterTest.TestEnum.TEST_ENUM, converter.convert("test_enum"));
        Assert.assertEquals(EnumValueConverterTest.TestEnum.TEST_ENUM, converter.convert("test-enum"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertNull() throws Exception {
        converter.convert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertInvalid() throws Exception {
        converter.convert("BEST");
    }

    @Test
    public void testToStringNull() throws Exception {
        Assert.assertNull(converter.toString(null));
    }
}

