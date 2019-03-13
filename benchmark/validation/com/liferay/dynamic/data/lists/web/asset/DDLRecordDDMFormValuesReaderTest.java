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
package com.liferay.dynamic.data.lists.web.asset;


import com.liferay.asset.kernel.model.DDMFormValuesReader;
import com.liferay.dynamic.data.lists.web.internal.asset.DDLRecordDDMFormValuesReader;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.portal.kernel.exception.PortalException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDLRecordDDMFormValuesReaderTest {
    @Test
    public void testGetDDMFormFieldValues() throws Exception {
        DDMFormValuesReader ddmFormValuesReader = new DDLRecordDDMFormValuesReaderTest.MockDDLRecordDDMFormValuesReader(createDDMFormValues());
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesReader.getDDMFormFieldValues("text");
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        Assert.assertEquals("Text", getDDMFormFieldValueName(ddmFormFieldValues.get(0)));
        Assert.assertEquals("NestedText", getDDMFormFieldValueName(ddmFormFieldValues.get(1)));
        Assert.assertEquals("NestedText", getDDMFormFieldValueName(ddmFormFieldValues.get(2)));
    }

    private static class MockDDLRecordDDMFormValuesReader extends DDLRecordDDMFormValuesReader {
        public MockDDLRecordDDMFormValuesReader(DDMFormValues ddmFormValues) {
            super(null);
            _ddmFormValues = ddmFormValues;
        }

        @Override
        public DDMFormValues getDDMFormValues() throws PortalException {
            return _ddmFormValues;
        }

        private final DDMFormValues _ddmFormValues;
    }
}

