/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.handler.codec.http;


import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class QueryStringDecoderTest {
    @Test
    public void testBasic() throws Exception {
        QueryStringDecoder d;
        d = new QueryStringDecoder("/foo?a=b=c");
        Assert.assertEquals("/foo", d.getPath());
        Assert.assertEquals(1, d.getParameters().size());
        Assert.assertEquals(1, d.getParameters().get("a").size());
        Assert.assertEquals("b=c", d.getParameters().get("a").get(0));
        d = new QueryStringDecoder("/foo?a=1&a=2");
        Assert.assertEquals("/foo", d.getPath());
        Assert.assertEquals(1, d.getParameters().size());
        Assert.assertEquals(2, d.getParameters().get("a").size());
        Assert.assertEquals("1", d.getParameters().get("a").get(0));
        Assert.assertEquals("2", d.getParameters().get("a").get(1));
        d = new QueryStringDecoder("/foo?a=&a=2");
        Assert.assertEquals("/foo", d.getPath());
        Assert.assertEquals(1, d.getParameters().size());
        Assert.assertEquals(2, d.getParameters().get("a").size());
        Assert.assertEquals("", d.getParameters().get("a").get(0));
        Assert.assertEquals("2", d.getParameters().get("a").get(1));
        d = new QueryStringDecoder("/foo?a=1&a=");
        Assert.assertEquals("/foo", d.getPath());
        Assert.assertEquals(1, d.getParameters().size());
        Assert.assertEquals(2, d.getParameters().get("a").size());
        Assert.assertEquals("1", d.getParameters().get("a").get(0));
        Assert.assertEquals("", d.getParameters().get("a").get(1));
        d = new QueryStringDecoder("/foo?a=1&a=&a=");
        Assert.assertEquals("/foo", d.getPath());
        Assert.assertEquals(1, d.getParameters().size());
        Assert.assertEquals(3, d.getParameters().get("a").size());
        Assert.assertEquals("1", d.getParameters().get("a").get(0));
        Assert.assertEquals("", d.getParameters().get("a").get(1));
        Assert.assertEquals("", d.getParameters().get("a").get(2));
        d = new QueryStringDecoder("/foo?a=1=&a==2");
        Assert.assertEquals("/foo", d.getPath());
        Assert.assertEquals(1, d.getParameters().size());
        Assert.assertEquals(2, d.getParameters().get("a").size());
        Assert.assertEquals("1=", d.getParameters().get("a").get(0));
        Assert.assertEquals("=2", d.getParameters().get("a").get(1));
    }

    @Test
    public void testExotic() throws Exception {
        QueryStringDecoderTest.assertQueryString("", "");
        QueryStringDecoderTest.assertQueryString("foo", "foo");
        QueryStringDecoderTest.assertQueryString("/foo", "/foo");
        QueryStringDecoderTest.assertQueryString("?a=", "?a");
        QueryStringDecoderTest.assertQueryString("foo?a=", "foo?a");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?a");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?a&");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?&a");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?&a&");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?&=a");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?=a&");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?a=&");
        QueryStringDecoderTest.assertQueryString("/foo?a=b&c=d", "/foo?a=b&&c=d");
        QueryStringDecoderTest.assertQueryString("/foo?a=b&c=d", "/foo?a=b&=&c=d");
        QueryStringDecoderTest.assertQueryString("/foo?a=b&c=d", "/foo?a=b&==&c=d");
        QueryStringDecoderTest.assertQueryString("/foo?a=b&c=&x=y", "/foo?a=b&c&x=y");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?a=");
        QueryStringDecoderTest.assertQueryString("/foo?a=", "/foo?&a=");
        QueryStringDecoderTest.assertQueryString("/foo?a=b&c=d", "/foo?a=b&c=d");
        QueryStringDecoderTest.assertQueryString("/foo?a=1&a=&a=", "/foo?a=1&a&a=");
    }

    @Test
    public void testHashDos() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append('?');
        for (int i = 0; i < 65536; i++) {
            buf.append('k');
            buf.append(i);
            buf.append("=v");
            buf.append(i);
            buf.append('&');
        }
        Assert.assertEquals(1024, new QueryStringDecoder(buf.toString()).getParameters().size());
    }

    @Test
    public void testHasPath() throws Exception {
        QueryStringDecoder decoder = new QueryStringDecoder("1=2", false);
        Assert.assertEquals("", decoder.getPath());
        Map<String, List<String>> params = decoder.getParameters();
        Assert.assertEquals(1, params.size());
        Assert.assertTrue(params.containsKey("1"));
        List<String> param = params.get("1");
        Assert.assertNotNull(param);
        Assert.assertEquals(1, param.size());
        Assert.assertEquals("2", param.get(0));
    }

    @Test
    public void testUrlDecoding() throws Exception {
        final String caffe = // "Caff?" but instead of putting the literal E-acute in the
        // source file, we directly use the UTF-8 encoding so as to
        // not rely on the platform's default encoding (not portable).
        new String(new byte[]{ 'C', 'a', 'f', 'f', ((byte) (195)), ((byte) (169)) }, "UTF-8");
        final String[] tests = new String[]{ // Encoded   ->   Decoded or error message substring
        "", "", "foo", "foo", "f%%b", "f%b", "f+o", "f o", "f++", "f  ", "fo%", "unterminated escape sequence", "%42", "B", "%5f", "_", "f%4", "partial escape sequence", "%x2", "invalid escape sequence `%x2' at index 0 of: %x2", "%4x", "invalid escape sequence `%4x' at index 0 of: %4x", "Caff%C3%A9", caffe };
        for (int i = 0; i < (tests.length); i += 2) {
            final String encoded = tests[i];
            final String expected = tests[(i + 1)];
            try {
                final String decoded = QueryStringDecoder.decodeComponent(encoded);
                Assert.assertEquals(expected, decoded);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(((((("String \"" + (e.getMessage())) + "\" does") + " not contain \"") + expected) + '"'), e.getMessage().contains(expected));
            }
        }
    }

    // See #189
    @Test
    public void testURI() {
        URI uri = URI.create("http://localhost:8080/foo?param1=value1&param2=value2&param3=value3");
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Assert.assertEquals("/foo", decoder.getPath());
        Map<String, List<String>> params = decoder.getParameters();
        Assert.assertEquals(3, params.size());
        Iterator<Map.Entry<String, List<String>>> entries = params.entrySet().iterator();
        Map.Entry<String, List<String>> entry = entries.next();
        Assert.assertEquals("param1", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value1", entry.getValue().get(0));
        entry = entries.next();
        Assert.assertEquals("param2", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value2", entry.getValue().get(0));
        entry = entries.next();
        Assert.assertEquals("param3", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value3", entry.getValue().get(0));
        Assert.assertFalse(entries.hasNext());
    }

    // See #189
    @Test
    public void testURISlashPath() {
        URI uri = URI.create("http://localhost:8080/?param1=value1&param2=value2&param3=value3");
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Assert.assertEquals("/", decoder.getPath());
        Map<String, List<String>> params = decoder.getParameters();
        Assert.assertEquals(3, params.size());
        Iterator<Map.Entry<String, List<String>>> entries = params.entrySet().iterator();
        Map.Entry<String, List<String>> entry = entries.next();
        Assert.assertEquals("param1", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value1", entry.getValue().get(0));
        entry = entries.next();
        Assert.assertEquals("param2", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value2", entry.getValue().get(0));
        entry = entries.next();
        Assert.assertEquals("param3", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value3", entry.getValue().get(0));
        Assert.assertFalse(entries.hasNext());
    }

    // See #189
    @Test
    public void testURINoPath() {
        URI uri = URI.create("http://localhost:8080?param1=value1&param2=value2&param3=value3");
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Assert.assertEquals("", decoder.getPath());
        Map<String, List<String>> params = decoder.getParameters();
        Assert.assertEquals(3, params.size());
        Iterator<Map.Entry<String, List<String>>> entries = params.entrySet().iterator();
        Map.Entry<String, List<String>> entry = entries.next();
        Assert.assertEquals("param1", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value1", entry.getValue().get(0));
        entry = entries.next();
        Assert.assertEquals("param2", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value2", entry.getValue().get(0));
        entry = entries.next();
        Assert.assertEquals("param3", entry.getKey());
        Assert.assertEquals(1, entry.getValue().size());
        Assert.assertEquals("value3", entry.getValue().get(0));
        Assert.assertFalse(entries.hasNext());
    }
}

