/**
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2018 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.utils;


import Settings.KEYS.ENGINE_VERSION_CHECK_URL;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jeremy Long
 */
public class HttpResourceConnectionTest extends BaseTest {
    /**
     * Test of fetch method, of class HttpResourceConnection.
     *
     * @throws Exception
     * 		thrown when an exception occurs.
     */
    @Test
    public void testFetch() throws Exception {
        URL url = new URL(getSettings().getString(ENGINE_VERSION_CHECK_URL));
        try (HttpResourceConnection resource = new HttpResourceConnection(getSettings())) {
            InputStream in = resource.fetch(url);
            byte[] read = new byte[90];
            in.read(read);
            String text = new String(read, StandardCharsets.UTF_8);
            Assert.assertTrue(text.matches("^\\d+\\.\\d+\\.\\d+.*"));
            Assert.assertFalse(resource.isClosed());
        }
    }

    /**
     * Test of close method, of class HttpResourceConnection.
     */
    @Test
    public void testClose() {
        HttpResourceConnection instance = new HttpResourceConnection(getSettings());
        instance.close();
        Assert.assertTrue(instance.isClosed());
    }

    /**
     * Test of getLastModified method, of class HttpResourceConnection.
     */
    @Test
    public void testGetLastModified() throws Exception {
        URL url = new URL(getSettings().getString(ENGINE_VERSION_CHECK_URL));
        HttpResourceConnection instance = new HttpResourceConnection(getSettings());
        long timestamp = instance.getLastModified(url);
        Assert.assertTrue("timestamp equal to zero?", (timestamp > 0));
    }

    /**
     * Test of isClosed method, of class HttpResourceConnection.
     */
    @Test
    public void testIsClosed() throws Exception {
        HttpResourceConnection resource = null;
        try {
            URL url = new URL(getSettings().getString(ENGINE_VERSION_CHECK_URL));
            resource = new HttpResourceConnection(getSettings());
            resource.fetch(url);
            Assert.assertFalse(resource.isClosed());
        } finally {
            if (resource != null) {
                resource.close();
                Assert.assertTrue(resource.isClosed());
            }
        }
    }
}

