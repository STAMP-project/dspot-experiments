package com.vaadin.v7.tests.data.converter;


import AnotherTestEnum.ONE;
import TestEnum.TWO;
import com.vaadin.tests.data.bean.AnotherTestEnum;
import com.vaadin.tests.data.bean.TestEnum;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.ReverseConverter;
import com.vaadin.v7.ui.TextField;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class AnyEnumToStringConverterTest {
    public class AnyEnumToStringConverter implements Converter<Enum, String> {
        public AnyEnumToStringConverter() {
        }

        @Override
        public String convertToModel(Enum value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            if (value == null) {
                return null;
            }
            return value.toString();
        }

        @Override
        public Enum convertToPresentation(String value, Class<? extends Enum> targetType, Locale locale) throws ConversionException {
            if (value == null) {
                return null;
            }
            for (Enum e : targetType.getEnumConstants()) {
                if (e.toString().equals(value)) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public Class<String> getModelType() {
            return String.class;
        }

        @Override
        public Class<Enum> getPresentationType() {
            return Enum.class;
        }
    }

    private AnyEnumToStringConverterTest.AnyEnumToStringConverter converter;

    @Test
    public void nullConversion() {
        Assert.assertEquals(null, converter.convertToModel(null, null, null));
    }

    @Test
    public void enumToStringConversion() {
        Assert.assertEquals(TWO.toString(), converter.convertToModel(TWO, String.class, null));
        Assert.assertEquals(AnotherTestEnum.TWO.toString(), converter.convertToModel(AnotherTestEnum.TWO, String.class, null));
    }

    @Test
    public void stringToEnumConversion() {
        Assert.assertEquals(TWO, converter.convertToPresentation(TWO.toString(), TestEnum.class, null));
        Assert.assertEquals(AnotherTestEnum.TWO, converter.convertToPresentation(AnotherTestEnum.TWO.toString(), AnotherTestEnum.class, null));
    }

    @Test
    public void stringToEnumWithField() {
        TextField tf = new TextField();
        tf.setConverter(new ReverseConverter(converter));
        tf.setPropertyDataSource(new com.vaadin.v7.data.util.ObjectProperty(AnotherTestEnum.TWO));
        Assert.assertEquals(AnotherTestEnum.TWO.toString(), tf.getValue());
        tf.setValue(ONE.toString());
        Assert.assertEquals(ONE.toString(), tf.getValue());
        Assert.assertEquals(ONE, tf.getConvertedValue());
        Assert.assertEquals(ONE, tf.getPropertyDataSource().getValue());
        tf.setPropertyDataSource(new com.vaadin.v7.data.util.ObjectProperty(TestEnum.TWO));
        Assert.assertEquals(TWO.toString(), tf.getValue());
        tf.setValue(TestEnum.ONE.toString());
        Assert.assertEquals(TestEnum.ONE.toString(), tf.getValue());
        Assert.assertEquals(TestEnum.ONE, tf.getConvertedValue());
        Assert.assertEquals(TestEnum.ONE, tf.getPropertyDataSource().getValue());
    }
}

