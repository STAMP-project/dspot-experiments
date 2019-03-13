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
package com.liferay.dynamic.data.mapping.validator.internal;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidationException.InvalidRowSize;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidationException.MustNotDuplicateFieldName;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidationException.MustSetDefaultLocale;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidationException.MustSetEqualLocaleForLayoutAndTitle;
import com.liferay.dynamic.data.mapping.validator.DDMFormLayoutValidator;
import org.junit.Test;


/**
 *
 *
 * @author Pablo Carvalho
 */
public class DDMFormLayoutValidatorTest {
    @Test(expected = MustNotDuplicateFieldName.class)
    public void testDuplicateFieldNames() throws Exception {
        DDMFormLayoutColumn ddmFormLayoutColumn1 = _createDDMFormLayoutColumn(6, "field1", "field2", "field3");
        DDMFormLayoutColumn ddmFormLayoutColumn2 = _createDDMFormLayoutColumn(6, "field1", "field3");
        DDMFormLayoutRow ddmFormLayoutRow = _createDDMFormLayoutRow(ddmFormLayoutColumn1);
        ddmFormLayoutRow.addDDMFormLayoutColumn(ddmFormLayoutColumn2);
        LocalizedValue title = _createLocalizedValue("Page1", US);
        DDMFormLayoutPage ddmFormLayoutPage = _createDDMFormLayoutPage(ddmFormLayoutRow, title);
        DDMFormLayout ddmFormLayout = _createDDMFormLayout(ddmFormLayoutPage, US);
        _ddmFormLayoutValidator.validate(ddmFormLayout);
    }

    @Test(expected = InvalidRowSize.class)
    public void testInvalidRowSize() throws Exception {
        DDMFormLayoutColumn ddmFormLayoutColumn1 = _createDDMFormLayoutColumn(6, "field1");
        DDMFormLayoutColumn ddmFormLayoutColumn2 = _createDDMFormLayoutColumn(7, "field2");
        DDMFormLayoutRow ddmFormLayoutRow = _createDDMFormLayoutRow(ddmFormLayoutColumn1);
        ddmFormLayoutRow.addDDMFormLayoutColumn(ddmFormLayoutColumn2);
        LocalizedValue title = _createLocalizedValue("Page1", US);
        DDMFormLayoutPage ddmFormLayoutPage = _createDDMFormLayoutPage(ddmFormLayoutRow, title);
        DDMFormLayout ddmFormLayout = _createDDMFormLayout(ddmFormLayoutPage, US);
        _ddmFormLayoutValidator.validate(ddmFormLayout);
    }

    @Test(expected = MustSetDefaultLocale.class)
    public void testNullDefaultLocale() throws Exception {
        DDMFormLayout ddmFormLayout = new DDMFormLayout();
        ddmFormLayout.setDefaultLocale(null);
        _ddmFormLayoutValidator.validate(ddmFormLayout);
    }

    @Test
    public void testValidDDMFormLayout() throws Exception {
        DDMFormLayoutColumn ddmFormLayoutColumn = _createDDMFormLayoutColumn(12, "field");
        DDMFormLayoutRow ddmFormLayoutRow = _createDDMFormLayoutRow(ddmFormLayoutColumn);
        LocalizedValue title = _createLocalizedValue("Page1", US);
        DDMFormLayoutPage ddmFormLayoutPage = _createDDMFormLayoutPage(ddmFormLayoutRow, title);
        DDMFormLayout ddmFormLayout = _createDDMFormLayout(ddmFormLayoutPage, US);
        _ddmFormLayoutValidator.validate(ddmFormLayout);
    }

    @Test(expected = MustSetEqualLocaleForLayoutAndTitle.class)
    public void testWrongDefaultLocaleSetForPageTitle() throws Exception {
        DDMFormLayoutColumn ddmFormLayoutColumn = _createDDMFormLayoutColumn(12, "field");
        DDMFormLayoutRow ddmFormLayoutRow = _createDDMFormLayoutRow(ddmFormLayoutColumn);
        LocalizedValue title = _createLocalizedValue("Page1", US);
        DDMFormLayoutPage ddmFormLayoutPage = _createDDMFormLayoutPage(ddmFormLayoutRow, title);
        DDMFormLayout ddmFormLayout = _createDDMFormLayout(ddmFormLayoutPage, BRAZIL);
        _ddmFormLayoutValidator.validate(ddmFormLayout);
    }

    private final DDMFormLayoutValidator _ddmFormLayoutValidator = new DDMFormLayoutValidatorImpl();
}

