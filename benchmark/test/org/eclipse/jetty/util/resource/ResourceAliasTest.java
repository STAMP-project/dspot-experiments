/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.resource;


import java.io.File;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ResourceAliasTest {
    static File __dir;

    /* ------------------------------------------------------------ */
    @Test
    public void testNullCharEndingFilename() throws Exception {
        File file = new File(ResourceAliasTest.__dir, "test.txt");
        Assertions.assertFalse(file.exists());
        Assertions.assertTrue(file.createNewFile());
        Assertions.assertTrue(file.exists());
        File file0 = new File(ResourceAliasTest.__dir, "test.txt\u0000");
        if (!(file0.exists()))
            return;
        // this file system does not suffer this problem

        Assertions.assertTrue(file0.exists());// This is an alias!

        Resource dir = Resource.newResource(ResourceAliasTest.__dir);
        // Test not alias paths
        Resource resource = Resource.newResource(file);
        Assertions.assertTrue(resource.exists());
        Assertions.assertNull(resource.getAlias());
        resource = Resource.newResource(file.getAbsoluteFile());
        Assertions.assertTrue(resource.exists());
        Assertions.assertNull(resource.getAlias());
        resource = Resource.newResource(file.toURI());
        Assertions.assertTrue(resource.exists());
        Assertions.assertNull(resource.getAlias());
        resource = Resource.newResource(file.toURI().toString());
        Assertions.assertTrue(resource.exists());
        Assertions.assertNull(resource.getAlias());
        resource = dir.addPath("test.txt");
        Assertions.assertTrue(resource.exists());
        Assertions.assertNull(resource.getAlias());
        // Test alias paths
        resource = Resource.newResource(file0);
        Assertions.assertTrue(resource.exists());
        Assertions.assertNotNull(resource.getAlias());
        resource = Resource.newResource(file0.getAbsoluteFile());
        Assertions.assertTrue(resource.exists());
        Assertions.assertNotNull(resource.getAlias());
        resource = Resource.newResource(file0.toURI());
        Assertions.assertTrue(resource.exists());
        Assertions.assertNotNull(resource.getAlias());
        resource = Resource.newResource(file0.toURI().toString());
        Assertions.assertTrue(resource.exists());
        Assertions.assertNotNull(resource.getAlias());
        try {
            resource = dir.addPath("test.txt\u0000");
            Assertions.assertTrue(resource.exists());
            Assertions.assertNotNull(resource.getAlias());
        } catch (MalformedURLException e) {
            Assertions.assertTrue(true);
        }
    }
}

