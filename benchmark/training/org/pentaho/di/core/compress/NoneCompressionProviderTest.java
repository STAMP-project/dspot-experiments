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
package org.pentaho.di.core.compress;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.compress.NoneCompressionProvider.NoneCompressionInputStream;
import org.pentaho.di.core.compress.NoneCompressionProvider.NoneCompressionOutputStream;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class NoneCompressionProviderTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String PROVIDER_NAME = "None";

    public CompressionProviderFactory factory = null;

    @Test
    public void testCtor() {
        NoneCompressionProvider ncp = new NoneCompressionProvider();
        Assert.assertNotNull(ncp);
    }

    @Test
    public void testGetName() {
        NoneCompressionProvider provider = ((NoneCompressionProvider) (factory.getCompressionProviderByName(NoneCompressionProviderTest.PROVIDER_NAME)));
        Assert.assertNotNull(provider);
        Assert.assertEquals(NoneCompressionProviderTest.PROVIDER_NAME, provider.getName());
    }

    @Test
    public void testGetProviderAttributes() {
        NoneCompressionProvider provider = ((NoneCompressionProvider) (factory.getCompressionProviderByName(NoneCompressionProviderTest.PROVIDER_NAME)));
        Assert.assertEquals("No compression", provider.getDescription());
        Assert.assertTrue(provider.supportsInput());
        Assert.assertTrue(provider.supportsOutput());
        Assert.assertNull(provider.getDefaultExtension());
    }

    @Test
    public void testCreateInputStream() throws IOException {
        NoneCompressionProvider provider = ((NoneCompressionProvider) (factory.getCompressionProviderByName(NoneCompressionProviderTest.PROVIDER_NAME)));
        ByteArrayInputStream in = new ByteArrayInputStream("Test".getBytes());
        NoneCompressionInputStream inStream = new NoneCompressionInputStream(in, provider);
        Assert.assertNotNull(inStream);
        NoneCompressionInputStream ncis = ((NoneCompressionInputStream) (provider.createInputStream(in)));
        Assert.assertNotNull(ncis);
    }

    @Test
    public void testCreateOutputStream() throws IOException {
        NoneCompressionProvider provider = ((NoneCompressionProvider) (factory.getCompressionProviderByName(NoneCompressionProviderTest.PROVIDER_NAME)));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NoneCompressionOutputStream outStream = new NoneCompressionOutputStream(out, provider);
        Assert.assertNotNull(outStream);
        NoneCompressionOutputStream ncis = ((NoneCompressionOutputStream) (provider.createOutputStream(out)));
        Assert.assertNotNull(ncis);
    }
}

