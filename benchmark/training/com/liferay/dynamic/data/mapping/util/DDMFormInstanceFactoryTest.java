/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.dynamic.data.mapping.util;


import com.liferay.dynamic.data.mapping.annotations.DDMForm;
import com.liferay.dynamic.data.mapping.annotations.DDMFormField;
import com.liferay.dynamic.data.mapping.annotations.com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDMFormInstanceFactoryTest {
    @Test
    public void testCreateDynamicFormWithFieldSet() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypesFieldSet.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormFieldValue primitiveTypesDDMFormFieldValue = new DDMFormFieldValue();
        primitiveTypesDDMFormFieldValue.setName("primitiveTypes");
        boolean expectedBooleanValue = true;
        primitiveTypesDDMFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("boolean", Boolean.toString(expectedBooleanValue)));
        double expectedDoubleValue = 2.5;
        primitiveTypesDDMFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("double", String.valueOf(expectedDoubleValue)));
        float expectedFloatValue = 3.5F;
        primitiveTypesDDMFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("float", String.valueOf(expectedFloatValue)));
        int expectedIntegerValue = 2015;
        primitiveTypesDDMFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("integer", String.valueOf(expectedIntegerValue)));
        long expectedLongValue = 1000L;
        primitiveTypesDDMFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("long", String.valueOf(expectedLongValue)));
        short expectedShortValue = 5;
        primitiveTypesDDMFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("short", String.valueOf(expectedShortValue)));
        String expectedStringValue = "Frank Sinatra";
        primitiveTypesDDMFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("string", expectedStringValue));
        ddmFormValues.addDDMFormFieldValue(primitiveTypesDDMFormFieldValue);
        DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypesFieldSet dynamicFormWithFieldSet = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypesFieldSet.class, ddmFormValues);
        DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypes dynamicFormWithPrimitiveTypes = dynamicFormWithFieldSet.primitiveTypes();
        Assert.assertEquals(expectedBooleanValue, dynamicFormWithPrimitiveTypes.booleanValue());
        Assert.assertEquals(expectedDoubleValue, dynamicFormWithPrimitiveTypes.doubleValue(), 0.1);
        Assert.assertEquals(expectedFloatValue, dynamicFormWithPrimitiveTypes.floatValue(), 0.1);
        Assert.assertEquals(expectedIntegerValue, dynamicFormWithPrimitiveTypes.integerValue());
        Assert.assertEquals(expectedLongValue, dynamicFormWithPrimitiveTypes.longValue());
        Assert.assertEquals(expectedShortValue, dynamicFormWithPrimitiveTypes.shortValue());
        Assert.assertEquals(expectedStringValue, dynamicFormWithPrimitiveTypes.stringValue());
    }

    @Test
    public void testCreateDynamicFormWithPrimitiveArrayTypes() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveArrayTypes.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        Boolean[] expectedBooleanValues = new Boolean[]{ true, false, true };
        for (boolean expectedBooleanValue : expectedBooleanValues) {
            ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("boolean", Boolean.toString(expectedBooleanValue)));
        }
        String[] expectedStringValues = new String[]{ "Nina Simone", "Billie Holiday" };
        for (String expectedStringValue : expectedStringValues) {
            ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("string", expectedStringValue));
        }
        DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveArrayTypes dynamicFormWithPrimitiveArrayTypes = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveArrayTypes.class, ddmFormValues);
        Assert.assertArrayEquals(expectedBooleanValues, dynamicFormWithPrimitiveArrayTypes.booleanValues());
        Assert.assertArrayEquals(expectedStringValues, dynamicFormWithPrimitiveArrayTypes.stringValues());
    }

    @Test
    public void testCreateDynamicFormWithPrimitiveArrayTypesFieldSet() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithRepeatablePrimitiveArrayTypesFieldSet.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormFieldValue ddmFormFieldValue1 = new DDMFormFieldValue();
        ddmFormFieldValue1.setName("primitiveArrayTypes");
        ddmFormFieldValue1.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("boolean", "true"));
        ddmFormFieldValue1.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("boolean", "false"));
        ddmFormFieldValue1.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("string", "A"));
        ddmFormFieldValue1.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("string", "B"));
        ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue1);
        DDMFormFieldValue ddmFormFieldValue2 = new DDMFormFieldValue();
        ddmFormFieldValue2.setName("primitiveArrayTypes");
        ddmFormFieldValue2.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("boolean", "false"));
        ddmFormFieldValue2.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("boolean", "true"));
        ddmFormFieldValue2.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("string", "C"));
        ddmFormFieldValue2.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("string", "D"));
        ddmFormFieldValue2.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("string", "E"));
        ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue2);
        DDMFormInstanceFactoryTest.DynamicFormWithRepeatablePrimitiveArrayTypesFieldSet dynamicFormWithRepeatablePrimitiveArrayTypesFieldSet = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithRepeatablePrimitiveArrayTypesFieldSet.class, ddmFormValues);
        DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveArrayTypes[] dynamicFormWithPrimitiveArrayTypes = dynamicFormWithRepeatablePrimitiveArrayTypesFieldSet.primitiveArrayTypes();
        Assert.assertEquals(Arrays.toString(dynamicFormWithPrimitiveArrayTypes), 2, dynamicFormWithPrimitiveArrayTypes.length);
        Assert.assertArrayEquals(new Boolean[]{ true, false }, dynamicFormWithPrimitiveArrayTypes[0].booleanValues());
        Assert.assertArrayEquals(new String[]{ "A", "B" }, dynamicFormWithPrimitiveArrayTypes[0].stringValues());
        Assert.assertArrayEquals(new Boolean[]{ false, true }, dynamicFormWithPrimitiveArrayTypes[1].booleanValues());
        Assert.assertArrayEquals(new String[]{ "C", "D", "E" }, dynamicFormWithPrimitiveArrayTypes[1].stringValues());
    }

    @Test
    public void testCreateDynamicFormWithPrimitiveTypes() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypes.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        boolean expectedBooleanValue = true;
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("boolean", Boolean.toString(expectedBooleanValue)));
        double expectedDoubleValue = 2.5;
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("double", String.valueOf(expectedDoubleValue)));
        float expectedFloatValue = 3.5F;
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("float", String.valueOf(expectedFloatValue)));
        int expectedIntegerValue = 2015;
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("integer", String.valueOf(expectedIntegerValue)));
        long expectedLongValue = 1000L;
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("long", String.valueOf(expectedLongValue)));
        short expectedShortValue = 5;
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("short", String.valueOf(expectedShortValue)));
        String expectedStringValue = "Frank Sinatra";
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("string", expectedStringValue));
        DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypes dynamicFormWithPrimitiveTypes = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypes.class, ddmFormValues);
        Assert.assertEquals(expectedBooleanValue, dynamicFormWithPrimitiveTypes.booleanValue());
        Assert.assertEquals(expectedDoubleValue, dynamicFormWithPrimitiveTypes.doubleValue(), 0.1);
        Assert.assertEquals(expectedFloatValue, dynamicFormWithPrimitiveTypes.floatValue(), 0.1);
        Assert.assertEquals(expectedIntegerValue, dynamicFormWithPrimitiveTypes.integerValue());
        Assert.assertEquals(expectedLongValue, dynamicFormWithPrimitiveTypes.longValue());
        Assert.assertEquals(expectedShortValue, dynamicFormWithPrimitiveTypes.shortValue());
        Assert.assertEquals(expectedStringValue, dynamicFormWithPrimitiveTypes.stringValue());
    }

    @Test
    public void testCreateDynamicFormWithRepeatableFieldSet() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithRepeatableFieldSet.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        String[][] expectedParameters = new String[][]{ new String[]{ "Parameter 1", "Value 1" }, new String[]{ "Parameter 2", "Value 2" } };
        for (String[] expectedParameter : expectedParameters) {
            DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();
            ddmFormFieldValue.setName("parameters");
            ddmFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("name", expectedParameter[0]));
            ddmFormFieldValue.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("value", expectedParameter[1]));
            ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
        }
        DDMFormInstanceFactoryTest.DynamicFormWithRepeatableFieldSet dynamicFormWithFieldSet = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithRepeatableFieldSet.class, ddmFormValues);
        DDMFormInstanceFactoryTest.Parameter[] parameters = dynamicFormWithFieldSet.parameters();
        for (int i = 0; i < (expectedParameters.length); i++) {
            DDMFormInstanceFactoryTest.Parameter parameter = parameters[i];
            Assert.assertEquals(expectedParameters[i][0], parameter.name());
            Assert.assertEquals(expectedParameters[i][1], parameter.value());
        }
    }

    @Test
    public void testCreateDynamicFormWithSelectFields() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithSelectFields.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("letters", "[\"b\", \"c\"]"));
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("letters", "[\"c\"]"));
        DDMFormInstanceFactoryTest.DynamicFormWithSelectFields dynamicFormWithSelectFields = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithSelectFields.class, ddmFormValues);
        String[] actualLetters = dynamicFormWithSelectFields.letters();
        Assert.assertEquals(Arrays.toString(actualLetters), 2, actualLetters.length);
        Assert.assertEquals("b,c", actualLetters[0]);
        Assert.assertEquals("c", actualLetters[1]);
        Assert.assertEquals("b", dynamicFormWithSelectFields.letter());
    }

    @Test
    public void testCreateDynamicFormWithTuple() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithTuple.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        String expectedId = StringUtil.randomString();
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("id", expectedId));
        String expectedName = StringUtil.randomString();
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("name", expectedName));
        String expectedValue = StringUtil.randomString();
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("value", expectedValue));
        DDMFormInstanceFactoryTest.DynamicFormWithTuple dynamicFormWithTuple = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithTuple.class, ddmFormValues);
        Assert.assertEquals(expectedId, dynamicFormWithTuple.id());
        Assert.assertEquals(expectedName, dynamicFormWithTuple.name());
        Assert.assertEquals(expectedValue, dynamicFormWithTuple.value());
        DDMFormInstanceFactoryTest.Parameter parameter = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.Parameter.class, ddmFormValues);
        Assert.assertEquals(expectedName, parameter.name());
        Assert.assertEquals(expectedValue, parameter.value());
    }

    @Test
    public void testCreateDynamicFormWithUnrelatedClassDefinition() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.Parameter.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        String expectedName = StringUtil.randomString();
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("name", expectedName));
        String expectedValue = StringUtil.randomString();
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("value", expectedValue));
        DDMFormInstanceFactoryTest.DynamicFormWithFieldSet dynamicFormWithFieldSet = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithFieldSet.class, ddmFormValues);
        Assert.assertNull(dynamicFormWithFieldSet.parameter());
    }

    @Test
    public void testGetDefaultValueDynamicFormWithFieldSet() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithFieldSet.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormInstanceFactoryTest.DynamicFormWithFieldSet dynamicFormWithFieldSet = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithFieldSet.class, ddmFormValues);
        Assert.assertEquals(null, dynamicFormWithFieldSet.parameter());
    }

    @Test
    public void testGetDefaultValueDynamicFormWithPrimitiveArrayTypes() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveArrayTypes.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveArrayTypes dynamicFormWithPrimitiveArrayTypes = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveArrayTypes.class, ddmFormValues);
        Assert.assertArrayEquals(new String[0], dynamicFormWithPrimitiveArrayTypes.stringValues());
        Assert.assertArrayEquals(new Boolean[0], dynamicFormWithPrimitiveArrayTypes.booleanValues());
    }

    @Test
    public void testGetDefaultValueDynamicFormWithRepeatableFieldSet() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithRepeatableFieldSet.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormInstanceFactoryTest.DynamicFormWithRepeatableFieldSet dynamicFormWithRepeatableFieldSet = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithRepeatableFieldSet.class, ddmFormValues);
        Assert.assertArrayEquals(new DDMFormInstanceFactoryTest.Parameter[0], dynamicFormWithRepeatableFieldSet.parameters());
    }

    @Test
    public void testGetDefaultValueFromPrimitiveTypes() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypes.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypes dynamicFormWithPrimitiveTypes = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypes.class, ddmFormValues);
        Assert.assertEquals(false, dynamicFormWithPrimitiveTypes.booleanValue());
        Assert.assertEquals(0.0, dynamicFormWithPrimitiveTypes.doubleValue(), 0.1);
        Assert.assertEquals(0.0F, dynamicFormWithPrimitiveTypes.floatValue(), 0.1);
        Assert.assertEquals(0, dynamicFormWithPrimitiveTypes.integerValue());
        Assert.assertEquals(0, dynamicFormWithPrimitiveTypes.longValue());
        Assert.assertEquals(0, dynamicFormWithPrimitiveTypes.shortValue());
        Assert.assertEquals(null, dynamicFormWithPrimitiveTypes.stringValue());
    }

    @Test
    public void testGetDefaultValueFromPrimitiveTypesWithPredefinedValue() {
        com.liferay.dynamic.data.mapping.model.DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypesWithPredefinedValue.class);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypesWithPredefinedValue dynamicFormWithPrimitiveTypesWithPredefinedValue = DDMFormInstanceFactory.create(DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypesWithPredefinedValue.class, ddmFormValues);
        Assert.assertEquals(true, dynamicFormWithPrimitiveTypesWithPredefinedValue.booleanValue());
        Assert.assertEquals(1.0, dynamicFormWithPrimitiveTypesWithPredefinedValue.doubleValue(), 0.1);
        Assert.assertEquals(1.0F, dynamicFormWithPrimitiveTypesWithPredefinedValue.floatValue(), 0.1);
        Assert.assertEquals(1, dynamicFormWithPrimitiveTypesWithPredefinedValue.integerValue());
        Assert.assertEquals(1, dynamicFormWithPrimitiveTypesWithPredefinedValue.longValue());
        Assert.assertEquals(1, dynamicFormWithPrimitiveTypesWithPredefinedValue.shortValue());
        Assert.assertEquals("Joe", dynamicFormWithPrimitiveTypesWithPredefinedValue.stringValue());
    }

    @DDMForm
    private interface DynamicFormWithFieldSet {
        @DDMFormField
        public DDMFormInstanceFactoryTest.Parameter parameter();
    }

    @DDMForm
    private interface DynamicFormWithPrimitiveArrayTypes {
        @DDMFormField(name = "boolean")
        public Boolean[] booleanValues();

        @DDMFormField(name = "string")
        public String[] stringValues();
    }

    @DDMForm
    private interface DynamicFormWithPrimitiveTypes {
        @DDMFormField(name = "boolean")
        public boolean booleanValue();

        @DDMFormField(name = "double")
        public double doubleValue();

        @DDMFormField(name = "float")
        public float floatValue();

        @DDMFormField(name = "integer")
        public int integerValue();

        @DDMFormField(name = "long")
        public long longValue();

        @DDMFormField(name = "short")
        public short shortValue();

        @DDMFormField(name = "string")
        public String stringValue();
    }

    @DDMForm
    private interface DynamicFormWithPrimitiveTypesFieldSet {
        @DDMFormField
        public DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveTypes primitiveTypes();
    }

    @DDMForm
    private interface DynamicFormWithPrimitiveTypesWithPredefinedValue {
        @DDMFormField(name = "boolean", predefinedValue = "true")
        public boolean booleanValue();

        @DDMFormField(name = "double", predefinedValue = "1.0")
        public double doubleValue();

        @DDMFormField(name = "float", predefinedValue = "1.0")
        public float floatValue();

        @DDMFormField(name = "integer", predefinedValue = "1")
        public int integerValue();

        @DDMFormField(name = "long", predefinedValue = "1")
        public long longValue();

        @DDMFormField(name = "short", predefinedValue = "1")
        public short shortValue();

        @DDMFormField(name = "string", predefinedValue = "Joe")
        public String stringValue();
    }

    @DDMForm
    private interface DynamicFormWithRepeatableFieldSet {
        @DDMFormField
        public DDMFormInstanceFactoryTest.Parameter[] parameters();
    }

    @DDMForm
    private interface DynamicFormWithRepeatablePrimitiveArrayTypesFieldSet {
        @DDMFormField
        public DDMFormInstanceFactoryTest.DynamicFormWithPrimitiveArrayTypes[] primitiveArrayTypes();
    }

    @DDMForm
    private interface DynamicFormWithSelectFields {
        @DDMFormField(optionLabels = { "A", "B", "C" }, optionValues = { "a", "b", "c" }, predefinedValue = "[\"b\"]", type = "select")
        public String letter();

        @DDMFormField(optionLabels = { "A", "B", "C" }, optionValues = { "a", "b", "c" }, type = "select")
        public String[] letters();
    }

    @DDMForm
    private interface DynamicFormWithTuple extends DDMFormInstanceFactoryTest.Parameter {
        @DDMFormField
        public String id();
    }

    @DDMForm
    private interface Parameter {
        @DDMFormField
        public String name();

        @DDMFormField
        public String value();
    }
}

