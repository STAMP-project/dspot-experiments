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


public final class AmplJsonValueWriterTest {
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test
    public void array() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.beginArray();
        writer.value("s");
        writer.value(1.5);
        writer.value(true);
        writer.nullValue();
        writer.endArray();
        org.assertj.core.api.Assertions.assertThat(((java.util.List<java.lang.Object>) (writer.root()))).containsExactly("s", 1.5, true, null);
    }

    @org.junit.Test
    public void object() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.setSerializeNulls(true);
        writer.beginObject();
        writer.name("a").value("s");
        writer.name("b").value(1.5);
        writer.name("c").value(true);
        writer.name("d").nullValue();
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(((java.util.Map<?, ?>) (writer.root()))).containsExactly(org.assertj.core.api.Assertions.entry("a", "s"), org.assertj.core.api.Assertions.entry("b", 1.5), org.assertj.core.api.Assertions.entry("c", true), org.assertj.core.api.Assertions.entry("d", null));
    }

    @org.junit.Test
    public void repeatedNameThrows() throws java.io.IOException {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.beginObject();
        writer.name("a").value(1L);
        try {
            writer.name("a").value(2L);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Map key 'a' has multiple values at path $.a: 1 and 2");
        }
    }

    @org.junit.Test
    public void valueLongEmitsLong() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.beginArray();
        writer.value(java.lang.Long.MIN_VALUE);
        writer.value((-1L));
        writer.value(0L);
        writer.value(1L);
        writer.value(java.lang.Long.MAX_VALUE);
        writer.endArray();
        java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(java.lang.Long.MIN_VALUE, (-1L), 0L, 1L, java.lang.Long.MAX_VALUE);
        org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @org.junit.Test
    public void valueDoubleEmitsDouble() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.setLenient(true);
        writer.beginArray();
        writer.value((-2.147483649E9));
        writer.value((-2.147483648E9));
        writer.value((-1.0));
        writer.value(0.0);
        writer.value(1.0);
        writer.value(2.147483647E9);
        writer.value(2.147483648E9);
        writer.value(9.007199254740991E15);
        writer.value(9.007199254740992E15);
        writer.value(9.007199254740994E15);
        writer.value(9.223372036854776E18);
        writer.value((-0.5));
        writer.value((-0.0));
        writer.value(0.5);
        writer.value(9.22337203685478E18);
        writer.value(java.lang.Double.NEGATIVE_INFINITY);
        writer.value(java.lang.Double.MIN_VALUE);
        writer.value(java.lang.Double.MIN_NORMAL);
        writer.value((-(java.lang.Double.MIN_NORMAL)));
        writer.value(java.lang.Double.MAX_VALUE);
        writer.value(java.lang.Double.POSITIVE_INFINITY);
        writer.value(java.lang.Double.NaN);
        writer.endArray();
        java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList((-2.147483649E9), (-2.147483648E9), (-1.0), 0.0, 1.0, 2.147483647E9, 2.147483648E9, 9.007199254740991E15, 9.007199254740992E15, 9.007199254740994E15, 9.223372036854776E18, (-0.5), (-0.0), 0.5, 9.22337203685478E18, java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.MIN_VALUE, java.lang.Double.MIN_NORMAL, (-(java.lang.Double.MIN_NORMAL)), java.lang.Double.MAX_VALUE, java.lang.Double.POSITIVE_INFINITY, java.lang.Double.NaN);
        org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @org.junit.Test
    public void primitiveIntegerTypesEmitLong() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.beginArray();
        writer.value(new java.lang.Byte(java.lang.Byte.MIN_VALUE));
        writer.value(new java.lang.Short(java.lang.Short.MIN_VALUE));
        writer.value(new java.lang.Integer(java.lang.Integer.MIN_VALUE));
        writer.value(new java.lang.Long(java.lang.Long.MIN_VALUE));
        writer.endArray();
        java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList((-128L), (-32768L), (-2147483648L), -9223372036854775808L);
        org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @org.junit.Test
    public void primitiveFloatingPointTypesEmitDouble() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.beginArray();
        writer.value(new java.lang.Float(0.5F));
        writer.value(new java.lang.Double(0.5));
        writer.endArray();
        java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(0.5, 0.5);
        org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @org.junit.Test
    public void otherNumberTypesEmitBigDecimal() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.beginArray();
        writer.value(new java.util.concurrent.atomic.AtomicInteger(-2147483648));
        writer.value(new java.util.concurrent.atomic.AtomicLong(-9223372036854775808L));
        writer.value(new java.math.BigInteger("-9223372036854775808"));
        writer.value(new java.math.BigInteger("-1"));
        writer.value(new java.math.BigInteger("0"));
        writer.value(new java.math.BigInteger("1"));
        writer.value(new java.math.BigInteger("9223372036854775807"));
        writer.value(new java.math.BigDecimal("-9223372036854775808"));
        writer.value(new java.math.BigDecimal("-1"));
        writer.value(new java.math.BigDecimal("0"));
        writer.value(new java.math.BigDecimal("1"));
        writer.value(new java.math.BigDecimal("9223372036854775807"));
        writer.value(new java.math.BigInteger("-9223372036854775809"));
        writer.value(new java.math.BigInteger("9223372036854775808"));
        writer.value(new java.math.BigDecimal("-9223372036854775809"));
        writer.value(new java.math.BigDecimal("9223372036854775808"));
        writer.value(new java.math.BigDecimal("0.5"));
        writer.value(new java.math.BigDecimal("100000e15"));
        writer.value(new java.math.BigDecimal("0.0000100e-10"));
        writer.endArray();
        java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-2147483648"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-1"), new java.math.BigDecimal("0"), new java.math.BigDecimal("1"), new java.math.BigDecimal("9223372036854775807"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-1"), new java.math.BigDecimal("0"), new java.math.BigDecimal("1"), new java.math.BigDecimal("9223372036854775807"), new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("9223372036854775808"), new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("9223372036854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("100000e15"), new java.math.BigDecimal("0.0000100e-10"));
        org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @org.junit.Test
    public void valueCustomNumberTypeEmitsLongOrBigDecimal() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        writer.beginArray();
        writer.value(stringNumber("-9223372036854775809"));
        writer.value(stringNumber("-9223372036854775808"));
        writer.value(stringNumber("0.5"));
        writer.value(stringNumber("1.0"));
        writer.endArray();
        java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("1.0"));
        org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
    }

    /**
     * Returns an instance of number whose {@link #toString} is {@code s}. Using the standard number
     * methods like {@link Number#doubleValue} are awkward because they may truncate or discard
     * precision.
     */
    private java.lang.Number stringNumber(final java.lang.String s) {
        return new java.lang.Number() {
            @java.lang.Override
            public int intValue() {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public long longValue() {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public float floatValue() {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public double doubleValue() {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public java.lang.String toString() {
                return s;
            }
        };
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#array */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void array_add1_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // MethodCallAdder
            writer.beginArray();
            writer.beginArray();
            writer.value("s");
            writer.value(1.5);
            writer.value(true);
            writer.nullValue();
            writer.endArray();
            org.assertj.core.api.Assertions.assertThat(((java.util.List<java.lang.Object>) (writer.root()))).containsExactly("s", 1.5, true, null);
            org.junit.Assert.fail("array_add1 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#array */
    @java.lang.SuppressWarnings(value = "unchecked")
    @org.junit.Test(timeout = 10000)
    public void array_add1_failAssert0_add8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_array_add1_failAssert0_add8__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_array_add1_failAssert0_add8__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_array_add1_failAssert0_add8__5).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_array_add1_failAssert0_add8__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_array_add1_failAssert0_add8__5).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_array_add1_failAssert0_add8__5).isLenient());
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_array_add1_failAssert0_add8__7 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_array_add1_failAssert0_add8__7).getPath(), "$[0][0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_array_add1_failAssert0_add8__7.equals(o_array_add1_failAssert0_add8__5));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_array_add1_failAssert0_add8__7).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_array_add1_failAssert0_add8__7).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_array_add1_failAssert0_add8__7).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_array_add1_failAssert0_add8__7.equals(writer));
            writer.beginArray();
            writer.value("s");
            writer.value(1.5);
            writer.value(true);
            writer.nullValue();
            writer.endArray();
            org.assertj.core.api.Assertions.assertThat(((java.util.List<java.lang.Object>) (writer.root()))).containsExactly("s", 1.5, true, null);
            org.junit.Assert.fail("array_add1 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#object */
    @org.junit.Test(timeout = 10000)
    public void object_add46() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        // MethodCallAdder
        writer.setSerializeNulls(true);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)writer).getSerializeNulls());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)writer).isLenient());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getPath(), "$");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getIndent(), "");
        writer.setSerializeNulls(true);
        writer.beginObject();
        writer.name("a").value("s");
        writer.name("b").value(1.5);
        writer.name("c").value(true);
        writer.name("d").nullValue();
        writer.endObject();
        org.assertj.core.api.Assertions.assertThat(((java.util.Map<?, ?>) (writer.root()))).containsExactly(org.assertj.core.api.Assertions.entry("a", "s"), org.assertj.core.api.Assertions.entry("b", 1.5), org.assertj.core.api.Assertions.entry("c", true), org.assertj.core.api.Assertions.entry("d", null));
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#object */
    @org.junit.Test(timeout = 10000)
    public void object_add47_failAssert0_add69() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            writer.setSerializeNulls(true);
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_object_add47_failAssert0_add69__6 = // MethodCallAdder
writer.beginObject();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)o_object_add47_failAssert0_add69__6).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_object_add47_failAssert0_add69__6).getPath(), "$.");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_object_add47_failAssert0_add69__6.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_object_add47_failAssert0_add69__6).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_object_add47_failAssert0_add69__6).isLenient());
            writer.beginObject();
            writer.name("a").value("s");
            writer.name("b").value(1.5);
            writer.name("c").value(true);
            // MethodCallAdder
            writer.name("d").nullValue();
            writer.name("d").nullValue();
            writer.endObject();
            org.assertj.core.api.Assertions.assertThat(((java.util.Map<?, ?>) (writer.root()))).containsExactly(org.assertj.core.api.Assertions.entry("a", "s"), org.assertj.core.api.Assertions.entry("b", 1.5), org.assertj.core.api.Assertions.entry("c", true), org.assertj.core.api.Assertions.entry("d", null));
            org.junit.Assert.fail("object_add47 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#object */
    @org.junit.Test(timeout = 10000)
    public void object_add47_failAssert0_add63_add178() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // MethodCallAdder
            writer.setSerializeNulls(true);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)writer).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)writer).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getPath(), "$");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getIndent(), "");
            // MethodCallAdder
            writer.setSerializeNulls(true);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)writer).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)writer).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getPath(), "$");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)writer).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)writer).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getPath(), "$");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getIndent(), "");
            writer.setSerializeNulls(true);
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_object_add47_failAssert0_add63__8 = // MethodCallAdder
writer.beginObject();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_object_add47_failAssert0_add63__8).getPath(), "$.");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_object_add47_failAssert0_add63__8).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)o_object_add47_failAssert0_add63__8).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_object_add47_failAssert0_add63__8.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_object_add47_failAssert0_add63__8).getIndent(), "");
            writer.beginObject();
            writer.name("a").value("s");
            writer.name("b").value(1.5);
            writer.name("c").value(true);
            writer.name("d").nullValue();
            writer.endObject();
            org.assertj.core.api.Assertions.assertThat(((java.util.Map<?, ?>) (writer.root()))).containsExactly(org.assertj.core.api.Assertions.entry("a", "s"), org.assertj.core.api.Assertions.entry("b", 1.5), org.assertj.core.api.Assertions.entry("c", true), org.assertj.core.api.Assertions.entry("d", null));
            org.junit.Assert.fail("object_add47 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#otherNumberTypesEmitBigDecimal */
    @org.junit.Test(timeout = 10000)
    public void otherNumberTypesEmitBigDecimal_add328_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // MethodCallAdder
            writer.beginArray();
            writer.beginArray();
            writer.value(new java.util.concurrent.atomic.AtomicInteger(-2147483648));
            writer.value(new java.util.concurrent.atomic.AtomicLong(-9223372036854775808L));
            writer.value(new java.math.BigInteger("-9223372036854775808"));
            writer.value(new java.math.BigInteger("-1"));
            writer.value(new java.math.BigInteger("0"));
            writer.value(new java.math.BigInteger("1"));
            writer.value(new java.math.BigInteger("9223372036854775807"));
            writer.value(new java.math.BigDecimal("-9223372036854775808"));
            writer.value(new java.math.BigDecimal("-1"));
            writer.value(new java.math.BigDecimal("0"));
            writer.value(new java.math.BigDecimal("1"));
            writer.value(new java.math.BigDecimal("9223372036854775807"));
            writer.value(new java.math.BigInteger("-9223372036854775809"));
            writer.value(new java.math.BigInteger("9223372036854775808"));
            writer.value(new java.math.BigDecimal("-9223372036854775809"));
            writer.value(new java.math.BigDecimal("9223372036854775808"));
            writer.value(new java.math.BigDecimal("0.5"));
            writer.value(new java.math.BigDecimal("100000e15"));
            writer.value(new java.math.BigDecimal("0.0000100e-10"));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-2147483648"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-1"), new java.math.BigDecimal("0"), new java.math.BigDecimal("1"), new java.math.BigDecimal("9223372036854775807"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-1"), new java.math.BigDecimal("0"), new java.math.BigDecimal("1"), new java.math.BigDecimal("9223372036854775807"), new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("9223372036854775808"), new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("9223372036854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("100000e15"), new java.math.BigDecimal("0.0000100e-10"));
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("otherNumberTypesEmitBigDecimal_add328 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#otherNumberTypesEmitBigDecimal */
    @org.junit.Test(timeout = 10000)
    public void otherNumberTypesEmitBigDecimal_literalMutation416_failAssert83_add8703() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_otherNumberTypesEmitBigDecimal_literalMutation416_failAssert83_add8703__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_otherNumberTypesEmitBigDecimal_literalMutation416_failAssert83_add8703__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_otherNumberTypesEmitBigDecimal_literalMutation416_failAssert83_add8703__5).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_otherNumberTypesEmitBigDecimal_literalMutation416_failAssert83_add8703__5).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_otherNumberTypesEmitBigDecimal_literalMutation416_failAssert83_add8703__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_otherNumberTypesEmitBigDecimal_literalMutation416_failAssert83_add8703__5).getSerializeNulls());
            writer.beginArray();
            writer.value(new java.util.concurrent.atomic.AtomicInteger(-2147483648));
            writer.value(new java.util.concurrent.atomic.AtomicLong(-9223372036854775808L));
            writer.value(new java.math.BigInteger("-9223372036854775808"));
            writer.value(new java.math.BigInteger("-1"));
            writer.value(new java.math.BigInteger("0"));
            writer.value(new java.math.BigInteger("1"));
            writer.value(new java.math.BigInteger("9223372036854775807"));
            writer.value(new java.math.BigDecimal("-9223372036854775808"));
            writer.value(new java.math.BigDecimal("-1"));
            writer.value(new java.math.BigDecimal("0"));
            writer.value(new java.math.BigDecimal("1"));
            writer.value(new java.math.BigDecimal("9223372036854775807"));
            writer.value(new java.math.BigInteger("-9223372036854775809"));
            writer.value(new java.math.BigInteger("9223372036854775808"));
            writer.value(new java.math.BigDecimal("-9223372036854775809"));
            writer.value(new java.math.BigDecimal("9223372036854775808"));
            writer.value(new java.math.BigDecimal("0.5"));
            writer.value(new java.math.BigDecimal("_&Ml%;sG#"));
            writer.value(new java.math.BigDecimal("0.0000100e-10"));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-2147483648"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-1"), new java.math.BigDecimal("0"), new java.math.BigDecimal("1"), new java.math.BigDecimal("9223372036854775807"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-1"), new java.math.BigDecimal("0"), new java.math.BigDecimal("1"), new java.math.BigDecimal("9223372036854775807"), new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("9223372036854775808"), new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("9223372036854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("100000e15"), new java.math.BigDecimal("0.0000100e-10"));
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("otherNumberTypesEmitBigDecimal_literalMutation416 should have thrown NumberFormatException");
        } catch (java.lang.NumberFormatException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#otherNumberTypesEmitBigDecimal */
    @org.junit.Test(timeout = 10000)
    public void otherNumberTypesEmitBigDecimal_add328_failAssert0_literalMutation637_failAssert3_literalMutation21818() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
                // AssertGenerator replace invocation
                com.squareup.moshi.JsonWriter o_otherNumberTypesEmitBigDecimal_add328_failAssert0_literalMutation637_failAssert3_literalMutation21818__7 = // MethodCallAdder
writer.beginArray();
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_otherNumberTypesEmitBigDecimal_add328_failAssert0_literalMutation637_failAssert3_literalMutation21818__7).isLenient());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_otherNumberTypesEmitBigDecimal_add328_failAssert0_literalMutation637_failAssert3_literalMutation21818__7).getIndent(), "");
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_otherNumberTypesEmitBigDecimal_add328_failAssert0_literalMutation637_failAssert3_literalMutation21818__7.equals(writer));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_otherNumberTypesEmitBigDecimal_add328_failAssert0_literalMutation637_failAssert3_literalMutation21818__7).getPath(), "$[0]");
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_otherNumberTypesEmitBigDecimal_add328_failAssert0_literalMutation637_failAssert3_literalMutation21818__7).getSerializeNulls());
                writer.beginArray();
                writer.value(new java.util.concurrent.atomic.AtomicInteger(-2147483648));
                writer.value(new java.util.concurrent.atomic.AtomicLong(-9223372036854775808L));
                writer.value(new java.math.BigInteger(""));
                writer.value(new java.math.BigInteger("-1"));
                writer.value(new java.math.BigInteger("0"));
                writer.value(new java.math.BigInteger("1"));
                writer.value(new java.math.BigInteger("9223372036854775807"));
                writer.value(new java.math.BigDecimal("-9223372036854775808"));
                writer.value(new java.math.BigDecimal("-1"));
                writer.value(new java.math.BigDecimal("0"));
                writer.value(new java.math.BigDecimal("1"));
                writer.value(new java.math.BigDecimal("9223372036854775807"));
                writer.value(new java.math.BigInteger("-9223372036854775809"));
                writer.value(new java.math.BigInteger("9223372036854775808"));
                writer.value(new java.math.BigDecimal("-9223372036854775809"));
                writer.value(new java.math.BigDecimal("9223372036854775808"));
                writer.value(new java.math.BigDecimal("0.5"));
                writer.value(new java.math.BigDecimal("100000e15"));
                writer.value(new java.math.BigDecimal("0.0000100e-10"));
                writer.endArray();
                java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-2147483648"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-1"), new java.math.BigDecimal("0"), new java.math.BigDecimal("1"), new java.math.BigDecimal("9223372036854775807"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("-1"), new java.math.BigDecimal("0"), new java.math.BigDecimal(""), new java.math.BigDecimal("9223372036854775807"), new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("9223372036854775808"), new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("9223372036854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("100000e15"), new java.math.BigDecimal("0.0000100e-10"));
                org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
                org.junit.Assert.fail("otherNumberTypesEmitBigDecimal_add328 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("otherNumberTypesEmitBigDecimal_add328_failAssert0_literalMutation637 should have thrown NumberFormatException");
        } catch (java.lang.NumberFormatException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#primitiveFloatingPointTypesEmitDouble */
    @org.junit.Test(timeout = 10000)
    public void primitiveFloatingPointTypesEmitDouble_add22312_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // MethodCallAdder
            writer.beginArray();
            writer.beginArray();
            writer.value(new java.lang.Float(0.5F));
            writer.value(new java.lang.Double(0.5));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(0.5, 0.5);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("primitiveFloatingPointTypesEmitDouble_add22312 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#primitiveFloatingPointTypesEmitDouble */
    @org.junit.Test(timeout = 10000)
    public void primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22332() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22332__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22332__5).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22332__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22332__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22332__5).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22332__5).isLenient());
            writer.beginArray();
            writer.value(new java.lang.Float(0.5F));
            writer.value(new java.lang.Double(0.5));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(0.5, 0.5);
            // MethodCallAdder
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("primitiveFloatingPointTypesEmitDouble_add22312 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#primitiveFloatingPointTypesEmitDouble */
    @org.junit.Test(timeout = 10000)
    public void primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328_add22374() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__5).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__5).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__5).isLenient());
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__7 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__7).getPath(), "$[0][0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__7).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__7.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__7).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__7).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__7.equals(o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328__5));
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328_add22374__31 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328_add22374__31.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328_add22374__31).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328_add22374__31).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328_add22374__31).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveFloatingPointTypesEmitDouble_add22312_failAssert0_add22328_add22374__31).getPath(), "$[0][0][0]");
            writer.beginArray();
            writer.value(new java.lang.Float(0.5F));
            writer.value(new java.lang.Double(0.5));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(0.5, 0.5);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("primitiveFloatingPointTypesEmitDouble_add22312 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#primitiveIntegerTypesEmitLong */
    @org.junit.Test(timeout = 10000)
    public void primitiveIntegerTypesEmitLong_add22405_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // MethodCallAdder
            writer.beginArray();
            writer.beginArray();
            writer.value(new java.lang.Byte(java.lang.Byte.MIN_VALUE));
            writer.value(new java.lang.Short(java.lang.Short.MIN_VALUE));
            writer.value(new java.lang.Integer(java.lang.Integer.MIN_VALUE));
            writer.value(new java.lang.Long(java.lang.Long.MIN_VALUE));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList((-128L), (-32768L), (-2147483648L), -9223372036854775808L);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("primitiveIntegerTypesEmitLong_add22405 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#primitiveIntegerTypesEmitLong */
    @org.junit.Test(timeout = 10000)
    public void primitiveIntegerTypesEmitLong_add22405_failAssert0_add22434() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22434__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22434__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22434__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22434__5).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22434__5).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22434__5).getSerializeNulls());
            writer.beginArray();
            writer.value(new java.lang.Byte(java.lang.Byte.MIN_VALUE));
            writer.value(new java.lang.Short(java.lang.Short.MIN_VALUE));
            writer.value(new java.lang.Integer(java.lang.Integer.MIN_VALUE));
            writer.value(new java.lang.Long(java.lang.Long.MIN_VALUE));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList((-128L), (-32768L), (-2147483648L), -9223372036854775808L);
            // MethodCallAdder
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("primitiveIntegerTypesEmitLong_add22405 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#primitiveIntegerTypesEmitLong */
    @org.junit.Test(timeout = 10000)
    public void primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428_add22495() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__5).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__5).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__5).getSerializeNulls());
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__7 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__7).getPath(), "$[0][0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__7).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__7.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__7.equals(o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__5));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__7).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428__7).getSerializeNulls());
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428_add22495__31 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428_add22495__31).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428_add22495__31).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428_add22495__31.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428_add22495__31).getPath(), "$[0][0][0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_primitiveIntegerTypesEmitLong_add22405_failAssert0_add22428_add22495__31).isLenient());
            writer.beginArray();
            writer.value(new java.lang.Byte(java.lang.Byte.MIN_VALUE));
            writer.value(new java.lang.Short(java.lang.Short.MIN_VALUE));
            writer.value(new java.lang.Integer(java.lang.Integer.MIN_VALUE));
            writer.value(new java.lang.Long(java.lang.Long.MIN_VALUE));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList((-128L), (-32768L), (-2147483648L), -9223372036854775808L);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("primitiveIntegerTypesEmitLong_add22405 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#repeatedNameThrows */
    @org.junit.Test(timeout = 10000)
    public void repeatedNameThrows_add22540_failAssert0_add22549() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_repeatedNameThrows_add22540_failAssert0_add22549__5 = // MethodCallAdder
writer.beginObject();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_repeatedNameThrows_add22540_failAssert0_add22549__5).getPath(), "$.");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_repeatedNameThrows_add22540_failAssert0_add22549__5).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_repeatedNameThrows_add22540_failAssert0_add22549__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_repeatedNameThrows_add22540_failAssert0_add22549__5).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_repeatedNameThrows_add22540_failAssert0_add22549__5).getIndent(), "");
            writer.beginObject();
            writer.name("a").value(1L);
            try {
                // MethodCallAdder
                writer.name("a").value(2L);
                writer.name("a").value(2L);
                org.junit.Assert.fail();
            } catch (java.lang.IllegalArgumentException expected) {
                org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Map key 'a' has multiple values at path $.a: 1 and 2");
            }
            org.junit.Assert.fail("repeatedNameThrows_add22540 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#repeatedNameThrows */
    @org.junit.Test(timeout = 10000)
    public void repeatedNameThrows_add22541_failAssert1_add22553_failAssert0_add22605() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
                // AssertGenerator replace invocation
                com.squareup.moshi.JsonWriter o_repeatedNameThrows_add22541_failAssert1_add22553_failAssert0_add22605__7 = // MethodCallAdder
writer.beginObject();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_repeatedNameThrows_add22541_failAssert1_add22553_failAssert0_add22605__7).getIndent(), "");
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_repeatedNameThrows_add22541_failAssert1_add22553_failAssert0_add22605__7).getSerializeNulls());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_repeatedNameThrows_add22541_failAssert1_add22553_failAssert0_add22605__7).getPath(), "$.");
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_repeatedNameThrows_add22541_failAssert1_add22553_failAssert0_add22605__7).isLenient());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_repeatedNameThrows_add22541_failAssert1_add22553_failAssert0_add22605__7.equals(writer));
                writer.beginObject();
                // MethodCallAdder
                writer.name("a").value(1L);
                // MethodCallAdder
                writer.name("a").value(1L);
                writer.name("a").value(1L);
                try {
                    writer.name("a").value(2L);
                    org.junit.Assert.fail();
                } catch (java.lang.IllegalArgumentException expected) {
                    org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Map key 'a' has multiple values at path $.a: 1 and 2");
                }
                org.junit.Assert.fail("repeatedNameThrows_add22541 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("repeatedNameThrows_add22541_failAssert1_add22553 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueCustomNumberTypeEmitsLongOrBigDecimal */
    @org.junit.Test(timeout = 10000)
    public void valueCustomNumberTypeEmitsLongOrBigDecimal_add22611_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // MethodCallAdder
            writer.beginArray();
            writer.beginArray();
            writer.value(stringNumber("-9223372036854775809"));
            writer.value(stringNumber("-9223372036854775808"));
            writer.value(stringNumber("0.5"));
            writer.value(stringNumber("1.0"));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("1.0"));
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("valueCustomNumberTypeEmitsLongOrBigDecimal_add22611 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueCustomNumberTypeEmitsLongOrBigDecimal */
    @org.junit.Test(timeout = 10000)
    public void valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22627_failAssert15_add22877() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22627_failAssert15_add22877__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22627_failAssert15_add22877__5).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22627_failAssert15_add22877__5).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22627_failAssert15_add22877__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22627_failAssert15_add22877__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22627_failAssert15_add22877__5).getIndent(), "");
            writer.beginArray();
            writer.value(stringNumber("-9223372036854775809"));
            writer.value(stringNumber("-9223372036854775808"));
            writer.value(stringNumber("0.5"));
            writer.value(stringNumber("1.0"));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("-9223372l36854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("1.0"));
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22627 should have thrown NumberFormatException");
        } catch (java.lang.NumberFormatException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueCustomNumberTypeEmitsLongOrBigDecimal */
    @org.junit.Test(timeout = 10000)
    public void valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622_failAssert10_add22772_literalMutation23339_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
                // AssertGenerator replace invocation
                com.squareup.moshi.JsonWriter o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622_failAssert10_add22772__5 = // MethodCallAdder
writer.beginArray();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622_failAssert10_add22772__5).getPath(), "$[0]");
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622_failAssert10_add22772__5).getSerializeNulls());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622_failAssert10_add22772__5).getIndent(), "");
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622_failAssert10_add22772__5).isLenient());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622_failAssert10_add22772__5.equals(writer));
                writer.beginArray();
                writer.value(stringNumber("-9223372036854775809"));
                writer.value(stringNumber("-9223372036854775808"));
                writer.value(stringNumber("0.5"));
                writer.value(stringNumber("1.0"));
                writer.endArray();
                java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("1.0"));
                org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
                org.junit.Assert.fail("valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622 should have thrown NumberFormatException");
            } catch (java.lang.NumberFormatException eee) {
            }
            org.junit.Assert.fail("valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22622_failAssert10_add22772_literalMutation23339 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueCustomNumberTypeEmitsLongOrBigDecimal */
    @org.junit.Test(timeout = 10000)
    public void valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060_add23276() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060__5).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060__5).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060__5).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060__5.equals(writer));
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060_add23276__17 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060_add23276__17).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060_add23276__17).getPath(), "$[0][0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060_add23276__17).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060_add23276__17.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636_failAssert24_add23060_add23276__17).getIndent(), "");
            writer.beginArray();
            writer.value(stringNumber("-9223372036854775809"));
            writer.value(stringNumber("-9223372036854775808"));
            writer.value(stringNumber("0.5"));
            writer.value(stringNumber("1.0"));
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(new java.math.BigDecimal("-9223372036854775809"), new java.math.BigDecimal("-9223372036854775808"), new java.math.BigDecimal("0.5"), new java.math.BigDecimal("l.0"));
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("valueCustomNumberTypeEmitsLongOrBigDecimal_literalMutation22636 should have thrown NumberFormatException");
        } catch (java.lang.NumberFormatException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueDoubleEmitsDouble */
    @org.junit.Test(timeout = 10000)
    public void valueDoubleEmitsDouble_add23654() throws java.lang.Exception {
        com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
        // MethodCallAdder
        writer.setLenient(true);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)writer).getSerializeNulls());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)writer).isLenient());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getPath(), "$");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getIndent(), "");
        writer.setLenient(true);
        writer.beginArray();
        writer.value((-2.147483649E9));
        writer.value((-2.147483648E9));
        writer.value((-1.0));
        writer.value(0.0);
        writer.value(1.0);
        writer.value(2.147483647E9);
        writer.value(2.147483648E9);
        writer.value(9.007199254740991E15);
        writer.value(9.007199254740992E15);
        writer.value(9.007199254740994E15);
        writer.value(9.223372036854776E18);
        writer.value((-0.5));
        writer.value((-0.0));
        writer.value(0.5);
        writer.value(9.22337203685478E18);
        writer.value(java.lang.Double.NEGATIVE_INFINITY);
        writer.value(java.lang.Double.MIN_VALUE);
        writer.value(java.lang.Double.MIN_NORMAL);
        writer.value((-(java.lang.Double.MIN_NORMAL)));
        writer.value(java.lang.Double.MAX_VALUE);
        writer.value(java.lang.Double.POSITIVE_INFINITY);
        writer.value(java.lang.Double.NaN);
        writer.endArray();
        java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList((-2.147483649E9), (-2.147483648E9), (-1.0), 0.0, 1.0, 2.147483647E9, 2.147483648E9, 9.007199254740991E15, 9.007199254740992E15, 9.007199254740994E15, 9.223372036854776E18, (-0.5), (-0.0), 0.5, 9.22337203685478E18, java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.MIN_VALUE, java.lang.Double.MIN_NORMAL, (-(java.lang.Double.MIN_NORMAL)), java.lang.Double.MAX_VALUE, java.lang.Double.POSITIVE_INFINITY, java.lang.Double.NaN);
        org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueDoubleEmitsDouble */
    @org.junit.Test(timeout = 10000)
    public void valueDoubleEmitsDouble_add23655_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            writer.setLenient(true);
            // MethodCallAdder
            writer.beginArray();
            writer.beginArray();
            writer.value((-2.147483649E9));
            writer.value((-2.147483648E9));
            writer.value((-1.0));
            writer.value(0.0);
            writer.value(1.0);
            writer.value(2.147483647E9);
            writer.value(2.147483648E9);
            writer.value(9.007199254740991E15);
            writer.value(9.007199254740992E15);
            writer.value(9.007199254740994E15);
            writer.value(9.223372036854776E18);
            writer.value((-0.5));
            writer.value((-0.0));
            writer.value(0.5);
            writer.value(9.22337203685478E18);
            writer.value(java.lang.Double.NEGATIVE_INFINITY);
            writer.value(java.lang.Double.MIN_VALUE);
            writer.value(java.lang.Double.MIN_NORMAL);
            writer.value((-(java.lang.Double.MIN_NORMAL)));
            writer.value(java.lang.Double.MAX_VALUE);
            writer.value(java.lang.Double.POSITIVE_INFINITY);
            writer.value(java.lang.Double.NaN);
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList((-2.147483649E9), (-2.147483648E9), (-1.0), 0.0, 1.0, 2.147483647E9, 2.147483648E9, 9.007199254740991E15, 9.007199254740992E15, 9.007199254740994E15, 9.223372036854776E18, (-0.5), (-0.0), 0.5, 9.22337203685478E18, java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.MIN_VALUE, java.lang.Double.MIN_NORMAL, (-(java.lang.Double.MIN_NORMAL)), java.lang.Double.MAX_VALUE, java.lang.Double.POSITIVE_INFINITY, java.lang.Double.NaN);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("valueDoubleEmitsDouble_add23655 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueDoubleEmitsDouble */
    @org.junit.Test(timeout = 10000)
    public void valueDoubleEmitsDouble_add23655_failAssert0_add23795_add24437() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // MethodCallAdder
            writer.setLenient(true);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)writer).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)writer).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getPath(), "$");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)writer).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)writer).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getPath(), "$");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getIndent(), "");
            // MethodCallAdder
            writer.setLenient(true);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)writer).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)writer).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getPath(), "$");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)writer).getIndent(), "");
            writer.setLenient(true);
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_valueDoubleEmitsDouble_add23655_failAssert0_add23795__8 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.moshi.JsonValueWriter)o_valueDoubleEmitsDouble_add23655_failAssert0_add23795__8).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueDoubleEmitsDouble_add23655_failAssert0_add23795__8).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_valueDoubleEmitsDouble_add23655_failAssert0_add23795__8.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueDoubleEmitsDouble_add23655_failAssert0_add23795__8).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueDoubleEmitsDouble_add23655_failAssert0_add23795__8).getSerializeNulls());
            writer.beginArray();
            writer.value((-2.147483649E9));
            writer.value((-2.147483648E9));
            writer.value((-1.0));
            writer.value(0.0);
            writer.value(1.0);
            writer.value(2.147483647E9);
            writer.value(2.147483648E9);
            writer.value(9.007199254740991E15);
            writer.value(9.007199254740992E15);
            writer.value(9.007199254740994E15);
            writer.value(9.223372036854776E18);
            writer.value((-0.5));
            writer.value((-0.0));
            writer.value(0.5);
            writer.value(9.22337203685478E18);
            writer.value(java.lang.Double.NEGATIVE_INFINITY);
            writer.value(java.lang.Double.MIN_VALUE);
            writer.value(java.lang.Double.MIN_NORMAL);
            writer.value((-(java.lang.Double.MIN_NORMAL)));
            writer.value(java.lang.Double.MAX_VALUE);
            writer.value(java.lang.Double.POSITIVE_INFINITY);
            writer.value(java.lang.Double.NaN);
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList((-2.147483649E9), (-2.147483648E9), (-1.0), 0.0, 1.0, 2.147483647E9, 2.147483648E9, 9.007199254740991E15, 9.007199254740992E15, 9.007199254740994E15, 9.223372036854776E18, (-0.5), (-0.0), 0.5, 9.22337203685478E18, java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.MIN_VALUE, java.lang.Double.MIN_NORMAL, (-(java.lang.Double.MIN_NORMAL)), java.lang.Double.MAX_VALUE, java.lang.Double.POSITIVE_INFINITY, java.lang.Double.NaN);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("valueDoubleEmitsDouble_add23655 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueLongEmitsLong */
    @org.junit.Test(timeout = 10000)
    public void valueLongEmitsLong_add24507_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // MethodCallAdder
            writer.beginArray();
            writer.beginArray();
            writer.value(java.lang.Long.MIN_VALUE);
            writer.value((-1L));
            writer.value(0L);
            writer.value(1L);
            writer.value(java.lang.Long.MAX_VALUE);
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(java.lang.Long.MIN_VALUE, (-1L), 0L, 1L, java.lang.Long.MAX_VALUE);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("valueLongEmitsLong_add24507 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonValueWriterTest#valueLongEmitsLong */
    @org.junit.Test(timeout = 10000)
    public void valueLongEmitsLong_add24507_failAssert0_add24522() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonValueWriter writer = new com.squareup.moshi.JsonValueWriter();
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_valueLongEmitsLong_add24507_failAssert0_add24522__5 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueLongEmitsLong_add24507_failAssert0_add24522__5).getSerializeNulls());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_valueLongEmitsLong_add24507_failAssert0_add24522__5.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueLongEmitsLong_add24507_failAssert0_add24522__5).getPath(), "$[0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueLongEmitsLong_add24507_failAssert0_add24522__5).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueLongEmitsLong_add24507_failAssert0_add24522__5).getIndent(), "");
            // AssertGenerator replace invocation
            com.squareup.moshi.JsonWriter o_valueLongEmitsLong_add24507_failAssert0_add24522__7 = // MethodCallAdder
writer.beginArray();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_valueLongEmitsLong_add24507_failAssert0_add24522__7.equals(writer));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_valueLongEmitsLong_add24507_failAssert0_add24522__7.equals(o_valueLongEmitsLong_add24507_failAssert0_add24522__5));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueLongEmitsLong_add24507_failAssert0_add24522__7).isLenient());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueLongEmitsLong_add24507_failAssert0_add24522__7).getIndent(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonValueWriter)o_valueLongEmitsLong_add24507_failAssert0_add24522__7).getPath(), "$[0][0]");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonValueWriter)o_valueLongEmitsLong_add24507_failAssert0_add24522__7).getSerializeNulls());
            writer.beginArray();
            writer.value(java.lang.Long.MIN_VALUE);
            writer.value((-1L));
            writer.value(0L);
            writer.value(1L);
            writer.value(java.lang.Long.MAX_VALUE);
            writer.endArray();
            java.util.List<java.lang.Number> numbers = java.util.Arrays.<java.lang.Number>asList(java.lang.Long.MIN_VALUE, (-1L), 0L, 1L, java.lang.Long.MAX_VALUE);
            org.assertj.core.api.Assertions.assertThat(((java.util.List<?>) (writer.root()))).isEqualTo(numbers);
            org.junit.Assert.fail("valueLongEmitsLong_add24507 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }
}

