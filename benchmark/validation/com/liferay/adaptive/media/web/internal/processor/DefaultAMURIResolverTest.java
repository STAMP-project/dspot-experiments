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
package com.liferay.adaptive.media.web.internal.processor;


import AMWebConstants.SERVLET_PATH;
import com.liferay.adaptive.media.AMURIResolver;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Adolfo P?rez
 */
public class DefaultAMURIResolverTest {
    @Test
    public void testMediaURIWhenPathDoesNotEndInSlash() {
        String pathModule = (StringPool.SLASH) + (RandomTestUtil.randomString());
        Mockito.when(_portal.getPathModule()).thenReturn(pathModule);
        URI relativeURI = URI.create(RandomTestUtil.randomString());
        URI uri = _amURIResolver.resolveURI(relativeURI);
        String uriString = uri.toString();
        Assert.assertTrue(uriString, uriString.contains(pathModule));
        Assert.assertTrue(uriString, uriString.contains(SERVLET_PATH));
        Assert.assertTrue(uriString, uriString.contains(relativeURI.toString()));
    }

    @Test
    public void testMediaURIWhenPathEndsInSlash() {
        String pathModule = ((StringPool.SLASH) + (RandomTestUtil.randomString())) + (StringPool.SLASH);
        Mockito.when(_portal.getPathModule()).thenReturn(pathModule);
        URI relativeURI = URI.create(RandomTestUtil.randomString());
        URI uri = _amURIResolver.resolveURI(relativeURI);
        String uriString = uri.toString();
        Assert.assertTrue(uriString, uriString.contains(pathModule));
        Assert.assertTrue(uriString, uriString.contains(SERVLET_PATH));
        Assert.assertTrue(uriString, uriString.contains(relativeURI.toString()));
    }

    private final AMURIResolver _amURIResolver = new DefaultAMURIResolver();

    private final Portal _portal = Mockito.mock(Portal.class);
}

