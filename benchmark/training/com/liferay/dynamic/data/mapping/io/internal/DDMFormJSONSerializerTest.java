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


import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import org.junit.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDMFormJSONSerializerTest extends BaseDDMFormSerializerTestCase {
    @Test
    public void testDDMFormSerialization() throws Exception {
        String expectedJSON = read("ddm-form-json-serializer-test-data.json");
        DDMForm ddmForm = createDDMForm();
        ddmForm.setDDMFormRules(createDDMFormRules());
        ddmForm.setDDMFormSuccessPageSettings(createDDMFormSuccessPageSettings());
        String actualJSON = serialize(ddmForm);
        JSONAssert.assertEquals(expectedJSON, actualJSON, false);
    }

    private final DDMFormJSONSerializer _ddmFormJSONSerializer = new DDMFormJSONSerializer();

    @Mock
    private DDMFormFieldType _defaultDDMFormFieldType;
}

