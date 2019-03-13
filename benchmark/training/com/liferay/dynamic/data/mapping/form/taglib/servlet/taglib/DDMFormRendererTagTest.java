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
package com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.taglib.internal.security.permission.DDMFormInstancePermission;
import com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib.util.DDMFormTaglibUtil;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Pedro Queiroz
 */
@PrepareForTest({ DDMFormInstancePermission.class, DDMFormTaglibUtil.class, LocaleUtil.class })
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceModelImpl", "com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl", "com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl", "com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceVersionModelImpl", "com.liferay.taglib.util.IncludeTag" })
public class DDMFormRendererTagTest extends PowerMockito {
    @Test
    public void testCreateDDMFormRenderingContext() {
        setDDMFormRendererTagInputs(1L, null, null, null, false, false);
        DDMForm ddmForm = new DDMForm();
        ddmForm.setDefaultLocale(US);
        DDMFormRenderingContext ddmFormRenderingContext = _ddmFormRendererTag.createDDMFormRenderingContext(ddmForm);
        Assert.assertNotNull(ddmFormRenderingContext.getContainerId());
        Assert.assertEquals(_ddmFormInstance.getGroupId(), ddmFormRenderingContext.getGroupId());
        Assert.assertNotNull(ddmFormRenderingContext.getHttpServletRequest());
        Assert.assertNotNull(ddmFormRenderingContext.getHttpServletResponse());
        Assert.assertEquals(US, ddmFormRenderingContext.getLocale());
        Assert.assertTrue(ddmFormRenderingContext.isViewMode());
        Assert.assertNotNull(ddmFormRenderingContext.getDDMFormValues());
        Assert.assertNotNull(ddmFormRenderingContext.getPortletNamespace());
        Assert.assertFalse(ddmFormRenderingContext.isReadOnly());
        Assert.assertFalse(ddmFormRenderingContext.isShowSubmitButton());
    }

    @Test
    public void testGetDDMFormDefaultLocaleWhenLocaleIsNotAvailable() {
        DDMForm ddmForm = new DDMForm();
        ddmForm.setDefaultLocale(BRAZIL);
        Locale locale = _ddmFormRendererTag.getLocale(_request, ddmForm);
        Assert.assertEquals(BRAZIL, locale);
    }

    @Test
    public void testGetFormInstanceWhenFormInstanceRecordIdHasHigherPriority() {
        setDDMFormRendererTagInputs(1L, 2L, null, 4L);
        DDMFormInstance ddmFormInstance = _ddmFormRendererTag.getDDMFormInstance();
        Assert.assertEquals(2L, ddmFormInstance.getFormInstanceId());
    }

    @Test
    public void testGetFormInstanceWhenFormInstanceRecordVersionIdHasHigherPriority() {
        setDDMFormRendererTagInputs(1L, 2L, 3L, 4L);
        DDMFormInstance ddmFormInstance = _ddmFormRendererTag.getDDMFormInstance();
        Assert.assertEquals(3L, ddmFormInstance.getFormInstanceId());
    }

    @Test
    public void testGetFormInstanceWhenFormInstanceVersionIdHasHigherPriority() {
        setDDMFormRendererTagInputs(1L, null, null, 4L);
        DDMFormInstance ddmFormInstance = _ddmFormRendererTag.getDDMFormInstance();
        Assert.assertEquals(4L, ddmFormInstance.getFormInstanceId());
    }

    @Test
    public void testGetFormInstanceWithFormInstanceId() {
        setDDMFormRendererTagInputs(1L, null, null, null);
        DDMFormInstance ddmFormInstance = _ddmFormRendererTag.getDDMFormInstance();
        Assert.assertEquals(1L, ddmFormInstance.getFormInstanceId());
    }

    @Test
    public void testGetFormInstanceWithFormInstanceRecordId() {
        setDDMFormRendererTagInputs(null, 2L, null, null);
        DDMFormInstance ddmFormInstance = _ddmFormRendererTag.getDDMFormInstance();
        Assert.assertEquals(2L, ddmFormInstance.getFormInstanceId());
    }

    @Test
    public void testGetFormInstanceWithFormInstanceRecordVersionId() {
        setDDMFormRendererTagInputs(null, null, 3L, null);
        DDMFormInstance ddmFormInstance = _ddmFormRendererTag.getDDMFormInstance();
        Assert.assertEquals(3L, ddmFormInstance.getFormInstanceId());
    }

    @Test
    public void testGetFormInstanceWithFormInstanceVersionId() {
        setDDMFormRendererTagInputs(null, null, null, 4L);
        DDMFormInstance ddmFormInstance = _ddmFormRendererTag.getDDMFormInstance();
        Assert.assertEquals(4L, ddmFormInstance.getFormInstanceId());
    }

    @Test
    public void testGetLocaleFromRequestWhenDDMFormIsNull() {
        Locale locale = _ddmFormRendererTag.getLocale(_request, null);
        Assert.assertEquals(US, locale);
    }

    @Test
    public void testGetLocaleWhenLocaleIsAvailable() {
        DDMForm ddmForm = new DDMForm();
        ddmForm.setDefaultLocale(BRAZIL);
        ddmForm.setAvailableLocales(createAvailableLocales(BRAZIL, US));
        Locale locale = _ddmFormRendererTag.getLocale(_request, ddmForm);
        Assert.assertEquals(US, locale);
    }

    @Test
    public void testGetRedirectURLWhenFormInstanceIsNull() {
        setDDMFormRendererTagInputs(null, null, null, null);
        String redirectURL = _ddmFormRendererTag.getRedirectURL();
        Assert.assertEquals(BLANK, redirectURL);
    }

    private DDMFormInstance _ddmFormInstance;

    @Mock
    private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

    private DDMFormInstanceRecord _ddmFormInstanceRecord;

    @Mock
    private DDMFormInstanceRecordLocalService _ddmFormInstanceRecordLocalService;

    private DDMFormInstanceRecordVersion _ddmFormInstanceRecordVersion;

    @Mock
    private DDMFormInstanceRecordVersionLocalService _ddmFormInstanceRecordVersionLocalService;

    private DDMFormInstanceVersion _ddmFormInstanceVersion;

    @Mock
    private DDMFormInstanceVersionLocalService _ddmFormInstanceVersionLocalService;

    private final DDMFormRendererTag _ddmFormRendererTag = new DDMFormRendererTag();

    @Mock
    private DDMFormTaglibUtil _ddmFormTaglibUtil;

    private DDMFormValues _ddmFormValues;

    @Mock
    private DDMFormValuesFactory _ddmFormValuesFactory;

    @Mock
    private Language _language;

    private final HttpServletRequest _request = new MockHttpServletRequest();
}

