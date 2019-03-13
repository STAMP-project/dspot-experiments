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


import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
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
 * @author Pedro Queiroz
 */
@PrepareForTest({ PortalClassLoaderUtil.class, ResourceBundleUtil.class })
@RunWith(PowerMockRunner.class)
public class DocumentLibraryDDMFormFieldTypeSettingsTest extends BaseDDMFormFieldTypeSettingsTestCase {
    @Test
    public void testCreateDocumentLibraryDDMFormFieldTypeSettingsDDMForm() {
        DDMForm ddmForm = DDMFormFactory.create(DocumentLibraryDDMFormFieldTypeSettings.class);
        Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(false);
        DDMFormField predefinedValueDDMFormField = ddmFormFieldsMap.get("predefinedValue");
        Assert.assertNotNull(predefinedValueDDMFormField);
        DDMFormField validationDDMFormField = ddmFormFieldsMap.get("validation");
        Assert.assertNotNull(validationDDMFormField);
        DDMFormField repeatableDDMFormField = ddmFormFieldsMap.get("repeatable");
        Assert.assertNotNull(repeatableDDMFormField);
    }
}

