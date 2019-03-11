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


import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;
import javax.portlet.RenderRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Adam Brandizzi
 */
@PrepareForTest({ LocaleUtil.class, ResourceBundleUtil.class, ResourceBundleLoaderUtil.class, ServiceTrackerCollections.class })
@RunWith(PowerMockRunner.class)
public class DDMFormAdminDisplayContextTest extends PowerMockito {
    @Test
    public void testGetFormURLForRestrictedFormInstance() throws Exception {
        setRenderRequestParamenter("formInstanceId", String.valueOf(DDMFormAdminDisplayContextTest._RESTRICTED_FORM_INSTANCE_ID));
        String formURL = _ddmFormAdminDisplayContext.getFormURL();
        Assert.assertEquals(getRestrictedFormURL(), formURL);
    }

    @Test
    public void testGetFormURLForSharedFormInstance() throws Exception {
        setRenderRequestParamenter("formInstanceId", String.valueOf(DDMFormAdminDisplayContextTest._SHARED_FORM_INSTANCE_ID));
        String formURL = _ddmFormAdminDisplayContext.getFormURL();
        Assert.assertEquals(getSharedFormURL(), formURL);
    }

    @Test
    public void testGetRestrictedFormURL() throws Exception {
        String restrictedFormURL = _ddmFormAdminDisplayContext.getRestrictedFormURL();
        Assert.assertEquals(getRestrictedFormURL(), restrictedFormURL);
    }

    @Test
    public void testGetSharedFormURL() throws Exception {
        String sharedFormURL = _ddmFormAdminDisplayContext.getSharedFormURL();
        Assert.assertEquals(getSharedFormURL(), sharedFormURL);
    }

    private static final String _FORM_APPLICATION_PATH = (Portal.FRIENDLY_URL_SEPARATOR) + "form/";

    private static final String _GROUP_FRIENDLY_URL_PATH = "/forms";

    private static final String _PORTAL_URL = "http://localhost:9999";

    private static final String _PRIVATE_FRIENDLY_URL_PATH = "/group";

    private static final String _PRIVATE_PAGE_FRIENDLY_URL_PATH = "/shared";

    private static final String _PUBLIC_FRIENDLY_URL_PATH = "/web";

    private static final String _PUBLIC_PAGE_FRIENDLY_URL_PATH = "/shared";

    private static final long _RESTRICTED_FORM_INSTANCE_ID = RandomTestUtil.randomLong();

    private static final long _SHARED_FORM_INSTANCE_ID = RandomTestUtil.randomLong();

    private DDMFormAdminDisplayContext _ddmFormAdminDisplayContext;

    private RenderRequest _renderRequest;

    @Mock
    private ServiceTrackerMap<String, ResourceBundleLoader> _serviceTrackerMap;
}

