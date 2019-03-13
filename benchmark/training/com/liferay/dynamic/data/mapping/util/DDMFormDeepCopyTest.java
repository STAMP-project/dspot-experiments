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


import LocaleUtil.BRAZIL;
import LocaleUtil.ENGLISH;
import LocaleUtil.FRANCE;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Leonardo Barros
 */
public class DDMFormDeepCopyTest extends BaseDDMTestCase {
    @Test
    public void testNestedFields() throws Exception {
        DDMForm ddmForm = createDDMForm();
        DDMFormField nameDDMFormField = new DDMFormField("Name", "textarea");
        ddmForm.addDDMFormField(nameDDMFormField);
        DDMFormField phoneDDMFormField = new DDMFormField("Phone", "text");
        nameDDMFormField.addNestedDDMFormField(phoneDDMFormField);
        DDMFormField contactDDMFormField = new DDMFormField("Name", "text");
        nameDDMFormField.addNestedDDMFormField(contactDDMFormField);
        DDMFormField addressDDMFormField = new DDMFormField("ZipCode", "text");
        contactDDMFormField.addNestedDDMFormField(addressDDMFormField);
        DDMForm copyDDMForm = BeanPropertiesUtil.deepCopyProperties(ddmForm);
        List<DDMFormField> copyDDMFormFields = copyDDMForm.getDDMFormFields();
        Assert.assertEquals(copyDDMFormFields.toString(), 1, copyDDMFormFields.size());
        DDMFormField copyDDMFormField = copyDDMFormFields.get(0);
        Assert.assertEquals("Name", copyDDMFormField.getName());
        Assert.assertEquals("textarea", copyDDMFormField.getType());
        List<DDMFormField> copyNestedDDMFormFields = copyDDMFormField.getNestedDDMFormFields();
        Assert.assertEquals(copyNestedDDMFormFields.toString(), 2, copyNestedDDMFormFields.size());
        DDMFormField copyNestedDDMFormField = copyNestedDDMFormFields.get(0);
        Assert.assertEquals("Phone", copyNestedDDMFormField.getName());
        Assert.assertEquals("text", copyNestedDDMFormField.getType());
        copyNestedDDMFormField = copyNestedDDMFormFields.get(1);
        Assert.assertEquals("Name", copyNestedDDMFormField.getName());
        Assert.assertEquals("text", copyNestedDDMFormField.getType());
        List<DDMFormField> nestedDDMFormFields = copyNestedDDMFormField.getNestedDDMFormFields();
        Assert.assertEquals(nestedDDMFormFields.toString(), 1, nestedDDMFormFields.size());
        copyNestedDDMFormField = nestedDDMFormFields.get(0);
        Assert.assertEquals("ZipCode", copyNestedDDMFormField.getName());
        Assert.assertEquals("text", copyNestedDDMFormField.getType());
    }

    @Test
    public void testSomeFieldProperties() throws Exception {
        DDMForm ddmForm = createDDMForm();
        DDMFormField nameDDMFormField = new DDMFormField("Name", "textarea");
        ddmForm.addDDMFormField(nameDDMFormField);
        nameDDMFormField.setFieldNamespace("namespace");
        nameDDMFormField.setIndexType("indexType");
        LocalizedValue label = new LocalizedValue(LocaleUtil.BRAZIL);
        label.addString(BRAZIL, "teste");
        nameDDMFormField.setLabel(label);
        nameDDMFormField.setLocalizable(true);
        nameDDMFormField.setMultiple(true);
        nameDDMFormField.setReadOnly(true);
        nameDDMFormField.setRepeatable(true);
        nameDDMFormField.setRequired(true);
        nameDDMFormField.setShowLabel(true);
        LocalizedValue style = new LocalizedValue(LocaleUtil.ENGLISH);
        style.addString(ENGLISH, "style");
        nameDDMFormField.setStyle(style);
        LocalizedValue tip = new LocalizedValue(LocaleUtil.FRANCE);
        tip.addString(FRANCE, "tip");
        nameDDMFormField.setTip(tip);
        nameDDMFormField.setVisibilityExpression("expression");
        DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();
        ddmFormFieldOptions.setDefaultLocale(BRAZIL);
        ddmFormFieldOptions.addOptionLabel("teste", ENGLISH, "label");
        nameDDMFormField.setDDMFormFieldOptions(ddmFormFieldOptions);
        DDMForm copyDDMForm = BeanPropertiesUtil.deepCopyProperties(ddmForm);
        List<DDMFormField> copyDDMFormFields = copyDDMForm.getDDMFormFields();
        DDMFormField copyDDMFormField = copyDDMFormFields.get(0);
        Assert.assertEquals("Name", copyDDMFormField.getName());
        Assert.assertEquals("textarea", copyDDMFormField.getType());
        Assert.assertEquals("namespace", copyDDMFormField.getFieldNamespace());
        Assert.assertEquals("indexType", copyDDMFormField.getIndexType());
        LocalizedValue copyLabel = copyDDMFormField.getLabel();
        Assert.assertEquals(BRAZIL, copyLabel.getDefaultLocale());
        Assert.assertEquals("teste", copyLabel.getString(BRAZIL));
        Assert.assertTrue(copyDDMFormField.isLocalizable());
        Assert.assertTrue(copyDDMFormField.isMultiple());
        Assert.assertTrue(copyDDMFormField.isReadOnly());
        Assert.assertTrue(copyDDMFormField.isRepeatable());
        Assert.assertTrue(copyDDMFormField.isRequired());
        Assert.assertTrue(copyDDMFormField.isShowLabel());
        LocalizedValue copyStyle = copyDDMFormField.getStyle();
        Assert.assertEquals(ENGLISH, copyStyle.getDefaultLocale());
        Assert.assertEquals("style", copyStyle.getString(ENGLISH));
        LocalizedValue copyTip = copyDDMFormField.getTip();
        Assert.assertEquals(FRANCE, copyTip.getDefaultLocale());
        Assert.assertEquals("tip", copyTip.getString(FRANCE));
        Assert.assertEquals("expression", copyDDMFormField.getVisibilityExpression());
        DDMFormFieldOptions copyDDMFormFieldOptions = copyDDMFormField.getDDMFormFieldOptions();
        Assert.assertEquals(BRAZIL, copyDDMFormFieldOptions.getDefaultLocale());
        Map<String, LocalizedValue> copyOptions = copyDDMFormFieldOptions.getOptions();
        Assert.assertTrue(copyOptions.containsKey("teste"));
        LocalizedValue copyTesteOptionLabel = copyOptions.get("teste");
        Assert.assertEquals("label", copyTesteOptionLabel.getString(ENGLISH));
    }

    @Test
    public void testWithFields() throws Exception {
        DDMForm ddmForm = createDDMForm();
        DDMFormField nameDDMFormField = new DDMFormField("Name", "textarea");
        DDMFormField phoneDDMFormField = new DDMFormField("Phone", "text");
        ddmForm.addDDMFormField(nameDDMFormField);
        ddmForm.addDDMFormField(phoneDDMFormField);
        DDMForm copyDDMForm = BeanPropertiesUtil.deepCopyProperties(ddmForm);
        List<DDMFormField> copyDDMFormFields = copyDDMForm.getDDMFormFields();
        Assert.assertEquals(copyDDMFormFields.toString(), 2, copyDDMFormFields.size());
        DDMFormField copyDDMFormField = copyDDMFormFields.get(0);
        Assert.assertEquals("Name", copyDDMFormField.getName());
        Assert.assertEquals("textarea", copyDDMFormField.getType());
        copyDDMFormField = copyDDMFormFields.get(1);
        Assert.assertEquals("Phone", copyDDMFormField.getName());
        Assert.assertEquals("text", copyDDMFormField.getType());
    }

    @Test
    public void testWithoutFields() throws Exception {
        DDMForm ddmForm = createDDMForm();
        DDMForm copyDDMForm = BeanPropertiesUtil.deepCopyProperties(ddmForm);
        Set<Locale> availableLocales = copyDDMForm.getAvailableLocales();
        Assert.assertFalse(availableLocales.toString(), availableLocales.isEmpty());
        Assert.assertEquals(availableLocales.toArray()[0], US);
    }
}

