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


public class SpecificEnumToStringConverterTest {
    public class SpecificEnumToStringConverter implements Converter<Enum, String> {
        private Class<? extends Enum> enumClass;

        public SpecificEnumToStringConverter(Class<? extends Enum> enumClass) {
            this.enumClass = enumClass;
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
            for (Enum e : enumClass.getEnumConstants()) {
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
            return ((Class<Enum>) (enumClass));
        }
    }

    SpecificEnumToStringConverterTest.SpecificEnumToStringConverter testEnumConverter;

    SpecificEnumToStringConverterTest.SpecificEnumToStringConverter anotherTestEnumConverter;

    @Test
    public void nullConversion() {
        Assert.assertEquals(null, testEnumConverter.convertToModel(null, null, null));
    }

    @Test
    public void enumToStringConversion() {
        Assert.assertEquals(TWO.toString(), testEnumConverter.convertToModel(TWO, String.class, null));
    }

    @Test
    public void stringToEnumConversion() {
        Assert.assertEquals(TWO, testEnumConverter.convertToPresentation(TWO.toString(), TestEnum.class, null));
    }

    @Test
    public void stringToEnumWithField() {
        TextField tf = new TextField();
        tf.setConverter(new ReverseConverter(anotherTestEnumConverter));
        tf.setPropertyDataSource(new com.vaadin.v7.data.util.ObjectProperty(AnotherTestEnum.TWO));
        Assert.assertEquals(AnotherTestEnum.TWO.toString(), tf.getValue());
        tf.setValue(ONE.toString());
        Assert.assertEquals(ONE.toString(), tf.getValue());
        Assert.assertEquals(ONE, tf.getConvertedValue());
        Assert.assertEquals(ONE, tf.getPropertyDataSource().getValue());
    }
}

