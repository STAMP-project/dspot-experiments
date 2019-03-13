/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.format.support;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;


/**
 *
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class FormattingConversionServiceFactoryBeanTests {
    @Test
    public void testDefaultFormattersOn() throws Exception {
        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
        factory.afterPropertiesSet();
        FormattingConversionService fcs = factory.getObject();
        TypeDescriptor descriptor = new TypeDescriptor(FormattingConversionServiceFactoryBeanTests.TestBean.class.getDeclaredField("pattern"));
        LocaleContextHolder.setLocale(Locale.GERMAN);
        try {
            Object value = fcs.convert("15,00", TypeDescriptor.valueOf(String.class), descriptor);
            Assert.assertEquals(15.0, value);
            value = fcs.convert(15.0, descriptor, TypeDescriptor.valueOf(String.class));
            Assert.assertEquals("15", value);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }

    @Test
    public void testDefaultFormattersOff() throws Exception {
        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
        factory.setRegisterDefaultFormatters(false);
        factory.afterPropertiesSet();
        FormattingConversionService fcs = factory.getObject();
        TypeDescriptor descriptor = new TypeDescriptor(FormattingConversionServiceFactoryBeanTests.TestBean.class.getDeclaredField("pattern"));
        try {
            fcs.convert("15,00", TypeDescriptor.valueOf(String.class), descriptor);
            Assert.fail("This format should not be parseable");
        } catch (ConversionFailedException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof NumberFormatException));
        }
    }

    @Test
    public void testCustomFormatter() throws Exception {
        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
        Set<Object> formatters = new HashSet<>();
        formatters.add(new FormattingConversionServiceFactoryBeanTests.TestBeanFormatter());
        formatters.add(new FormattingConversionServiceFactoryBeanTests.SpecialIntAnnotationFormatterFactory());
        factory.setFormatters(formatters);
        factory.afterPropertiesSet();
        FormattingConversionService fcs = factory.getObject();
        FormattingConversionServiceFactoryBeanTests.TestBean testBean = fcs.convert("5", FormattingConversionServiceFactoryBeanTests.TestBean.class);
        Assert.assertEquals(5, testBean.getSpecialInt());
        Assert.assertEquals("5", fcs.convert(testBean, String.class));
        TypeDescriptor descriptor = new TypeDescriptor(FormattingConversionServiceFactoryBeanTests.TestBean.class.getDeclaredField("specialInt"));
        Object value = fcs.convert(":5", TypeDescriptor.valueOf(String.class), descriptor);
        Assert.assertEquals(5, value);
        value = fcs.convert(5, descriptor, TypeDescriptor.valueOf(String.class));
        Assert.assertEquals(":5", value);
    }

    @Test
    public void testFormatterRegistrar() throws Exception {
        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
        Set<FormatterRegistrar> registrars = new HashSet<>();
        registrars.add(new FormattingConversionServiceFactoryBeanTests.TestFormatterRegistrar());
        factory.setFormatterRegistrars(registrars);
        factory.afterPropertiesSet();
        FormattingConversionService fcs = factory.getObject();
        FormattingConversionServiceFactoryBeanTests.TestBean testBean = fcs.convert("5", FormattingConversionServiceFactoryBeanTests.TestBean.class);
        Assert.assertEquals(5, testBean.getSpecialInt());
        Assert.assertEquals("5", fcs.convert(testBean, String.class));
    }

    @Test
    public void testInvalidFormatter() throws Exception {
        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
        Set<Object> formatters = new HashSet<>();
        formatters.add(new Object());
        factory.setFormatters(formatters);
        try {
            factory.afterPropertiesSet();
            Assert.fail("Expected formatter to be rejected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    private @interface SpecialInt {
        @AliasFor("alias")
        String value() default "";

        @AliasFor("value")
        String alias() default "";
    }

    private static class TestBean {
        @NumberFormat(pattern = "##,00")
        private double pattern;

        @FormattingConversionServiceFactoryBeanTests.SpecialInt("aliased")
        private int specialInt;

        public int getSpecialInt() {
            return specialInt;
        }

        public void setSpecialInt(int field) {
            this.specialInt = field;
        }
    }

    private static class TestBeanFormatter implements Formatter<FormattingConversionServiceFactoryBeanTests.TestBean> {
        @Override
        public String print(FormattingConversionServiceFactoryBeanTests.TestBean object, Locale locale) {
            return String.valueOf(object.getSpecialInt());
        }

        @Override
        public FormattingConversionServiceFactoryBeanTests.TestBean parse(String text, Locale locale) throws ParseException {
            FormattingConversionServiceFactoryBeanTests.TestBean object = new FormattingConversionServiceFactoryBeanTests.TestBean();
            object.setSpecialInt(Integer.parseInt(text));
            return object;
        }
    }

    private static class SpecialIntAnnotationFormatterFactory implements AnnotationFormatterFactory<FormattingConversionServiceFactoryBeanTests.SpecialInt> {
        private final Set<Class<?>> fieldTypes = new HashSet<>(1);

        public SpecialIntAnnotationFormatterFactory() {
            fieldTypes.add(Integer.class);
        }

        @Override
        public Set<Class<?>> getFieldTypes() {
            return fieldTypes;
        }

        @Override
        public Printer<?> getPrinter(FormattingConversionServiceFactoryBeanTests.SpecialInt annotation, Class<?> fieldType) {
            Assert.assertEquals("aliased", annotation.value());
            Assert.assertEquals("aliased", annotation.alias());
            return new Printer<Integer>() {
                @Override
                public String print(Integer object, Locale locale) {
                    return ":" + (object.toString());
                }
            };
        }

        @Override
        public Parser<?> getParser(FormattingConversionServiceFactoryBeanTests.SpecialInt annotation, Class<?> fieldType) {
            Assert.assertEquals("aliased", annotation.value());
            Assert.assertEquals("aliased", annotation.alias());
            return new Parser<Integer>() {
                @Override
                public Integer parse(String text, Locale locale) throws ParseException {
                    return Integer.parseInt(text.substring(1));
                }
            };
        }
    }

    private static class TestFormatterRegistrar implements FormatterRegistrar {
        @Override
        public void registerFormatters(FormatterRegistry registry) {
            registry.addFormatter(new FormattingConversionServiceFactoryBeanTests.TestBeanFormatter());
        }
    }
}

