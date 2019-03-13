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


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Test;


public final class JsonValueWriterTest {
    @SuppressWarnings("unchecked")
    @Test
    public void array() throws Exception {
        JsonValueWriter writer = new JsonValueWriter();
        writer.beginArray();
        writer.value("s");
        writer.value(1.5);
        writer.value(true);
        writer.nullValue();
        writer.endArray();
        assertThat(((List<Object>) (writer.root()))).containsExactly("s", 1.5, true, null);
    }

    @Test
    public void object() throws Exception {
        JsonValueWriter writer = new JsonValueWriter();
        writer.setSerializeNulls(true);
        writer.beginObject();
        writer.name("a").value("s");
        writer.name("b").value(1.5);
        writer.name("c").value(true);
        writer.name("d").nullValue();
        writer.endObject();
        assertThat(((java.util.Map<String, Object>) (writer.root()))).containsExactly(new AbstractMap.SimpleEntry<String, Object>("a", "s"), new AbstractMap.SimpleEntry<String, Object>("b", 1.5), new AbstractMap.SimpleEntry<String, Object>("c", true), new AbstractMap.SimpleEntry<String, Object>("d", null));
    }

    @Test
    public void repeatedNameThrows() throws IOException {
        JsonValueWriter writer = new JsonValueWriter();
        writer.beginObject();
        writer.name("a").value(1L);
        try {
            writer.name("a").value(2L);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("Map key 'a' has multiple values at path $.a: 1 and 2");
        }
    }

    @Test
    public void valueLongEmitsLong() throws Exception {
        JsonValueWriter writer = new JsonValueWriter();
        writer.beginArray();
        writer.value(Long.MIN_VALUE);
        writer.value((-1L));
        writer.value(0L);
        writer.value(1L);
        writer.value(Long.MAX_VALUE);
        writer.endArray();
        List<Number> numbers = Arrays.<Number>asList(Long.MIN_VALUE, (-1L), 0L, 1L, Long.MAX_VALUE);
        assertThat(((List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @Test
    public void valueDoubleEmitsDouble() throws Exception {
        JsonValueWriter writer = new JsonValueWriter();
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
        writer.value(Double.NEGATIVE_INFINITY);
        writer.value(Double.MIN_VALUE);
        writer.value(Double.MIN_NORMAL);
        writer.value((-(Double.MIN_NORMAL)));
        writer.value(Double.MAX_VALUE);
        writer.value(Double.POSITIVE_INFINITY);
        writer.value(Double.NaN);
        writer.endArray();
        List<Number> numbers = Arrays.<Number>asList((-2.147483649E9), (-2.147483648E9), (-1.0), 0.0, 1.0, 2.147483647E9, 2.147483648E9, 9.007199254740991E15, 9.007199254740992E15, 9.007199254740994E15, 9.223372036854776E18, (-0.5), (-0.0), 0.5, 9.22337203685478E18, Double.NEGATIVE_INFINITY, Double.MIN_VALUE, Double.MIN_NORMAL, (-(Double.MIN_NORMAL)), Double.MAX_VALUE, Double.POSITIVE_INFINITY, Double.NaN);
        assertThat(((List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @Test
    public void primitiveIntegerTypesEmitLong() throws Exception {
        JsonValueWriter writer = new JsonValueWriter();
        writer.beginArray();
        writer.value(new Byte(Byte.MIN_VALUE));
        writer.value(new Short(Short.MIN_VALUE));
        writer.value(new Integer(Integer.MIN_VALUE));
        writer.value(new Long(Long.MIN_VALUE));
        writer.endArray();
        List<Number> numbers = Arrays.<Number>asList((-128L), (-32768L), (-2147483648L), -9223372036854775808L);
        assertThat(((List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @Test
    public void primitiveFloatingPointTypesEmitDouble() throws Exception {
        JsonValueWriter writer = new JsonValueWriter();
        writer.beginArray();
        writer.value(new Float(0.5F));
        writer.value(new Double(0.5));
        writer.endArray();
        List<Number> numbers = Arrays.<Number>asList(0.5, 0.5);
        assertThat(((List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @Test
    public void otherNumberTypesEmitBigDecimal() throws Exception {
        JsonValueWriter writer = new JsonValueWriter();
        writer.beginArray();
        writer.value(new AtomicInteger(-2147483648));
        writer.value(new AtomicLong(-9223372036854775808L));
        writer.value(new BigInteger("-9223372036854775808"));
        writer.value(new BigInteger("-1"));
        writer.value(new BigInteger("0"));
        writer.value(new BigInteger("1"));
        writer.value(new BigInteger("9223372036854775807"));
        writer.value(new BigDecimal("-9223372036854775808"));
        writer.value(new BigDecimal("-1"));
        writer.value(new BigDecimal("0"));
        writer.value(new BigDecimal("1"));
        writer.value(new BigDecimal("9223372036854775807"));
        writer.value(new BigInteger("-9223372036854775809"));
        writer.value(new BigInteger("9223372036854775808"));
        writer.value(new BigDecimal("-9223372036854775809"));
        writer.value(new BigDecimal("9223372036854775808"));
        writer.value(new BigDecimal("0.5"));
        writer.value(new BigDecimal("100000e15"));
        writer.value(new BigDecimal("0.0000100e-10"));
        writer.endArray();
        List<Number> numbers = Arrays.<Number>asList(new BigDecimal("-2147483648"), new BigDecimal("-9223372036854775808"), new BigDecimal("-9223372036854775808"), new BigDecimal("-1"), new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("9223372036854775807"), new BigDecimal("-9223372036854775808"), new BigDecimal("-1"), new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("9223372036854775807"), new BigDecimal("-9223372036854775809"), new BigDecimal("9223372036854775808"), new BigDecimal("-9223372036854775809"), new BigDecimal("9223372036854775808"), new BigDecimal("0.5"), new BigDecimal("100000e15"), new BigDecimal("0.0000100e-10"));
        assertThat(((List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @Test
    public void valueCustomNumberTypeEmitsLongOrBigDecimal() throws Exception {
        JsonValueWriter writer = new JsonValueWriter();
        writer.beginArray();
        writer.value(stringNumber("-9223372036854775809"));
        writer.value(stringNumber("-9223372036854775808"));
        writer.value(stringNumber("0.5"));
        writer.value(stringNumber("1.0"));
        writer.endArray();
        List<Number> numbers = Arrays.<Number>asList(new BigDecimal("-9223372036854775809"), new BigDecimal("-9223372036854775808"), new BigDecimal("0.5"), new BigDecimal("1.0"));
        assertThat(((List<?>) (writer.root()))).isEqualTo(numbers);
    }

    @Test
    public void valueFromSource() throws IOException {
        JsonValueWriter writer = new JsonValueWriter();
        writer.beginObject();
        writer.name("a");
        writer.value(new Buffer().writeUtf8("[\"value\"]"));
        writer.name("b");
        writer.value(new Buffer().writeUtf8("2"));
        writer.name("c");
        writer.value(3);
        writer.name("d");
        writer.value(new Buffer().writeUtf8("null"));
        writer.endObject();
        assertThat(((java.util.Map<String, Object>) (writer.root()))).containsExactly(new AbstractMap.SimpleEntry<String, Object>("a", Collections.singletonList("value")), new AbstractMap.SimpleEntry<String, Object>("b", 2.0), new AbstractMap.SimpleEntry<String, Object>("c", 3L), new AbstractMap.SimpleEntry<String, Object>("d", null));
    }
}

