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
package org.pentaho.di.core.compress.zip;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class ZIPCompressionProviderTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String PROVIDER_NAME = "Zip";

    public CompressionProviderFactory factory = null;

    @Test
    public void testCtor() {
        ZIPCompressionProvider ncp = new ZIPCompressionProvider();
        Assert.assertNotNull(ncp);
    }

    @Test
    public void testGetName() {
        ZIPCompressionProvider provider = ((ZIPCompressionProvider) (factory.getCompressionProviderByName(ZIPCompressionProviderTest.PROVIDER_NAME)));
        Assert.assertNotNull(provider);
        Assert.assertEquals(ZIPCompressionProviderTest.PROVIDER_NAME, provider.getName());
    }

    @Test
    public void testGetProviderAttributes() {
        ZIPCompressionProvider provider = ((ZIPCompressionProvider) (factory.getCompressionProviderByName(ZIPCompressionProviderTest.PROVIDER_NAME)));
        Assert.assertEquals("ZIP compression", provider.getDescription());
        Assert.assertTrue(provider.supportsInput());
        Assert.assertTrue(provider.supportsOutput());
        Assert.assertEquals("zip", provider.getDefaultExtension());
    }

    @Test
    public void testCreateInputStream() throws IOException {
        ZIPCompressionProvider provider = ((ZIPCompressionProvider) (factory.getCompressionProviderByName(ZIPCompressionProviderTest.PROVIDER_NAME)));
        ByteArrayInputStream in = new ByteArrayInputStream("Test".getBytes());
        ZipInputStream zis = new ZipInputStream(in);
        ZIPCompressionInputStream inStream = new ZIPCompressionInputStream(in, provider);
        Assert.assertNotNull(inStream);
        ZIPCompressionInputStream ncis = provider.createInputStream(in);
        Assert.assertNotNull(ncis);
        ZIPCompressionInputStream ncis2 = provider.createInputStream(zis);
        Assert.assertNotNull(ncis2);
    }

    @Test
    public void testCreateOutputStream() throws IOException {
        ZIPCompressionProvider provider = ((ZIPCompressionProvider) (factory.getCompressionProviderByName(ZIPCompressionProviderTest.PROVIDER_NAME)));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(out);
        ZIPCompressionOutputStream outStream = new ZIPCompressionOutputStream(out, provider);
        Assert.assertNotNull(outStream);
        ZIPCompressionOutputStream ncis = provider.createOutputStream(out);
        Assert.assertNotNull(ncis);
        ZIPCompressionOutputStream ncis2 = provider.createOutputStream(zos);
        Assert.assertNotNull(ncis2);
    }
}

