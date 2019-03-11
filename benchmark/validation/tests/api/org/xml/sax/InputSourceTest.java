/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.api.org.xml.sax;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.xml.sax.InputSource;


public class InputSourceTest extends TestCase {
    public void testInputSource() {
        InputSource i = new InputSource();
        TestCase.assertNull(i.getByteStream());
        TestCase.assertNull(i.getCharacterStream());
        TestCase.assertNull(i.getEncoding());
        TestCase.assertNull(i.getPublicId());
        TestCase.assertNull(i.getSystemId());
    }

    public void testInputSourceString() {
        InputSource i = new InputSource("Foo");
        TestCase.assertNull(i.getByteStream());
        TestCase.assertNull(i.getCharacterStream());
        TestCase.assertNull(i.getEncoding());
        TestCase.assertNull(i.getPublicId());
        TestCase.assertEquals("Foo", i.getSystemId());
    }

    public void testInputSourceInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        // Ordinary case
        InputSource i = new InputSource(bais);
        TestCase.assertEquals(bais, i.getByteStream());
        TestCase.assertNull(i.getCharacterStream());
        TestCase.assertNull(i.getEncoding());
        TestCase.assertNull(i.getPublicId());
        TestCase.assertNull(i.getSystemId());
        // No input stream
        i = new InputSource(((InputStream) (null)));
        TestCase.assertNull(i.getByteStream());
        TestCase.assertNull(i.getCharacterStream());
        TestCase.assertNull(i.getEncoding());
        TestCase.assertNull(i.getPublicId());
        TestCase.assertNull(i.getSystemId());
    }

    public void testInputSourceReader() {
        StringReader sr = new StringReader("Hello, world.");
        // Ordinary case
        InputSource i = new InputSource(sr);
        TestCase.assertNull(i.getByteStream());
        TestCase.assertEquals(sr, i.getCharacterStream());
        TestCase.assertNull(i.getEncoding());
        TestCase.assertNull(i.getPublicId());
        TestCase.assertNull(i.getSystemId());
        // No reader
        i = new InputSource(((Reader) (null)));
        TestCase.assertNull(i.getByteStream());
        TestCase.assertNull(i.getCharacterStream());
        TestCase.assertNull(i.getEncoding());
        TestCase.assertNull(i.getPublicId());
        TestCase.assertNull(i.getSystemId());
    }

    public void testSetPublicIdGetPublicId() {
        InputSource i = new InputSource();
        i.setPublicId("Foo");
        TestCase.assertEquals("Foo", i.getPublicId());
        i.setPublicId(null);
        TestCase.assertNull(i.getPublicId());
    }

    public void testSetSystemIdGetSystemId() {
        InputSource i = new InputSource();
        i.setSystemId("Foo");
        TestCase.assertEquals("Foo", i.getSystemId());
        i.setSystemId(null);
        TestCase.assertNull(i.getSystemId());
    }

    public void testSetByteStreamGetByteStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        InputSource i = new InputSource();
        // Ordinary case
        i.setByteStream(bais);
        TestCase.assertEquals(bais, i.getByteStream());
        // No input stream
        i.setByteStream(null);
        TestCase.assertNull(i.getByteStream());
    }

    public void testSetEncodingGetEncoding() {
        InputSource i = new InputSource();
        // Ordinary case
        i.setEncoding("Klingon");
        TestCase.assertEquals("Klingon", i.getEncoding());
        // No encoding
        i.setEncoding(null);
        TestCase.assertNull(i.getEncoding());
    }

    public void testSetCharacterStreamGetCharacterStream() {
        StringReader sr = new StringReader("Hello, world.");
        InputSource i = new InputSource();
        // Ordinary case
        i.setCharacterStream(sr);
        TestCase.assertNull(i.getByteStream());
        TestCase.assertEquals(sr, i.getCharacterStream());
        TestCase.assertNull(i.getEncoding());
        TestCase.assertNull(i.getPublicId());
        TestCase.assertNull(i.getSystemId());
        // No reader
        i.setCharacterStream(null);
        TestCase.assertNull(i.getByteStream());
        TestCase.assertNull(i.getCharacterStream());
        TestCase.assertNull(i.getEncoding());
        TestCase.assertNull(i.getPublicId());
        TestCase.assertNull(i.getSystemId());
    }
}

