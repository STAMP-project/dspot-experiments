/**
 * Copyright (C) 2014 Google Inc.
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
package com.google.gson.stream;


import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.IOException;
import java.io.StringReader;
import junit.framework.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JsonReaderPathTest {
    @Parameterized.Parameter
    public JsonReaderPathTest.Factory factory;

    @Test
    public void path() throws IOException {
        JsonReader reader = factory.create("{\"a\":[2,true,false,null,\"b\",{\"c\":\"d\"},[3]]}");
        Assert.assertEquals("$", reader.getPath());
        reader.beginObject();
        Assert.assertEquals("$.", reader.getPath());
        reader.nextName();
        Assert.assertEquals("$.a", reader.getPath());
        reader.beginArray();
        Assert.assertEquals("$.a[0]", reader.getPath());
        reader.nextInt();
        Assert.assertEquals("$.a[1]", reader.getPath());
        reader.nextBoolean();
        Assert.assertEquals("$.a[2]", reader.getPath());
        reader.nextBoolean();
        Assert.assertEquals("$.a[3]", reader.getPath());
        reader.nextNull();
        Assert.assertEquals("$.a[4]", reader.getPath());
        reader.nextString();
        Assert.assertEquals("$.a[5]", reader.getPath());
        reader.beginObject();
        Assert.assertEquals("$.a[5].", reader.getPath());
        reader.nextName();
        Assert.assertEquals("$.a[5].c", reader.getPath());
        reader.nextString();
        Assert.assertEquals("$.a[5].c", reader.getPath());
        reader.endObject();
        Assert.assertEquals("$.a[6]", reader.getPath());
        reader.beginArray();
        Assert.assertEquals("$.a[6][0]", reader.getPath());
        reader.nextInt();
        Assert.assertEquals("$.a[6][1]", reader.getPath());
        reader.endArray();
        Assert.assertEquals("$.a[7]", reader.getPath());
        reader.endArray();
        Assert.assertEquals("$.a", reader.getPath());
        reader.endObject();
        Assert.assertEquals("$", reader.getPath());
    }

    @Test
    public void objectPath() throws IOException {
        JsonReader reader = factory.create("{\"a\":1,\"b\":2}");
        Assert.assertEquals("$", reader.getPath());
        reader.peek();
        Assert.assertEquals("$", reader.getPath());
        reader.beginObject();
        Assert.assertEquals("$.", reader.getPath());
        reader.peek();
        Assert.assertEquals("$.", reader.getPath());
        reader.nextName();
        Assert.assertEquals("$.a", reader.getPath());
        reader.peek();
        Assert.assertEquals("$.a", reader.getPath());
        reader.nextInt();
        Assert.assertEquals("$.a", reader.getPath());
        reader.peek();
        Assert.assertEquals("$.a", reader.getPath());
        reader.nextName();
        Assert.assertEquals("$.b", reader.getPath());
        reader.peek();
        Assert.assertEquals("$.b", reader.getPath());
        reader.nextInt();
        Assert.assertEquals("$.b", reader.getPath());
        reader.peek();
        Assert.assertEquals("$.b", reader.getPath());
        reader.endObject();
        Assert.assertEquals("$", reader.getPath());
        reader.peek();
        Assert.assertEquals("$", reader.getPath());
        reader.close();
        Assert.assertEquals("$", reader.getPath());
    }

    @Test
    public void arrayPath() throws IOException {
        JsonReader reader = factory.create("[1,2]");
        Assert.assertEquals("$", reader.getPath());
        reader.peek();
        Assert.assertEquals("$", reader.getPath());
        reader.beginArray();
        Assert.assertEquals("$[0]", reader.getPath());
        reader.peek();
        Assert.assertEquals("$[0]", reader.getPath());
        reader.nextInt();
        Assert.assertEquals("$[1]", reader.getPath());
        reader.peek();
        Assert.assertEquals("$[1]", reader.getPath());
        reader.nextInt();
        Assert.assertEquals("$[2]", reader.getPath());
        reader.peek();
        Assert.assertEquals("$[2]", reader.getPath());
        reader.endArray();
        Assert.assertEquals("$", reader.getPath());
        reader.peek();
        Assert.assertEquals("$", reader.getPath());
        reader.close();
        Assert.assertEquals("$", reader.getPath());
    }

    @Test
    public void multipleTopLevelValuesInOneDocument() throws IOException {
        Assume.assumeTrue(((factory) == (JsonReaderPathTest.Factory.STRING_READER)));
        JsonReader reader = factory.create("[][]");
        reader.setLenient(true);
        reader.beginArray();
        reader.endArray();
        Assert.assertEquals("$", reader.getPath());
        reader.beginArray();
        reader.endArray();
        Assert.assertEquals("$", reader.getPath());
    }

    @Test
    public void skipArrayElements() throws IOException {
        JsonReader reader = factory.create("[1,2,3]");
        reader.beginArray();
        reader.skipValue();
        reader.skipValue();
        Assert.assertEquals("$[2]", reader.getPath());
    }

    @Test
    public void skipObjectNames() throws IOException {
        JsonReader reader = factory.create("{\"a\":1}");
        reader.beginObject();
        reader.skipValue();
        Assert.assertEquals("$.null", reader.getPath());
    }

    @Test
    public void skipObjectValues() throws IOException {
        JsonReader reader = factory.create("{\"a\":1,\"b\":2}");
        reader.beginObject();
        Assert.assertEquals("$.", reader.getPath());
        reader.nextName();
        reader.skipValue();
        Assert.assertEquals("$.null", reader.getPath());
        reader.nextName();
        Assert.assertEquals("$.b", reader.getPath());
    }

    @Test
    public void skipNestedStructures() throws IOException {
        JsonReader reader = factory.create("[[1,2,3],4]");
        reader.beginArray();
        reader.skipValue();
        Assert.assertEquals("$[1]", reader.getPath());
    }

    @Test
    public void arrayOfObjects() throws IOException {
        JsonReader reader = factory.create("[{},{},{}]");
        reader.beginArray();
        Assert.assertEquals("$[0]", reader.getPath());
        reader.beginObject();
        Assert.assertEquals("$[0].", reader.getPath());
        reader.endObject();
        Assert.assertEquals("$[1]", reader.getPath());
        reader.beginObject();
        Assert.assertEquals("$[1].", reader.getPath());
        reader.endObject();
        Assert.assertEquals("$[2]", reader.getPath());
        reader.beginObject();
        Assert.assertEquals("$[2].", reader.getPath());
        reader.endObject();
        Assert.assertEquals("$[3]", reader.getPath());
        reader.endArray();
        Assert.assertEquals("$", reader.getPath());
    }

    @Test
    public void arrayOfArrays() throws IOException {
        JsonReader reader = factory.create("[[],[],[]]");
        reader.beginArray();
        Assert.assertEquals("$[0]", reader.getPath());
        reader.beginArray();
        Assert.assertEquals("$[0][0]", reader.getPath());
        reader.endArray();
        Assert.assertEquals("$[1]", reader.getPath());
        reader.beginArray();
        Assert.assertEquals("$[1][0]", reader.getPath());
        reader.endArray();
        Assert.assertEquals("$[2]", reader.getPath());
        reader.beginArray();
        Assert.assertEquals("$[2][0]", reader.getPath());
        reader.endArray();
        Assert.assertEquals("$[3]", reader.getPath());
        reader.endArray();
        Assert.assertEquals("$", reader.getPath());
    }

    enum Factory {

        STRING_READER() {
            @Override
            public JsonReader create(String data) {
                return new JsonReader(new StringReader(data));
            }
        },
        OBJECT_READER() {
            @Override
            public JsonReader create(String data) {
                JsonElement element = Streams.parse(new JsonReader(new StringReader(data)));
                return new JsonTreeReader(element);
            }
        };
        abstract JsonReader create(String data);
    }
}

