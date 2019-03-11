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


import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.util.HtmlImpl;
import java.util.Map;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Pedro Queiroz
 */
@RunWith(PowerMockRunner.class)
public class DocumentLibraryDDMFormFieldTemplateContextContributorTest extends BaseDDMFormFieldTypeSettingsTestCase {
    @Test
    public void testGetParametersShouldContainFileEntryURL() {
        DDMFormField ddmFormField = new DDMFormField("field", "numeric");
        DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setHttpServletRequest(createHttpServletRequest());
        ddmFormFieldRenderingContext.setProperty("groupId", 12345);
        ddmFormFieldRenderingContext.setReadOnly(true);
        ddmFormFieldRenderingContext.setValue("{\"uuid\": \"0000-1111\", \"title\": \"File Title\"}");
        Map<String, Object> parameters = spy.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        Assert.assertTrue(parameters.containsKey("fileEntryURL"));
    }

    @Test
    public void testGetParametersShouldContainItemSelectorAuthToken() {
        DDMFormField ddmFormField = new DDMFormField("field", "numeric");
        DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setHttpServletRequest(createHttpServletRequest());
        ddmFormFieldRenderingContext.setValue("{\"uuid\": \"0000-1111\", \"title\": \"Title\"}");
        Map<String, Object> parameters = spy.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        Assert.assertEquals("token", parameters.get("itemSelectorAuthToken"));
    }

    @Test
    public void testGetParametersShouldNotContainFileEntryURL() {
        DDMFormField ddmFormField = new DDMFormField("field", "numeric");
        DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setHttpServletRequest(createHttpServletRequest());
        ddmFormFieldRenderingContext.setReadOnly(false);
        ddmFormFieldRenderingContext.setValue("{\"uuid\": \"0000-1111\", \"title\": \"File Title\"}");
        Map<String, Object> parameters = spy.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        Assert.assertFalse(parameters.containsKey("fileEntryURL"));
    }

    @Test
    public void testGetParametersShouldUseFileEntryTitle() {
        DDMFormField ddmFormField = new DDMFormField("field", "numeric");
        DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();
        DDMFormFieldRenderingContext ddmFormFieldRenderingContext = new DDMFormFieldRenderingContext();
        ddmFormFieldRenderingContext.setHttpServletRequest(createHttpServletRequest());
        ddmFormFieldRenderingContext.setReadOnly(true);
        ddmFormFieldRenderingContext.setValue("{\"uuid\": \"0000-1111\", \"title\": \"Old Title\"}");
        Map<String, Object> parameters = spy.getParameters(ddmFormField, ddmFormFieldRenderingContext);
        Assert.assertEquals("New Title", parameters.get("fileEntryTitle"));
    }

    @Mock
    private DLAppService _dlAppService;

    private final DocumentLibraryDDMFormFieldTemplateContextContributor _documentLibraryDDMFormFieldTemplateContextContributor = new DocumentLibraryDDMFormFieldTemplateContextContributor();

    @Mock
    private FileEntry _fileEntry;

    private final Html _html = new HtmlImpl();

    private final JSONFactory _jsonFactory = new JSONFactoryImpl();

    @Mock
    private ResourceBundle _resourceBundle;
}

