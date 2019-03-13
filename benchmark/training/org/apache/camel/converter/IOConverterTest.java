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
package org.apache.camel.converter;


import Exchange.CHARSET_NAME;
import Exchange.FILE_NAME;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.ObjectHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for {@link IOConverter}
 */
public class IOConverterTest extends ContextTestSupport {
    private static final byte[] TESTDATA = "My test data".getBytes();

    @Test
    public void testToBytes() throws Exception {
        File file = new File("src/test/resources/org/apache/camel/converter/dummy.txt");
        byte[] data = IOConverter.toBytes(Files.newInputStream(Paths.get(file.getAbsolutePath())));
        Assert.assertEquals("get the wrong byte size", file.length(), data.length);
        Assert.assertEquals('#', ((char) (data[0])));
        // should contain Hello World!
        String s = new String(data);
        Assert.assertTrue("Should contain Hello World!", s.contains("Hello World"));
    }

    @Test
    public void testCopy() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(IOConverterTest.TESTDATA);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOHelper.copy(bis, bos);
        assertEquals(IOConverterTest.TESTDATA, bos.toByteArray());
    }

    @Test
    public void testToOutputStreamFile() throws Exception {
        template.sendBodyAndHeader("file://target/data/test", "Hello World", FILE_NAME, "hello.txt");
        File file = new File("target/data/test/hello.txt");
        OutputStream os = IOConverter.toOutputStream(file);
        TestSupport.assertIsInstanceOf(BufferedOutputStream.class, os);
        os.close();
    }

    @Test
    public void testToWriterFile() throws Exception {
        template.sendBodyAndHeader("file://target/data/test", "Hello World", FILE_NAME, "hello.txt");
        File file = new File("target/data/test/hello.txt");
        Writer writer = IOConverter.toWriter(file, null);
        TestSupport.assertIsInstanceOf(BufferedWriter.class, writer);
        writer.close();
    }

    @Test
    public void testToReader() throws Exception {
        StringReader reader = IOConverter.toReader("Hello");
        Assert.assertEquals("Hello", IOConverter.toString(reader));
    }

    @Test
    public void testBytesToReader() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        String defaultCharacterSet = ObjectHelper.getDefaultCharacterSet();
        exchange.setProperty(CHARSET_NAME, defaultCharacterSet);
        byte[] bytes = "Hello World".getBytes(defaultCharacterSet);
        Reader reader = IOConverter.toReader(bytes, exchange);
        Assert.assertEquals("Hello World", IOConverter.toString(reader));
    }

    @Test
    public void testToInputStreamExchange() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.setProperty(CHARSET_NAME, ObjectHelper.getDefaultCharacterSet());
        InputStream is = IOConverter.toInputStream("Hello World", exchange);
        Assert.assertNotNull(is);
        Assert.assertEquals("Hello World", IOConverter.toString(is, exchange));
    }

    @Test
    public void testToInputStreamStringBufferAndBuilderExchange() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.setProperty(CHARSET_NAME, ObjectHelper.getDefaultCharacterSet());
        StringBuffer buffer = new StringBuffer();
        buffer.append("Hello World");
        InputStream is = IOConverter.toInputStream(buffer, exchange);
        Assert.assertNotNull(is);
        Assert.assertEquals("Hello World", IOConverter.toString(is, exchange));
        StringBuilder builder = new StringBuilder();
        builder.append("Hello World");
        is = IOConverter.toInputStream(builder, exchange);
        Assert.assertNotNull(is);
        Assert.assertEquals("Hello World", IOConverter.toString(is, exchange));
    }

    @Test
    public void testToInputStreamBufferReader() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.setProperty(CHARSET_NAME, ObjectHelper.getDefaultCharacterSet());
        BufferedReader br = IOHelper.buffered(new StringReader("Hello World"));
        InputStream is = IOConverter.toInputStream(br, exchange);
        Assert.assertNotNull(is);
    }

    @Test
    public void testToByteArrayFile() throws Exception {
        template.sendBodyAndHeader("file://target/data/test", "Hello World", FILE_NAME, "hello.txt");
        File file = new File("target/data/test/hello.txt");
        byte[] data = IOConverter.toByteArray(file);
        Assert.assertNotNull(data);
        Assert.assertEquals(11, data.length);
    }

    @Test
    public void testToStringBufferReader() throws Exception {
        BufferedReader br = IOHelper.buffered(new StringReader("Hello World"));
        String s = IOConverter.toString(br);
        Assert.assertNotNull(s);
        Assert.assertEquals("Hello World", s);
    }

    @Test
    public void testToByteArrayBufferReader() throws Exception {
        BufferedReader br = IOHelper.buffered(new StringReader("Hello World"));
        byte[] bytes = IOConverter.toByteArray(br, null);
        Assert.assertNotNull(bytes);
        Assert.assertEquals(11, bytes.length);
    }

    @Test
    public void testToByteArrayReader() throws Exception {
        Reader br = IOHelper.buffered(new StringReader("Hello World"));
        byte[] bytes = IOConverter.toByteArray(br, null);
        Assert.assertNotNull(bytes);
        Assert.assertEquals(11, bytes.length);
    }

    @Test
    public void testToByteArrayOutputStream() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write("Hello World".getBytes());
        byte[] bytes = IOConverter.toByteArray(os);
        Assert.assertNotNull(bytes);
        Assert.assertEquals(11, bytes.length);
    }

    @Test
    public void testToStringOutputStream() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write("Hello World".getBytes());
        String s = IOConverter.toString(os, null);
        Assert.assertNotNull(s);
        Assert.assertEquals("Hello World", s);
    }

    @Test
    public void testToInputStreamOutputStream() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write("Hello World".getBytes());
        InputStream is = IOConverter.toInputStream(os);
        Assert.assertNotNull(is);
        Assert.assertEquals("Hello World", IOConverter.toString(is, null));
    }

    @Test
    public void testToInputStreamUrl() throws Exception {
        URL url = ObjectHelper.loadResourceAsURL("log4j2.properties");
        InputStream is = IOConverter.toInputStream(url);
        TestSupport.assertIsInstanceOf(BufferedInputStream.class, is);
    }

    @Test
    public void testStringUrl() throws Exception {
        URL url = ObjectHelper.loadResourceAsURL("log4j2.properties");
        String s = IOConverter.toString(url, null);
        Assert.assertNotNull(s);
    }

    @Test
    public void testStringByBufferedReader() throws Exception {
        BufferedReader br = IOHelper.buffered(new StringReader("Hello World"));
        Assert.assertEquals("Hello World", IOConverter.toString(br));
    }

    @Test
    public void testByteArrayByBufferedReader() throws Exception {
        Reader reader = new StringReader("Hello World");
        byte[] data = IOConverter.toByteArray(reader, null);
        Assert.assertNotNull(data);
        Assert.assertEquals("Hello World", context.getTypeConverter().convertTo(String.class, data));
    }

    @Test
    public void testInputStreamToString() throws Exception {
        String data = "46\u00b037\'00\"N\"";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.setProperty(CHARSET_NAME, "UTF-8");
        String result = IOConverter.toString(is, exchange);
        Assert.assertEquals("Get a wrong result", data, result);
    }

    @Test
    public void testToPropertiesFromReader() throws Exception {
        Reader br = IOHelper.buffered(new StringReader("foo=123\nbar=456"));
        Properties p = IOConverter.toProperties(br);
        Assert.assertNotNull(p);
        Assert.assertEquals(2, p.size());
        Assert.assertEquals("123", p.get("foo"));
        Assert.assertEquals("456", p.get("bar"));
    }

    @Test
    public void testToPropertiesFromFile() throws Exception {
        Properties p = IOConverter.toProperties(new File("src/test/resources/log4j2.properties"));
        Assert.assertNotNull(p);
        Assert.assertTrue(("Should be 8 or more properties, was " + (p.size())), ((p.size()) >= 8));
        String root = ((String) (p.get("rootLogger.level")));
        Assert.assertNotNull(root);
        Assert.assertTrue(root.contains("INFO"));
    }
}

