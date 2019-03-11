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
package com.liferay.dynamic.data.mapping.form.renderer.internal;


import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDMFormFieldTemplateContextFactoryTest {
    @Test
    public void testNotReadOnlyTextFieldAndReadOnlyForm() {
        // Dynamic data mapping form
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField("Field1", false, false, false);
        boolean readOnly = false;
        ddmFormField.setReadOnly(readOnly);
        ddmForm.addDDMFormField(ddmFormField);
        // Dynamic data mapping form field evaluation
        String instanceId = StringUtil.randomString();
        Map<String, Object> changedProperties = new HashMap<>();
        changedProperties.put("readOnly", readOnly);
        changedProperties.put("visible", true);
        Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>> ddmFormFieldsPropertyChanges = new HashMap<>();
        ddmFormFieldsPropertyChanges.put(new DDMFormEvaluatorFieldContextKey("Field1", instanceId), changedProperties);
        // Dynamic data mapping form values
        List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("Field1", "Value 1");
        ddmFormFieldValue.setInstanceId(instanceId);
        ddmFormFieldValues.add(ddmFormFieldValue);
        DDMFormFieldTemplateContextFactory ddmFormFieldTemplateContextFactory = createDDMFormFieldTemplateContextFactory(ddmForm, ddmFormFieldsPropertyChanges, ddmFormFieldValues, true, getTextDDMFormFieldRenderer(), getTextDDMFormFieldTemplateContextContributor());
        List<Object> fields = ddmFormFieldTemplateContextFactory.create();
        Assert.assertEquals(fields.toString(), 1, fields.size());
        Map<String, Object> fieldTemplateContext = ((Map<String, Object>) (fields.get(0)));
        Assert.assertEquals(true, MapUtil.getBoolean(fieldTemplateContext, "readOnly"));
    }

    @Test
    public void testReadOnlyTextFieldAndNotReadOnlyForm() {
        // Dynamic data mapping form
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField("Field1", false, false, true);
        boolean readOnly = true;
        ddmFormField.setReadOnly(readOnly);
        ddmForm.addDDMFormField(ddmFormField);
        // Dynamic data mapping form field evaluation
        String instanceId = StringUtil.randomString();
        Map<String, Object> changedProperties = new HashMap<>();
        changedProperties.put("readOnly", readOnly);
        changedProperties.put("visible", true);
        Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>> ddmFormFieldsPropertyChanges = new HashMap<>();
        ddmFormFieldsPropertyChanges.put(new DDMFormEvaluatorFieldContextKey("Field1", instanceId), changedProperties);
        // Dynamic data mapping form values
        List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("Field1", "Value 1");
        ddmFormFieldValue.setInstanceId(instanceId);
        ddmFormFieldValues.add(ddmFormFieldValue);
        DDMFormFieldTemplateContextFactory ddmFormFieldTemplateContextFactory = createDDMFormFieldTemplateContextFactory(ddmForm, ddmFormFieldsPropertyChanges, ddmFormFieldValues, false, getTextDDMFormFieldRenderer(), getTextDDMFormFieldTemplateContextContributor());
        List<Object> fields = ddmFormFieldTemplateContextFactory.create();
        Assert.assertEquals(fields.toString(), 1, fields.size());
        Map<String, Object> fieldTemplateContext = ((Map<String, Object>) (fields.get(0)));
        Assert.assertEquals(true, MapUtil.getBoolean(fieldTemplateContext, "readOnly"));
    }

    @Test
    public void testTextField() {
        // Dynamic data mapping form
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        boolean required = true;
        DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField("Field1", false, false, required);
        ddmFormField.setLabel(DDMFormValuesTestUtil.createLocalizedValue("Field 1", DDMFormFieldTemplateContextFactoryTest._LOCALE));
        ddmFormField.setReadOnly(false);
        ddmFormField.setTip(DDMFormValuesTestUtil.createLocalizedValue("This is a tip.", DDMFormFieldTemplateContextFactoryTest._LOCALE));
        ddmFormField.setProperty("displayStyle", "singleline");
        ddmForm.addDDMFormField(ddmFormField);
        // Dynamic data mapping form field evaluation
        String instanceId = StringUtil.randomString();
        Map<String, Object> changedProperties = new HashMap<>();
        changedProperties.put("required", true);
        changedProperties.put("valid", true);
        changedProperties.put("visible", true);
        Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>> ddmFormFieldsPropertyChanges = new HashMap<>();
        ddmFormFieldsPropertyChanges.put(new DDMFormEvaluatorFieldContextKey("Field1", instanceId), changedProperties);
        // Dynamic data mapping form values
        List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("Field1", "Value 1");
        ddmFormFieldValue.setInstanceId(instanceId);
        ddmFormFieldValues.add(ddmFormFieldValue);
        DDMFormFieldTemplateContextFactory ddmFormFieldTemplateContextFactory = createDDMFormFieldTemplateContextFactory(ddmForm, ddmFormFieldsPropertyChanges, ddmFormFieldValues, false, getTextDDMFormFieldRenderer(), getTextDDMFormFieldTemplateContextContributor());
        List<Object> fields = ddmFormFieldTemplateContextFactory.create();
        Assert.assertEquals(fields.toString(), 1, fields.size());
        Map<String, Object> fieldTemplateContext = ((Map<String, Object>) (fields.get(0)));
        Assert.assertEquals("singleline", MapUtil.getString(fieldTemplateContext, "displayStyle"));
        Assert.assertEquals("Field 1", MapUtil.getString(fieldTemplateContext, "label"));
        Assert.assertEquals(false, MapUtil.getBoolean(fieldTemplateContext, "readOnly"));
        Assert.assertEquals(false, MapUtil.getBoolean(fieldTemplateContext, "repeatable"));
        Assert.assertEquals(true, MapUtil.getBoolean(fieldTemplateContext, "required"));
        Assert.assertEquals("This is a tip.", MapUtil.getString(fieldTemplateContext, "tip"));
        Assert.assertEquals(true, MapUtil.getBoolean(fieldTemplateContext, "valid"));
        Assert.assertEquals(BLANK, MapUtil.getString(fieldTemplateContext, "validationErrorMessage"));
        Assert.assertEquals("Value 1", MapUtil.getString(fieldTemplateContext, "value"));
        Assert.assertEquals(true, MapUtil.getBoolean(fieldTemplateContext, "visible"));
        String expectedName = String.format(DDMFormFieldTemplateContextFactoryTest._FIELD_NAME_FORMAT, "Field1", instanceId, 0, DDMFormFieldTemplateContextFactoryTest._LOCALE.toString());
        Assert.assertEquals(expectedName, MapUtil.getString(fieldTemplateContext, "name"));
    }

    private static final String _FIELD_NAME_FORMAT = "_PORTLET_NAMESPACE_ddm$$%s$%s$%d$$%s";

    private static final Locale _LOCALE = LocaleUtil.US;

    private static final String _PORTLET_NAMESPACE = "_PORTLET_NAMESPACE_";

    private HttpServletRequest _request;
}

