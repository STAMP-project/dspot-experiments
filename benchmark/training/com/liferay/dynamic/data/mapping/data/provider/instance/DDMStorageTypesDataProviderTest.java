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
package com.liferay.dynamic.data.mapping.data.provider.instance;


import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterTracker;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;


/**
 *
 *
 * @author Leonardo Barros
 */
@RunWith(MockitoJUnitRunner.class)
public class DDMStorageTypesDataProviderTest extends PowerMockito {
    @Test(expected = UnsupportedOperationException.class)
    public void testGetSettings() {
        _ddmStorageTypesDataProvider.getSettings();
    }

    @Test
    public void testMultipleStorageAdapter() throws Exception {
        Set<String> expectedSet = new TreeSet() {
            {
                add("json");
                add("txt");
                add("xml");
            }
        };
        _testStorageTypes(expectedSet);
    }

    @Test
    public void testSingleStorageAdapter() throws Exception {
        Set<String> expectedSet = new TreeSet() {
            {
                add("json");
            }
        };
        _testStorageTypes(expectedSet);
    }

    @Mock
    private DDMStorageAdapterTracker _ddmStorageAdapterTracker;

    private DDMStorageTypesDataProvider _ddmStorageTypesDataProvider;
}

