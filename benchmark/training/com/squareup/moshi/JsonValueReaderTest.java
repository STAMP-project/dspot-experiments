/**
 * Copyright (C) 2017 Square, Inc.
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
package com.squareup.moshi;


import JsonReader.Token.BEGIN_ARRAY;
import JsonReader.Token.BEGIN_OBJECT;
import JsonReader.Token.BOOLEAN;
import JsonReader.Token.END_ARRAY;
import JsonReader.Token.END_DOCUMENT;
import JsonReader.Token.END_OBJECT;
import JsonReader.Token.NAME;
import JsonReader.Token.NULL;
import JsonReader.Token.NUMBER;
import JsonReader.Token.STRING;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public final class JsonValueReaderTest {
    @Test
    public void array() throws Exception {
        List<Object> root = new ArrayList<>();
        root.add("s");
        root.add(1.5);
        root.add(true);
        root.add(null);
        JsonReader reader = new JsonValueReader(root);
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(BEGIN_ARRAY);
        reader.beginArray();
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(STRING);
        assertThat(reader.nextString()).isEqualTo("s");
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NUMBER);
        assertThat(reader.nextDouble()).isEqualTo(1.5);
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(BOOLEAN);
        assertThat(reader.nextBoolean()).isEqualTo(true);
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NULL);
        assertThat(reader.nextNull()).isNull();
        assertThat(reader.hasNext()).isFalse();
        assertThat(reader.peek()).isEqualTo(END_ARRAY);
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void object() throws Exception {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("a", "s");
        root.put("b", 1.5);
        root.put("c", true);
        root.put("d", null);
        JsonReader reader = new JsonValueReader(root);
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(BEGIN_OBJECT);
        reader.beginObject();
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NAME);
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.peek()).isEqualTo(STRING);
        assertThat(reader.nextString()).isEqualTo("s");
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NAME);
        assertThat(reader.nextName()).isEqualTo("b");
        assertThat(reader.peek()).isEqualTo(NUMBER);
        assertThat(reader.nextDouble()).isEqualTo(1.5);
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NAME);
        assertThat(reader.nextName()).isEqualTo("c");
        assertThat(reader.peek()).isEqualTo(BOOLEAN);
        assertThat(reader.nextBoolean()).isEqualTo(true);
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NAME);
        assertThat(reader.nextName()).isEqualTo("d");
        assertThat(reader.peek()).isEqualTo(NULL);
        assertThat(reader.nextNull()).isNull();
        assertThat(reader.hasNext()).isFalse();
        assertThat(reader.peek()).isEqualTo(END_OBJECT);
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void nesting() throws Exception {
        List<Map<String, List<Map<String, Double>>>> root = Collections.singletonList(Collections.singletonMap("a", Collections.singletonList(Collections.singletonMap("b", 1.5))));
        JsonReader reader = new JsonValueReader(root);
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(BEGIN_ARRAY);
        reader.beginArray();
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(BEGIN_OBJECT);
        reader.beginObject();
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NAME);
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(BEGIN_ARRAY);
        reader.beginArray();
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(BEGIN_OBJECT);
        reader.beginObject();
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NAME);
        assertThat(reader.nextName()).isEqualTo("b");
        assertThat(reader.hasNext()).isTrue();
        assertThat(reader.peek()).isEqualTo(NUMBER);
        assertThat(reader.nextDouble()).isEqualTo(1.5);
        assertThat(reader.hasNext()).isFalse();
        assertThat(reader.peek()).isEqualTo(END_OBJECT);
        reader.endObject();
        assertThat(reader.hasNext()).isFalse();
        assertThat(reader.peek()).isEqualTo(END_ARRAY);
        reader.endArray();
        assertThat(reader.hasNext()).isFalse();
        assertThat(reader.peek()).isEqualTo(END_OBJECT);
        reader.endObject();
        assertThat(reader.hasNext()).isFalse();
        assertThat(reader.peek()).isEqualTo(END_ARRAY);
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void promoteNameToValue() throws Exception {
        Map<String, String> root = Collections.singletonMap("a", "b");
        JsonReader reader = new JsonValueReader(root);
        reader.beginObject();
        reader.promoteNameToValue();
        assertThat(reader.peek()).isEqualTo(STRING);
        assertThat(reader.nextString()).isEqualTo("a");
        assertThat(reader.peek()).isEqualTo(STRING);
        assertThat(reader.nextString()).isEqualTo("b");
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void endArrayTooEarly() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList("s"));
        reader.beginArray();
        try {
            reader.endArray();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected END_ARRAY but was s, a java.lang.String, at path $[0]");
        }
    }

    @Test
    public void endObjectTooEarly() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonMap("a", "b"));
        reader.beginObject();
        try {
            reader.endObject();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessageStartingWith("Expected END_OBJECT but was a=b");
        }
    }

    @Test
    public void unsupportedType() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList(new StringBuilder("x")));
        reader.beginArray();
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a JSON value but was x, a java.lang.StringBuilder, at path $[0]");
        }
    }

    @Test
    public void unsupportedKeyType() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonMap(new StringBuilder("x"), "y"));
        reader.beginObject();
        try {
            reader.nextName();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected NAME but was x, a java.lang.StringBuilder, at path $.");
        }
    }

    @Test
    public void nullKey() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonMap(null, "y"));
        reader.beginObject();
        try {
            reader.nextName();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected NAME but was null at path $.");
        }
    }

    @Test
    public void unexpectedIntType() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList(new StringBuilder("1")));
        reader.beginArray();
        try {
            reader.nextInt();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected NUMBER but was 1, a java.lang.StringBuilder, at path $[0]");
        }
    }

    @Test
    public void unexpectedLongType() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList(new StringBuilder("1")));
        reader.beginArray();
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected NUMBER but was 1, a java.lang.StringBuilder, at path $[0]");
        }
    }

    @Test
    public void unexpectedDoubleType() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList(new StringBuilder("1")));
        reader.beginArray();
        try {
            reader.nextDouble();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected NUMBER but was 1, a java.lang.StringBuilder, at path $[0]");
        }
    }

    @Test
    public void unexpectedStringType() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList(new StringBuilder("s")));
        reader.beginArray();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected STRING but was s, a java.lang.StringBuilder, at path $[0]");
        }
    }

    @Test
    public void unexpectedBooleanType() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList(new StringBuilder("true")));
        reader.beginArray();
        try {
            reader.nextBoolean();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected BOOLEAN but was true, a java.lang.StringBuilder, at path $[0]");
        }
    }

    @Test
    public void unexpectedNullType() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList(new StringBuilder("null")));
        reader.beginArray();
        try {
            reader.nextNull();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected NULL but was null, a java.lang.StringBuilder, at path $[0]");
        }
    }

    @Test
    public void skipRoot() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList(new StringBuilder("x")));
        reader.skipValue();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void skipListValue() throws Exception {
        List<Object> root = new ArrayList<>();
        root.add("a");
        root.add("b");
        root.add("c");
        JsonReader reader = new JsonValueReader(root);
        reader.beginArray();
        assertThat(reader.getPath()).isEqualTo("$[0]");
        assertThat(reader.nextString()).isEqualTo("a");
        assertThat(reader.getPath()).isEqualTo("$[1]");
        reader.skipValue();
        assertThat(reader.getPath()).isEqualTo("$[2]");
        assertThat(reader.nextString()).isEqualTo("c");
        reader.endArray();
    }

    @Test
    public void skipObjectName() throws Exception {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("a", "s");
        root.put("b", 1.5);
        root.put("c", true);
        JsonReader reader = new JsonValueReader(root);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.getPath()).isEqualTo("$.a");
        assertThat(reader.nextString()).isEqualTo("s");
        assertThat(reader.getPath()).isEqualTo("$.a");
        reader.skipValue();
        assertThat(reader.getPath()).isEqualTo("$.null");
        assertThat(reader.nextDouble()).isEqualTo(1.5);
        assertThat(reader.getPath()).isEqualTo("$.null");
        assertThat(reader.nextName()).isEqualTo("c");
        assertThat(reader.getPath()).isEqualTo("$.c");
        assertThat(reader.nextBoolean()).isEqualTo(true);
        assertThat(reader.getPath()).isEqualTo("$.c");
        reader.endObject();
    }

    @Test
    public void skipObjectValue() throws Exception {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("a", "s");
        root.put("b", 1.5);
        root.put("c", true);
        JsonReader reader = new JsonValueReader(root);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.getPath()).isEqualTo("$.a");
        assertThat(reader.nextString()).isEqualTo("s");
        assertThat(reader.getPath()).isEqualTo("$.a");
        assertThat(reader.nextName()).isEqualTo("b");
        assertThat(reader.getPath()).isEqualTo("$.b");
        reader.skipValue();
        assertThat(reader.getPath()).isEqualTo("$.null");
        assertThat(reader.nextName()).isEqualTo("c");
        assertThat(reader.getPath()).isEqualTo("$.c");
        assertThat(reader.nextBoolean()).isEqualTo(true);
        assertThat(reader.getPath()).isEqualTo("$.c");
        reader.endObject();
    }

    @Test
    public void failOnUnknown() throws Exception {
        JsonReader reader = new JsonValueReader(Collections.singletonList("a"));
        reader.setFailOnUnknown(true);
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Cannot skip unexpected STRING at $[0]");
        }
    }

    @Test
    public void close() throws Exception {
        try {
            JsonReader reader = new JsonValueReader(Collections.singletonList("a"));
            reader.beginArray();
            reader.close();
            reader.nextString();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            JsonReader reader = new JsonValueReader(Collections.singletonList("a"));
            reader.close();
            reader.beginArray();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void numberToStringCoersion() throws Exception {
        JsonReader reader = new JsonValueReader(Arrays.asList(0, 9223372036854775807L, 2.5, 3.01F, "a", "5"));
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("0");
        assertThat(reader.nextString()).isEqualTo("9223372036854775807");
        assertThat(reader.nextString()).isEqualTo("2.5");
        assertThat(reader.nextString()).isEqualTo("3.01");
        assertThat(reader.nextString()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("5");
        reader.endArray();
    }

    @Test
    public void tooDeeplyNestedArrays() throws IOException {
        Object root = Collections.emptyList();
        for (int i = 0; i < ((TestUtil.MAX_DEPTH) + 1); i++) {
            root = Collections.singletonList(root);
        }
        JsonReader reader = new JsonValueReader(root);
        for (int i = 0; i < (TestUtil.MAX_DEPTH); i++) {
            reader.beginArray();
        }
        try {
            reader.beginArray();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage(("Nesting too deep at $" + (TestUtil.repeat("[0]", ((TestUtil.MAX_DEPTH) + 1)))));
        }
    }

    @Test
    public void tooDeeplyNestedObjects() throws IOException {
        Object root = Boolean.TRUE;
        for (int i = 0; i < ((TestUtil.MAX_DEPTH) + 1); i++) {
            root = Collections.singletonMap("a", root);
        }
        JsonReader reader = new JsonValueReader(root);
        for (int i = 0; i < (TestUtil.MAX_DEPTH); i++) {
            reader.beginObject();
            assertThat(reader.nextName()).isEqualTo("a");
        }
        try {
            reader.beginObject();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage((("Nesting too deep at $" + (TestUtil.repeat(".a", TestUtil.MAX_DEPTH))) + "."));
        }
    }
}

