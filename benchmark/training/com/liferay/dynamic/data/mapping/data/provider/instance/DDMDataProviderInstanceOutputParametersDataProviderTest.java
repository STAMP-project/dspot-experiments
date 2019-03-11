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
package com.liferay.dynamic.data.mapping.data.provider.instance;


import DDMDataProviderRequest.Builder;
import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderTracker;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerTracker;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Leonardo Barros
 */
@PrepareForTest(ResourceBundleUtil.class)
@RunWith(PowerMockRunner.class)
public class DDMDataProviderInstanceOutputParametersDataProviderTest extends PowerMockito {
    @Test
    public void testGetData() throws Exception {
        DDMDataProviderRequest.Builder builder = Builder.newBuilder();
        DDMDataProviderRequest ddmDataProviderRequest = builder.withParameter("dataProviderInstanceId", "1").build();
        when(_ddmDataProviderInstanceService.getDataProviderInstance(1)).thenReturn(_ddmDataProviderInstance);
        when(_ddmDataProviderInstance.getType()).thenReturn("rest");
        when(_ddmDataProviderTracker.getDDMDataProvider("rest")).thenReturn(_ddmDataProvider);
        when(_ddmDataProvider.getSettings()).thenReturn(((Class) (TestDDMDataProviderParameterSettings.class)));
        DDMForm ddmForm = DDMFormFactory.create(_ddmDataProvider.getSettings());
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("url", "http://someservice.com/countries/api/"));
        DDMFormFieldValue outputParamaters = DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("outputParameters", BLANK);
        ddmFormValues.addDDMFormFieldValue(outputParamaters);
        outputParamaters.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("outputParameterName", "Country Id"));
        outputParamaters.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("outputParameterPath", "countryId"));
        outputParamaters.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("outputParameterType", "[\"number\"]"));
        outputParamaters = DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("outputParameters", BLANK);
        ddmFormValues.addDDMFormFieldValue(outputParamaters);
        outputParamaters.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("outputParameterName", "Country Name"));
        outputParamaters.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("outputParameterPath", "countryName"));
        outputParamaters.addNestedDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("outputParameterType", "[\"string\"]"));
        DDMFormValuesDeserializerDeserializeResponse ddmFormValuesDeserializerDeserializeResponse = DDMFormValuesDeserializerDeserializeResponse.Builder.newBuilder(ddmFormValues).build();
        when(_ddmFormValuesDeserializer.deserialize(Matchers.any(DDMFormValuesDeserializerDeserializeRequest.class))).thenReturn(ddmFormValuesDeserializerDeserializeResponse);
        DDMDataProviderResponse ddmDataProviderResponse = _ddmDataProviderInstanceOutputParametersDataProvider.getData(ddmDataProviderRequest);
        Optional<List<KeyValuePair>> outputParameterNamesOptional = ddmDataProviderResponse.getOutputOptional("outputParameterNames", List.class);
        Assert.assertTrue(outputParameterNamesOptional.isPresent());
        List<KeyValuePair> keyValuePairs = new ArrayList() {
            {
                add(new KeyValuePair("Country Id", "Country Id"));
                add(new KeyValuePair("Country Name", "Country Name"));
            }
        };
        Assert.assertEquals(keyValuePairs.toString(), keyValuePairs, outputParameterNamesOptional.get());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetSettings() {
        _ddmDataProviderInstanceOutputParametersDataProvider.getSettings();
    }

    @Test
    public void testThrowException() throws Exception {
        DDMDataProviderRequest.Builder builder = Builder.newBuilder();
        DDMDataProviderRequest ddmDataProviderRequest = builder.withParameter("dataProviderInstanceId", "1").build();
        when(_ddmDataProviderInstanceService.getDataProviderInstance(1)).thenThrow(Exception.class);
        DDMDataProviderResponse ddmDataProviderResponse = _ddmDataProviderInstanceOutputParametersDataProvider.getData(ddmDataProviderRequest);
        Optional<List<KeyValuePair>> optional = ddmDataProviderResponse.getOutputOptional("outputParameterNames", List.class);
        Assert.assertTrue(optional.isPresent());
        List<KeyValuePair> keyValuePairs = optional.get();
        Assert.assertEquals(keyValuePairs.toString(), 0, keyValuePairs.size());
    }

    @Test
    public void testWithInvalidSettingsClass() throws Exception {
        DDMDataProviderRequest.Builder builder = Builder.newBuilder();
        DDMDataProviderRequest ddmDataProviderRequest = builder.withParameter("dataProviderInstanceId", "1").build();
        when(_ddmDataProviderInstanceService.getDataProviderInstance(1)).thenReturn(_ddmDataProviderInstance);
        when(_ddmDataProviderInstance.getType()).thenReturn("rest");
        when(_ddmDataProviderTracker.getDDMDataProvider("rest")).thenReturn(_ddmDataProvider);
        when(_ddmDataProvider.getSettings()).thenReturn(((Class) (Object.class)));
        DDMDataProviderResponse ddmDataProviderResponse = _ddmDataProviderInstanceOutputParametersDataProvider.getData(ddmDataProviderRequest);
        Optional<List<KeyValuePair>> outputParameterNamesOptional = ddmDataProviderResponse.getOutputOptional("outputParameterNames", List.class);
        Assert.assertTrue(outputParameterNamesOptional.isPresent());
        List<KeyValuePair> outputParameterNames = outputParameterNamesOptional.get();
        Assert.assertEquals(outputParameterNames.toString(), 0, outputParameterNames.size());
    }

    @Test
    public void testWithoutDataProviderInstanceIdParameter() throws Exception {
        DDMDataProviderRequest.Builder builder = Builder.newBuilder();
        DDMDataProviderRequest ddmDataProviderRequest = builder.build();
        DDMDataProviderResponse ddmDataProviderResponse = _ddmDataProviderInstanceOutputParametersDataProvider.getData(ddmDataProviderRequest);
        Assert.assertFalse(ddmDataProviderResponse.hasOutput("outputParameterNames"));
    }

    @Mock
    private DDMDataProvider _ddmDataProvider;

    @Mock
    private DDMDataProviderInstance _ddmDataProviderInstance;

    private DDMDataProviderInstanceOutputParametersDataProvider _ddmDataProviderInstanceOutputParametersDataProvider;

    @Mock
    private DDMDataProviderInstanceService _ddmDataProviderInstanceService;

    @Mock
    private DDMDataProviderTracker _ddmDataProviderTracker;

    @Mock
    private DDMFormValuesDeserializer _ddmFormValuesDeserializer;

    @Mock
    private DDMFormValuesDeserializerTracker _ddmFormValuesDeserializerTracker;
}

