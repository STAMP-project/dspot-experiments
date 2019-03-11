/**
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.internal.bind;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import junit.framework.TestCase;


@SuppressWarnings("resource")
public final class JsonElementReaderTest extends TestCase {
    public void testNumbers() throws IOException {
        JsonElement element = new JsonParser().parse("[1, 2, 3]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        TestCase.assertEquals(1, reader.nextInt());
        TestCase.assertEquals(2L, reader.nextLong());
        TestCase.assertEquals(3.0, reader.nextDouble());
        reader.endArray();
    }

    public void testLenientNansAndInfinities() throws IOException {
        JsonElement element = new JsonParser().parse("[NaN, -Infinity, Infinity]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.setLenient(true);
        reader.beginArray();
        TestCase.assertTrue(Double.isNaN(reader.nextDouble()));
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, reader.nextDouble());
        TestCase.assertEquals(Double.POSITIVE_INFINITY, reader.nextDouble());
        reader.endArray();
    }

    public void testStrictNansAndInfinities() throws IOException {
        JsonElement element = new JsonParser().parse("[NaN, -Infinity, Infinity]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.setLenient(false);
        reader.beginArray();
        try {
            reader.nextDouble();
            TestCase.fail();
        } catch (NumberFormatException e) {
        }
        TestCase.assertEquals("NaN", reader.nextString());
        try {
            reader.nextDouble();
            TestCase.fail();
        } catch (NumberFormatException e) {
        }
        TestCase.assertEquals("-Infinity", reader.nextString());
        try {
            reader.nextDouble();
            TestCase.fail();
        } catch (NumberFormatException e) {
        }
        TestCase.assertEquals("Infinity", reader.nextString());
        reader.endArray();
    }

    public void testNumbersFromStrings() throws IOException {
        JsonElement element = new JsonParser().parse("[\"1\", \"2\", \"3\"]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        TestCase.assertEquals(1, reader.nextInt());
        TestCase.assertEquals(2L, reader.nextLong());
        TestCase.assertEquals(3.0, reader.nextDouble());
        reader.endArray();
    }

    public void testStringsFromNumbers() throws IOException {
        JsonElement element = new JsonParser().parse("[1]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        TestCase.assertEquals("1", reader.nextString());
        reader.endArray();
    }

    public void testBooleans() throws IOException {
        JsonElement element = new JsonParser().parse("[true, false]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        TestCase.assertEquals(true, reader.nextBoolean());
        TestCase.assertEquals(false, reader.nextBoolean());
        reader.endArray();
    }

    public void testNulls() throws IOException {
        JsonElement element = new JsonParser().parse("[null,null]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        reader.nextNull();
        reader.nextNull();
        reader.endArray();
    }

    public void testStrings() throws IOException {
        JsonElement element = new JsonParser().parse("[\"A\",\"B\"]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        TestCase.assertEquals("A", reader.nextString());
        TestCase.assertEquals("B", reader.nextString());
        reader.endArray();
    }

    public void testArray() throws IOException {
        JsonElement element = new JsonParser().parse("[1, 2, 3]");
        JsonTreeReader reader = new JsonTreeReader(element);
        TestCase.assertEquals(JsonToken.BEGIN_ARRAY, reader.peek());
        reader.beginArray();
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        TestCase.assertEquals(1, reader.nextInt());
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        TestCase.assertEquals(2, reader.nextInt());
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        TestCase.assertEquals(3, reader.nextInt());
        TestCase.assertEquals(JsonToken.END_ARRAY, reader.peek());
        reader.endArray();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testObject() throws IOException {
        JsonElement element = new JsonParser().parse("{\"A\": 1, \"B\": 2}");
        JsonTreeReader reader = new JsonTreeReader(element);
        TestCase.assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
        reader.beginObject();
        TestCase.assertEquals(JsonToken.NAME, reader.peek());
        TestCase.assertEquals("A", reader.nextName());
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        TestCase.assertEquals(1, reader.nextInt());
        TestCase.assertEquals(JsonToken.NAME, reader.peek());
        TestCase.assertEquals("B", reader.nextName());
        TestCase.assertEquals(JsonToken.NUMBER, reader.peek());
        TestCase.assertEquals(2, reader.nextInt());
        TestCase.assertEquals(JsonToken.END_OBJECT, reader.peek());
        reader.endObject();
        TestCase.assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    }

    public void testEmptyArray() throws IOException {
        JsonElement element = new JsonParser().parse("[]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        reader.endArray();
    }

    public void testNestedArrays() throws IOException {
        JsonElement element = new JsonParser().parse("[[],[[]]]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        reader.beginArray();
        reader.endArray();
        reader.beginArray();
        reader.beginArray();
        reader.endArray();
        reader.endArray();
        reader.endArray();
    }

    public void testNestedObjects() throws IOException {
        JsonElement element = new JsonParser().parse("{\"A\":{},\"B\":{\"C\":{}}}");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginObject();
        TestCase.assertEquals("A", reader.nextName());
        reader.beginObject();
        reader.endObject();
        TestCase.assertEquals("B", reader.nextName());
        reader.beginObject();
        TestCase.assertEquals("C", reader.nextName());
        reader.beginObject();
        reader.endObject();
        reader.endObject();
        reader.endObject();
    }

    public void testEmptyObject() throws IOException {
        JsonElement element = new JsonParser().parse("{}");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginObject();
        reader.endObject();
    }

    public void testSkipValue() throws IOException {
        JsonElement element = new JsonParser().parse("[\"A\",{\"B\":[[]]},\"C\",[[]],\"D\",null]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        TestCase.assertEquals("A", reader.nextString());
        reader.skipValue();
        TestCase.assertEquals("C", reader.nextString());
        reader.skipValue();
        TestCase.assertEquals("D", reader.nextString());
        reader.skipValue();
        reader.endArray();
    }

    public void testWrongType() throws IOException {
        JsonElement element = new JsonParser().parse("[[],\"A\"]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        try {
            reader.nextBoolean();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextNull();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextString();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextInt();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextLong();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextDouble();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextName();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.beginObject();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.endArray();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.endObject();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        reader.beginArray();
        reader.endArray();
        try {
            reader.nextBoolean();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextNull();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            reader.nextInt();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        try {
            reader.nextLong();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        try {
            reader.nextDouble();
            TestCase.fail();
        } catch (NumberFormatException expected) {
        }
        try {
            reader.nextName();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals("A", reader.nextString());
        reader.endArray();
    }

    public void testEarlyClose() throws IOException {
        JsonElement element = new JsonParser().parse("[1, 2, 3]");
        JsonTreeReader reader = new JsonTreeReader(element);
        reader.beginArray();
        reader.close();
        try {
            reader.peek();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }
}

