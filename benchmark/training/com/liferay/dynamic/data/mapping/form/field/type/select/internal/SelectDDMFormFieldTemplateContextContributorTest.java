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
package com.liferay.dynamic.data.mapping.form.field.type.select.internal;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Marcellus Tavares
 */
@PrepareForTest({ PortalClassLoaderUtil.class, ResourceBundleUtil.class })
@RunWith(PowerMockRunner.class)
public class SelectDDMFormFieldTemplateContextContributorTest extends BaseDDMFormFieldTypeSettingsTestCase {
    @Test
    public void testGetMultiple1() {
        String fieldName = "field";
        DDMFormField ddmFormField = new DDMFormField(fieldName, "select");
        ddmFormField.setProperty("multiple", "true");
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setProperty("changedProperties", null);
        Assert.assertEquals(true, _selectDDMFormFieldTemplateContextContributor.getMultiple(ddmFormField, ddmFormFieldRenderingContext));
    }

    @Test
    public void testGetMultiple2() {
        DDMFormField ddmFormField = new DDMFormField("field", "select");
        ddmFormField.setProperty("multiple", "true");
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        Map<String, Object> changedProperties = new HashMap<>();
        ddmFormFieldRenderingContext.setProperty("changedProperties", changedProperties);
        Assert.assertEquals(true, _selectDDMFormFieldTemplateContextContributor.getMultiple(ddmFormField, ddmFormFieldRenderingContext));
    }

    @Test
    public void testGetMultiple3() {
        DDMFormField ddmFormField = new DDMFormField("field", "select");
        ddmFormField.setProperty("multiple", "false");
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        Map<String, Object> changedProperties = new HashMap<>();
        changedProperties.put("multiple", true);
        ddmFormFieldRenderingContext.setProperty("changedProperties", changedProperties);
        Assert.assertEquals(true, _selectDDMFormFieldTemplateContextContributor.getMultiple(ddmFormField, ddmFormFieldRenderingContext));
    }

    @Test
    public void testGetOptions() {
        List<Object> expectedOptions = new ArrayList<>();
        expectedOptions.add(createOption("Label 1", "value 1"));
        expectedOptions.add(createOption("Label 2", "value 2"));
        expectedOptions.add(createOption("Label 3", "value 3"));
        DDMFormFieldOptions ddmFormFieldOptions = createDDMFormFieldOptions();
        List<Object> actualOptions = getActualOptions(ddmFormFieldOptions, US);
        Assert.assertEquals(expectedOptions, actualOptions);
    }

    @Test
    public void testGetParameters1() throws Exception {
        DDMFormField ddmFormField = new DDMFormField("field", "select");
        ddmFormField.setProperty("dataSourceType", "data-provider");
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setLocale(US);
        ddmFormFieldRenderingContext.setValue("[\"value 1\"]");
        setUpDDMFormFieldOptionsFactory(ddmFormField, ddmFormFieldRenderingContext);
        SelectDDMFormFieldTemplateContextContributor spy = createSpy();
        Map<String, Object> parameters = spy.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        Assert.assertTrue(parameters.containsKey("dataSourceType"));
        Assert.assertEquals("data-provider", parameters.get("dataSourceType"));
        Assert.assertTrue(parameters.containsKey("multiple"));
        Assert.assertEquals(false, parameters.get("multiple"));
        Assert.assertTrue(parameters.containsKey("options"));
        List<Object> options = ((List<Object>) (parameters.get("options")));
        Assert.assertEquals(options.toString(), 3, options.size());
        Map<String, String> optionMap = ((Map<String, String>) (options.get(0)));
        Assert.assertEquals("Label 1", optionMap.get("label"));
        Assert.assertEquals("value 1", optionMap.get("value"));
        optionMap = ((Map<String, String>) (options.get(1)));
        Assert.assertEquals("Label 2", optionMap.get("label"));
        Assert.assertEquals("value 2", optionMap.get("value"));
        optionMap = ((Map<String, String>) (options.get(2)));
        Assert.assertEquals("Label 3", optionMap.get("label"));
        Assert.assertEquals("value 3", optionMap.get("value"));
        List<String> value = ((List<String>) (parameters.get("value")));
        Assert.assertEquals(value.toString(), 1, value.size());
        Assert.assertTrue(value.toString(), value.contains("value 1"));
    }

    @Test
    public void testGetParameters2() throws Exception {
        DDMFormField ddmFormField = new DDMFormField("field", "select");
        ddmFormField.setProperty("dataSourceType", "manual");
        ddmFormField.setMultiple(true);
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setLocale(US);
        LocalizedValue predefinedValue = new LocalizedValue();
        predefinedValue.setDefaultLocale(US);
        predefinedValue.addString(US, "[\"value 2\",\"value 3\"]");
        ddmFormField.setPredefinedValue(predefinedValue);
        setUpDDMFormFieldOptionsFactory(ddmFormField, ddmFormFieldRenderingContext);
        SelectDDMFormFieldTemplateContextContributor spy = createSpy();
        Map<String, Object> parameters = spy.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        Assert.assertTrue(parameters.containsKey("dataSourceType"));
        Assert.assertEquals("manual", parameters.get("dataSourceType"));
        Assert.assertTrue(parameters.containsKey("multiple"));
        Assert.assertEquals(true, parameters.get("multiple"));
        Assert.assertTrue(parameters.containsKey("options"));
        List<Object> options = ((List<Object>) (parameters.get("options")));
        Assert.assertEquals(options.toString(), 3, options.size());
        Map<String, String> optionMap = ((Map<String, String>) (options.get(0)));
        Assert.assertEquals("Label 1", optionMap.get("label"));
        Assert.assertEquals("value 1", optionMap.get("value"));
        optionMap = ((Map<String, String>) (options.get(1)));
        Assert.assertEquals("Label 2", optionMap.get("label"));
        Assert.assertEquals("value 2", optionMap.get("value"));
        optionMap = ((Map<String, String>) (options.get(2)));
        Assert.assertEquals("Label 3", optionMap.get("label"));
        Assert.assertEquals("value 3", optionMap.get("value"));
        List<String> predefinedValueParameter = ((List<String>) (parameters.get("predefinedValue")));
        Assert.assertEquals(predefinedValueParameter.toString(), 2, predefinedValueParameter.size());
        Assert.assertTrue(predefinedValueParameter.toString(), predefinedValueParameter.contains("value 2"));
        Assert.assertTrue(predefinedValueParameter.toString(), predefinedValueParameter.contains("value 3"));
    }

    @Test
    public void testGetValue1() {
        List<String> values = _selectDDMFormFieldTemplateContextContributor.getValue("[\"a\",\"b\"]");
        Assert.assertTrue(values.toString(), values.contains("a"));
        Assert.assertTrue(values.toString(), values.contains("b"));
    }

    @Test
    public void testGetValue2() {
        List<String> values = _selectDDMFormFieldTemplateContextContributor.getValue("INVALID_JSON");
        Assert.assertTrue(values.toString(), values.isEmpty());
    }

    @Mock
    private DDMFormFieldOptionsFactory _ddmFormFieldOptionsFactory;

    private final JSONFactory _jsonFactory = new JSONFactoryImpl();

    @Mock
    private ResourceBundle _resourceBundle;

    @Mock
    private ResourceBundleLoader _resourceBundleLoader;

    private final SelectDDMFormFieldTemplateContextContributor _selectDDMFormFieldTemplateContextContributor = new SelectDDMFormFieldTemplateContextContributor();
}

