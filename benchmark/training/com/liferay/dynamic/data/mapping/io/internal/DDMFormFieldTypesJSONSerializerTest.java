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
import com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDMFormFieldTypesJSONSerializerTest extends BaseDDMTestCase {
    @Test
    public void testSerializationWithEmptyParameterList() throws Exception {
        List<DDMFormFieldType> ddmFormFieldTypes = Collections.emptyList();
        String actualJSON = serialize(ddmFormFieldTypes);
        Assert.assertEquals("[]", actualJSON);
    }

    @Test
    public void testSerializationWithNonemptyParameterList() throws Exception {
        List<DDMFormFieldType> ddmFormFieldTypes = new ArrayList<>();
        DDMFormFieldType ddmFormFieldType = getMockedDDMFormFieldType();
        ddmFormFieldTypes.add(ddmFormFieldType);
        String actualJSON = serialize(ddmFormFieldTypes);
        JSONAssert.assertEquals(createExpectedJSON(), actualJSON, false);
    }

    private final DDMFormFieldTypesSerializer _ddmFormFieldTypesSerializer = new DDMFormFieldTypesJSONSerializer();
}

