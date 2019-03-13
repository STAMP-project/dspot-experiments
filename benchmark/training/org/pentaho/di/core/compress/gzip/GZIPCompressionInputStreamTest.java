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


import java.io.IOException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.compress.CompressionProvider;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class GZIPCompressionInputStreamTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String PROVIDER_NAME = "GZip";

    protected CompressionProviderFactory factory = null;

    protected GZIPCompressionInputStream inStream = null;

    protected CompressionProvider provider = null;

    @Test
    public void testCtor() {
        Assert.assertNotNull(inStream);
    }

    @Test
    public void getZIPCompressionProvider() {
        CompressionProvider provider = inStream.getCompressionProvider();
        Assert.assertEquals(provider.getName(), GZIPCompressionInputStreamTest.PROVIDER_NAME);
    }

    @Test
    public void testNextEntry() throws IOException {
        Assert.assertNull(inStream.nextEntry());
    }

    @Test
    public void testClose() throws IOException {
        inStream = new GZIPCompressionInputStream(createGZIPInputStream(), provider) {};
        inStream.close();
    }

    @Test
    public void testRead() throws IOException {
        inStream = new GZIPCompressionInputStream(createGZIPInputStream(), provider) {};
        inStream.read(new byte[100], 0, inStream.available());
    }
}

