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
package com.liferay.server.manager.internal.executor;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class ExecutorPathResolverTest {
    @Test
    public void testGetExactMatchExecutor() {
        Assert.assertEquals("/", _executorPathResolver.getExecutorPath("/"));
        Assert.assertEquals("/server", _executorPathResolver.getExecutorPath("/server"));
        Assert.assertEquals("/plugins", _executorPathResolver.getExecutorPath("/plugins"));
    }

    @Test
    public void testGetNextExecutorsPaths() {
        Assert.assertEquals(Arrays.asList("/plugins", "/server"), _executorPathResolver.getNextExecutorsPaths(null));
        Assert.assertEquals(Arrays.asList("/server/log"), _executorPathResolver.getNextExecutorsPaths("/server"));
        Assert.assertEquals(Arrays.asList("/server/log/error", "/server/log/output"), _executorPathResolver.getNextExecutorsPaths("/server/log/xyz"));
    }

    @Test
    public void testGetNullPathMatchExecutor() {
        Assert.assertEquals("/", _executorPathResolver.getExecutorPath(null));
        Assert.assertEquals("/", _executorPathResolver.getExecutorPath(""));
    }

    @Test
    public void testGetPartialMatchExecutor() {
        Assert.assertEquals("/", _executorPathResolver.getExecutorPath("/xyz"));
        Assert.assertEquals("/server/log", _executorPathResolver.getExecutorPath("/server/log/xyz"));
        Assert.assertEquals("/plugins", _executorPathResolver.getExecutorPath("/plugins/xyz"));
    }

    private ExecutorPathResolver _executorPathResolver;
}

