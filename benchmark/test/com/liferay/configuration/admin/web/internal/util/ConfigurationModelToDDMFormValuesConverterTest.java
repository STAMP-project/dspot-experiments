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


import StringPool.BLANK;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Vector;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.service.cm.Configuration;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class ConfigurationModelToDDMFormValuesConverterTest extends Mockito {
    @Test
    public void testGetValuesByConfigurationAndNegativeCardinalityWithTextField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, (-2));
        whenGetDefaultValue(extendedAttributeDefinition, null);
        whenGetID(extendedAttributeDefinition, "Text");
        Configuration configuration = Mockito.mock(Configuration.class);
        Dictionary<String, Object> properties = new Hashtable<>();
        Vector<String> vector = new Vector<>();
        vector.add("Joe Bloggs");
        vector.add("Ella Fitzgerald");
        properties.put("Text", vector);
        whenGetProperties(configuration, properties);
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValues ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 2, ddmFormFieldValues.size());
        Assert.assertEquals("Joe Bloggs", getValueString(ddmFormFieldValues.get(0)));
        Assert.assertEquals("Ella Fitzgerald", getValueString(ddmFormFieldValues.get(1)));
    }

    @Test
    public void testGetValuesByConfigurationAndPositiveCardinalityWithTextField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 2);
        whenGetDefaultValue(extendedAttributeDefinition, null);
        whenGetID(extendedAttributeDefinition, "Text");
        Configuration configuration = Mockito.mock(Configuration.class);
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("Text", new String[]{ "Joe Bloggs", "Ella Fitzgerald" });
        whenGetProperties(configuration, properties);
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValues ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 2, ddmFormFieldValues.size());
        Assert.assertEquals("Joe Bloggs", getValueString(ddmFormFieldValues.get(0)));
        Assert.assertEquals("Ella Fitzgerald", getValueString(ddmFormFieldValues.get(1)));
    }

    @Test
    public void testGetValuesByConfigurationWithCheckboxField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Boolean");
        Configuration configuration = Mockito.mock(Configuration.class);
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("Boolean", Boolean.TRUE);
        whenGetProperties(configuration, properties);
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValues ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        Assert.assertEquals("true", getValueString(ddmFormFieldValues.get(0)));
    }

    @Test
    public void testGetValuesByConfigurationWithPresentKey() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetDefaultValue(extendedAttributeDefinition, new String[]{ "9999" });
        whenGetID(extendedAttributeDefinition, "Long");
        Configuration configuration = Mockito.mock(Configuration.class);
        Dictionary<String, Object> properties = new Hashtable<>();
        whenGetProperties(configuration, properties);
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValues ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        Assert.assertEquals("It should return the default value when no key is set", "9999", getValueString(ddmFormFieldValues.get(0)));
        properties.put("Long", 0L);
        ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        Assert.assertEquals("It should return the configuration value if they key is set", "0", getValueString(ddmFormFieldValues.get(0)));
    }

    @Test
    public void testGetValuesByDefaultValueWithCheckboxField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Boolean");
        whenGetDefaultValue(extendedAttributeDefinition, new String[]{ "false" });
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        DDMFormValues ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        Assert.assertEquals("false", getValueString(ddmFormFieldValues.get(0)));
    }

    @Test
    public void testGetValuesByDefaultValueWithSelectField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetDefaultValue(extendedAttributeDefinition, new String[]{ "REQUEST_HEADER" });
        whenGetID(extendedAttributeDefinition, "Select");
        whenGetOptionLabels(extendedAttributeDefinition, new String[]{ "COOKIE", "REQUEST_HEADER" });
        whenGetOptionValues(extendedAttributeDefinition, new String[]{ "COOKIE", "REQUEST_HEADER" });
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        DDMFormValues ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        Assert.assertEquals("[\"REQUEST_HEADER\"]", getValueString(ddmFormFieldValues.get(0)));
    }

    @Test
    public void testGetValuesByDefaultValueWithTextField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 2);
        whenGetDefaultValue(extendedAttributeDefinition, new String[]{ "Joe Bloggs|Ella Fitzgerald" });
        whenGetID(extendedAttributeDefinition, "Text");
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        DDMFormValues ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 2, ddmFormFieldValues.size());
        Assert.assertEquals("Joe Bloggs", getValueString(ddmFormFieldValues.get(0)));
        Assert.assertEquals("Ella Fitzgerald", getValueString(ddmFormFieldValues.get(1)));
    }

    @Test
    public void testGetValuesByEmptyDefaultValueWithTextField() {
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetDefaultValue(extendedAttributeDefinition, null);
        whenGetID(extendedAttributeDefinition, "Text");
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, null, null, null, false);
        DDMFormValues ddmFormValues = getDDMFormValues(configurationModel, getDDMForm(configurationModel));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        Assert.assertEquals(BLANK, getValueString(ddmFormFieldValues.get(0)));
    }

    private final Locale _enLocale = LocaleUtil.US;

    private static class EmptyResourceBundle extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[0][];
        }
    }
}

