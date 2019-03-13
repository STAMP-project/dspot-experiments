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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class CompressionOutputStreamTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String PROVIDER_NAME = "None";

    public CompressionProviderFactory factory = null;

    public CompressionOutputStream outStream = null;

    @Test
    public void testCtor() {
        Assert.assertNotNull(outStream);
    }

    @Test
    public void getCompressionProvider() {
        CompressionProvider provider = outStream.getCompressionProvider();
        Assert.assertEquals(provider.getName(), CompressionOutputStreamTest.PROVIDER_NAME);
    }

    @Test
    public void testClose() throws IOException {
        CompressionProvider provider = outStream.getCompressionProvider();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outStream = new CompressionOutputStreamTest.DummyCompressionOS(out, provider);
        outStream.close();
    }

    @Test
    public void testWrite() throws IOException {
        CompressionProvider provider = outStream.getCompressionProvider();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outStream = new CompressionOutputStreamTest.DummyCompressionOS(out, provider);
        outStream.write("Test".getBytes());
    }

    @Test
    public void testAddEntry() throws IOException {
        CompressionProvider provider = outStream.getCompressionProvider();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outStream = new CompressionOutputStreamTest.DummyCompressionOS(out, provider);
        outStream.addEntry(null, null);
    }

    private static class DummyCompressionOS extends CompressionOutputStream {
        public DummyCompressionOS(OutputStream out, CompressionProvider provider) {
            super(out, provider);
        }
    }
}

