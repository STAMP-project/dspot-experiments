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
package com.liferay.dynamic.data.mapping.form.builder.internal.servlet;


import com.liferay.dynamic.data.mapping.annotations.DDMForm;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderParameterSettings;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerTracker;
import com.liferay.dynamic.data.mapping.io.internal.DDMFormValuesJSONDeserializer;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Rafael Praxedes
 */
@PrepareForTest(ResourceBundleUtil.class)
@RunWith(PowerMockRunner.class)
public class DDMDataProviderInstanceParameterSettingsServletTest extends PowerMockito {
    @Test
    public void testCreateParametersJSONObject() throws Exception {
        JSONObject parametersJSONObject = _ddmDataProviderInstanceParameterSettingsServlet.createParametersJSONObject(_ddmDataProvider, getDataProviderFormValues("form-values-data-provider-settings-1.json"));
        String expectedValue = read("data-provider-input-output-parameters-1.json");
        JSONAssert.assertEquals(expectedValue, parametersJSONObject.toString(), false);
    }

    @Test
    public void testCreateParametersJSONObjectWithoutLabels() throws Exception {
        JSONObject parametersJSONObject = _ddmDataProviderInstanceParameterSettingsServlet.createParametersJSONObject(_ddmDataProvider, getDataProviderFormValues("form-values-data-provider-settings-2.json"));
        String expectedValue = read("data-provider-input-output-parameters-2.json");
        JSONAssert.assertEquals(expectedValue, parametersJSONObject.toString(), false);
    }

    private DDMDataProvider _ddmDataProvider;

    private DDMDataProviderInstanceParameterSettingsServlet _ddmDataProviderInstanceParameterSettingsServlet;

    @Mock
    private DDMFormValuesDeserializerTracker _ddmFormValuesDeserializerTracker;

    private final DDMFormValuesDeserializer _ddmFormValuesJSONDeserializer = new DDMFormValuesJSONDeserializer();

    private final JSONFactory _jsonFactory = new JSONFactoryImpl();

    @DDMForm
    private interface DDMDataProviderSettings extends DDMDataProviderParameterSettings {}
}

