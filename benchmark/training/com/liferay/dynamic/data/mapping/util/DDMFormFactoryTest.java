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
package com.liferay.dynamic.data.mapping.util;


import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeSettings;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldTypeSettings;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Leonardo Barros
 * @author Marcellus Tavares
 */
@PrepareForTest(ResourceBundleUtil.class)
@RunWith(PowerMockRunner.class)
public class DDMFormFactoryTest {
    @Test
    public void testCreateDDMFormFieldTypeSettingsDDMForm() {
        DDMForm ddmForm = DDMFormFactory.create(DDMFormFieldTypeSettings.class);
        Assert.assertNotNull(ddmForm);
        assertRequiredDDMFormFieldTypeSettings(ddmForm.getDDMFormFieldsMap(false));
    }

    @Test
    public void testCreateDefaultDDMFormFieldTypeSettingsDDMForm() {
        DDMForm ddmForm = DDMFormFactory.create(DefaultDDMFormFieldTypeSettings.class);
        Assert.assertNotNull(ddmForm);
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(false);
        assertRequiredDDMFormFieldTypeSettings(ddmFormFieldsMap);
        DDMFormField indexTypeDDMFormField = ddmFormFieldsMap.get("indexType");
        Assert.assertNotNull(indexTypeDDMFormField);
        Assert.assertEquals("string", indexTypeDDMFormField.getDataType());
        Assert.assertEquals("radio", indexTypeDDMFormField.getType());
        DDMFormField labelDDMFormField = ddmFormFieldsMap.get("label");
        Assert.assertNotNull(labelDDMFormField);
        Assert.assertEquals("string", labelDDMFormField.getDataType());
        Assert.assertEquals("key_value", labelDDMFormField.getType());
        Assert.assertEquals(true, labelDDMFormField.isLocalizable());
        DDMFormField localizableDDMFormField = ddmFormFieldsMap.get("localizable");
        Assert.assertNotNull(localizableDDMFormField);
        Assert.assertEquals("boolean", localizableDDMFormField.getDataType());
        Assert.assertEquals("checkbox", localizableDDMFormField.getType());
        DDMFormField predefinedValueDDMFormField = ddmFormFieldsMap.get("predefinedValue");
        Assert.assertNotNull(predefinedValueDDMFormField);
        Assert.assertEquals("string", predefinedValueDDMFormField.getDataType());
        Assert.assertEquals("text", predefinedValueDDMFormField.getType());
        Assert.assertEquals(true, predefinedValueDDMFormField.isLocalizable());
        DDMFormField readOnlyDDMFormField = ddmFormFieldsMap.get("readOnly");
        Assert.assertNotNull(readOnlyDDMFormField);
        Assert.assertEquals("boolean", readOnlyDDMFormField.getDataType());
        Assert.assertEquals("checkbox", readOnlyDDMFormField.getType());
        DDMFormField repeatableDDMFormField = ddmFormFieldsMap.get("repeatable");
        Assert.assertNotNull(repeatableDDMFormField);
        Assert.assertEquals("boolean", repeatableDDMFormField.getDataType());
        Assert.assertEquals("checkbox", repeatableDDMFormField.getType());
        DDMFormField requiredDDMFormField = ddmFormFieldsMap.get("readOnly");
        Assert.assertNotNull(requiredDDMFormField);
        Assert.assertEquals("boolean", requiredDDMFormField.getDataType());
        Assert.assertEquals("checkbox", requiredDDMFormField.getType());
        DDMFormField showLabelDDMFormField = ddmFormFieldsMap.get("showLabel");
        Assert.assertNotNull(showLabelDDMFormField);
        Assert.assertEquals("boolean", showLabelDDMFormField.getDataType());
        Assert.assertEquals("checkbox", showLabelDDMFormField.getType());
        DDMFormField tipDDMFormField = ddmFormFieldsMap.get("tip");
        Assert.assertNotNull(tipDDMFormField);
        Assert.assertEquals("string", tipDDMFormField.getDataType());
        Assert.assertEquals("text", tipDDMFormField.getType());
        Assert.assertEquals(true, tipDDMFormField.isLocalizable());
    }

    @Test
    public void testCreateDynamicFormWithoutRules() {
        DDMForm ddmForm = DDMFormFactory.create(DDMFormFactoryTest.DynamicFormWithoutRules.class);
        List<DDMFormRule> ddmFormRules = ddmForm.getDDMFormRules();
        Assert.assertEquals(ddmFormRules.toString(), 0, ddmFormRules.size());
    }

    @Test
    public void testCreateDynamicFormWithRules() {
        DDMForm ddmForm = DDMFormFactory.create(DDMFormFactoryTest.DynamicFormWithRules.class);
        List<DDMFormRule> ddmFormRules = ddmForm.getDDMFormRules();
        Assert.assertEquals(ddmFormRules.toString(), 2, ddmFormRules.size());
        DDMFormRule ddmFormRule = ddmFormRules.get(0);
        Assert.assertEquals("condition1", ddmFormRule.getCondition());
        List<String> ddmFormRuleActions = ddmFormRule.getActions();
        Assert.assertEquals(ddmFormRuleActions.toString(), 2, ddmFormRuleActions.size());
        Assert.assertArrayEquals(new String[]{ "action1", "action2" }, ddmFormRuleActions.toArray());
        ddmFormRule = ddmFormRules.get(1);
        Assert.assertEquals("TRUE", ddmFormRule.getCondition());
        ddmFormRuleActions = ddmFormRule.getActions();
        Assert.assertEquals(ddmFormRuleActions.toString(), 1, ddmFormRuleActions.size());
        Assert.assertArrayEquals(new String[]{ "action1" }, ddmFormRuleActions.toArray());
    }

    @Test
    public void testeCreateDynamicFormWithFieldSet() {
        DDMForm ddmForm = DDMFormFactory.create(DDMFormFactoryTest.DynamicFormWithFieldSet.class);
        List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();
        Assert.assertEquals(ddmFormFields.toString(), 1, ddmFormFields.size());
        DDMFormField ddmFormField = ddmFormFields.get(0);
        Assert.assertEquals("parameters", ddmFormField.getName());
        Assert.assertEquals("fieldset", ddmFormField.getType());
        Assert.assertEquals(BLANK, ddmFormField.getDataType());
        Assert.assertTrue(ddmFormField.isRepeatable());
        List<DDMFormField> nestedDDMFormFields = ddmFormField.getNestedDDMFormFields();
        Assert.assertEquals(nestedDDMFormFields.toString(), 2, nestedDDMFormFields.size());
        DDMFormField nestedDDMFormField1 = nestedDDMFormFields.get(0);
        Assert.assertEquals("parameterName", nestedDDMFormField1.getName());
        Assert.assertEquals("text", nestedDDMFormField1.getType());
        DDMFormField nestedDDMFormField2 = nestedDDMFormFields.get(1);
        Assert.assertEquals("parameterValue", nestedDDMFormField2.getName());
        Assert.assertEquals("text", nestedDDMFormField2.getType());
    }

    @Mock
    private Language _language;

    @com.liferay.dynamic.data.mapping.annotations.DDMForm
    private interface DynamicFormWithFieldSet {
        @com.liferay.dynamic.data.mapping.annotations.DDMFormField
        public DDMFormFactoryTest.ParametersFieldSetSettings[] parameters();
    }

    @com.liferay.dynamic.data.mapping.annotations.DDMForm
    private interface DynamicFormWithoutRules {}

    @DDMForm(rules = { @DDMFormRule(actions = { "action1", "action2" }, condition = "condition1"), @DDMFormRule(actions = "action1") })
    private interface DynamicFormWithRules {}

    @com.liferay.dynamic.data.mapping.annotations.DDMForm
    private interface ParametersFieldSetSettings {
        @com.liferay.dynamic.data.mapping.annotations.DDMFormField
        public String parameterName();

        @com.liferay.dynamic.data.mapping.annotations.DDMFormField
        public String parameterValue();
    }
}

