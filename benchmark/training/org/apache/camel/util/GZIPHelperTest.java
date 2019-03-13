/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.camel.converter.IOConverter;
import org.apache.camel.support.GZIPHelper;
import org.junit.Assert;
import org.junit.Test;


public class GZIPHelperTest {
    private static byte[] sampleBytes = new byte[]{ 1, 2, 3, 1, 2, 3 };

    private static String sampleString = "<Hello>World</Hello>";

    @Test
    public void toGZIPInputStreamShouldReturnTheSameInputStream() throws IOException {
        InputStream inputStream = GZIPHelper.uncompressGzip("text", new ByteArrayInputStream(GZIPHelperTest.sampleBytes));
        byte[] bytes = new byte[6];
        inputStream.read(bytes);
        Assert.assertEquals((-1), inputStream.read());
        Assert.assertArrayEquals(GZIPHelperTest.sampleBytes, bytes);
    }

    @Test
    public void toGZIPInputStreamShouldReturnAByteArrayInputStream() throws IOException {
        InputStream inputStream = GZIPHelper.compressGzip("text", GZIPHelperTest.sampleBytes);
        byte[] bytes = IOConverter.toBytes(inputStream);
        Assert.assertArrayEquals(GZIPHelperTest.sampleBytes, bytes);
    }

    @Test
    public void testCompressAndUnCompressData() throws IOException {
        InputStream inputStream = GZIPHelper.compressGzip("gzip", new ByteArrayInputStream(GZIPHelperTest.sampleString.getBytes()));
        Assert.assertNotNull("The inputStream should not be null.", inputStream);
        inputStream = GZIPHelper.uncompressGzip("gzip", inputStream);
        String result = IOConverter.toString(inputStream, null);
        Assert.assertEquals("The result is wrong.", GZIPHelperTest.sampleString, result);
    }

    @Test
    public void testIsGzipMessage() {
        Assert.assertTrue(GZIPHelper.isGzip(createMessageWithContentEncodingHeader("gzip")));
        Assert.assertTrue(GZIPHelper.isGzip(createMessageWithContentEncodingHeader("GZip")));
        Assert.assertFalse(GZIPHelper.isGzip(createMessageWithContentEncodingHeader(null)));
        Assert.assertFalse(GZIPHelper.isGzip(createMessageWithContentEncodingHeader("zip")));
    }

    @Test
    public void isGzipString() {
        Assert.assertTrue(GZIPHelper.isGzip("gzip"));
        Assert.assertTrue(GZIPHelper.isGzip("GZip"));
        Assert.assertFalse(GZIPHelper.isGzip(((String) (null))));
        Assert.assertFalse(GZIPHelper.isGzip("zip"));
    }
}

