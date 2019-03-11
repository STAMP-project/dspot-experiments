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
package com.liferay.dynamic.data.mapping.form.field.type.date.internal;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Bruno Basto
 */
public class DateDDMFormFieldValueRendererTest {
    @Test
    public void testRender() {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField("birthday", "Birthday", "date", "string", false, false, false);
        ddmForm.addDDMFormField(ddmFormField);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("birthday", new UnlocalizedValue("2015-01-25"));
        ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
        DateDDMFormFieldValueRenderer dateDDMFormFieldValueRenderer = new DateDDMFormFieldValueRenderer();
        Assert.assertEquals("1/25/15", dateDDMFormFieldValueRenderer.render(ddmFormFieldValue, US));
        Assert.assertEquals("25/01/15", dateDDMFormFieldValueRenderer.render(ddmFormFieldValue, BRAZIL));
        ddmFormFieldValue.setValue(new UnlocalizedValue(""));
        Assert.assertEquals(BLANK, dateDDMFormFieldValueRenderer.render(ddmFormFieldValue, US));
    }
}

