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
package com.liferay.adaptive.media.image.internal.storage;


import com.liferay.portal.kernel.test.util.RandomTestUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Adolfo P?rez
 */
public class ImageStorageTest {
    @Test
    public void testGetConfigurationEntryPath() {
        String configurationUuid = RandomTestUtil.randomString();
        String configurationEntryPath = _imageStorage.getConfigurationEntryPath(configurationUuid);
        Assert.assertEquals(("adaptive/" + configurationUuid), configurationEntryPath);
    }

    private final ImageStorage _imageStorage = new ImageStorage();
}

