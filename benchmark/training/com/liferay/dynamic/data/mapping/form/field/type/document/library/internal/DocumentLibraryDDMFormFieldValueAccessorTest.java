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
package com.liferay.dynamic.data.mapping.form.field.type.document.library.internal;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import org.junit.Assert;
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
public class DocumentLibraryDDMFormFieldValueAccessorTest extends PowerMockito {
    @Test
    public void testEmpty() {
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("documentLibrary", new UnlocalizedValue("{}"));
        Assert.assertTrue(_documentLibraryDDMFormFieldValueAccessor.isEmpty(ddmFormFieldValue, US));
    }

    @Test
    public void testNotEmpty() {
        StringBundler sb = new StringBundler(4);
        sb.append("{\"groupId\":\"32964\",");
        sb.append("\"title\":\"Welcome to Liferay Forms!\",");
        sb.append("\"type\":\"document\",");
        sb.append("\"uuid\":\"f85c8ae1-603b-04eb-1132-12645d73519e\"}");
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("documentLibrary", new UnlocalizedValue(sb.toString()));
        Assert.assertFalse(_documentLibraryDDMFormFieldValueAccessor.isEmpty(ddmFormFieldValue, US));
    }

    private DocumentLibraryDDMFormFieldValueAccessor _documentLibraryDDMFormFieldValueAccessor;

    private final JSONFactory _jsonFactory = new JSONFactoryImpl();
}

