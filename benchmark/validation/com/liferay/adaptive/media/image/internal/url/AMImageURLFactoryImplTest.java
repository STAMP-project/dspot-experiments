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
package com.liferay.adaptive.media.image.internal.url;


import com.liferay.adaptive.media.AMURIResolver;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.internal.configuration.AMImageConfigurationEntryImpl;
import com.liferay.portal.kernel.repository.model.FileVersion;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author Alejandro Tard?n
 */
@RunWith(MockitoJUnitRunner.class)
public class AMImageURLFactoryImplTest {
    @Test
    public void testCreatesURLForFileEntry() throws Exception {
        URI uri = _amImageURLFactory.createFileEntryURL(_fileVersion, _amImageConfigurationEntry);
        Assert.assertEquals(("prefix/image/1/theUuid/fileName?t=" + (_modifiedDate.getTime())), uri.toString());
    }

    @Test
    public void testCreatesURLForFileVersion() throws Exception {
        URI uri = _amImageURLFactory.createFileVersionURL(_fileVersion, _amImageConfigurationEntry);
        Assert.assertEquals("prefix/image/1/2/theUuid/fileName", uri.toString());
    }

    private static final String _UUID = "theUuid";

    private final AMImageConfigurationEntry _amImageConfigurationEntry = new AMImageConfigurationEntryImpl("small", AMImageURLFactoryImplTest._UUID, new HashMap());

    private final AMImageURLFactoryImpl _amImageURLFactory = new AMImageURLFactoryImpl();

    @Mock
    private AMURIResolver _amURIResolver;

    @Mock
    private FileVersion _fileVersion;

    private final Date _modifiedDate = new Date();
}

