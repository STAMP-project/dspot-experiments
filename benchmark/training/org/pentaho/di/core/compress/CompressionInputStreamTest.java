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
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class CompressionInputStreamTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String PROVIDER_NAME = "None";

    public CompressionProviderFactory factory = null;

    public CompressionInputStream inStream = null;

    @Test
    public void testCtor() {
        Assert.assertNotNull(inStream);
    }

    @Test
    public void getCompressionProvider() {
        CompressionProvider provider = inStream.getCompressionProvider();
        Assert.assertEquals(provider.getName(), CompressionInputStreamTest.PROVIDER_NAME);
    }

    @Test
    public void testNextEntry() throws IOException {
        Assert.assertNull(inStream.nextEntry());
    }

    @Test
    public void testClose() throws IOException {
        CompressionProvider provider = inStream.getCompressionProvider();
        ByteArrayInputStream in = CompressionInputStreamTest.createTestInputStream();
        inStream = new CompressionInputStreamTest.DummyCompressionIS(in, provider);
        inStream.close();
    }

    @Test
    public void testRead() throws IOException {
        CompressionProvider provider = inStream.getCompressionProvider();
        ByteArrayInputStream in = CompressionInputStreamTest.createTestInputStream();
        inStream = new CompressionInputStreamTest.DummyCompressionIS(in, provider);
        Assert.assertEquals(inStream.available(), inStream.read(new byte[100], 0, inStream.available()));
    }

    @Test
    public void delegatesReadBuffer() throws Exception {
        ByteArrayInputStream in = CompressionInputStreamTest.createTestInputStream();
        in = Mockito.spy(in);
        inStream = new CompressionInputStreamTest.DummyCompressionIS(in, inStream.getCompressionProvider());
        inStream.read(new byte[16]);
        Mockito.verify(in).read(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void delegatesReadBufferWithParams() throws Exception {
        ByteArrayInputStream in = CompressionInputStreamTest.createTestInputStream();
        in = Mockito.spy(in);
        inStream = new CompressionInputStreamTest.DummyCompressionIS(in, inStream.getCompressionProvider());
        inStream.read(new byte[16], 0, 16);
        Mockito.verify(in).read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    private static class DummyCompressionIS extends CompressionInputStream {
        public DummyCompressionIS(InputStream in, CompressionProvider provider) {
            super(in, provider);
        }
    }
}

