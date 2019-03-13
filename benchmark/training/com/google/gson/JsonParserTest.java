/**
 * Copyright (C) 2009 Google Inc.
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
package com.google.gson;


import com.google.gson.common.TestTypes;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.StringReader;
import junit.framework.TestCase;


/**
 * Unit test for {@link JsonParser}
 *
 * @author Inderjeet Singh
 */
public class JsonParserTest extends TestCase {
    private JsonParser parser;

    public void testParseInvalidJson() {
        try {
            parser.parse("[[]");
            TestCase.fail();
        } catch (JsonSyntaxException expected) {
        }
    }

    public void testParseUnquotedStringArrayFails() {
        JsonElement element = parser.parse("[a,b,c]");
        TestCase.assertEquals("a", element.getAsJsonArray().get(0).getAsString());
        TestCase.assertEquals("b", element.getAsJsonArray().get(1).getAsString());
        TestCase.assertEquals("c", element.getAsJsonArray().get(2).getAsString());
        TestCase.assertEquals(3, element.getAsJsonArray().size());
    }

    public void testParseString() {
        String json = "{a:10,b:'c'}";
        JsonElement e = parser.parse(json);
        TestCase.assertTrue(e.isJsonObject());
        TestCase.assertEquals(10, e.getAsJsonObject().get("a").getAsInt());
        TestCase.assertEquals("c", e.getAsJsonObject().get("b").getAsString());
    }

    public void testParseEmptyString() {
        JsonElement e = parser.parse("\"   \"");
        TestCase.assertTrue(e.isJsonPrimitive());
        TestCase.assertEquals("   ", e.getAsString());
    }

    public void testParseEmptyWhitespaceInput() {
        JsonElement e = parser.parse("     ");
        TestCase.assertTrue(e.isJsonNull());
    }

    public void testParseUnquotedSingleWordStringFails() {
        TestCase.assertEquals("Test", parser.parse("Test").getAsString());
    }

    public void testParseUnquotedMultiWordStringFails() {
        String unquotedSentence = "Test is a test..blah blah";
        try {
            parser.parse(unquotedSentence);
            TestCase.fail();
        } catch (JsonSyntaxException expected) {
        }
    }

    public void testParseMixedArray() {
        String json = "[{},13,\"stringValue\"]";
        JsonElement e = parser.parse(json);
        TestCase.assertTrue(e.isJsonArray());
        JsonArray array = e.getAsJsonArray();
        TestCase.assertEquals("{}", array.get(0).toString());
        TestCase.assertEquals(13, array.get(1).getAsInt());
        TestCase.assertEquals("stringValue", array.get(2).getAsString());
    }

    public void testParseReader() {
        StringReader reader = new StringReader("{a:10,b:'c'}");
        JsonElement e = parser.parse(reader);
        TestCase.assertTrue(e.isJsonObject());
        TestCase.assertEquals(10, e.getAsJsonObject().get("a").getAsInt());
        TestCase.assertEquals("c", e.getAsJsonObject().get("b").getAsString());
    }

    public void testReadWriteTwoObjects() throws Exception {
        Gson gson = new Gson();
        CharArrayWriter writer = new CharArrayWriter();
        TestTypes.BagOfPrimitives expectedOne = new TestTypes.BagOfPrimitives(1, 1, true, "one");
        writer.write(gson.toJson(expectedOne).toCharArray());
        TestTypes.BagOfPrimitives expectedTwo = new TestTypes.BagOfPrimitives(2, 2, false, "two");
        writer.write(gson.toJson(expectedTwo).toCharArray());
        CharArrayReader reader = new CharArrayReader(writer.toCharArray());
        JsonReader parser = new JsonReader(reader);
        parser.setLenient(true);
        JsonElement element1 = Streams.parse(parser);
        JsonElement element2 = Streams.parse(parser);
        TestTypes.BagOfPrimitives actualOne = gson.fromJson(element1, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("one", actualOne.stringValue);
        TestTypes.BagOfPrimitives actualTwo = gson.fromJson(element2, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals("two", actualTwo.stringValue);
    }
}

