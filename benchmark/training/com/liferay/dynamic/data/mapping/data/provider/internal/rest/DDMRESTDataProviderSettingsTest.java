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
package com.liferay.dynamic.data.mapping.data.provider.internal.rest;


import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Leonardo Barros
 */
@PrepareForTest(ResourceBundleUtil.class)
@RunWith(PowerMockRunner.class)
public class DDMRESTDataProviderSettingsTest {
    @Test
    public void testCreateForm() {
        DDMForm ddmForm = DDMFormFactory.create(DDMRESTDataProviderSettings.class);
        Map<String, DDMFormField> ddmFormFields = ddmForm.getDDMFormFieldsMap(false);
        Assert.assertEquals(ddmFormFields.toString(), 12, ddmFormFields.size());
        assertCacheable(ddmFormFields.get("cacheable"));
        assertFilterable(ddmFormFields.get("filterable"));
        assertFilterParameterName(ddmFormFields.get("filterParameterName"));
        assertInputParameters(ddmFormFields.get("inputParameters"));
        assertOutputParameters(ddmFormFields.get("outputParameters"));
        assertPagination(ddmFormFields.get("pagination"));
        assertPaginationEndParameterName(ddmFormFields.get("paginationEndParameterName"));
        assertPassword(ddmFormFields.get("password"));
        assertStartPaginationParameterName(ddmFormFields.get("paginationStartParameterName"));
        assertTimeout(ddmFormFields.get("timeout"));
        assertURL(ddmFormFields.get("url"));
        assertUsername(ddmFormFields.get("username"));
    }
}

