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


import com.liferay.talend.runtime.apio.operation.Operation;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Zolt?n Tak?cs
 */
public class ApioSingleModelTest {
    @Test
    public void testGetResourceOperations() {
        List<Operation> operations = ApioSingleModelTest._apioJsonLDResource.getResourceOperations();
        Assert.assertThat(operations.size(), CoreMatchers.equalTo(2));
        Operation operation = operations.get(0);
        String method = operation.getMethod();
        String expects = operation.getExpects();
        Assert.assertThat(method, CoreMatchers.equalTo("DELETE"));
        Assert.assertThat(expects, CoreMatchers.is(ApioSingleModelTest._EMPTY_STRING));
        operation = operations.get(1);
        method = operation.getMethod();
        expects = operation.getExpects();
        Assert.assertThat(method, CoreMatchers.equalTo("PUT"));
        Assert.assertThat(expects, CoreMatchers.equalTo("https://apiosample.wedeploy.io/f/u/people"));
    }

    @Test
    public void testIsSingleModel() {
        Assert.assertThat(ApioSingleModelTest._apioJsonLDResource.isSingleModel(), CoreMatchers.is(true));
    }

    private static final String _EMPTY_STRING = "";

    private static ApioSingleModel _apioJsonLDResource;
}

