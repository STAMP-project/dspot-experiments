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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.compress.CompressionProvider;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class ZIPCompressionOutputStreamTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String PROVIDER_NAME = "Zip";

    public CompressionProviderFactory factory = null;

    public ZIPCompressionOutputStream outStream = null;

    private ByteArrayOutputStream internalStream;

    @Test
    public void testCtor() {
        Assert.assertNotNull(outStream);
    }

    @Test
    public void getCompressionProvider() {
        CompressionProvider provider = outStream.getCompressionProvider();
        Assert.assertEquals(provider.getName(), ZIPCompressionOutputStreamTest.PROVIDER_NAME);
    }

    @Test
    public void testClose() throws IOException {
        CompressionProvider provider = outStream.getCompressionProvider();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outStream = new ZIPCompressionOutputStream(out, provider);
        outStream.close();
    }

    @Test
    public void testAddEntryAndWrite() throws IOException {
        CompressionProvider provider = outStream.getCompressionProvider();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outStream = new ZIPCompressionOutputStream(out, provider);
        outStream.addEntry("./test.zip", null);
        outStream.write("Test".getBytes());
    }

    @Test
    public void directoriesHierarchyIsIgnored() throws Exception {
        outStream.addEntry(ZIPCompressionOutputStreamTest.createFilePath("1", "~", "pentaho", "dir"), "txt");
        outStream.close();
        Map<String, String> map = ZIPCompressionOutputStreamTest.readArchive(internalStream.toByteArray());
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("1.txt", map.keySet().iterator().next());
    }

    @Test
    public void extraZipExtensionIsIgnored() throws Exception {
        outStream.addEntry(ZIPCompressionOutputStreamTest.createFilePath("1.zip", "~", "pentaho", "dir"), "txt");
        outStream.close();
        Map<String, String> map = ZIPCompressionOutputStreamTest.readArchive(internalStream.toByteArray());
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("1.txt", map.keySet().iterator().next());
    }

    @Test
    public void absentExtensionIsOk() throws Exception {
        outStream.addEntry(ZIPCompressionOutputStreamTest.createFilePath("1", "~", "pentaho", "dir"), null);
        outStream.close();
        Map<String, String> map = ZIPCompressionOutputStreamTest.readArchive(internalStream.toByteArray());
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("1", map.keySet().iterator().next());
    }

    @Test
    public void createsWellFormedArchive() throws Exception {
        outStream.addEntry("1", "txt");
        outStream.write("1.txt".getBytes());
        outStream.addEntry("2", "txt");
        outStream.write("2.txt".getBytes());
        outStream.close();
        Map<String, String> map = ZIPCompressionOutputStreamTest.readArchive(internalStream.toByteArray());
        Assert.assertEquals("1.txt", map.remove("1.txt"));
        Assert.assertEquals("2.txt", map.remove("2.txt"));
        Assert.assertTrue(map.isEmpty());
    }
}

