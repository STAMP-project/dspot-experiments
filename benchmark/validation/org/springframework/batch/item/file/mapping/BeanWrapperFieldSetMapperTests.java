/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.item.file.mapping;


import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;


public class BeanWrapperFieldSetMapperTests {
    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private TimeZone defaultTimeZone = TimeZone.getDefault();

    @Test
    public void testNameAndTypeSpecified() throws Exception {
        boolean errorCaught = false;
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        mapper.setPrototypeBeanName("foo");
        try {
            mapper.afterPropertiesSet();
        } catch (IllegalStateException ise) {
            errorCaught = true;
            Assert.assertEquals("Both name and type cannot be specified together.", ise.getMessage());
        }
        if (!errorCaught) {
            Assert.fail();
        }
    }

    @Test
    public void testNameNorTypeSpecified() throws Exception {
        boolean errorCaught = false;
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        try {
            mapper.afterPropertiesSet();
        } catch (IllegalStateException ise) {
            errorCaught = true;
            Assert.assertEquals("Either name or type must be provided.", ise.getMessage());
        }
        if (!errorCaught) {
            Assert.fail();
        }
    }

    @Test
    public void testVanillaBeanCreatedFromType() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        mapper.afterPropertiesSet();
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "This is some dummy string", "true", "C" }, new String[]{ "varString", "varBoolean", "varChar" });
        BeanWrapperFieldSetMapperTests.TestObject result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("This is some dummy string", result.getVarString());
        Assert.assertEquals(true, result.isVarBoolean());
        Assert.assertEquals('C', result.getVarChar());
    }

    @Test
    public void testNullPropertyAutoCreated() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestNestedA> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestNestedA.class);
        mapper.afterPropertiesSet();
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "Foo", "Bar" }, new String[]{ "valueA", "testObjectB.valueA" });
        BeanWrapperFieldSetMapperTests.TestNestedA result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("Bar", result.getTestObjectB().getValueA());
    }

    @Test
    public void testMapperWithSingleton() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        context.getBeanFactory().registerSingleton("bean", new BeanWrapperFieldSetMapperTests.TestObject());
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "This is some dummy string", "true", "C" }, new String[]{ "varString", "varBoolean", "varChar" });
        BeanWrapperFieldSetMapperTests.TestObject result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("This is some dummy string", result.getVarString());
        Assert.assertEquals(true, result.isVarBoolean());
        Assert.assertEquals('C', result.getVarChar());
    }

    @Test
    public void testPropertyNameMatching() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        mapper.setDistanceLimit(2);
        context.getBeanFactory().registerSingleton("bean", new BeanWrapperFieldSetMapperTests.TestObject());
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "This is some dummy string", "true", "C" }, new String[]{ "VarString", "VAR_BOOLEAN", "VAR_CHAR" });
        BeanWrapperFieldSetMapperTests.TestObject result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("This is some dummy string", result.getVarString());
        Assert.assertEquals(true, result.isVarBoolean());
        Assert.assertEquals('C', result.getVarChar());
    }

    @Test
    @SuppressWarnings({ "unchecked", "resource" })
    public void testMapperWithPrototype() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("bean-wrapper.xml", getClass());
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = ((BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject>) (context.getBean("fieldSetMapper")));
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "This is some dummy string", "true", "C" }, new String[]{ "varString", "varBoolean", "varChar" });
        BeanWrapperFieldSetMapperTests.TestObject result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("This is some dummy string", result.getVarString());
        Assert.assertEquals(true, result.isVarBoolean());
        Assert.assertEquals('C', result.getVarChar());
    }

    @Test
    public void testMapperWithNestedBeanPaths() throws Exception {
        BeanWrapperFieldSetMapperTests.TestNestedA testNestedA = new BeanWrapperFieldSetMapperTests.TestNestedA();
        BeanWrapperFieldSetMapperTests.TestNestedB testNestedB = new BeanWrapperFieldSetMapperTests.TestNestedB();
        testNestedA.setTestObjectB(testNestedB);
        testNestedB.setTestObjectC(new BeanWrapperFieldSetMapperTests.TestNestedC());
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestNestedA> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        context.getBeanFactory().registerSingleton("bean", testNestedA);
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "This is some dummy string", "1", "Another dummy", "2" }, new String[]{ "valueA", "valueB", "testObjectB.valueA", "testObjectB.testObjectC.value" });
        BeanWrapperFieldSetMapperTests.TestNestedA result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("This is some dummy string", result.getValueA());
        Assert.assertEquals(1, result.getValueB());
        Assert.assertEquals("Another dummy", result.getTestObjectB().getValueA());
        Assert.assertEquals(2, result.getTestObjectB().getTestObjectC().getValue());
    }

    @Test
    public void testMapperWithSimilarNamePropertyMatches() throws Exception {
        BeanWrapperFieldSetMapperTests.TestNestedA testNestedA = new BeanWrapperFieldSetMapperTests.TestNestedA();
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestNestedA> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        mapper.setDistanceLimit(2);
        context.getBeanFactory().registerSingleton("bean", testNestedA);
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "This is some dummy string", "1" }, new String[]{ "VALUE_A", "VALUE_B" });
        BeanWrapperFieldSetMapperTests.TestNestedA result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("This is some dummy string", result.getValueA());
        Assert.assertEquals(1, result.getValueB());
    }

    @Test
    public void testMapperWithNotVerySimilarNamePropertyMatches() throws Exception {
        BeanWrapperFieldSetMapperTests.TestNestedC testNestedC = new BeanWrapperFieldSetMapperTests.TestNestedC();
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestNestedC> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        context.getBeanFactory().registerSingleton("bean", testNestedC);
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "1" }, new String[]{ "foo" });
        BeanWrapperFieldSetMapperTests.TestNestedC result = mapper.mapFieldSet(fieldSet);
        // "foo" is similar enough to "value" that it matches - but only because
        // nothing else does...
        Assert.assertEquals(1, result.getValue());
    }

    @Test
    public void testMapperWithNestedBeanPathsAndPropertyMatches() throws Exception {
        BeanWrapperFieldSetMapperTests.TestNestedA testNestedA = new BeanWrapperFieldSetMapperTests.TestNestedA();
        BeanWrapperFieldSetMapperTests.TestNestedB testNestedB = new BeanWrapperFieldSetMapperTests.TestNestedB();
        testNestedA.setTestObjectB(testNestedB);
        testNestedB.setTestObjectC(new BeanWrapperFieldSetMapperTests.TestNestedC());
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestNestedA> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        context.getBeanFactory().registerSingleton("bean", testNestedA);
        mapper.setDistanceLimit(2);
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "Another dummy", "2" }, new String[]{ "TestObjectB.ValueA", "TestObjectB.TestObjectC.Value" });
        BeanWrapperFieldSetMapperTests.TestNestedA result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("Another dummy", result.getTestObjectB().getValueA());
        Assert.assertEquals(2, result.getTestObjectB().getTestObjectC().getValue());
    }

    @Test
    public void testMapperWithNestedBeanPathsAndPropertyMisMatches() throws Exception {
        BeanWrapperFieldSetMapperTests.TestNestedA testNestedA = new BeanWrapperFieldSetMapperTests.TestNestedA();
        BeanWrapperFieldSetMapperTests.TestNestedB testNestedB = new BeanWrapperFieldSetMapperTests.TestNestedB();
        testNestedA.setTestObjectB(testNestedB);
        BeanWrapperFieldSetMapper<?> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        context.getBeanFactory().registerSingleton("bean", testNestedA);
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "Another dummy" }, new String[]{ "TestObjectB.foo" });
        try {
            mapper.mapFieldSet(fieldSet);
            Assert.fail("Expected NotWritablePropertyException");
        } catch (NotWritablePropertyException e) {
            // expected
        }
    }

    @Test
    public void testMapperWithNestedBeanPathsAndPropertyPrefixMisMatches() throws Exception {
        BeanWrapperFieldSetMapperTests.TestNestedA testNestedA = new BeanWrapperFieldSetMapperTests.TestNestedA();
        BeanWrapperFieldSetMapperTests.TestNestedB testNestedB = new BeanWrapperFieldSetMapperTests.TestNestedB();
        testNestedA.setTestObjectB(testNestedB);
        BeanWrapperFieldSetMapper<?> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        context.getBeanFactory().registerSingleton("bean", testNestedA);
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "2" }, new String[]{ "TestObjectA.garbage" });
        try {
            mapper.mapFieldSet(fieldSet);
            Assert.fail("Expected NotWritablePropertyException");
        } catch (NotWritablePropertyException e) {
            // expected
        }
    }

    @Test
    public void testPlainBeanWrapper() throws Exception {
        BeanWrapperFieldSetMapperTests.TestObject result = new BeanWrapperFieldSetMapperTests.TestObject();
        BeanWrapperImpl wrapper = new BeanWrapperImpl(result);
        PropertiesEditor editor = new PropertiesEditor();
        editor.setAsText("varString=This is some dummy string\nvarBoolean=true\nvarChar=C");
        Properties props = ((Properties) (editor.getValue()));
        wrapper.setPropertyValues(props);
        Assert.assertEquals("This is some dummy string", result.getVarString());
        Assert.assertEquals(true, result.isVarBoolean());
        Assert.assertEquals('C', result.getVarChar());
    }

    @Test
    public void testNestedList() throws Exception {
        BeanWrapperFieldSetMapperTests.TestNestedList nestedList = new BeanWrapperFieldSetMapperTests.TestNestedList();
        List<BeanWrapperFieldSetMapperTests.TestNestedC> nestedC = new ArrayList<>();
        nestedC.add(new BeanWrapperFieldSetMapperTests.TestNestedC());
        nestedC.add(new BeanWrapperFieldSetMapperTests.TestNestedC());
        nestedC.add(new BeanWrapperFieldSetMapperTests.TestNestedC());
        nestedList.setNestedC(nestedC);
        BeanWrapperFieldSetMapper<?> mapper = new BeanWrapperFieldSetMapper();
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        context.getBeanFactory().registerSingleton("bean", nestedList);
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "1", "2", "3" }, new String[]{ "NestedC[0].Value", "NestedC[1].Value", "NestedC[2].Value" });
        mapper.mapFieldSet(fieldSet);
        Assert.assertEquals(1, nestedList.getNestedC().get(0).getValue());
        Assert.assertEquals(2, nestedList.getNestedC().get(1).getValue());
        Assert.assertEquals(3, nestedList.getNestedC().get(2).getValue());
    }

    @Test
    public void testAutoPopulateNestedList() throws Exception {
        BeanWrapperFieldSetMapperTests.TestNestedList nestedList = new BeanWrapperFieldSetMapperTests.TestNestedList();
        BeanWrapperFieldSetMapper<?> mapper = new BeanWrapperFieldSetMapper<Object>() {
            @Override
            protected void initBinder(DataBinder binder) {
                // Use reflection so it compiles (and fails) with Spring 2.5
                ReflectionTestUtils.setField(binder, "autoGrowNestedPaths", true);
            }
        };
        @SuppressWarnings("resource")
        StaticApplicationContext context = new StaticApplicationContext();
        mapper.setBeanFactory(context);
        context.getBeanFactory().registerSingleton("bean", nestedList);
        mapper.setPrototypeBeanName("bean");
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "1", "2", "3" }, new String[]{ "NestedC[0].Value", "NestedC[1].Value", "NestedC[2].Value" });
        mapper.mapFieldSet(fieldSet);
        Assert.assertEquals(1, nestedList.getNestedC().get(0).getValue());
        Assert.assertEquals(2, nestedList.getNestedC().get(1).getValue());
        Assert.assertEquals(3, nestedList.getNestedC().get(2).getValue());
    }

    @Test
    public void testPaddedLongWithNoEditor() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "00009" }, new String[]{ "varLong" });
        BeanWrapperFieldSetMapperTests.TestObject bean = mapper.mapFieldSet(fieldSet);
        // since Spring 2.5.5 this is OK (before that BATCH-261)
        Assert.assertEquals(9, bean.getVarLong());
    }

    @Test
    public void testPaddedLongWithEditor() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "00009" }, new String[]{ "varLong" });
        mapper.setCustomEditors(Collections.singletonMap(Long.TYPE, new CustomNumberEditor(Long.class, NumberFormat.getNumberInstance(), true)));
        BeanWrapperFieldSetMapperTests.TestObject bean = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals(9, bean.getVarLong());
    }

    @Test
    public void testPaddedLongWithDefaultAndCustomEditor() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "00009", "78" }, new String[]{ "varLong", "varInt" });
        mapper.setCustomEditors(Collections.singletonMap(Long.TYPE, new CustomNumberEditor(Long.class, NumberFormat.getNumberInstance(), true)));
        BeanWrapperFieldSetMapperTests.TestObject bean = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals(9, bean.getVarLong());
        Assert.assertEquals(78, bean.getVarInt());
    }

    @Test
    public void testNumberFormatWithDefaultAndCustomEditor() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "9.876,1", "7,890.1" }, new String[]{ "varDouble", "varFloat" });
        Map<Class<?>, PropertyEditor> editors = new HashMap<>();
        editors.put(Double.TYPE, new CustomNumberEditor(Double.class, NumberFormat.getInstance(Locale.GERMAN), true));
        editors.put(Float.TYPE, new CustomNumberEditor(Float.class, NumberFormat.getInstance(Locale.UK), true));
        mapper.setCustomEditors(editors);
        BeanWrapperFieldSetMapperTests.TestObject bean = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals(9876.1, bean.getVarDouble(), 0.01);
        Assert.assertEquals(7890.1, bean.getVarFloat(), 0.01);
    }

    @Test
    public void testConversionWithTestConverter() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "SHOULD BE CONVERTED" }, new String[]{ "varString" });
        mapper.setConversionService(new BeanWrapperFieldSetMapperTests.TestConversion());
        mapper.afterPropertiesSet();
        BeanWrapperFieldSetMapperTests.TestObject bean = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("Expecting the conversion to have returned \"CONVERTED\"", bean.getVarString(), "CONVERTED");
    }

    @Test
    public void testDefaultConversion() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        final String sampleString = "myString";
        Date date = new Date();
        BigDecimal bigDecimal = new BigDecimal(12345L);
        String dateString = date.toString();
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "12", "12345", "true", "Z", "123", "12345", "12345", "12", dateString, "12345", sampleString }, new String[]{ "varInt", "varLong", "varBoolean", "varChar", "varByte", "varFloat", "varDouble", "varShort", "varDate", "varBigDecimal", "varString" });
        mapper.setConversionService(new DefaultConversionService());
        mapper.afterPropertiesSet();
        BeanWrapperFieldSetMapperTests.TestObject bean = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("Expected 12 for varInt", bean.getVarInt(), 12);
        Assert.assertEquals("Expected 12345 for varLong", bean.getVarLong(), 12345L);
        Assert.assertEquals("Expected true for varBoolean", bean.isVarBoolean(), true);
        Assert.assertEquals("Expected Z for varChar", bean.getVarChar(), 'Z');
        Assert.assertEquals("Expected A for varByte", bean.getVarByte(), 123);
        Assert.assertEquals("Expected 12345 for varFloat", bean.getVarFloat(), 12345.0F, 1.0F);
        Assert.assertEquals("Expected 12345 for varDouble", bean.getVarDouble(), 12345.0, 1.0);
        Assert.assertEquals("Expected 12 for varShort", bean.getVarShort(), 12);
        Assert.assertEquals("Expected currentDate for varDate", bean.getVarDate().toString(), dateString);
        Assert.assertEquals("Expected 12345 for varBigDecimal", bean.getVarBigDecimal(), bigDecimal);
        Assert.assertEquals((("Expected " + sampleString) + " for varString"), bean.getVarString(), sampleString);
    }

    @Test
    public void testConversionAndCustomEditor() throws Exception {
        boolean errorCaught = false;
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        mapper.setConversionService(new BeanWrapperFieldSetMapperTests.TestConversion());
        mapper.setCustomEditors(Collections.singletonMap(Long.TYPE, new CustomNumberEditor(Long.class, NumberFormat.getNumberInstance(), true)));
        try {
            mapper.afterPropertiesSet();
        } catch (IllegalStateException ise) {
            errorCaught = true;
            Assert.assertEquals("Both customEditor and conversionService cannot be specified together.", ise.getMessage());
        }
        if (!errorCaught) {
            Assert.fail();
        }
    }

    @Test
    public void testBinderWithErrors() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "foo", "7890.1" }, new String[]{ "varDouble", "varFloat" });
        try {
            mapper.mapFieldSet(fieldSet);
            Assert.fail("Expected BindException");
        } catch (BindException e) {
            Assert.assertEquals(1, e.getErrorCount());
            Assert.assertEquals("typeMismatch", e.getFieldError("varDouble").getCode());
        }
    }

    @Test
    public void testFieldSpecificCustomEditor() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestTwoDoubles> mapper = new BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestTwoDoubles>() {
            @Override
            protected void initBinder(DataBinder binder) {
                binder.registerCustomEditor(Double.TYPE, "value", new CustomNumberEditor(Double.class, NumberFormat.getNumberInstance(Locale.GERMAN), true));
            }
        };
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestTwoDoubles.class);
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "9.876,1", "7890.1" }, new String[]{ "value", "other" });
        BeanWrapperFieldSetMapperTests.TestTwoDoubles bean = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals(9876.1, bean.getValue(), 0.01);
        Assert.assertEquals(7890.1, bean.getOther(), 0.01);
    }

    @Test
    public void testFieldSpecificCustomEditorWithRegistry() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestTwoDoubles> mapper = new BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestTwoDoubles>() {
            @Override
            public void registerCustomEditors(PropertyEditorRegistry registry) {
                super.registerCustomEditors(registry);
                registry.registerCustomEditor(Double.TYPE, "value", new CustomNumberEditor(Double.class, NumberFormat.getNumberInstance(Locale.GERMAN), true));
            }
        };
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestTwoDoubles.class);
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "9.876,1", "7890.1" }, new String[]{ "value", "other" });
        BeanWrapperFieldSetMapperTests.TestTwoDoubles bean = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals(9876.1, bean.getValue(), 0.01);
        Assert.assertEquals(7890.1, bean.getOther(), 0.01);
    }

    @Test
    public void testStrict() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setStrict(true);
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        mapper.afterPropertiesSet();
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "This is some dummy string", "This won't be mapped", "true", "C" }, new String[]{ "varString", "illegalPropertyName", "varBoolean", "varChar" });
        try {
            mapper.mapFieldSet(fieldSet);
            Assert.fail("expected error");
        } catch (NotWritablePropertyException e) {
            Assert.assertTrue(e.getMessage().contains("'illegalPropertyName'"));
        }
    }

    @Test
    public void testNotStrict() throws Exception {
        BeanWrapperFieldSetMapper<BeanWrapperFieldSetMapperTests.TestObject> mapper = new BeanWrapperFieldSetMapper();
        mapper.setStrict(false);
        mapper.setTargetType(BeanWrapperFieldSetMapperTests.TestObject.class);
        mapper.afterPropertiesSet();
        FieldSet fieldSet = new DefaultFieldSet(new String[]{ "This is some dummy string", "This won't be mapped", "true", "C" }, new String[]{ "varString", "illegalPropertyName", "varBoolean", "varChar" });
        BeanWrapperFieldSetMapperTests.TestObject result = mapper.mapFieldSet(fieldSet);
        Assert.assertEquals("This is some dummy string", result.getVarString());
        Assert.assertEquals(true, result.isVarBoolean());
        Assert.assertEquals('C', result.getVarChar());
    }

    private static class TestNestedList {
        List<BeanWrapperFieldSetMapperTests.TestNestedC> nestedC = new ArrayList<>();

        public List<BeanWrapperFieldSetMapperTests.TestNestedC> getNestedC() {
            return nestedC;
        }

        public void setNestedC(List<BeanWrapperFieldSetMapperTests.TestNestedC> nestedC) {
            this.nestedC = nestedC;
        }
    }

    public static class TestNestedA {
        private String valueA;

        private int valueB;

        BeanWrapperFieldSetMapperTests.TestNestedB testObjectB;

        public BeanWrapperFieldSetMapperTests.TestNestedB getTestObjectB() {
            return testObjectB;
        }

        public void setTestObjectB(BeanWrapperFieldSetMapperTests.TestNestedB testObjectB) {
            this.testObjectB = testObjectB;
        }

        public String getValueA() {
            return valueA;
        }

        public void setValueA(String valueA) {
            this.valueA = valueA;
        }

        public int getValueB() {
            return valueB;
        }

        public void setValueB(int valueB) {
            this.valueB = valueB;
        }
    }

    public static class TestNestedB {
        private String valueA;

        private BeanWrapperFieldSetMapperTests.TestNestedC testObjectC;

        public BeanWrapperFieldSetMapperTests.TestNestedC getTestObjectC() {
            return testObjectC;
        }

        public void setTestObjectC(BeanWrapperFieldSetMapperTests.TestNestedC testObjectC) {
            this.testObjectC = testObjectC;
        }

        public String getValueA() {
            return valueA;
        }

        public void setValueA(String valueA) {
            this.valueA = valueA;
        }
    }

    public static class TestNestedC {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class TestTwoDoubles {
        private double value;

        private double other;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getOther() {
            return other;
        }

        public void setOther(double other) {
            this.other = other;
        }
    }

    public static class TestObject {
        String varString;

        boolean varBoolean;

        char varChar;

        byte varByte;

        short varShort;

        int varInt;

        long varLong;

        float varFloat;

        double varDouble;

        BigDecimal varBigDecimal;

        Date varDate;

        public Date getVarDate() {
            return ((Date) (varDate.clone()));
        }

        public void setVarDate(Date varDate) {
            this.varDate = (varDate == null) ? null : ((Date) (varDate.clone()));
        }

        public TestObject() {
        }

        public BigDecimal getVarBigDecimal() {
            return varBigDecimal;
        }

        public void setVarBigDecimal(BigDecimal varBigDecimal) {
            this.varBigDecimal = varBigDecimal;
        }

        public boolean isVarBoolean() {
            return varBoolean;
        }

        public void setVarBoolean(boolean varBoolean) {
            this.varBoolean = varBoolean;
        }

        public byte getVarByte() {
            return varByte;
        }

        public void setVarByte(byte varByte) {
            this.varByte = varByte;
        }

        public char getVarChar() {
            return varChar;
        }

        public void setVarChar(char varChar) {
            this.varChar = varChar;
        }

        public double getVarDouble() {
            return varDouble;
        }

        public void setVarDouble(double varDouble) {
            this.varDouble = varDouble;
        }

        public float getVarFloat() {
            return varFloat;
        }

        public void setVarFloat(float varFloat) {
            this.varFloat = varFloat;
        }

        public long getVarLong() {
            return varLong;
        }

        public void setVarLong(long varLong) {
            this.varLong = varLong;
        }

        public short getVarShort() {
            return varShort;
        }

        public void setVarShort(short varShort) {
            this.varShort = varShort;
        }

        public String getVarString() {
            return varString;
        }

        public void setVarString(String varString) {
            this.varString = varString;
        }

        public int getVarInt() {
            return varInt;
        }

        public void setVarInt(int varInt) {
            this.varInt = varInt;
        }
    }

    public static class TestConversion implements ConversionService {
        @Override
        public boolean canConvert(@Nullable
        Class<?> sourceType, Class<?> targetType) {
            return true;
        }

        @Override
        public boolean canConvert(@Nullable
        TypeDescriptor sourceType, TypeDescriptor targetType) {
            return true;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T convert(@Nullable
        Object source, Class<T> targetType) {
            return ((T) ("CONVERTED"));
        }

        @Nullable
        @Override
        public Object convert(@Nullable
        Object source, @Nullable
        TypeDescriptor sourceType, TypeDescriptor targetType) {
            return "CONVERTED";
        }
    }
}

