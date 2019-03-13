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
package com.liferay.dynamic.data.mapping.io.internal;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDMFormValuesJSONDeserializerTest extends BaseDDMTestCase {
    @Test
    public void testDeserializationWithEmptyFields() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("Text1", false, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Select1", "Select1", "select", "string", true, false, false));
        String serializedDDMFormValues = read("ddm-form-values-json-deserializer-empty-values.json");
        DDMFormValues ddmFormValues = deserialize(serializedDDMFormValues, ddmForm);
        Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap = ddmFormValues.getDDMFormFieldValuesMap();
        Assert.assertEquals(ddmFormFieldValuesMap.toString(), 2, ddmFormFieldValuesMap.size());
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get("Text1");
        DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);
        Value value = ddmFormFieldValue.getValue();
        Assert.assertTrue((value instanceof UnlocalizedValue));
        Assert.assertEquals(BLANK, value.getString(value.getDefaultLocale()));
        ddmFormFieldValues = ddmFormFieldValuesMap.get("Select1");
        ddmFormFieldValue = ddmFormFieldValues.get(0);
        value = ddmFormFieldValue.getValue();
        Assert.assertTrue((value instanceof LocalizedValue));
        Assert.assertEquals("[]", value.getString(value.getDefaultLocale()));
    }

    @Test
    public void testDeserializationWithParentRepeatableField() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField separatorDDMFormField = DDMFormTestUtil.createSeparatorDDMFormField("Separator", true);
        separatorDDMFormField.addNestedDDMFormField(DDMFormTestUtil.createLocalizableTextDDMFormField("Text"));
        ddmForm.addDDMFormField(separatorDDMFormField);
        String serializedDDMFormValues = read("ddm-form-values-json-deserializer-parent-repeatable-field.json");
        DDMFormValues ddmFormValues = deserialize(serializedDDMFormValues, ddmForm);
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        for (int i = 0; i < (ddmFormFieldValues.size()); i++) {
            DDMFormFieldValue separatorDDMFormFieldValue = ddmFormFieldValues.get(i);
            testSeparatorDDMFormFieldValueValue(separatorDDMFormFieldValue);
            List<DDMFormFieldValue> separatorNestedDDMFormFieldValues = separatorDDMFormFieldValue.getNestedDDMFormFieldValues();
            Assert.assertEquals(separatorNestedDDMFormFieldValues.toString(), 1, separatorNestedDDMFormFieldValues.size());
            testTextDDMFormFieldValue(separatorNestedDDMFormFieldValues.get(0), ("Content " + i), ("Conteudo " + i));
        }
    }

    @Test
    public void testDeserializationWithRepeatableField() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("Text", true, true, false));
        String serializedDDMFormValues = read("ddm-form-values-json-deserializer-repeatable-field.json");
        DDMFormValues ddmFormValues = deserialize(serializedDDMFormValues, ddmForm);
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        for (int i = 0; i < (ddmFormFieldValues.size()); i++) {
            testTextDDMFormFieldValue(ddmFormFieldValues.get(i), ("Name " + i), ("Nome " + i));
        }
    }

    @Test
    public void testDeserializationWithSimpleFields() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Boolean", "Boolean", "checkbox", "boolean", true, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Documents_and_Media", "Documents_and_Media", "document-library", "string", true, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Geolocation", "Geolocation", "geolocation", "string", true, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("HTML", "HTML", "html", "string", true, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Image", "Image", "image", "string", true, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Link_to_Page", "Link to Page", "link_to_page", "string", true, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Select", "Select", "select", "string", true, false, false));
        String serializedDDMFormValues = read("ddm-form-values-json-deserializer-test-data.json");
        DDMFormValues ddmFormValues = deserialize(serializedDDMFormValues, ddmForm);
        testAvailableLocales(ddmFormValues);
        testDefaultLocale(ddmFormValues);
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 7, ddmFormFieldValues.size());
        testBooleanDDMFormFieldValueValues(ddmFormFieldValues.get(0));
        testDocumentLibraryDDMFormFieldValueValues(ddmFormFieldValues.get(1));
        testGeolocationDDMFormFieldValueValues(ddmFormFieldValues.get(2));
        testHTMLDDMFormFieldValueValues(ddmFormFieldValues.get(3));
        testImageDDMFormFieldValueValues(ddmFormFieldValues.get(4));
        testLinkToPageDDMFormFieldValueValues(ddmFormFieldValues.get(5));
        testSelectDDMFormFieldValueValues(ddmFormFieldValues.get(6));
    }

    @Test
    public void testDeserializationWithUnlocalizableField() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Boolean", "Boolean", "checkbox", "boolean", false, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Documents_and_Media", "Documents_and_Media", "document-library", "string", false, false, false));
        String serializedDDMFormValues = read("ddm-form-values-json-deserializer-unlocalizable-fields.json");
        DDMFormValues ddmFormValues = deserialize(serializedDDMFormValues, ddmForm);
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 2, ddmFormFieldValues.size());
        DDMFormFieldValue booleanDDMFormFieldValue = ddmFormFieldValues.get(0);
        Assert.assertEquals("usht", booleanDDMFormFieldValue.getInstanceId());
        Value booleanValue = booleanDDMFormFieldValue.getValue();
        Assert.assertFalse(booleanValue.isLocalized());
        Assert.assertEquals("false", booleanValue.getString(US));
        Assert.assertEquals("false", booleanValue.getString(BRAZIL));
        DDMFormFieldValue documentLibraryDDMFormFieldValue = ddmFormFieldValues.get(1);
        Assert.assertEquals("xdwp", documentLibraryDDMFormFieldValue.getInstanceId());
        Value documentLibraryValue = documentLibraryDDMFormFieldValue.getValue();
        Assert.assertFalse(documentLibraryValue.isLocalized());
        JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();
        expectedJSONObject.put("groupId", 10192);
        expectedJSONObject.put("uuid", "c8acdf70-e101-46a6-83e5-c5f5e087b0dc");
        expectedJSONObject.put("version", 1.0);
        JSONAssert.assertEquals(expectedJSONObject.toString(), documentLibraryValue.getString(US), false);
        JSONAssert.assertEquals(expectedJSONObject.toString(), documentLibraryValue.getString(BRAZIL), false);
    }

    private final DDMFormValuesDeserializer _ddmFormValuesDeserializer = new DDMFormValuesJSONDeserializer();
}

