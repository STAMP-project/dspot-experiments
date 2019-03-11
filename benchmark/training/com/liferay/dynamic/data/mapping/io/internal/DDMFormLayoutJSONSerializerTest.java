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


import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDMFormLayoutJSONSerializerTest extends BaseDDMTestCase {
    @Test
    public void testDDMFormLayoutSerialization() throws Exception {
        String expectedJSON = read("ddm-form-layout-json-serializer-test-data.json");
        DDMFormLayout ddmFormLayout = createDDMFormLayout();
        String actualJSON = serialize(ddmFormLayout);
        JSONAssert.assertEquals(expectedJSON, actualJSON, false);
    }

    private final DDMFormLayoutJSONSerializer _ddmFormLayoutJSONSerializer = new DDMFormLayoutJSONSerializer();
}

