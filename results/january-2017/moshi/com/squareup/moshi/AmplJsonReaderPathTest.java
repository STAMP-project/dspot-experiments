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


package com.squareup.moshi;


@org.junit.runner.RunWith(value = org.junit.runners.Parameterized.class)
public final class AmplJsonReaderPathTest {
    @org.junit.runners.Parameterized.Parameter
    public com.squareup.moshi.JsonCodecFactory factory;

    @org.junit.runners.Parameterized.Parameters(name = "{0}")
    public static java.util.List<java.lang.Object[]> parameters() {
        return com.squareup.moshi.JsonCodecFactory.factories();
    }

    @org.junit.Test
    public void path() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("{\"a\":[2,true,false,null,\"b\",{\"c\":\"d\"},[3]]}");
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.");
        reader.nextName();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[0]");
        reader.nextInt();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[1]");
        reader.nextBoolean();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[2]");
        reader.nextBoolean();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[3]");
        reader.nextNull();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[4]");
        reader.nextString();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[5]");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[5].");
        reader.nextName();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[5].c");
        reader.nextString();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[5].c");
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[6]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[6][0]");
        reader.nextInt();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[6][1]");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a[7]");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a");
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void arrayOfObjects() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("[{},{},{}]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[0]");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[0].");
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[1]");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[1].");
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[2]");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[2].");
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[3]");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void arrayOfArrays() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("[[],[],[]]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[0]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[0][0]");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[1]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[1][0]");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[2]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[2][0]");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[3]");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void objectPath() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("{\"a\":1,\"b\":2}");
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.");
        reader.nextName();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a");
        reader.nextInt();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.a");
        reader.nextName();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.b");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.b");
        reader.nextInt();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.b");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.b");
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.close();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void arrayPath() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("[1,2]");
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[0]");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[0]");
        reader.nextInt();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[1]");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[1]");
        reader.nextInt();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[2]");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[2]");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.peek();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.close();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void multipleTopLevelValuesInOneDocument() throws java.io.IOException {
        org.junit.Assume.assumeTrue(factory.encodesToBytes());
        com.squareup.moshi.JsonReader reader = factory.newReader("[][]");
        reader.setLenient(true);
        reader.beginArray();
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
        reader.beginArray();
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void skipArrayElements() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("[1,2,3]");
        reader.beginArray();
        reader.skipValue();
        reader.skipValue();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[2]");
    }

    @org.junit.Test
    public void skipObjectNames() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("{\"a\":1}");
        reader.beginObject();
        reader.skipValue();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.null");
    }

    @org.junit.Test
    public void skipObjectValues() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("{\"a\":1,\"b\":2}");
        reader.beginObject();
        reader.nextName();
        reader.skipValue();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.null");
        reader.nextName();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$.b");
    }

    @org.junit.Test
    public void skipNestedStructures() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = factory.newReader("[[1,2,3],4]");
        reader.beginArray();
        reader.skipValue();
        org.assertj.core.api.Assertions.assertThat(reader.getPath()).isEqualTo("$[1]");
    }
}

