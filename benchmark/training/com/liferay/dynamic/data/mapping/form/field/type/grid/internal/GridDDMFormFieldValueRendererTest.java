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
package com.liferay.dynamic.data.mapping.form.field.type.grid.internal;


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
 * @author Pedro Queiroz
 */
public class GridDDMFormFieldValueRendererTest {
    @Test
    public void testRender() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField("Grid", "Grid", "grid", "string", false, false, false);
        DDMFormFieldOptions ddmFormFieldRows = new DDMFormFieldOptions();
        ddmFormFieldRows.addOptionLabel("rowValue 1", US, "rowLabel 1");
        ddmFormFieldRows.addOptionLabel("rowValue 2", US, "rowLabel 2");
        ddmFormField.setProperty("rows", ddmFormFieldRows);
        DDMFormFieldOptions ddmFormFieldColumns = new DDMFormFieldOptions();
        ddmFormFieldColumns.addOptionLabel("columnValue 1", US, "columnLabel 1");
        ddmFormFieldColumns.addOptionLabel("columnValue 2", US, "columnLabel 2");
        ddmFormField.setProperty("columns", ddmFormFieldColumns);
        ddmForm.addDDMFormField(ddmFormField);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Grid", new UnlocalizedValue("{\"rowValue 1\":\"columnValue 1\"}"));
        ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
        GridDDMFormFieldValueRenderer gridDDMFormFieldValueRenderer = createGridDDMFormFieldValueRenderer();
        Assert.assertEquals("rowLabel 1: columnLabel 1", gridDDMFormFieldValueRenderer.render(ddmFormFieldValue, US));
    }

    @Test
    public void testRenderWithTwoRows() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField("Grid", "Grid", "grid", "string", false, false, false);
        DDMFormFieldOptions ddmFormFieldRows = new DDMFormFieldOptions();
        ddmFormFieldRows.addOptionLabel("rowValue 1", US, "rowLabel 1");
        ddmFormFieldRows.addOptionLabel("rowValue 2", US, "rowLabel 2");
        ddmFormField.setProperty("rows", ddmFormFieldRows);
        DDMFormFieldOptions ddmFormFieldColumns = new DDMFormFieldOptions();
        ddmFormFieldColumns.addOptionLabel("columnValue 1", US, "columnLabel 1");
        ddmFormFieldColumns.addOptionLabel("columnValue 2", US, "columnLabel 2");
        ddmFormField.setProperty("columns", ddmFormFieldColumns);
        ddmForm.addDDMFormField(ddmFormField);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        String value = "{\"rowValue 2\":\"columnValue 2\", \"rowValue 1\":\"" + "columnValue 1\"}";
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Grid", new UnlocalizedValue(value));
        ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
        GridDDMFormFieldValueRenderer gridDDMFormFieldValueRenderer = createGridDDMFormFieldValueRenderer();
        Assert.assertEquals("rowLabel 1: columnLabel 1, rowLabel 2: columnLabel 2", gridDDMFormFieldValueRenderer.render(ddmFormFieldValue, US));
    }
}

