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


@org.junit.runner.RunWith(value = org.junit.runners.Parameterized.class)
public final class AmplJsonAdapterTest {
    @org.junit.runners.Parameterized.Parameter
    public com.squareup.moshi.JsonCodecFactory factory;

    @org.junit.runners.Parameterized.Parameters(name = "{0}")
    public static java.util.List<java.lang.Object[]> parameters() {
        return com.squareup.moshi.JsonCodecFactory.factories();
    }

    @org.junit.Test
    public void lenient() throws java.lang.Exception {
        com.squareup.moshi.JsonAdapter<java.lang.Double> lenient = new com.squareup.moshi.JsonAdapter<java.lang.Double>() {
            @java.lang.Override
            public java.lang.Double fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                return reader.nextDouble();
            }

            @java.lang.Override
            public void toJson(com.squareup.moshi.JsonWriter writer, java.lang.Double value) throws java.io.IOException {
                writer.value(value);
            }
        }.lenient();
        com.squareup.moshi.JsonReader reader = factory.newReader("[-Infinity, NaN, Infinity]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(lenient.fromJson(reader)).isEqualTo(java.lang.Double.NEGATIVE_INFINITY);
        org.assertj.core.api.Assertions.assertThat(lenient.fromJson(reader)).isNaN();
        org.assertj.core.api.Assertions.assertThat(lenient.fromJson(reader)).isEqualTo(java.lang.Double.POSITIVE_INFINITY);
        reader.endArray();
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        writer.beginArray();
        lenient.toJson(writer, java.lang.Double.NEGATIVE_INFINITY);
        lenient.toJson(writer, java.lang.Double.NaN);
        lenient.toJson(writer, java.lang.Double.POSITIVE_INFINITY);
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(factory.json()).isEqualTo("[-Infinity,NaN,Infinity]");
    }

    @org.junit.Test
    public void nullSafe() throws java.lang.Exception {
        com.squareup.moshi.JsonAdapter<java.lang.String> toUpperCase = new com.squareup.moshi.JsonAdapter<java.lang.String>() {
            @java.lang.Override
            public java.lang.String fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                return reader.nextString().toUpperCase(java.util.Locale.US);
            }

            @java.lang.Override
            public void toJson(com.squareup.moshi.JsonWriter writer, java.lang.String value) throws java.io.IOException {
                writer.value(value.toUpperCase(java.util.Locale.US));
            }
        }.nullSafe();
        com.squareup.moshi.JsonReader reader = factory.newReader("[\"a\", null, \"c\"]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(toUpperCase.fromJson(reader)).isEqualTo("A");
        org.assertj.core.api.Assertions.assertThat(toUpperCase.fromJson(reader)).isNull();
        org.assertj.core.api.Assertions.assertThat(toUpperCase.fromJson(reader)).isEqualTo("C");
        reader.endArray();
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        writer.beginArray();
        toUpperCase.toJson(writer, "a");
        toUpperCase.toJson(writer, null);
        toUpperCase.toJson(writer, "c");
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(factory.json()).isEqualTo("[\"A\",null,\"C\"]");
    }

    @org.junit.Test
    public void failOnUnknown() throws java.lang.Exception {
        com.squareup.moshi.JsonAdapter<java.lang.String> alwaysSkip = new com.squareup.moshi.JsonAdapter<java.lang.String>() {
            @java.lang.Override
            public java.lang.String fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                reader.skipValue();
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void toJson(com.squareup.moshi.JsonWriter writer, java.lang.String value) throws java.io.IOException {
                throw new java.lang.AssertionError();
            }
        }.failOnUnknown();
        com.squareup.moshi.JsonReader reader = factory.newReader("[\"a\"]");
        reader.beginArray();
        try {
            alwaysSkip.fromJson(reader);
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Cannot skip unexpected STRING at $[0]");
        }
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("a");
        reader.endArray();
    }

    @org.junit.Test
    public void indent() throws java.lang.Exception {
        org.junit.Assume.assumeTrue(factory.encodesToBytes());
        com.squareup.moshi.JsonAdapter<java.util.List<java.lang.String>> indent = new com.squareup.moshi.JsonAdapter<java.util.List<java.lang.String>>() {
            @java.lang.Override
            public java.util.List<java.lang.String> fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void toJson(com.squareup.moshi.JsonWriter writer, java.util.List<java.lang.String> value) throws java.io.IOException {
                writer.beginArray();
                for (java.lang.String s : value) {
                    writer.value(s);
                }
                writer.endArray();
            }
        }.indent("\t\t\t");
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        indent.toJson(writer, java.util.Arrays.asList("a", "b", "c"));
        org.assertj.core.api.Assertions.assertThat(factory.json()).isEqualTo(("" + (((("[\n" + "\t\t\t\"a\",\n") + "\t\t\t\"b\",\n") + "\t\t\t\"c\"\n") + "]")));
    }

    @org.junit.Test
    public void serializeNulls() throws java.lang.Exception {
        com.squareup.moshi.JsonAdapter<java.util.Map<java.lang.String, java.lang.String>> serializeNulls = new com.squareup.moshi.JsonAdapter<java.util.Map<java.lang.String, java.lang.String>>() {
            @java.lang.Override
            public java.util.Map<java.lang.String, java.lang.String> fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void toJson(com.squareup.moshi.JsonWriter writer, java.util.Map<java.lang.String, java.lang.String> map) throws java.io.IOException {
                writer.beginObject();
                for (java.util.Map.Entry<java.lang.String, java.lang.String> entry : map.entrySet()) {
                    writer.name(entry.getKey()).value(entry.getValue());
                }
                writer.endObject();
            }
        }.serializeNulls();
        com.squareup.moshi.JsonWriter writer = factory.newWriter();
        serializeNulls.toJson(writer, java.util.Collections.<java.lang.String, java.lang.String>singletonMap("a", null));
        org.assertj.core.api.Assertions.assertThat(factory.json()).isEqualTo("{\"a\":null}");
    }
}

