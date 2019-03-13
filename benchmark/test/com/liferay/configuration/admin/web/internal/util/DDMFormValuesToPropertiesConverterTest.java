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
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.Dictionary;
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
public class DDMFormValuesToPropertiesConverterTest extends Mockito {
    @Test
    public void testArrayBooleanValues() {
        DDMForm ddmForm = new DDMForm();
        ddmForm.addAvailableLocale(_enLocale);
        ddmForm.setDefaultLocale(_enLocale);
        DDMFormField booleanDDMFormField = DDMFormTestUtil.createDDMFormField("Boolean", "Boolean", CHECKBOX, "boolean", false, true, false);
        ddmForm.addDDMFormField(booleanDDMFormField);
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.addAvailableLocale(_enLocale);
        ddmFormValues.setDefaultLocale(_enLocale);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Boolean", "true", _enLocale));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Boolean", "false", _enLocale));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Boolean", "true", _enLocale));
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 3);
        whenGetID(extendedAttributeDefinition, "Boolean");
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValuesToPropertiesConverter ddmFormValuesToPropertiesConverter = new DDMFormValuesToPropertiesConverter(configurationModel, ddmFormValues, _jsonFactory, _enLocale);
        Dictionary<String, Object> properties = ddmFormValuesToPropertiesConverter.getProperties();
        Object value = properties.get("Boolean");
        Boolean[] booleanValues = ((Boolean[]) (value));
        Assert.assertEquals(true, booleanValues[0]);
        Assert.assertEquals(false, booleanValues[1]);
        Assert.assertEquals(true, booleanValues[2]);
    }

    @Test
    public void testSimpleBooleanValue() {
        DDMForm ddmForm = new DDMForm();
        ddmForm.addAvailableLocale(_enLocale);
        ddmForm.setDefaultLocale(_enLocale);
        DDMFormField booleanDDMFormField = DDMFormTestUtil.createDDMFormField("Boolean", "Boolean", CHECKBOX, "boolean", false, false, false);
        ddmForm.addDDMFormField(booleanDDMFormField);
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.addAvailableLocale(_enLocale);
        ddmFormValues.setDefaultLocale(_enLocale);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Boolean", "true", _enLocale));
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Boolean");
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValuesToPropertiesConverter ddmFormValuesToPropertiesConverter = new DDMFormValuesToPropertiesConverter(configurationModel, ddmFormValues, _jsonFactory, _enLocale);
        Dictionary<String, Object> properties = ddmFormValuesToPropertiesConverter.getProperties();
        Assert.assertEquals(true, properties.get("Boolean"));
    }

    @Test
    public void testSimpleIntegerInvalidValue() {
        DDMForm ddmForm = new DDMForm();
        ddmForm.addAvailableLocale(_enLocale);
        ddmForm.setDefaultLocale(_enLocale);
        DDMFormField integerDDMFormField = DDMFormTestUtil.createDDMFormField("Integer", "Integer", TEXT, "integer", false, false, false);
        ddmForm.addDDMFormField(integerDDMFormField);
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.addAvailableLocale(_enLocale);
        ddmFormValues.setDefaultLocale(_enLocale);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Integer", "42f", _enLocale));
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Integer");
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValuesToPropertiesConverter ddmFormValuesToPropertiesConverter = new DDMFormValuesToPropertiesConverter(configurationModel, ddmFormValues, _jsonFactory, _enLocale);
        Dictionary<String, Object> properties = ddmFormValuesToPropertiesConverter.getProperties();
        Assert.assertEquals(0, properties.get("Integer"));
    }

    @Test
    public void testSimpleIntegerValue() {
        DDMForm ddmForm = new DDMForm();
        ddmForm.addAvailableLocale(_enLocale);
        ddmForm.setDefaultLocale(_enLocale);
        DDMFormField integerDDMFormField = DDMFormTestUtil.createDDMFormField("Integer", "Integer", TEXT, "integer", false, false, false);
        ddmForm.addDDMFormField(integerDDMFormField);
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.addAvailableLocale(_enLocale);
        ddmFormValues.setDefaultLocale(_enLocale);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Integer", "42", _enLocale));
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Integer");
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValuesToPropertiesConverter ddmFormValuesToPropertiesConverter = new DDMFormValuesToPropertiesConverter(configurationModel, ddmFormValues, _jsonFactory, _enLocale);
        Dictionary<String, Object> properties = ddmFormValuesToPropertiesConverter.getProperties();
        Assert.assertEquals(42, properties.get("Integer"));
    }

    @Test
    public void testSimpleSelectValue() {
        DDMForm ddmForm = new DDMForm();
        ddmForm.addAvailableLocale(_enLocale);
        ddmForm.setDefaultLocale(_enLocale);
        DDMFormField integerDDMFormField = DDMFormTestUtil.createDDMFormField("Select", "Select", SELECT, "integer", false, false, false);
        ddmForm.addDDMFormField(integerDDMFormField);
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.addAvailableLocale(_enLocale);
        ddmFormValues.setDefaultLocale(_enLocale);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Select", "[\"42\"]", _enLocale));
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, 0);
        whenGetID(extendedAttributeDefinition, "Select");
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValuesToPropertiesConverter ddmFormValuesToPropertiesConverter = new DDMFormValuesToPropertiesConverter(configurationModel, ddmFormValues, _jsonFactory, _enLocale);
        Dictionary<String, Object> properties = ddmFormValuesToPropertiesConverter.getProperties();
        Assert.assertEquals(42, properties.get("Select"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testVectorBooleanValues() {
        DDMForm ddmForm = new DDMForm();
        ddmForm.addAvailableLocale(_enLocale);
        ddmForm.setDefaultLocale(_enLocale);
        DDMFormField booleanDDMFormField = DDMFormTestUtil.createDDMFormField("Boolean", "Boolean", CHECKBOX, "boolean", false, true, false);
        ddmForm.addDDMFormField(booleanDDMFormField);
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.addAvailableLocale(_enLocale);
        ddmFormValues.setDefaultLocale(_enLocale);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Boolean", "true", _enLocale));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Boolean", "false", _enLocale));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Boolean", "true", _enLocale));
        ExtendedObjectClassDefinition extendedObjectClassDefinition = Mockito.mock(ExtendedObjectClassDefinition.class);
        ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(ExtendedAttributeDefinition.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        whenGetAttributeDefinitions(extendedObjectClassDefinition, new ExtendedAttributeDefinition[]{ extendedAttributeDefinition });
        whenGetCardinality(extendedAttributeDefinition, (-3));
        whenGetID(extendedAttributeDefinition, "Boolean");
        ConfigurationModel configurationModel = new ConfigurationModel(extendedObjectClassDefinition, configuration, null, null, false);
        DDMFormValuesToPropertiesConverter ddmFormValuesToPropertiesConverter = new DDMFormValuesToPropertiesConverter(configurationModel, ddmFormValues, _jsonFactory, _enLocale);
        Dictionary<String, Object> properties = ddmFormValuesToPropertiesConverter.getProperties();
        Object value = properties.get("Boolean");
        Vector<Boolean> booleanValues = ((Vector<Boolean>) (value));
        Assert.assertEquals(true, booleanValues.get(0));
        Assert.assertEquals(false, booleanValues.get(1));
        Assert.assertEquals(true, booleanValues.get(2));
    }

    private final Locale _enLocale = LocaleUtil.US;

    private JSONFactory _jsonFactory;
}

