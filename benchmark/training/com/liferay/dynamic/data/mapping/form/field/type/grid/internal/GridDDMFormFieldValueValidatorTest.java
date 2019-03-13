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
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueValidationException;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Pedro Queiroz
 */
@RunWith(PowerMockRunner.class)
public class GridDDMFormFieldValueValidatorTest extends PowerMockito {
    @Test(expected = DDMFormFieldValueValidationException.class)
    public void testValidationWithEmptyColumns() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField("Grid", "Grid", "grid", "string", false, false, false);
        DDMFormFieldOptions ddmFormFieldRows = new DDMFormFieldOptions();
        ddmFormFieldRows.addOptionLabel("rowValue 1", US, "rowLabel 1");
        ddmFormFieldRows.addOptionLabel("rowValue 2", US, "rowLabel 2");
        ddmFormField.setProperty("rows", ddmFormFieldRows);
        DDMFormFieldOptions ddmFormFieldColumns = new DDMFormFieldOptions();
        ddmFormField.setProperty("columns", ddmFormFieldColumns);
        ddmForm.addDDMFormField(ddmFormField);
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Grid", new UnlocalizedValue("{\"rowValue 1\":\"columnValue 1\"}"));
        _gridDDMFormFieldValueValidator.validate(ddmFormField, ddmFormFieldValue.getValue());
    }

    @Test(expected = DDMFormFieldValueValidationException.class)
    public void testValidationWithInvalidColumnValue() throws Exception {
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
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Grid", new UnlocalizedValue("{\"rowValue 1\":\"columnValue 4\"}"));
        _gridDDMFormFieldValueValidator.validate(ddmFormField, ddmFormFieldValue.getValue());
    }

    @Test(expected = DDMFormFieldValueValidationException.class)
    public void testValidationWithNoColumns() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField("Grid", "Grid", "grid", "string", false, false, false);
        DDMFormFieldOptions ddmFormFieldRows = new DDMFormFieldOptions();
        ddmFormFieldRows.addOptionLabel("rowValue 1", US, "rowLabel 1");
        ddmFormFieldRows.addOptionLabel("rowValue 2", US, "rowLabel 2");
        ddmFormField.setProperty("rows", ddmFormFieldRows);
        DDMFormFieldOptions ddmFormFieldColumns = null;
        ddmFormField.setProperty("columns", ddmFormFieldColumns);
        ddmForm.addDDMFormField(ddmFormField);
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Grid", new UnlocalizedValue("{\"rowValue 1\":\"columnValue 1\"}"));
        _gridDDMFormFieldValueValidator.validate(ddmFormField, ddmFormFieldValue.getValue());
    }

    private GridDDMFormFieldValueValidator _gridDDMFormFieldValueValidator;

    private final JSONFactory _jsonFactory = new JSONFactoryImpl();
}

