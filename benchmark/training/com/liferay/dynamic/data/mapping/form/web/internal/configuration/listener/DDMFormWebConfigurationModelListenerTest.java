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
package com.liferay.dynamic.data.mapping.form.web.internal.configuration.listener;


import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import java.util.Dictionary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Pedro Queiroz
 */
@PrepareForTest(ResourceBundleUtil.class)
@RunWith(PowerMockRunner.class)
public class DDMFormWebConfigurationModelListenerTest extends PowerMockito {
    @Test(expected = ConfigurationModelListenerException.class)
    public void testNegativeAutosaveIntervalShouldThrowException() throws ConfigurationModelListenerException {
        Dictionary<String, Object> properties = new com.liferay.portal.kernel.util.HashMapDictionary();
        properties.put("autosaveInterval", "-1");
        _ddmFormWebConfigurationModelListener.onBeforeSave(null, properties);
    }

    private DDMFormWebConfigurationModelListener _ddmFormWebConfigurationModelListener;
}

