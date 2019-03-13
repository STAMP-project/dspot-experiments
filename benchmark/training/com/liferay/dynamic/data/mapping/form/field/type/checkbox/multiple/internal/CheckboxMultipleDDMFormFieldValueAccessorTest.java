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
package com.liferay.dynamic.data.mapping.form.field.type.checkbox.multiple.internal;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rafael Praxedes
 */
public class CheckboxMultipleDDMFormFieldValueAccessorTest {
    @Test
    public void testGetCheckboxMultipleValue1() {
        JSONArray expectedJSONArray = createJSONArray("value 1");
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("CheckboxMultiple", new com.liferay.dynamic.data.mapping.model.UnlocalizedValue(expectedJSONArray.toString()));
        JSONArray actualJSONArray = _checkboxMultipleDDMFormFieldValueAccessor.getValue(ddmFormFieldValue, US);
        Assert.assertEquals(expectedJSONArray.toString(), actualJSONArray.toString());
    }

    @Test
    public void testGetCheckboxMultipleValue2() {
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("CheckboxMultiple", new com.liferay.dynamic.data.mapping.model.UnlocalizedValue(StringPool.BLANK));
        JSONArray actualJSONArray = _checkboxMultipleDDMFormFieldValueAccessor.getValue(ddmFormFieldValue, US);
        Assert.assertTrue(((actualJSONArray.length()) == 0));
    }

    private CheckboxMultipleDDMFormFieldValueAccessor _checkboxMultipleDDMFormFieldValueAccessor;

    private final JSONFactory _jsonFactory = new JSONFactoryImpl();
}

