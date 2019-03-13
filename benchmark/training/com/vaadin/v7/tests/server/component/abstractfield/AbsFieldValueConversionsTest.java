package com.vaadin.v7.tests.server.component.abstractfield;


import com.vaadin.server.VaadinSession;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.v7.data.util.MethodProperty;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextField;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class AbsFieldValueConversionsTest {
    Person paulaBean = new Person("Paula", "Brilliant", "paula@brilliant.com", 34, Sex.FEMALE, new com.vaadin.tests.data.bean.Address("Paula street 1", 12345, "P-town", Country.FINLAND));

    /**
     * Java uses a non-breaking space (ascii 160) instead of space when
     * formatting
     */
    private static final char FORMATTED_SPACE = 160;

    @Test
    public void testWithoutConversion() {
        TextField tf = new TextField();
        tf.setPropertyDataSource(new MethodProperty<String>(paulaBean, "firstName"));
        Assert.assertEquals("Paula", tf.getValue());
        Assert.assertEquals("Paula", tf.getPropertyDataSource().getValue());
        tf.setValue("abc");
        Assert.assertEquals("abc", tf.getValue());
        Assert.assertEquals("abc", tf.getPropertyDataSource().getValue());
        Assert.assertEquals("abc", paulaBean.getFirstName());
    }

    @Test
    public void testNonmodifiedBufferedFieldConversion() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        TextField tf = new TextField("salary");
        tf.setBuffered(true);
        tf.setLocale(new Locale("en", "US"));
        ObjectProperty<Integer> ds = new ObjectProperty<Integer>(123456789);
        tf.setPropertyDataSource(ds);
        Assert.assertEquals(((Integer) (123456789)), ds.getValue());
        Assert.assertEquals("123,456,789", tf.getValue());
        tf.setLocale(new Locale("fi", "FI"));
        Assert.assertEquals(((Integer) (123456789)), ds.getValue());
        Assert.assertEquals((((("123" + (AbsFieldValueConversionsTest.FORMATTED_SPACE)) + "456") + (AbsFieldValueConversionsTest.FORMATTED_SPACE)) + "789"), tf.getValue());
    }

    @Test
    public void testModifiedBufferedFieldConversion() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        TextField tf = new TextField("salary");
        tf.setBuffered(true);
        tf.setLocale(new Locale("en", "US"));
        ObjectProperty<Integer> ds = new ObjectProperty<Integer>(123456789);
        tf.setPropertyDataSource(ds);
        Assert.assertEquals(((Integer) (123456789)), ds.getValue());
        Assert.assertEquals("123,456,789", tf.getValue());
        tf.setValue("123,123");
        Assert.assertEquals(((Integer) (123456789)), ds.getValue());
        Assert.assertEquals("123,123", tf.getValue());
        tf.setLocale(new Locale("fi", "FI"));
        Assert.assertEquals(((Integer) (123456789)), ds.getValue());
        // Value should not be updated when field is buffered
        Assert.assertEquals("123,123", tf.getValue());
    }

    @Test
    public void testStringIdentityConversion() {
        TextField tf = new TextField();
        tf.setConverter(new com.vaadin.v7.data.util.converter.Converter<String, String>() {
            @Override
            public String convertToModel(String value, Class<? extends String> targetType, Locale locale) {
                return value;
            }

            @Override
            public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) {
                return value;
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        tf.setPropertyDataSource(new MethodProperty<String>(paulaBean, "firstName"));
        Assert.assertEquals("Paula", tf.getValue());
        Assert.assertEquals("Paula", tf.getPropertyDataSource().getValue());
        tf.setValue("abc");
        Assert.assertEquals("abc", tf.getValue());
        Assert.assertEquals("abc", tf.getPropertyDataSource().getValue());
        Assert.assertEquals("abc", paulaBean.getFirstName());
    }

    @Test
    public void testIntegerStringConversion() {
        TextField tf = new TextField();
        tf.setConverter(new StringToIntegerConverter());
        tf.setPropertyDataSource(new MethodProperty<Integer>(paulaBean, "age"));
        Assert.assertEquals(34, tf.getPropertyDataSource().getValue());
        Assert.assertEquals("34", tf.getValue());
        tf.setValue("12");
        Assert.assertEquals(12, tf.getPropertyDataSource().getValue());
        Assert.assertEquals("12", tf.getValue());
        tf.getPropertyDataSource().setValue(42);
        Assert.assertEquals(42, tf.getPropertyDataSource().getValue());
        Assert.assertEquals("42", tf.getValue());
    }

    @Test
    public void testChangeReadOnlyFieldLocale() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        TextField tf = new TextField("salary");
        tf.setLocale(new Locale("en", "US"));
        ObjectProperty<Integer> ds = new ObjectProperty<Integer>(123456789);
        ds.setReadOnly(true);
        tf.setPropertyDataSource(ds);
        Assert.assertEquals(((Integer) (123456789)), ds.getValue());
        Assert.assertEquals("123,456,789", tf.getValue());
        tf.setLocale(new Locale("fi", "FI"));
        Assert.assertEquals(((Integer) (123456789)), ds.getValue());
        Assert.assertEquals((((("123" + (AbsFieldValueConversionsTest.FORMATTED_SPACE)) + "456") + (AbsFieldValueConversionsTest.FORMATTED_SPACE)) + "789"), tf.getValue());
    }

    @Test
    public void testBooleanNullConversion() {
        CheckBox cb = new CheckBox();
        cb.setConverter(new com.vaadin.v7.data.util.converter.Converter<Boolean, Boolean>() {
            @Override
            public Boolean convertToModel(Boolean value, Class<? extends Boolean> targetType, Locale locale) {
                // value from a CheckBox should never be null as long as it is
                // not set to null (handled by conversion below).
                Assert.assertNotNull(value);
                return value;
            }

            @Override
            public Boolean convertToPresentation(Boolean value, Class<? extends Boolean> targetType, Locale locale) {
                // Datamodel -> field
                if (value == null) {
                    return false;
                }
                return value;
            }

            @Override
            public Class<Boolean> getModelType() {
                return Boolean.class;
            }

            @Override
            public Class<Boolean> getPresentationType() {
                return Boolean.class;
            }
        });
        MethodProperty<Boolean> property = new MethodProperty<Boolean>(paulaBean, "deceased");
        cb.setPropertyDataSource(property);
        Assert.assertEquals(Boolean.FALSE, property.getValue());
        Assert.assertEquals(Boolean.FALSE, cb.getValue());
        Boolean newDmValue = cb.getConverter().convertToPresentation(cb.getValue(), Boolean.class, new Locale("fi", "FI"));
        Assert.assertEquals(Boolean.FALSE, newDmValue);
        // FIXME: Should be able to set to false here to cause datamodel to be
        // set to false but the change will not be propagated to the Property
        // (field value is already false)
        cb.setValue(true);
        Assert.assertEquals(Boolean.TRUE, cb.getValue());
        Assert.assertEquals(Boolean.TRUE, property.getValue());
        cb.setValue(false);
        Assert.assertEquals(Boolean.FALSE, cb.getValue());
        Assert.assertEquals(Boolean.FALSE, property.getValue());
    }

    // Now specific to Integer because StringToNumberConverter has been removed
    public static class NumberBean {
        private Integer number;

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }
    }

    @Test
    public void testNumberDoubleConverterChange() {
        final VaadinSession a = new AlwaysLockedVaadinSession(null);
        VaadinSession.setCurrent(a);
        TextField tf = new TextField() {
            @Override
            public VaadinSession getSession() {
                return a;
            }
        };
        AbsFieldValueConversionsTest.NumberBean nb = new AbsFieldValueConversionsTest.NumberBean();
        nb.setNumber(490);
        tf.setPropertyDataSource(new MethodProperty<Number>(nb, "number"));
        Assert.assertEquals(490, tf.getPropertyDataSource().getValue());
        Assert.assertEquals("490", tf.getValue());
        com.vaadin.v7.data.util.converter.Converter c1 = tf.getConverter();
        tf.setPropertyDataSource(new MethodProperty<Number>(nb, "number"));
        com.vaadin.v7.data.util.converter.Converter c2 = tf.getConverter();
        Assert.assertTrue("StringToInteger converter is ok for integer types and should stay even though property is changed", (c1 == c2));
        Assert.assertEquals(490, tf.getPropertyDataSource().getValue());
        Assert.assertEquals("490", tf.getValue());
    }

    @Test
    public void testNullConverter() {
        TextField tf = new TextField("foo");
        tf.setConverter(new StringToIntegerConverter());
        tf.setPropertyDataSource(new ObjectProperty<Integer>(12));
        tf.setConverter(((com.vaadin.v7.data.util.converter.Converter) (null)));
        try {
            Object v = tf.getConvertedValue();
            System.out.println(v);
            Assert.fail("Trying to convert String -> Integer should fail when there is no converter");
        } catch (ConversionException e) {
            // ok, should happen when there is no converter but conversion is
            // needed
        }
    }
}

