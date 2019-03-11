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
package com.liferay.talend.runtime.apio.jsonld;


import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Zolt?n Tak?cs
 */
public class ApioUtilsTest {
    @Test
    public void testGetTypeCoercionTermKeys() {
        JsonNode contextJsonNode = ApioUtilsTest._apioJsonLDResource.getContextJsonNode();
        List<String> typeCoercionTermKeys = ApioUtils.getTypeCoercionTermKeys(contextJsonNode);
        Assert.assertThat(typeCoercionTermKeys.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(typeCoercionTermKeys, CoreMatchers.hasItems("blogPosts", "folder"));
    }

    @Test
    public void testHasValueOf() {
        JsonNode typeJsonNode = ApioUtilsTest._apioJsonLDResource.getTypeJsonNode();
        Assert.assertThat(ApioUtils.hasValueOf("BlogPosting", typeJsonNode), CoreMatchers.is(false));
        Assert.assertThat(ApioUtils.hasValueOf("WebSite", typeJsonNode), CoreMatchers.is(true));
    }

    @Test
    public void testIsSingleModel() {
        Assert.assertThat(ApioUtilsTest._apioJsonLDResource.isSingleModel(), CoreMatchers.is(true));
    }

    private static ApioSingleModel _apioJsonLDResource;
}

