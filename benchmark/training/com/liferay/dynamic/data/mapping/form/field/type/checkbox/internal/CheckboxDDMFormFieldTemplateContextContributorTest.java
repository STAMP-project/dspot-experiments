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


import LocaleUtil.US;
import StringPool.FALSE;
import StringPool.TRUE;
import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Marcellus Tavares
 */
@RunWith(PowerMockRunner.class)
public class CheckboxDDMFormFieldTemplateContextContributorTest extends BaseDDMFormFieldTypeSettingsTestCase {
    @Test
    public void testGetNotDefinedPredefinedValue() {
        DDMFormField ddmFormField = new DDMFormField("field", "checkbox");
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setLocale(US);
        Map<String, Object> parameters = _checkboxDDMFormFieldTemplateContextContributor.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        boolean predefinedValue = ((boolean) (parameters.get("predefinedValue")));
        Assert.assertEquals(false, predefinedValue);
    }

    @Test
    public void testGetPredefinedValueFalse() {
        DDMFormField ddmFormField = new DDMFormField("field", "checkbox");
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setLocale(US);
        LocalizedValue predefinedValue = new LocalizedValue(LocaleUtil.US);
        predefinedValue.addString(US, FALSE);
        ddmFormField.setProperty("predefinedValue", predefinedValue);
        Map<String, Object> parameters = _checkboxDDMFormFieldTemplateContextContributor.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        boolean actualPredefinedValue = ((boolean) (parameters.get("predefinedValue")));
        Assert.assertEquals(false, actualPredefinedValue);
    }

    @Test
    public void testGetPredefinedValueTrue() {
        DDMFormField ddmFormField = new DDMFormField("field", "checkbox");
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setLocale(US);
        LocalizedValue predefinedValue = new LocalizedValue(LocaleUtil.US);
        predefinedValue.addString(US, TRUE);
        ddmFormField.setProperty("predefinedValue", predefinedValue);
        Map<String, Object> parameters = _checkboxDDMFormFieldTemplateContextContributor.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        boolean actualPredefinedValue = ((boolean) (parameters.get("predefinedValue")));
        Assert.assertEquals(true, actualPredefinedValue);
    }

    private final CheckboxDDMFormFieldTemplateContextContributor _checkboxDDMFormFieldTemplateContextContributor = new CheckboxDDMFormFieldTemplateContextContributor();
}

