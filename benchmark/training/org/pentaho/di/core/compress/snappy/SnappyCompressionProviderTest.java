/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.compress.snappy;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.xerial.snappy.SnappyInputStream;


public class SnappyCompressionProviderTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String PROVIDER_NAME = "Snappy";

    public CompressionProviderFactory factory = null;

    @Test
    public void testCtor() {
        SnappyCompressionProvider ncp = new SnappyCompressionProvider();
        Assert.assertNotNull(ncp);
    }

    @Test
    public void testGetName() {
        SnappyCompressionProvider provider = ((SnappyCompressionProvider) (factory.getCompressionProviderByName(SnappyCompressionProviderTest.PROVIDER_NAME)));
        Assert.assertNotNull(provider);
        Assert.assertEquals(SnappyCompressionProviderTest.PROVIDER_NAME, provider.getName());
    }

    @Test
    public void testGetProviderAttributes() {
        SnappyCompressionProvider provider = ((SnappyCompressionProvider) (factory.getCompressionProviderByName(SnappyCompressionProviderTest.PROVIDER_NAME)));
        Assert.assertEquals("Snappy compression", provider.getDescription());
        Assert.assertTrue(provider.supportsInput());
        Assert.assertTrue(provider.supportsOutput());
        Assert.assertNull(provider.getDefaultExtension());
    }

    @Test
    public void testCreateInputStream() throws IOException {
        SnappyCompressionProvider provider = ((SnappyCompressionProvider) (factory.getCompressionProviderByName(SnappyCompressionProviderTest.PROVIDER_NAME)));
        SnappyInputStream in = createSnappyInputStream();
        SnappyCompressionInputStream inStream = new SnappyCompressionInputStream(in, provider);
        Assert.assertNotNull(inStream);
        SnappyCompressionInputStream ncis = provider.createInputStream(in);
        Assert.assertNotNull(ncis);
    }

    @Test
    public void testCreateOutputStream() throws IOException {
        SnappyCompressionProvider provider = ((SnappyCompressionProvider) (factory.getCompressionProviderByName(SnappyCompressionProviderTest.PROVIDER_NAME)));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SnappyCompressionOutputStream outStream = new SnappyCompressionOutputStream(out, provider);
        Assert.assertNotNull(outStream);
        SnappyCompressionOutputStream ncis = provider.createOutputStream(out);
        Assert.assertNotNull(ncis);
    }
}

