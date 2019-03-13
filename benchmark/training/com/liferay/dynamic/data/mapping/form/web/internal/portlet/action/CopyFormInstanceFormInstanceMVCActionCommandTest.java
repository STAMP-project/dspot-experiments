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
package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import java.util.List;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Marcellus Tavares
 */
@PrepareForTest(ResourceBundleUtil.class)
@RunWith(PowerMockRunner.class)
public class CopyFormInstanceFormInstanceMVCActionCommandTest extends PowerMockito {
    @Test
    public void testCreateFormInstanceSettingsDDMFormValues() throws Exception {
        DDMForm formInstanceSettingsDDMForm = DDMFormFactory.create(DDMFormInstanceSettings.class);
        DDMFormInstance formInstance = mock(DDMFormInstance.class);
        DDMFormValues ddmFormValues = createDDMFormValues(formInstanceSettingsDDMForm);
        when(formInstance.getSettingsDDMFormValues()).thenReturn(ddmFormValues);
        DDMFormValues formInstanceSettingsDDMFormValuesCopy = _copyFormInstanceMVCActionCommand.createFormInstanceSettingsDDMFormValues(formInstance);
        Assert.assertEquals(formInstanceSettingsDDMForm, formInstanceSettingsDDMFormValuesCopy.getDDMForm());
        List<DDMFormFieldValue> formInstanceSettingsDDMFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        List<DDMFormFieldValue> formInstanceSettingsDDMFormFieldValuesCopy = formInstanceSettingsDDMFormValuesCopy.getDDMFormFieldValues();
        Assert.assertEquals(formInstanceSettingsDDMFormFieldValuesCopy.toString(), getDDMFormFieldsSize(formInstanceSettingsDDMForm), formInstanceSettingsDDMFormFieldValuesCopy.size());
        for (int i = 0; i < (formInstanceSettingsDDMFormFieldValuesCopy.size()); i++) {
            DDMFormFieldValue ddmFormFieldValue = formInstanceSettingsDDMFormFieldValues.get(i);
            DDMFormFieldValue ddmFormFieldValueCopy = formInstanceSettingsDDMFormFieldValuesCopy.get(i);
            Value valueCopy = ddmFormFieldValueCopy.getValue();
            DDMFormField ddmFormField = ddmFormFieldValueCopy.getDDMFormField();
            if (Objects.equals(ddmFormField.getName(), "published")) {
                Assert.assertEquals("false", valueCopy.getString(US));
            } else {
                Value value = ddmFormFieldValue.getValue();
                Assert.assertEquals(value.getString(US), valueCopy.getString(US));
            }
        }
    }

    private final CopyFormInstanceMVCActionCommand _copyFormInstanceMVCActionCommand = new CopyFormInstanceMVCActionCommand();
}

