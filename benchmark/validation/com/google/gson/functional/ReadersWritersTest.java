/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.common.TestTypes;
import com.google.gson.reflect.TypeToken;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Functional tests for the support of {@link Reader}s and {@link Writer}s.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ReadersWritersTest extends TestCase {
    private Gson gson;

    public void testWriterForSerialization() throws Exception {
        Writer writer = new StringWriter();
        TestTypes.BagOfPrimitives src = new TestTypes.BagOfPrimitives();
        gson.toJson(src, writer);
        TestCase.assertEquals(src.getExpectedJson(), writer.toString());
    }

    public void testReaderForDeserialization() throws Exception {
        TestTypes.BagOfPrimitives expected = new TestTypes.BagOfPrimitives();
        Reader json = new StringReader(expected.getExpectedJson());
        TestTypes.BagOfPrimitives actual = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals(expected, actual);
    }

    public void testTopLevelNullObjectSerializationWithWriter() {
        StringWriter writer = new StringWriter();
        gson.toJson(null, writer);
        TestCase.assertEquals("null", writer.toString());
    }

    public void testTopLevelNullObjectDeserializationWithReader() {
        StringReader reader = new StringReader("null");
        Integer nullIntObject = gson.fromJson(reader, Integer.class);
        TestCase.assertNull(nullIntObject);
    }

    public void testTopLevelNullObjectSerializationWithWriterAndSerializeNulls() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        StringWriter writer = new StringWriter();
        gson.toJson(null, writer);
        TestCase.assertEquals("null", writer.toString());
    }

    public void testTopLevelNullObjectDeserializationWithReaderAndSerializeNulls() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        StringReader reader = new StringReader("null");
        Integer nullIntObject = gson.fromJson(reader, Integer.class);
        TestCase.assertNull(nullIntObject);
    }

    public void testReadWriteTwoStrings() throws IOException {
        Gson gson = new Gson();
        CharArrayWriter writer = new CharArrayWriter();
        writer.write(gson.toJson("one").toCharArray());
        writer.write(gson.toJson("two").toCharArray());
        CharArrayReader reader = new CharArrayReader(writer.toCharArray());
        JsonStreamParser parser = new JsonStreamParser(reader);
        String actualOne = gson.fromJson(parser.next(), String.class);
        TestCase.assertEquals("one", actualOne);
        String actualTwo = gson.fromJson(parser.next(), String.class);
        TestCase.assertEquals("two", actualTwo);
    }

    public void testReadWriteTwoObjects() throws IOException {
        Gson gson = new Gson();
        CharArrayWriter writer = new CharArrayWriter();
        TestTypes.BagOfPrimitives expectedOne = new TestTypes.BagOfPrimitives(1, 1, true, "one");
        writer.write(gson.toJson(expectedOne).toCharArray());
        TestTypes.BagOfPrimitives expectedTwo = new TestTypes.BagOfPrimitives(2, 2, false, "two");
        writer.write(gson.toJson(expectedTwo).toCharArray());
        CharArrayReader reader = new CharArrayReader(writer.toCharArray());
        JsonStreamParser parser = new JsonStreamParser(reader);
        TestTypes.BagOfPrimitives actualOne = gson.fromJson(parser.next(), TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("one", actualOne.stringValue);
        TestTypes.BagOfPrimitives actualTwo = gson.fromJson(parser.next(), TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("two", actualTwo.stringValue);
        TestCase.assertFalse(parser.hasNext());
    }

    public void testTypeMismatchThrowsJsonSyntaxExceptionForStrings() {
        try {
            gson.fromJson("true", new TypeToken<Map<String, String>>() {}.getType());
            TestCase.fail();
        } catch (JsonSyntaxException expected) {
        }
    }

    public void testTypeMismatchThrowsJsonSyntaxExceptionForReaders() {
        try {
            gson.fromJson(new StringReader("true"), new TypeToken<Map<String, String>>() {}.getType());
            TestCase.fail();
        } catch (JsonSyntaxException expected) {
        }
    }
}

