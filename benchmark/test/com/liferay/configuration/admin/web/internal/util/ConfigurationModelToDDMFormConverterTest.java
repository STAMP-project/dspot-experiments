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
package com.liferay.configuration.admin.web.internal.util;


import DDMFormFieldType.CHECKBOX;
import DDMFormFieldType.SELECT;
import DDMFormFieldType.TEXT;
import ExtendedAttributeDefinition.BOOLEAN;
import ExtendedAttributeDefinition.INTEGER;
import ExtendedAttributeDefinition.PASSWORD;
import ExtendedAttributeDefinition.STRING;
import ExtendedObjectClassDefinition.ALL;
import ExtendedObjectClassDefinition.OPTIONAL;
import ExtendedObjectClassDefinition.REQUIRED;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class ConfigurationModelToDDMFormConverterTest extends Mockito {
    @Test
    public void testGetWithCheckboxField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, ALL);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, REQUIRED);
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Boolean");
        whenGetType(extendedAttributeDefinition, BOOLEAN);
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        ConfigurationModelToDDMFormConverter configurationModelToDDMFormConverter = Mockito.spy(new ConfigurationModelToDDMFormConverter(configurationModel, _enLocale, new ConfigurationModelToDDMFormConverterTest.EmptyResourceBundle()));
        whenGetConfigurationDDMForm(configurationModelToDDMFormConverter, null);
        DDMForm ddmForm = configurationModelToDDMFormConverter.getDDMForm();
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(false);
        DDMFormField ddmFormField = ddmFormFieldsMap.get("Boolean");
        Assert.assertNotNull(ddmFormField);
        Assert.assertEquals(CHECKBOX, ddmFormField.getType());
        Assert.assertEquals("boolean", ddmFormField.getDataType());
        Assert.assertFalse(ddmFormField.isRepeatable());
        Assert.assertFalse(ddmFormField.isRequired());
        Value predefinedValue = ddmFormField.getPredefinedValue();
        Assert.assertEquals("false", predefinedValue.getString(_enLocale));
    }

    @Test
    public void testGetWithIntegerField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, ALL);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, REQUIRED);
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Integer");
        whenGetType(extendedAttributeDefinition, INTEGER);
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        ConfigurationModelToDDMFormConverter configurationModelToDDMFormConverter = Mockito.spy(new ConfigurationModelToDDMFormConverter(configurationModel, _enLocale, new ConfigurationModelToDDMFormConverterTest.EmptyResourceBundle()));
        whenGetConfigurationDDMForm(configurationModelToDDMFormConverter, null);
        DDMForm ddmForm = configurationModelToDDMFormConverter.getDDMForm();
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(false);
        DDMFormField ddmFormField = ddmFormFieldsMap.get("Integer");
        Assert.assertNotNull(ddmFormField);
        Assert.assertEquals(TEXT, ddmFormField.getType());
        Assert.assertEquals("integer", ddmFormField.getDataType());
        Assert.assertFalse(ddmFormField.isRepeatable());
        Assert.assertTrue(ddmFormField.isRequired());
        LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();
        Assert.assertEquals(_enLocale, predefinedValue.getDefaultLocale());
        Assert.assertEquals("0", predefinedValue.getString(_enLocale));
    }

    @Test
    public void testGetWithPasswordField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, ALL);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, OPTIONAL);
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Password");
        whenGetType(extendedAttributeDefinition, PASSWORD);
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        ConfigurationModelToDDMFormConverter configurationModelToDDMFormConverter = Mockito.spy(new ConfigurationModelToDDMFormConverter(configurationModel, _enLocale, new ConfigurationModelToDDMFormConverterTest.EmptyResourceBundle()));
        whenGetConfigurationDDMForm(configurationModelToDDMFormConverter, null);
        DDMForm ddmForm = configurationModelToDDMFormConverter.getDDMForm();
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(false);
        DDMFormField ddmFormField = ddmFormFieldsMap.get("Password");
        Assert.assertNotNull(ddmFormField);
        Assert.assertEquals(DDMFormFieldType.PASSWORD, ddmFormField.getType());
        Assert.assertEquals("string", ddmFormField.getDataType());
        Assert.assertFalse(ddmFormField.isRepeatable());
        Assert.assertFalse(ddmFormField.isRequired());
    }

    @Test
    public void testGetWithSelectField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, ALL);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, REQUIRED);
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Select");
        whenGetType(extendedAttributeDefinition, STRING);
        whenGetOptionLabels(extendedAttributeDefinition, new String[]{ "Label 1", "Label 2" });
        whenGetOptionValues(extendedAttributeDefinition, new String[]{ "Value 1", "Value 2" });
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        ConfigurationModelToDDMFormConverter configurationModelToDDMFormConverter = Mockito.spy(new ConfigurationModelToDDMFormConverter(configurationModel, _enLocale, new ConfigurationModelToDDMFormConverterTest.EmptyResourceBundle()));
        whenGetConfigurationDDMForm(configurationModelToDDMFormConverter, null);
        DDMForm ddmForm = configurationModelToDDMFormConverter.getDDMForm();
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(false);
        DDMFormField ddmFormField = ddmFormFieldsMap.get("Select");
        Assert.assertNotNull(ddmFormField);
        Assert.assertEquals(SELECT, ddmFormField.getType());
        Assert.assertEquals("string", ddmFormField.getDataType());
        Assert.assertFalse(ddmFormField.isRepeatable());
        Assert.assertTrue(ddmFormField.isRequired());
        DDMFormFieldOptions ddmFormFieldOptions = ddmFormField.getDDMFormFieldOptions();
        Assert.assertEquals(_enLocale, ddmFormFieldOptions.getDefaultLocale());
        Set<String> optionValues = ddmFormFieldOptions.getOptionsValues();
        Assert.assertTrue(optionValues.toString(), optionValues.contains("Value 1"));
        Assert.assertTrue(optionValues.toString(), optionValues.contains("Value 2"));
        LocalizedValue value1Labels = ddmFormFieldOptions.getOptionLabels("Value 1");
        Assert.assertEquals(_enLocale, value1Labels.getDefaultLocale());
        Assert.assertEquals("Label 1", value1Labels.getString(_enLocale));
        LocalizedValue value2Labels = ddmFormFieldOptions.getOptionLabels("Value 2");
        Assert.assertEquals(_enLocale, value2Labels.getDefaultLocale());
        Assert.assertEquals("Label 2", value2Labels.getString(_enLocale));
    }

    @Test
    public void testGetWithTextField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, ALL);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition }, OPTIONAL);
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Text");
        whenGetType(extendedAttributeDefinition, STRING);
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        ConfigurationModelToDDMFormConverter configurationModelToDDMFormConverter = Mockito.spy(new ConfigurationModelToDDMFormConverter(configurationModel, _enLocale, new ConfigurationModelToDDMFormConverterTest.EmptyResourceBundle()));
        whenGetConfigurationDDMForm(configurationModelToDDMFormConverter, null);
        DDMForm ddmForm = configurationModelToDDMFormConverter.getDDMForm();
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(false);
        DDMFormField ddmFormField = ddmFormFieldsMap.get("Text");
        Assert.assertNotNull(ddmFormField);
        Assert.assertEquals(TEXT, ddmFormField.getType());
        Assert.assertEquals("string", ddmFormField.getDataType());
        Assert.assertFalse(ddmFormField.isRepeatable());
        Assert.assertFalse(ddmFormField.isRequired());
    }

    private final Locale _enLocale = LocaleUtil.US;

    private static class EmptyResourceBundle extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[0][];
        }
    }
}

