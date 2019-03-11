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
package com.liferay.dynamic.data.mapping.io.internal;


import LocaleUtil.BRAZIL;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.petra.string.StringBundler;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pablo Carvalho
 */
public abstract class BaseDDMFormDeserializerTestCase extends BaseDDMTestCase {
    @Test
    public void testDDMFormAndFieldsDefaultLocales() throws Exception {
        StringBundler sb = new StringBundler(4);
        sb.append("ddm-form-");
        sb.append(getDeserializerType());
        sb.append("-deserializer-different-default-locale");
        sb.append(getTestFileExtension());
        String serializedDDMForm = read(sb.toString());
        DDMForm ddmForm = deserialize(serializedDDMForm);
        Assert.assertEquals(BRAZIL, ddmForm.getDefaultLocale());
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(true);
        DDMFormField selectDDMFormField = ddmFormFieldsMap.get("Select5979");
        LocalizedValue selectLabel = selectDDMFormField.getLabel();
        Assert.assertEquals(BRAZIL, selectLabel.getDefaultLocale());
        DDMFormFieldOptions ddmFormFieldOptions = selectDDMFormField.getDDMFormFieldOptions();
        for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
            LocalizedValue optionLabel = ddmFormFieldOptions.getOptionLabels(optionValue);
            Assert.assertEquals(BRAZIL, optionLabel.getDefaultLocale());
        }
    }

    @Test
    public void testDDMFormDeserialization() throws Exception {
        StringBundler sb = new StringBundler(4);
        sb.append("ddm-form-");
        sb.append(getDeserializerType());
        sb.append("-deserializer-test-data");
        sb.append(getTestFileExtension());
        String serializedDDMForm = read(sb.toString());
        DDMForm ddmForm = deserialize(serializedDDMForm);
        testAvailableLocales(ddmForm);
        testDDMFormRules(ddmForm.getDDMFormRules());
        testDefaultLocale(ddmForm);
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(true);
        testBooleanDDMFormField(ddmFormFieldsMap.get("Boolean2282"));
        testDateDDMFormField(ddmFormFieldsMap.get("Date2510"));
        testDecimalDDMFormField(ddmFormFieldsMap.get("Decimal3479"));
        testDocumentLibraryDDMFormField(ddmFormFieldsMap.get("Documents_and_Media4036"));
        testHTMLDDMFormField(ddmFormFieldsMap.get("HTML4512"));
        testNestedDDMFormFields(ddmFormFieldsMap.get("Text6980"));
        testRadioDDMFormField(ddmFormFieldsMap.get("Radio5699"));
        testDDMFormSuccessPageSettings(ddmForm.getDDMFormSuccessPageSettings());
    }
}

