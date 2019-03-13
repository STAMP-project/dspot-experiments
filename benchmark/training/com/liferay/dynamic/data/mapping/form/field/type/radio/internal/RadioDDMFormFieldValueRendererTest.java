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
package com.liferay.dynamic.data.mapping.form.field.type.radio.internal;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
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
 * @author Renato Rego
 */
public class RadioDDMFormFieldValueRendererTest {
    @Test
    public void testRender() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField("Radio", "Radio", "radio", "string", false, false, false);
        DDMFormFieldOptions ddmFormFieldOptions = ddmFormField.getDDMFormFieldOptions();
        ddmFormFieldOptions.addOptionLabel("value 1", US, "option 1");
        ddmFormFieldOptions.addOptionLabel("value 2", US, "option 2");
        ddmForm.addDDMFormField(ddmFormField);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Radio", new UnlocalizedValue("value 1"));
        ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
        RadioDDMFormFieldValueRenderer radioDDMFormFieldValueRenderer = createRadioDDMFormFieldValueRenderer();
        Assert.assertEquals("option 1", radioDDMFormFieldValueRenderer.render(ddmFormFieldValue, US));
    }
}

