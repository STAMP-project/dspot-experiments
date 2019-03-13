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


import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor;
import java.io.IOException;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author Jeyvison Nascimento
 */
@RunWith(MockitoJUnitRunner.class)
public class DDMFormContextToDDMFormTest {
    @Test
    public void testGetValueFromValueAccessor() throws IOException {
        Mockito.when(_ddmFormFieldTypeServicesTracker.getDDMFormFieldValueAccessor(Matchers.anyString())).thenReturn(_ddmFormFieldValueAccessor);
        Mockito.when(_ddmFormFieldValueAccessor.getValue(Matchers.any(), Matchers.any())).thenReturn(false);
        _ddmFormContextToDDMForm.ddmFormFieldTypeServicesTracker = _ddmFormFieldTypeServicesTracker;
        Object result = _ddmFormContextToDDMForm.getValueFromValueAccessor("checkbox", "false", Locale.US);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testGetValueWithoutValueAccessor() throws IOException {
        Mockito.when(_ddmFormFieldTypeServicesTracker.getDDMFormFieldValueAccessor(Matchers.anyString())).thenReturn(null);
        _ddmFormContextToDDMForm.ddmFormFieldTypeServicesTracker = _ddmFormFieldTypeServicesTracker;
        Object result = _ddmFormContextToDDMForm.getValueFromValueAccessor("checkbox", "false", Locale.US);
        Assert.assertEquals("false", result);
    }

    private DDMFormContextToDDMForm _ddmFormContextToDDMForm;

    @Mock
    private DDMFormFieldTypeServicesTracker _ddmFormFieldTypeServicesTracker;

    @Mock
    private DDMFormFieldValueAccessor _ddmFormFieldValueAccessor;
}

