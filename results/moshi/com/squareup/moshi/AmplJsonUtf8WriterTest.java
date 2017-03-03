/**
 * Copyright (C) 2010 Google Inc.
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


public final class AmplJsonUtf8WriterTest {
    @org.junit.Test
    public void prettyPrintObject() throws java.io.IOException {
        okio.Buffer buffer = new okio.Buffer();
        com.squareup.moshi.JsonWriter writer = com.squareup.moshi.JsonWriter.of(buffer);
        writer.setSerializeNulls(true);
        writer.setIndent("   ");
        writer.beginObject();
        writer.name("a").value(true);
        writer.name("b").value(false);
        writer.name("c").value(5.0);
        writer.name("e").nullValue();
        writer.name("f").beginArray();
        writer.value(6.0);
        writer.value(7.0);
        writer.endArray();
        writer.name("g").beginObject();
        writer.name("h").value(8.0);
        writer.name("i").value(9.0);
        writer.endObject();
        writer.endObject();
        java.lang.String expected = "{\n" + (((((((((((("   \"a\": true,\n" + "   \"b\": false,\n") + "   \"c\": 5.0,\n") + "   \"e\": null,\n") + "   \"f\": [\n") + "      6.0,\n") + "      7.0\n") + "   ],\n") + "   \"g\": {\n") + "      \"h\": 8.0,\n") + "      \"i\": 9.0\n") + "   }\n") + "}");
        org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).isEqualTo(expected);
    }

    @org.junit.Test
    public void prettyPrintArray() throws java.io.IOException {
        okio.Buffer buffer = new okio.Buffer();
        com.squareup.moshi.JsonWriter writer = com.squareup.moshi.JsonWriter.of(buffer);
        writer.setIndent("   ");
        writer.beginArray();
        writer.value(true);
        writer.value(false);
        writer.value(5.0);
        writer.nullValue();
        writer.beginObject();
        writer.name("a").value(6.0);
        writer.name("b").value(7.0);
        writer.endObject();
        writer.beginArray();
        writer.value(8.0);
        writer.value(9.0);
        writer.endArray();
        writer.endArray();
        java.lang.String expected = "[\n" + (((((((((((("   true,\n" + "   false,\n") + "   5.0,\n") + "   null,\n") + "   {\n") + "      \"a\": 6.0,\n") + "      \"b\": 7.0\n") + "   },\n") + "   [\n") + "      8.0,\n") + "      9.0\n") + "   ]\n") + "]");
        org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).isEqualTo(expected);
    }

    @org.junit.Test
    public void repeatedNameIgnored() throws java.io.IOException {
        okio.Buffer buffer = new okio.Buffer();
        com.squareup.moshi.JsonWriter writer = com.squareup.moshi.JsonWriter.of(buffer);
        writer.beginObject();
        writer.name("a").value(1);
        writer.name("a").value(2);
        writer.endObject();
        // JsonWriter doesn't attempt to detect duplicate names
        org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).isEqualTo("{\"a\":1,\"a\":2}");
    }

    /* amplification of com.squareup.moshi.JsonUtf8WriterTest#prettyPrintObject */
    @org.junit.Test(timeout = 10000)
    public void prettyPrintObject_add13114_failAssert0_add13208() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            okio.Buffer buffer = new okio.Buffer();
            com.squareup.moshi.JsonWriter writer = com.squareup.moshi.JsonWriter.of(buffer);
            writer.setSerializeNulls(true);
            writer.setIndent("   ");
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_prettyPrintObject_add13114_failAssert0_add13208__9 = // MethodCallAdder
writer.beginObject();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Writer)o_prettyPrintObject_add13114_failAssert0_add13208__9).getPath(), "$.");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Writer)o_prettyPrintObject_add13114_failAssert0_add13208__9).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_prettyPrintObject_add13114_failAssert0_add13208__9.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Writer)o_prettyPrintObject_add13114_failAssert0_add13208__9).getIndent(), "   ");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonUtf8Writer)o_prettyPrintObject_add13114_failAssert0_add13208__9).getSerializeNulls());
            writer.beginObject();
            writer.name("a").value(true);
            writer.name("b").value(false);
            writer.name("c").value(5.0);
            writer.name("e").nullValue();
            writer.name("f").beginArray();
            writer.value(6.0);
            writer.value(7.0);
            writer.endArray();
            writer.name("g").beginObject();
            writer.name("h").value(8.0);
            writer.name("i").value(9.0);
            // MethodCallAdder
            writer.endObject();
            writer.endObject();
            writer.endObject();
            java.lang.String expected = "{\n" + (((((((((((("   \"a\": true,\n" + "   \"b\": false,\n") + "   \"c\": 5.0,\n") + "   \"e\": null,\n") + "   \"f\": [\n") + "      6.0,\n") + "      7.0\n") + "   ],\n") + "   \"g\": {\n") + "      \"h\": 8.0,\n") + "      \"i\": 9.0\n") + "   }\n") + "}");
            org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).isEqualTo(expected);
            org.junit.Assert.fail("prettyPrintObject_add13114 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8WriterTest#repeatedNameIgnored */
    @org.junit.Test(timeout = 10000)
    public void repeatedNameIgnored_add17244_failAssert0_add17250() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            okio.Buffer buffer = new okio.Buffer();
            com.squareup.moshi.JsonWriter writer = com.squareup.moshi.JsonWriter.of(buffer);
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_repeatedNameIgnored_add17244_failAssert0_add17250__7 = // MethodCallAdder
writer.beginObject();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Writer)o_repeatedNameIgnored_add17244_failAssert0_add17250__7).getPath(), "$.");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Writer)o_repeatedNameIgnored_add17244_failAssert0_add17250__7).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Writer)o_repeatedNameIgnored_add17244_failAssert0_add17250__7).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Writer)o_repeatedNameIgnored_add17244_failAssert0_add17250__7).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_repeatedNameIgnored_add17244_failAssert0_add17250__7.equals(writer));
            // MethodCallAdder
            writer.beginObject();
            writer.beginObject();
            writer.name("a").value(1);
            writer.name("a").value(2);
            writer.endObject();
            // JsonWriter doesn't attempt to detect duplicate names
            org.assertj.core.api.Assertions.assertThat(buffer.readUtf8()).isEqualTo("{\"a\":1,\"a\":2}");
            org.junit.Assert.fail("repeatedNameIgnored_add17244 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }
}

