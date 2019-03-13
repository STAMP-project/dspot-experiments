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
package com.liferay.dynamic.data.mapping.form.builder.internal.context;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializerRequest;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;


/**
 *
 *
 * @author Rodrigo Paulino
 */
@RunWith(MockitoJUnitRunner.class)
public class DDMFormContextToDDMFormValuesTest extends PowerMockito {
    @Test
    public void testDeserializeWithLanguageDifferentThanSiteDefault() throws Exception {
        String serializedDDMFormValues = read("ddm-form-values.json");
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("TextField1", true, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("TextField2", true, false, false));
        DDMFormContextDeserializerRequest ddmFormContextDeserializerRequest = DDMFormContextDeserializerRequest.with(ddmForm, serializedDDMFormValues);
        ddmFormContextDeserializerRequest.addProperty("currentLocale", BRAZIL);
        DDMFormValues ddmFormValues = _ddmFormContextToDDMFormValues.deserialize(ddmFormContextDeserializerRequest);
        Assert.assertNotEquals(LocaleUtil.getSiteDefault(), BRAZIL);
        Assert.assertEquals(ddmFormValues.getDefaultLocale(), BRAZIL);
        Set<Locale> availableLocales = ddmFormValues.getAvailableLocales();
        Assert.assertEquals(availableLocales.toString(), 1, availableLocales.size());
        Assert.assertTrue(availableLocales.contains(BRAZIL));
    }

    @Test
    public void testGetDDMFormFieldValues() throws Exception {
        String json = read("ddm-form-values-pages-only.json");
        JSONArray jsonArray = new JSONArrayImpl(json);
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("TextField1", true, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("TextField2", true, false, false));
        List<DDMFormFieldValue> ddmFormFieldValues1 = new ArrayList<>(2);
        LocalizedValue value1 = new LocalizedValue();
        value1.addString(BRAZIL, "Texto 1");
        DDMFormFieldValue ddmFormFieldValue1 = DDMFormValuesTestUtil.createDDMFormFieldValue("eBvF8zup", "TextField1", value1);
        LocalizedValue value2 = new LocalizedValue();
        value2.addString(BRAZIL, "Texto 2");
        DDMFormFieldValue ddmFormFieldValue2 = DDMFormValuesTestUtil.createDDMFormFieldValue("6VYYLvfJ", "TextField2", value2);
        ddmFormFieldValues1.add(ddmFormFieldValue1);
        ddmFormFieldValues1.add(ddmFormFieldValue2);
        List<DDMFormFieldValue> ddmFormFieldValues2 = _ddmFormContextToDDMFormValues.getDDMFormFieldValues(jsonArray, ddmForm);
        Assert.assertTrue(Objects.equals(ddmFormFieldValues1, ddmFormFieldValues2));
    }

    @Test
    public void testGetLocalizedValue() {
        JSONObject jsonObject = new JSONObjectImpl();
        String enValue = StringUtil.randomString();
        String ptValue = StringUtil.randomString();
        jsonObject.put("en_US", enValue);
        jsonObject.put("pt_BR", ptValue);
        Value value1 = DDMFormValuesTestUtil.createLocalizedValue(enValue, ptValue, US);
        Value value2 = _ddmFormContextToDDMFormValues.getLocalizedValue(jsonObject);
        Assert.assertTrue(Objects.equals(value1, value2));
    }

    private DDMFormContextToDDMFormValues _ddmFormContextToDDMFormValues;

    @Mock
    private Language _language;
}

