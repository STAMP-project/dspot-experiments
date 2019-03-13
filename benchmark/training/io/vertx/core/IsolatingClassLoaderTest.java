/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core;


import io.vertx.core.impl.IsolatingClassLoader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link io.vertx.core.impl.IsolatingClassLoader}
 */
public class IsolatingClassLoaderTest {
    private String resourceName = "resource.json";

    private URL url1;

    private URL url2;

    private URL url3;

    private URLClassLoader ucl;

    private IsolatingClassLoader icl;

    @Test
    public void testGetResource() throws Exception {
        URL resource = ucl.getResource(resourceName);
        checkResource(url2, resource);
        resource = icl.getResource(resourceName);
        checkResource(url1, resource);
    }

    @Test
    public void testGetResourceNull() throws Exception {
        resourceName = "null_resource";
        URL resource = ucl.getResource(resourceName);
        Assert.assertNull(resource);
        resource = icl.getResource(resourceName);
        Assert.assertNull(resource);
    }

    @Test
    public void testGetResources() throws Exception {
        Enumeration<URL> resources = ucl.getResources(resourceName);
        List<URL> list = Collections.list(resources);
        Assert.assertEquals(2, list.size());
        checkResource(url2, list.get(0));
        checkResource(url3, list.get(1));
        resources = icl.getResources(resourceName);
        list = Collections.list(resources);
        Assert.assertEquals(3, list.size());
        checkResource(url1, list.get(0));
        checkResource(url2, list.get(1));
        checkResource(url3, list.get(2));
    }

    @Test
    public void testGetResourcesNull() throws Exception {
        resourceName = "null_resource";
        Enumeration<URL> resources = ucl.getResources(resourceName);
        List<URL> list = Collections.list(resources);
        Assert.assertEquals(0, list.size());
        resources = icl.getResources(resourceName);
        list = Collections.list(resources);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testGetResourceAsStream() throws Exception {
        testGetResourceAsStream(2, ucl);
        testGetResourceAsStream(1, icl);
    }
}

