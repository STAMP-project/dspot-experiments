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
public final class AmplJsonWriterPathTest {
    @org.junit.runners.Parameterized.Parameter
    public com.squareup.moshi.JsonCodecFactory factory;

    @org.junit.runners.Parameterized.Parameters(name = "{0}")
    public static java.util.List<java.lang.Object[]> parameters() {
        return com.squareup.moshi.JsonCodecFactory.factories();
    }

    @org.junit.Test
    public void path() throws java.io.IOException {
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.");
        writer.name("a");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[0]");
        writer.value(2);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[1]");
        writer.value(true);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[2]");
        writer.value(false);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[3]");
        writer.nullValue();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[4]");
        writer.value("b");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[5]");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[5].");
        writer.name("c");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[5].c");
        writer.value("d");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[5].c");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[6]");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[6][0]");
        writer.value(3);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[6][1]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a[7]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void arrayOfObjects() throws java.io.IOException {
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0]");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0].");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[1]");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[1].");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[2]");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[2].");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[3]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void arrayOfArrays() throws java.io.IOException {
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0]");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0][0]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[1]");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[1][0]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[2]");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[2][0]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[3]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void objectPath() throws java.io.IOException {
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.");
        writer.name("a");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a");
        writer.value(1);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a");
        writer.name("b");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.b");
        writer.value(2);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.b");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.close();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void nestedObjects() throws java.io.IOException {
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.");
        writer.name("a");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a.");
        writer.name("b");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a.b");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a.b.");
        writer.name("c");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a.b.c");
        writer.nullValue();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a.b.c");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a.b");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void arrayPath() throws java.io.IOException {
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0]");
        writer.value(1);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[1]");
        writer.value(true);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[2]");
        writer.value("a");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[3]");
        writer.value(5.5);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[4]");
        writer.value(java.math.BigInteger.ONE);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[5]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.close();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void nestedArrays() throws java.io.IOException {
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0]");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0][0]");
        writer.beginArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0][0][0]");
        writer.nullValue();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0][0][1]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[0][1]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$[1]");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.close();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void multipleTopLevelValuesInOneDocument() throws java.io.IOException {
        org.junit.Assume.assumeTrue(factory.encodesToBytes());
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        writer.setLenient(true);
        writer.beginArray();
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.beginArray();
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }

    @org.junit.Test
    public void skipNulls() throws java.io.IOException {
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        writer.setSerializeNulls(false);
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
        writer.beginObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.");
        writer.name("a");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a");
        writer.nullValue();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.a");
        writer.name("b");
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.b");
        writer.nullValue();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$.b");
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(writer.getPath()).isEqualTo("$");
    }
}

