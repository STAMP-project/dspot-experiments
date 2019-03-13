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


import BeanDefinition.SCOPE_PROTOTYPE;
import NumberFormat.Style;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.datetime.joda.JodaDateTimeFormatAnnotationFormatterFactory;
import org.springframework.format.number.NumberStyleFormatter;


/**
 *
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Kazuki Shimizu
 * @author Sam Brannen
 */
public class FormattingConversionServiceTests {
    private FormattingConversionService formattingService;

    @Test
    public void formatFieldForTypeWithFormatter() throws ParseException {
        formattingService.addFormatterForFieldType(Number.class, new NumberStyleFormatter());
        String formatted = formattingService.convert(3, String.class);
        Assert.assertEquals("3", formatted);
        Integer i = formattingService.convert("3", Integer.class);
        Assert.assertEquals(new Integer(3), i);
    }

    @Test
    public void formatFieldForTypeWithPrinterParserWithCoercion() throws ParseException {
        formattingService.addConverter(new org.springframework.core.convert.converter.Converter<DateTime, LocalDate>() {
            @Override
            public LocalDate convert(DateTime source) {
                return source.toLocalDate();
            }
        });
        formattingService.addFormatterForFieldType(LocalDate.class, new org.springframework.format.datetime.joda.ReadablePartialPrinter(DateTimeFormat.shortDate()), new org.springframework.format.datetime.joda.DateTimeParser(DateTimeFormat.shortDate()));
        String formatted = formattingService.convert(new LocalDate(2009, 10, 31), String.class);
        Assert.assertEquals("10/31/09", formatted);
        LocalDate date = formattingService.convert("10/31/09", LocalDate.class);
        Assert.assertEquals(new LocalDate(2009, 10, 31), date);
    }

    @Test
    @SuppressWarnings("resource")
    public void formatFieldForValueInjection() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.registerBeanDefinition("valueBean", new RootBeanDefinition(FormattingConversionServiceTests.ValueBean.class));
        ac.registerBeanDefinition("conversionService", new RootBeanDefinition(FormattingConversionServiceFactoryBean.class));
        ac.refresh();
        FormattingConversionServiceTests.ValueBean valueBean = ac.getBean(FormattingConversionServiceTests.ValueBean.class);
        Assert.assertEquals(new LocalDate(2009, 10, 31), new LocalDate(valueBean.date));
    }

    @Test
    @SuppressWarnings("resource")
    public void formatFieldForValueInjectionUsingMetaAnnotations() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        RootBeanDefinition bd = new RootBeanDefinition(FormattingConversionServiceTests.MetaValueBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        ac.registerBeanDefinition("valueBean", bd);
        ac.registerBeanDefinition("conversionService", new RootBeanDefinition(FormattingConversionServiceFactoryBean.class));
        ac.registerBeanDefinition("ppc", new RootBeanDefinition(PropertyPlaceholderConfigurer.class));
        ac.refresh();
        System.setProperty("myDate", "10-31-09");
        System.setProperty("myNumber", "99.99%");
        try {
            FormattingConversionServiceTests.MetaValueBean valueBean = ac.getBean(FormattingConversionServiceTests.MetaValueBean.class);
            Assert.assertEquals(new LocalDate(2009, 10, 31), new LocalDate(valueBean.date));
            Assert.assertEquals(Double.valueOf(0.9999), valueBean.number);
        } finally {
            System.clearProperty("myDate");
            System.clearProperty("myNumber");
        }
    }

    @Test
    public void formatFieldForAnnotation() throws Exception {
        formattingService.addFormatterForFieldAnnotation(new JodaDateTimeFormatAnnotationFormatterFactory());
        doTestFormatFieldForAnnotation(FormattingConversionServiceTests.Model.class, false);
    }

    @Test
    public void formatFieldForAnnotationWithDirectFieldAccess() throws Exception {
        formattingService.addFormatterForFieldAnnotation(new JodaDateTimeFormatAnnotationFormatterFactory());
        doTestFormatFieldForAnnotation(FormattingConversionServiceTests.Model.class, true);
    }

    @Test
    @SuppressWarnings("resource")
    public void formatFieldForAnnotationWithPlaceholders() throws Exception {
        GenericApplicationContext context = new GenericApplicationContext();
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("dateStyle", "S-");
        props.setProperty("datePattern", "M-d-yy");
        ppc.setProperties(props);
        context.getBeanFactory().registerSingleton("ppc", ppc);
        context.refresh();
        context.getBeanFactory().initializeBean(formattingService, "formattingService");
        formattingService.addFormatterForFieldAnnotation(new JodaDateTimeFormatAnnotationFormatterFactory());
        doTestFormatFieldForAnnotation(FormattingConversionServiceTests.ModelWithPlaceholders.class, false);
    }

    @Test
    @SuppressWarnings("resource")
    public void formatFieldForAnnotationWithPlaceholdersAndFactoryBean() throws Exception {
        GenericApplicationContext context = new GenericApplicationContext();
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("dateStyle", "S-");
        props.setProperty("datePattern", "M-d-yy");
        ppc.setProperties(props);
        context.registerBeanDefinition("formattingService", new RootBeanDefinition(FormattingConversionServiceFactoryBean.class));
        context.getBeanFactory().registerSingleton("ppc", ppc);
        context.refresh();
        formattingService = context.getBean("formattingService", FormattingConversionService.class);
        doTestFormatFieldForAnnotation(FormattingConversionServiceTests.ModelWithPlaceholders.class, false);
    }

    @Test
    public void printNull() throws ParseException {
        formattingService.addFormatterForFieldType(Number.class, new NumberStyleFormatter());
        Assert.assertEquals("", formattingService.convert(null, TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(String.class)));
    }

    @Test
    public void parseNull() throws ParseException {
        formattingService.addFormatterForFieldType(Number.class, new NumberStyleFormatter());
        Assert.assertNull(formattingService.convert(null, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
    }

    @Test
    public void parseEmptyString() throws ParseException {
        formattingService.addFormatterForFieldType(Number.class, new NumberStyleFormatter());
        Assert.assertNull(formattingService.convert("", TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
    }

    @Test
    public void parseBlankString() throws ParseException {
        formattingService.addFormatterForFieldType(Number.class, new NumberStyleFormatter());
        Assert.assertNull(formattingService.convert("     ", TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
    }

    @Test(expected = ConversionFailedException.class)
    public void parseParserReturnsNull() throws ParseException {
        formattingService.addFormatterForFieldType(Integer.class, new FormattingConversionServiceTests.NullReturningFormatter());
        Assert.assertNull(formattingService.convert("1", TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
    }

    @Test(expected = ConversionFailedException.class)
    public void parseNullPrimitiveProperty() throws ParseException {
        formattingService.addFormatterForFieldType(Integer.class, new NumberStyleFormatter());
        Assert.assertNull(formattingService.convert(null, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(int.class)));
    }

    @Test
    public void printNullDefault() throws ParseException {
        Assert.assertEquals(null, formattingService.convert(null, TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(String.class)));
    }

    @Test
    public void parseNullDefault() throws ParseException {
        Assert.assertNull(formattingService.convert(null, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
    }

    @Test
    public void parseEmptyStringDefault() throws ParseException {
        Assert.assertNull(formattingService.convert("", TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
    }

    @Test
    public void formatFieldForAnnotationWithSubclassAsFieldType() throws Exception {
        formattingService.addFormatterForFieldAnnotation(new JodaDateTimeFormatAnnotationFormatterFactory() {
            @Override
            public Printer<?> getPrinter(org.springframework.format.annotation.DateTimeFormat annotation, Class<?> fieldType) {
                Assert.assertEquals(FormattingConversionServiceTests.MyDate.class, fieldType);
                return super.getPrinter(annotation, fieldType);
            }
        });
        formattingService.addConverter(new org.springframework.core.convert.converter.Converter<FormattingConversionServiceTests.MyDate, Long>() {
            @Override
            public Long convert(FormattingConversionServiceTests.MyDate source) {
                return source.getTime();
            }
        });
        formattingService.addConverter(new org.springframework.core.convert.converter.Converter<FormattingConversionServiceTests.MyDate, Date>() {
            @Override
            public Date convert(FormattingConversionServiceTests.MyDate source) {
                return source;
            }
        });
        formattingService.convert(new FormattingConversionServiceTests.MyDate(), new TypeDescriptor(FormattingConversionServiceTests.ModelWithSubclassField.class.getField("date")), TypeDescriptor.valueOf(String.class));
    }

    @Test
    public void registerDefaultValueViaFormatter() {
        registerDefaultValue(Date.class, new Date());
    }

    @Test
    public void introspectedFormatter() throws ParseException {
        formattingService.addFormatter(new NumberStyleFormatter());
        Assert.assertNull(formattingService.convert(null, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
    }

    @Test
    public void proxiedFormatter() throws ParseException {
        Formatter<?> formatter = new NumberStyleFormatter();
        formattingService.addFormatter(((Formatter<?>) (getProxy())));
        Assert.assertNull(formattingService.convert(null, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class)));
    }

    @Test
    public void introspectedConverter() {
        formattingService.addConverter(new FormattingConversionServiceTests.IntegerConverter());
        Assert.assertEquals(Integer.valueOf(1), formattingService.convert("1", Integer.class));
    }

    @Test
    public void proxiedConverter() {
        org.springframework.core.convert.converter.Converter<?, ?> converter = new FormattingConversionServiceTests.IntegerConverter();
        formattingService.addConverter(((org.springframework.core.convert.converter.Converter<?, ?>) (getProxy())));
        Assert.assertEquals(Integer.valueOf(1), formattingService.convert("1", Integer.class));
    }

    @Test
    public void introspectedConverterFactory() {
        formattingService.addConverterFactory(new FormattingConversionServiceTests.IntegerConverterFactory());
        Assert.assertEquals(Integer.valueOf(1), formattingService.convert("1", Integer.class));
    }

    @Test
    public void proxiedConverterFactory() {
        ConverterFactory<?, ?> converterFactory = new FormattingConversionServiceTests.IntegerConverterFactory();
        formattingService.addConverterFactory(((ConverterFactory<?, ?>) (getProxy())));
        Assert.assertEquals(Integer.valueOf(1), formattingService.convert("1", Integer.class));
    }

    public static class ValueBean {
        @Value("10-31-09")
        @DateTimeFormat(pattern = "MM-d-yy")
        public Date date;
    }

    public static class MetaValueBean {
        @FormattingConversionServiceTests.MyDateAnn
        public Date date;

        @FormattingConversionServiceTests.MyNumberAnn
        public Double number;
    }

    @Value("${myDate}")
    @DateTimeFormat(pattern = "MM-d-yy")
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyDateAnn {}

    @Value("${myNumber}")
    @NumberFormat(style = Style.PERCENT)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyNumberAnn {}

    public static class Model {
        @DateTimeFormat(style = "S-")
        public Date date;

        @DateTimeFormat(pattern = "M-d-yy")
        public List<Date> dates;

        public List<Date> getDates() {
            return dates;
        }

        public void setDates(List<Date> dates) {
            this.dates = dates;
        }
    }

    public static class ModelWithPlaceholders {
        @DateTimeFormat(style = "${dateStyle}")
        public Date date;

        @FormattingConversionServiceTests.MyDatePattern
        public List<Date> dates;

        public List<Date> getDates() {
            return dates;
        }

        public void setDates(List<Date> dates) {
            this.dates = dates;
        }
    }

    @DateTimeFormat(pattern = "${datePattern}")
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyDatePattern {}

    public static class NullReturningFormatter implements Formatter<Integer> {
        @Override
        public String print(Integer object, Locale locale) {
            return null;
        }

        @Override
        public Integer parse(String text, Locale locale) throws ParseException {
            return null;
        }
    }

    @SuppressWarnings("serial")
    public static class MyDate extends Date {}

    private static class ModelWithSubclassField {
        @DateTimeFormat(style = "S-")
        public FormattingConversionServiceTests.MyDate date;
    }

    private static class IntegerConverter implements org.springframework.core.convert.converter.Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            return Integer.parseInt(source);
        }
    }

    private static class IntegerConverterFactory implements ConverterFactory<String, Number> {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends Number> org.springframework.core.convert.converter.Converter<String, T> getConverter(Class<T> targetType) {
            if ((Integer.class) == targetType) {
                return ((org.springframework.core.convert.converter.Converter<String, T>) (new FormattingConversionServiceTests.IntegerConverter()));
            } else {
                throw new IllegalStateException();
            }
        }
    }
}

