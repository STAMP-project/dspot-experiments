package cn.hutool.core.convert;


import org.junit.Assert;
import org.junit.Test;


/**
 * ConverterRegistry ????
 *
 * @author Looly
 */
public class ConverterRegistryTest {
    @Test
    public void getConverterTest() {
        Converter<Object> converter = ConverterRegistry.getInstance().getConverter(CharSequence.class, false);
        Assert.assertNotNull(converter);
    }

    @Test
    public void customTest() {
        int a = 454553;
        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        CharSequence result = converterRegistry.convert(CharSequence.class, a);
        Assert.assertEquals("454553", result);
        // ?????????CharSequence?????Hutool?????CharSequence??????????
        // ?????????????????CharSequence????????
        converterRegistry.putCustom(CharSequence.class, ConverterRegistryTest.CustomConverter.class);
        result = converterRegistry.convert(CharSequence.class, a);
        Assert.assertEquals("Custom: 454553", result);
    }

    public static class CustomConverter implements Converter<CharSequence> {
        @Override
        public CharSequence convert(Object value, CharSequence defaultValue) throws IllegalArgumentException {
            return "Custom: " + (value.toString());
        }
    }
}

