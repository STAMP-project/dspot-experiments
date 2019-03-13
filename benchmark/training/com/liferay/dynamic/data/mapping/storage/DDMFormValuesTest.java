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
package com.liferay.dynamic.data.mapping.storage;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDMFormValuesTest extends BaseDDMTestCase {
    @Test
    public void testDDMFormFieldValuesMap() {
        DDMFormValues ddmFormValues = createDDMFormValues(null);
        String fieldName = StringUtil.randomString();
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue(fieldName, null));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue(fieldName, null));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue(fieldName, null));
        Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap = ddmFormValues.getDDMFormFieldValuesMap();
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(fieldName);
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
    }

    @Test
    public void testEqualsWithDifferentAvailableLocales() {
        DDMFormValues ddmFormValues1 = createDDMFormValues(null, createAvailableLocales(US), US);
        DDMFormValues ddmFormValues2 = createDDMFormValues(null, createAvailableLocales(BRAZIL, US), US);
        Assert.assertFalse(ddmFormValues1.equals(ddmFormValues2));
    }

    @Test
    public void testEqualsWithDifferentDDMFormFieldValues() {
        DDMFormValues ddmFormValues1 = createDDMFormValues(null, createAvailableLocales(US), US);
        ddmFormValues1.addDDMFormFieldValue(createDDMFormFieldValue(StringUtil.randomString(), StringUtil.randomString(), new com.liferay.dynamic.data.mapping.model.UnlocalizedValue(StringUtil.randomString())));
        DDMFormValues ddmFormValues2 = createDDMFormValues(null, createAvailableLocales(US), US);
        ddmFormValues2.addDDMFormFieldValue(createDDMFormFieldValue(StringUtil.randomString(), StringUtil.randomString(), new com.liferay.dynamic.data.mapping.model.UnlocalizedValue(StringUtil.randomString())));
        Assert.assertFalse(ddmFormValues1.equals(ddmFormValues2));
    }

    @Test
    public void testEqualsWithDifferentDefaultLocale() {
        DDMFormValues ddmFormValues1 = createDDMFormValues(null, createAvailableLocales(US), US);
        DDMFormValues ddmFormValues2 = createDDMFormValues(null, createAvailableLocales(US), BRAZIL);
        Assert.assertFalse(ddmFormValues1.equals(ddmFormValues2));
    }

    @Test
    public void testEqualsWithDifferentOrderOfDDMFormFieldValues() {
        DDMFormValues ddmFormValues1 = createDDMFormValues(null, createAvailableLocales(US), US);
        DDMFormFieldValue nestedDDMFormFieldValue1 = createDDMFormFieldValue(StringUtil.randomString(), StringUtil.randomString(), new com.liferay.dynamic.data.mapping.model.UnlocalizedValue(StringUtil.randomString()));
        DDMFormFieldValue nestedDDMFormFieldValue2 = createDDMFormFieldValue(StringUtil.randomString(), StringUtil.randomString(), new com.liferay.dynamic.data.mapping.model.UnlocalizedValue(StringUtil.randomString()));
        ddmFormValues1.addDDMFormFieldValue(nestedDDMFormFieldValue1);
        ddmFormValues1.addDDMFormFieldValue(nestedDDMFormFieldValue2);
        DDMFormValues ddmFormValues2 = createDDMFormValues(null, createAvailableLocales(US), US);
        ddmFormValues2.addDDMFormFieldValue(nestedDDMFormFieldValue2);
        ddmFormValues2.addDDMFormFieldValue(nestedDDMFormFieldValue1);
        Assert.assertFalse(ddmFormValues1.equals(ddmFormValues2));
    }

    @Test
    public void testEqualsWithSameAttributes() {
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(StringUtil.randomString(), StringUtil.randomString(), new com.liferay.dynamic.data.mapping.model.UnlocalizedValue(StringUtil.randomString()));
        DDMFormValues ddmFormValues1 = createDDMFormValues(null, createAvailableLocales(US), US);
        ddmFormValues1.addDDMFormFieldValue(ddmFormFieldValue);
        DDMFormValues ddmFormValues2 = createDDMFormValues(null, createAvailableLocales(US), US);
        ddmFormValues2.addDDMFormFieldValue(ddmFormFieldValue);
        Assert.assertTrue(ddmFormValues1.equals(ddmFormValues2));
    }
}

