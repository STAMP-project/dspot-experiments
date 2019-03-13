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
package org.apache.camel.converter.stream;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.StreamCache;
import org.apache.camel.converter.IOConverter;
import org.apache.camel.util.IOHelper;
import org.junit.Assert;
import org.junit.Test;


public class CachedOutputStreamTest extends ContextTestSupport {
    private static final String TEST_STRING = "This is a test string and it has enough" + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa ";

    private Exchange exchange;

    @Test
    public void testCachedStreamAccessStreamWhenExchangeOnCompletion() throws Exception {
        context.start();
        CachedOutputStream cos = new CachedOutputStream(exchange, false);
        cos.write(CachedOutputStreamTest.TEST_STRING.getBytes("UTF-8"));
        File file = new File("target/cachedir");
        String[] files = file.list();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        Assert.assertTrue("The file name should start with cos", files[0].startsWith("cos"));
        InputStream is = cos.getWrappedInputStream();
        exchange.getUnitOfWork().done(exchange);
        String temp = CachedOutputStreamTest.toString(is);
        Assert.assertEquals("Get a wrong stream content", temp, CachedOutputStreamTest.TEST_STRING);
        IOHelper.close(is);
        files = file.list();
        Assert.assertEquals("we should have a temp file", 0, files.length);
        IOHelper.close(cos);
    }

    @Test
    public void testCacheStreamToFileAndCloseStream() throws Exception {
        context.start();
        CachedOutputStream cos = new CachedOutputStream(exchange);
        cos.write(CachedOutputStreamTest.TEST_STRING.getBytes("UTF-8"));
        File file = new File("target/cachedir");
        String[] files = file.list();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        Assert.assertTrue("The file name should start with cos", files[0].startsWith("cos"));
        StreamCache cache = cos.newStreamCache();
        Assert.assertTrue("Should get the FileInputStreamCache", (cache instanceof FileInputStreamCache));
        String temp = CachedOutputStreamTest.toString(((InputStream) (cache)));
        ((InputStream) (cache)).close();
        files = file.list();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        Assert.assertEquals("Cached a wrong file", temp, CachedOutputStreamTest.TEST_STRING);
        exchange.getUnitOfWork().done(exchange);
        try {
            cache.reset();
            // The stream is closed, so the temp file is gone.
            Assert.fail("we expect the exception here");
        } catch (Exception exception) {
            // do nothing
        }
        files = file.list();
        Assert.assertEquals("we should have no temp file", 0, files.length);
        IOHelper.close(cos);
    }

    @Test
    public void testCacheStreamToFileAndCloseStreamEncrypted() throws Exception {
        // set some stream or 8-bit block cipher transformation name
        context.getStreamCachingStrategy().setSpoolChiper("RC4");
        context.start();
        CachedOutputStream cos = new CachedOutputStream(exchange);
        cos.write(CachedOutputStreamTest.TEST_STRING.getBytes("UTF-8"));
        cos.flush();
        File file = new File("target/cachedir");
        String[] files = file.list();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        Assert.assertTrue("The content is written", ((new File(file, files[0]).length()) > 10));
        FileInputStream tmpin = new FileInputStream(new File(file, files[0]));
        String temp = CachedOutputStreamTest.toString(tmpin);
        Assert.assertTrue("The content is not encrypted", (((temp.length()) > 0) && ((temp.indexOf("aaa")) < 0)));
        tmpin.close();
        StreamCache cache = cos.newStreamCache();
        Assert.assertTrue("Should get the FileInputStreamCache", (cache instanceof FileInputStreamCache));
        temp = CachedOutputStreamTest.toString(((InputStream) (cache)));
        ((InputStream) (cache)).close();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        Assert.assertEquals("Cached a wrong file", temp, CachedOutputStreamTest.TEST_STRING);
        exchange.getUnitOfWork().done(exchange);
        try {
            cache.reset();
            // The stream is closed, so the temp file is gone.
            Assert.fail("we expect the exception here");
        } catch (Exception exception) {
            // do nothing
        }
        files = file.list();
        Assert.assertEquals("we should have no temp file", 0, files.length);
        IOHelper.close(cos);
    }

    @Test
    public void testCacheStreamToFileCloseStreamBeforeDone() throws Exception {
        context.start();
        CachedOutputStream cos = new CachedOutputStream(exchange);
        cos.write(CachedOutputStreamTest.TEST_STRING.getBytes("UTF-8"));
        File file = new File("target/cachedir");
        String[] files = file.list();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        Assert.assertTrue("The file name should start with cos", files[0].startsWith("cos"));
        StreamCache cache = cos.newStreamCache();
        Assert.assertTrue("Should get the FileInputStreamCache", (cache instanceof FileInputStreamCache));
        String temp = CachedOutputStreamTest.toString(((InputStream) (cache)));
        Assert.assertEquals("Cached a wrong file", temp, CachedOutputStreamTest.TEST_STRING);
        cache.reset();
        temp = CachedOutputStreamTest.toString(((InputStream) (cache)));
        Assert.assertEquals("Cached a wrong file", temp, CachedOutputStreamTest.TEST_STRING);
        ((InputStream) (cache)).close();
        files = file.list();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        exchange.getUnitOfWork().done(exchange);
        files = file.list();
        Assert.assertEquals("we should have no temp file", 0, files.length);
        IOHelper.close(cos);
    }

    @Test
    public void testCacheStreamToMemory() throws Exception {
        context.getStreamCachingStrategy().setSpoolThreshold(1024);
        context.start();
        CachedOutputStream cos = new CachedOutputStream(exchange);
        cos.write(CachedOutputStreamTest.TEST_STRING.getBytes("UTF-8"));
        File file = new File("target/cachedir");
        String[] files = file.list();
        Assert.assertEquals("we should have no temp file", 0, files.length);
        StreamCache cache = cos.newStreamCache();
        Assert.assertTrue("Should get the InputStreamCache", (cache instanceof InputStreamCache));
        String temp = IOConverter.toString(((InputStream) (cache)), null);
        Assert.assertEquals("Cached a wrong file", temp, CachedOutputStreamTest.TEST_STRING);
        IOHelper.close(cos);
    }

    @Test
    public void testCacheStreamToMemoryAsDiskIsDisabled() throws Exception {
        // -1 disables disk based cache
        context.getStreamCachingStrategy().setSpoolThreshold((-1));
        context.start();
        CachedOutputStream cos = new CachedOutputStream(exchange);
        cos.write(CachedOutputStreamTest.TEST_STRING.getBytes("UTF-8"));
        File file = new File("target/cachedir");
        String[] files = file.list();
        Assert.assertEquals("we should have no temp file", 0, files.length);
        StreamCache cache = cos.newStreamCache();
        Assert.assertTrue("Should get the InputStreamCache", (cache instanceof InputStreamCache));
        String temp = IOConverter.toString(((InputStream) (cache)), null);
        Assert.assertEquals("Cached a wrong file", temp, CachedOutputStreamTest.TEST_STRING);
        exchange.getUnitOfWork().done(exchange);
        IOHelper.close(cos);
    }

    @Test
    public void testCachedOutputStreamCustomBufferSize() throws Exception {
        // double the default buffer size
        context.getStreamCachingStrategy().setBufferSize(8192);
        context.start();
        CachedOutputStream cos = new CachedOutputStream(exchange);
        cos.write(CachedOutputStreamTest.TEST_STRING.getBytes("UTF-8"));
        Assert.assertEquals("we should have a custom buffer size", cos.getStrategyBufferSize(), 8192);
        // make sure things still work after custom buffer size set
        File file = new File("target/cachedir");
        String[] files = file.list();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        Assert.assertTrue("The file name should start with cos", files[0].startsWith("cos"));
        StreamCache cache = cos.newStreamCache();
        Assert.assertTrue("Should get the FileInputStreamCache", (cache instanceof FileInputStreamCache));
        String temp = CachedOutputStreamTest.toString(((InputStream) (cache)));
        Assert.assertEquals("Cached a wrong file", temp, CachedOutputStreamTest.TEST_STRING);
        cache.reset();
        temp = CachedOutputStreamTest.toString(((InputStream) (cache)));
        Assert.assertEquals("Cached a wrong file", temp, CachedOutputStreamTest.TEST_STRING);
        ((InputStream) (cache)).close();
        files = file.list();
        Assert.assertEquals("we should have a temp file", 1, files.length);
        exchange.getUnitOfWork().done(exchange);
        files = file.list();
        Assert.assertEquals("we should have no temp file", 0, files.length);
        IOHelper.close(cos);
    }

    @Test
    public void testCachedOutputStreamEmptyInput() throws Exception {
        context.start();
        CachedOutputStream cos = new CachedOutputStream(exchange, false);
        // write an empty string
        cos.write("".getBytes("UTF-8"));
        InputStream is = cos.getWrappedInputStream();
        Assert.assertNotNull(is);
        // copy to output stream
        ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
        IOHelper.copy(is, bos);
        Assert.assertNotNull(bos);
        byte[] data = bos.toByteArray();
        Assert.assertEquals(0, data.length);
        IOHelper.close(bos);
        IOHelper.close(cos);
    }
}

