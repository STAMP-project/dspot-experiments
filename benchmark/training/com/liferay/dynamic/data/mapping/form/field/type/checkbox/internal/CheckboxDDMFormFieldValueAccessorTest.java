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
package com.liferay.dynamic.data.mapping.form.field.type.checkbox.internal;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Renato Rego
 */
public class CheckboxDDMFormFieldValueAccessorTest {
    @Test
    public void testGetWithLocalizedValue() {
        Value value = new com.liferay.dynamic.data.mapping.model.LocalizedValue(LocaleUtil.US);
        value.addString(BRAZIL, "true");
        value.addString(US, "false");
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Checkbox", value);
        CheckboxDDMFormFieldValueAccessor checkboxDDMFormFieldValueAccessor = new CheckboxDDMFormFieldValueAccessor();
        Assert.assertEquals(Boolean.TRUE, checkboxDDMFormFieldValueAccessor.getValue(ddmFormFieldValue, BRAZIL));
        Assert.assertEquals(Boolean.FALSE, checkboxDDMFormFieldValueAccessor.getValue(ddmFormFieldValue, US));
    }

    @Test
    public void testGetWithUnlocalizedValue() {
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Checkbox", new UnlocalizedValue("true"));
        CheckboxDDMFormFieldValueAccessor checkboxDDMFormFieldValueAccessor = new CheckboxDDMFormFieldValueAccessor();
        Assert.assertEquals(Boolean.TRUE, checkboxDDMFormFieldValueAccessor.getValue(ddmFormFieldValue, BRAZIL));
        Assert.assertEquals(Boolean.TRUE, checkboxDDMFormFieldValueAccessor.getValue(ddmFormFieldValue, US));
    }
}

