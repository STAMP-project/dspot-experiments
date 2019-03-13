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
package com.liferay.dynamic.data.mapping.storage;


import LocaleUtil.SPAIN;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.storage.impl.GeolocationFieldRenderer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.JavaDetector;
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
 * @author Adolfo P?rez
 */
@PrepareForTest({ LanguageUtil.class, JSONFactoryUtil.class })
@RunWith(PowerMockRunner.class)
public class GeolocationFieldRendererTest extends PowerMockito {
    @Test
    public void testRenderedValuesFollowLocaleConventions() {
        FieldRenderer fieldRenderer = new GeolocationFieldRenderer();
        if (JavaDetector.isJDK8()) {
            Assert.assertEquals("Latitud: 9,877, Longitud: 1,234", fieldRenderer.render(createField(), SPAIN));
        } else {
            Assert.assertEquals("Latitud: 9,876, Longitud: 1,234", fieldRenderer.render(createField(), SPAIN));
        }
    }

    @Test
    public void testRenderedValuesShouldHave3DecimalPlaces() {
        FieldRenderer fieldRenderer = new GeolocationFieldRenderer();
        if (JavaDetector.isJDK8()) {
            Assert.assertEquals("Latitude: 9.877, Longitude: 1.234", fieldRenderer.render(createField(), US));
        } else {
            Assert.assertEquals("Latitude: 9.876, Longitude: 1.234", fieldRenderer.render(createField(), US));
        }
    }

    @Mock
    private Language _language;
}

