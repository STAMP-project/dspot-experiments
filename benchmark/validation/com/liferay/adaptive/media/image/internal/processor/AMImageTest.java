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
package com.liferay.adaptive.media.image.internal.processor;


import AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT;
import com.liferay.adaptive.media.image.internal.configuration.AMImageAttributeMapping;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Adolfo P?rez
 */
public class AMImageTest {
    @Test
    public void testGetAttributeDelegatesOnMapping() {
        AMImageAttributeMapping amImageAttributeMapping = Mockito.mock(AMImageAttributeMapping.class);
        AMImage amImage = new AMImage(() -> null, amImageAttributeMapping, URI.create("/"));
        amImage.getValueOptional(AM_IMAGE_ATTRIBUTE_HEIGHT);
        Mockito.verify(amImageAttributeMapping).getValueOptional(AM_IMAGE_ATTRIBUTE_HEIGHT);
    }

    @Test
    public void testGetInputStreamDelegatesOnSupplier() {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Supplier<InputStream> supplier = () -> inputStream;
        AMImageAttributeMapping amImageAttributeMapping = AMImageAttributeMapping.fromProperties(Collections.emptyMap());
        AMImage amImage = new AMImage(supplier, amImageAttributeMapping, URI.create("/"));
        Assert.assertEquals(inputStream, amImage.getInputStream());
    }
}

