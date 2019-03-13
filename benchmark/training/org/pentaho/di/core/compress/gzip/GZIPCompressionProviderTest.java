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
package org.pentaho.di.core.compress.gzip;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class GZIPCompressionProviderTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String PROVIDER_NAME = "GZip";

    public CompressionProviderFactory factory = null;

    @Test
    public void testCtor() {
        GZIPCompressionProvider ncp = new GZIPCompressionProvider();
        Assert.assertNotNull(ncp);
    }

    @Test
    public void testGetName() {
        GZIPCompressionProvider provider = ((GZIPCompressionProvider) (factory.getCompressionProviderByName(GZIPCompressionProviderTest.PROVIDER_NAME)));
        Assert.assertNotNull(provider);
        Assert.assertEquals(GZIPCompressionProviderTest.PROVIDER_NAME, provider.getName());
    }

    @Test
    public void testGetProviderAttributes() {
        GZIPCompressionProvider provider = ((GZIPCompressionProvider) (factory.getCompressionProviderByName(GZIPCompressionProviderTest.PROVIDER_NAME)));
        Assert.assertEquals("GZIP compression", provider.getDescription());
        Assert.assertTrue(provider.supportsInput());
        Assert.assertTrue(provider.supportsOutput());
        Assert.assertEquals("gz", provider.getDefaultExtension());
    }

    @Test
    public void testCreateInputStream() throws IOException {
        GZIPCompressionProvider provider = ((GZIPCompressionProvider) (factory.getCompressionProviderByName(GZIPCompressionProviderTest.PROVIDER_NAME)));
        // Create an in-memory GZIP output stream for use by the input stream (to avoid exceptions)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        byte[] testBytes = "Test".getBytes();
        gos.write(testBytes);
        ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());
        // Test stream creation paths
        GZIPInputStream gis = new GZIPInputStream(in);
        in = new ByteArrayInputStream(baos.toByteArray());
        GZIPCompressionInputStream ncis = provider.createInputStream(in);
        Assert.assertNotNull(ncis);
        GZIPCompressionInputStream ncis2 = provider.createInputStream(gis);
        Assert.assertNotNull(ncis2);
    }

    @Test
    public void testCreateOutputStream() throws IOException {
        GZIPCompressionProvider provider = ((GZIPCompressionProvider) (factory.getCompressionProviderByName(GZIPCompressionProviderTest.PROVIDER_NAME)));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(out);
        GZIPCompressionOutputStream outStream = new GZIPCompressionOutputStream(out, provider);
        Assert.assertNotNull(outStream);
        out = new ByteArrayOutputStream();
        GZIPCompressionOutputStream ncis = provider.createOutputStream(out);
        Assert.assertNotNull(ncis);
        GZIPCompressionOutputStream ncis2 = provider.createOutputStream(gos);
        Assert.assertNotNull(ncis2);
    }
}

