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
package com.liferay.dynamic.data.mapping.form.web.internal.display.context;


import LocaleUtil.SPAIN;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.portlet.PortletSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.portlet.MockRenderRequest;


/**
 *
 *
 * @author Adam Brandizzi
 */
@PrepareForTest(LocaleUtil.class)
@RunWith(PowerMockRunner.class)
public class DDMFormDisplayContextTest extends PowerMockito {
    @Test
    public void testDDMFormRenderingContextLocaleIsThemeDisplayLocale() throws Exception {
        DDMFormDisplayContext ddmFormDisplayContext = createDDMFormDisplayContext();
        Locale defaultLocale = LocaleUtil.BRAZIL;
        Set<Locale> availableLocales = new HashSet<>();
        availableLocales.add(defaultLocale);
        availableLocales.add(SPAIN);
        DDMForm ddmForm = createDDMForm(availableLocales, defaultLocale);
        _request.addParameter("languageId", LocaleUtil.toLanguageId(SPAIN));
        DDMFormRenderingContext ddmFormRenderingContext = ddmFormDisplayContext.createDDMFormRenderingContext(ddmForm);
        Assert.assertEquals(SPAIN, ddmFormRenderingContext.getLocale());
    }

    @Test
    public void testIsFormAvailableForGuest() throws Exception {
        when(_ddmFormInstanceLocalService.fetchFormInstance(Matchers.anyLong())).thenReturn(mock(DDMFormInstance.class));
        when(_ddmFormInstanceService.fetchFormInstance(Matchers.anyLong())).thenReturn(null);
        DDMFormDisplayContext ddmFormDisplayContext = createDDMFormDisplayContext();
        Assert.assertFalse(ddmFormDisplayContext.isFormAvailable());
    }

    @Test
    public void testIsFormAvailableForLoggedUser() throws Exception {
        when(_ddmFormInstanceLocalService.fetchFormInstance(Matchers.anyLong())).thenReturn(mock(DDMFormInstance.class));
        when(_ddmFormInstanceService.fetchFormInstance(Matchers.anyLong())).thenReturn(mock(DDMFormInstance.class));
        DDMFormDisplayContext ddmFormDisplayContext = createDDMFormDisplayContext();
        Assert.assertTrue(ddmFormDisplayContext.isFormAvailable());
    }

    @Test
    public void testIsSharedFormWithoutPortletSession() throws Exception {
        MockRenderRequest renderRequest = mockRenderRequest();
        Assert.assertNull(renderRequest.getPortletSession(false));
        renderRequest.setParameter("shared", Boolean.TRUE.toString());
        DDMFormDisplayContext createDDMFormDisplayContext = createDDMFormDisplayContext(renderRequest);
        Assert.assertTrue(createDDMFormDisplayContext.isFormShared());
    }

    @Test
    public void testIsSharedFormWithPortletSession() throws Exception {
        MockRenderRequest renderRequest = mockRenderRequest();
        PortletSession portletSession = renderRequest.getPortletSession(true);
        Assert.assertNotNull(portletSession);
        portletSession.setAttribute("shared", Boolean.TRUE);
        DDMFormDisplayContext createDDMFormDisplayContext = createDDMFormDisplayContext(renderRequest);
        Assert.assertTrue(createDDMFormDisplayContext.isFormShared());
    }

    @Mock
    private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

    @Mock
    private DDMFormInstanceService _ddmFormInstanceService;

    @Mock
    private Language _language;

    @Mock
    private MockHttpServletRequest _request;
}

