/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.header;


import Schema.BOOLEAN_SCHEMA;
import Schema.BYTES_SCHEMA;
import Schema.FLOAT32_SCHEMA;
import Schema.FLOAT64_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.INT8_SCHEMA;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_BYTES_SCHEMA;
import Schema.OPTIONAL_FLOAT32_SCHEMA;
import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT16_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_INT8_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import Time.SCHEMA;
import Type.STRING;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.data.Values;
import org.junit.Assert;
import org.junit.Test;


public class ConnectHeadersTest {
    private static final GregorianCalendar EPOCH_PLUS_TEN_THOUSAND_DAYS;

    private static final GregorianCalendar EPOCH_PLUS_TEN_THOUSAND_MILLIS;

    static {
        EPOCH_PLUS_TEN_THOUSAND_DAYS = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        ConnectHeadersTest.EPOCH_PLUS_TEN_THOUSAND_DAYS.setTimeZone(TimeZone.getTimeZone("UTC"));
        ConnectHeadersTest.EPOCH_PLUS_TEN_THOUSAND_DAYS.add(Calendar.DATE, 10000);
        EPOCH_PLUS_TEN_THOUSAND_MILLIS = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        ConnectHeadersTest.EPOCH_PLUS_TEN_THOUSAND_MILLIS.setTimeZone(TimeZone.getTimeZone("UTC"));
        ConnectHeadersTest.EPOCH_PLUS_TEN_THOUSAND_MILLIS.add(Calendar.MILLISECOND, 10000);
    }

    private ConnectHeaders headers;

    private Iterator<Header> iter;

    private String key;

    private String other;

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullKey() {
        headers.add(null, "value", STRING_SCHEMA);
    }

    @Test
    public void shouldBeEquals() {
        Headers other = new ConnectHeaders();
        Assert.assertEquals(headers, other);
        Assert.assertEquals(headers.hashCode(), other.hashCode());
        populate(headers);
        Assert.assertNotEquals(headers, other);
        Assert.assertNotEquals(headers.hashCode(), other.hashCode());
        populate(other);
        Assert.assertEquals(headers, other);
        Assert.assertEquals(headers.hashCode(), other.hashCode());
        headers.addString("wow", "some value");
        Assert.assertNotEquals(headers, other);
    }

    @Test
    public void shouldHaveToString() {
        // empty
        Assert.assertNotNull(headers.toString());
        // not empty
        populate(headers);
        Assert.assertNotNull(headers.toString());
    }

    @Test
    public void shouldAddMultipleHeadersWithSameKeyAndRetainLatest() {
        populate(headers);
        Header header = headers.lastWithName(key);
        assertHeader(header, key, STRING_SCHEMA, "third");
        iter = headers.allWithName(key);
        assertNextHeader(iter, key, BOOLEAN_SCHEMA, true);
        assertNextHeader(iter, key, INT32_SCHEMA, 0);
        assertNextHeader(iter, key, OPTIONAL_STRING_SCHEMA, null);
        assertNextHeader(iter, key, STRING_SCHEMA, "third");
        assertNoNextHeader(iter);
        iter = headers.allWithName(other);
        assertOnlyNextHeader(iter, other, STRING_SCHEMA, "other value");
        headers.retainLatest(other);
        assertOnlySingleHeader(other, STRING_SCHEMA, "other value");
        headers.retainLatest(key);
        assertOnlySingleHeader(key, STRING_SCHEMA, "third");
        headers.retainLatest();
        assertOnlySingleHeader(other, STRING_SCHEMA, "other value");
        assertOnlySingleHeader(key, STRING_SCHEMA, "third");
    }

    @Test
    public void shouldAddHeadersWithPrimitiveValues() {
        String key = "k1";
        headers.addBoolean(key, true);
        headers.addByte(key, ((byte) (0)));
        headers.addShort(key, ((short) (0)));
        headers.addInt(key, 0);
        headers.addLong(key, 0);
        headers.addFloat(key, 1.0F);
        headers.addDouble(key, 1.0);
        headers.addString(key, null);
        headers.addString(key, "third");
    }

    @Test
    public void shouldAddHeadersWithNullObjectValuesWithOptionalSchema() {
        addHeader("k1", BOOLEAN_SCHEMA, true);
        addHeader("k2", STRING_SCHEMA, "hello");
        addHeader("k3", OPTIONAL_STRING_SCHEMA, null);
    }

    @Test
    public void shouldNotAddHeadersWithNullObjectValuesWithNonOptionalSchema() {
        attemptAndFailToAddHeader("k1", BOOLEAN_SCHEMA, null);
        attemptAndFailToAddHeader("k2", STRING_SCHEMA, null);
    }

    @Test
    public void shouldNotAddHeadersWithObjectValuesAndMismatchedSchema() {
        attemptAndFailToAddHeader("k1", BOOLEAN_SCHEMA, "wrong");
        attemptAndFailToAddHeader("k2", OPTIONAL_STRING_SCHEMA, 0L);
    }

    @Test
    public void shouldRemoveAllHeadersWithSameKey() {
        populate(headers);
        iter = headers.allWithName(key);
        assertContainsHeader(key, BOOLEAN_SCHEMA, true);
        assertContainsHeader(key, INT32_SCHEMA, 0);
        assertContainsHeader(key, STRING_SCHEMA, "third");
        assertOnlySingleHeader(other, STRING_SCHEMA, "other value");
        headers.remove(key);
        assertNoHeaderWithKey(key);
        assertOnlySingleHeader(other, STRING_SCHEMA, "other value");
    }

    @Test
    public void shouldRemoveAllHeaders() {
        populate(headers);
        iter = headers.allWithName(key);
        assertContainsHeader(key, BOOLEAN_SCHEMA, true);
        assertContainsHeader(key, INT32_SCHEMA, 0);
        assertContainsHeader(key, STRING_SCHEMA, "third");
        assertOnlySingleHeader(other, STRING_SCHEMA, "other value");
        headers.clear();
        assertNoHeaderWithKey(key);
        assertNoHeaderWithKey(other);
        Assert.assertEquals(0, headers.size());
        Assert.assertTrue(headers.isEmpty());
    }

    @Test
    public void shouldTransformHeaders() {
        populate(headers);
        iter = headers.allWithName(key);
        assertNextHeader(iter, key, BOOLEAN_SCHEMA, true);
        assertNextHeader(iter, key, INT32_SCHEMA, 0);
        assertNextHeader(iter, key, OPTIONAL_STRING_SCHEMA, null);
        assertNextHeader(iter, key, STRING_SCHEMA, "third");
        assertNoNextHeader(iter);
        iter = headers.allWithName(other);
        assertOnlyNextHeader(iter, other, STRING_SCHEMA, "other value");
        // Transform the headers
        Assert.assertEquals(5, headers.size());
        headers.apply(appendToKey("-suffix"));
        Assert.assertEquals(5, headers.size());
        assertNoHeaderWithKey(key);
        assertNoHeaderWithKey(other);
        String altKey = (key) + "-suffix";
        iter = headers.allWithName(altKey);
        assertNextHeader(iter, altKey, BOOLEAN_SCHEMA, true);
        assertNextHeader(iter, altKey, INT32_SCHEMA, 0);
        assertNextHeader(iter, altKey, OPTIONAL_STRING_SCHEMA, null);
        assertNextHeader(iter, altKey, STRING_SCHEMA, "third");
        assertNoNextHeader(iter);
        iter = headers.allWithName(((other) + "-suffix"));
        assertOnlyNextHeader(iter, ((other) + "-suffix"), STRING_SCHEMA, "other value");
    }

    @Test
    public void shouldTransformHeadersWithKey() {
        populate(headers);
        iter = headers.allWithName(key);
        assertNextHeader(iter, key, BOOLEAN_SCHEMA, true);
        assertNextHeader(iter, key, INT32_SCHEMA, 0);
        assertNextHeader(iter, key, OPTIONAL_STRING_SCHEMA, null);
        assertNextHeader(iter, key, STRING_SCHEMA, "third");
        assertNoNextHeader(iter);
        iter = headers.allWithName(other);
        assertOnlyNextHeader(iter, other, STRING_SCHEMA, "other value");
        // Transform the headers
        Assert.assertEquals(5, headers.size());
        headers.apply(key, appendToKey("-suffix"));
        Assert.assertEquals(5, headers.size());
        assertNoHeaderWithKey(key);
        String altKey = (key) + "-suffix";
        iter = headers.allWithName(altKey);
        assertNextHeader(iter, altKey, BOOLEAN_SCHEMA, true);
        assertNextHeader(iter, altKey, INT32_SCHEMA, 0);
        assertNextHeader(iter, altKey, OPTIONAL_STRING_SCHEMA, null);
        assertNextHeader(iter, altKey, STRING_SCHEMA, "third");
        assertNoNextHeader(iter);
        iter = headers.allWithName(other);
        assertOnlyNextHeader(iter, other, STRING_SCHEMA, "other value");
    }

    @Test
    public void shouldTransformAndRemoveHeaders() {
        populate(headers);
        iter = headers.allWithName(key);
        assertNextHeader(iter, key, BOOLEAN_SCHEMA, true);
        assertNextHeader(iter, key, INT32_SCHEMA, 0);
        assertNextHeader(iter, key, OPTIONAL_STRING_SCHEMA, null);
        assertNextHeader(iter, key, STRING_SCHEMA, "third");
        assertNoNextHeader(iter);
        iter = headers.allWithName(other);
        assertOnlyNextHeader(iter, other, STRING_SCHEMA, "other value");
        // Transform the headers
        Assert.assertEquals(5, headers.size());
        headers.apply(key, removeHeadersOfType(STRING));
        Assert.assertEquals(3, headers.size());
        iter = headers.allWithName(key);
        assertNextHeader(iter, key, BOOLEAN_SCHEMA, true);
        assertNextHeader(iter, key, INT32_SCHEMA, 0);
        assertNoNextHeader(iter);
        assertHeader(headers.lastWithName(key), key, INT32_SCHEMA, 0);
        iter = headers.allWithName(other);
        assertOnlyNextHeader(iter, other, STRING_SCHEMA, "other value");
        // Transform the headers
        Assert.assertEquals(3, headers.size());
        headers.apply(removeHeadersOfType(STRING));
        Assert.assertEquals(2, headers.size());
        assertNoHeaderWithKey(other);
        iter = headers.allWithName(key);
        assertNextHeader(iter, key, BOOLEAN_SCHEMA, true);
        assertNextHeader(iter, key, INT32_SCHEMA, 0);
        assertNoNextHeader(iter);
    }

    @Test
    public void shouldValidateBuildInTypes() {
        assertSchemaMatches(OPTIONAL_BOOLEAN_SCHEMA, null);
        assertSchemaMatches(OPTIONAL_BYTES_SCHEMA, null);
        assertSchemaMatches(OPTIONAL_INT8_SCHEMA, null);
        assertSchemaMatches(OPTIONAL_INT16_SCHEMA, null);
        assertSchemaMatches(OPTIONAL_INT32_SCHEMA, null);
        assertSchemaMatches(OPTIONAL_INT64_SCHEMA, null);
        assertSchemaMatches(OPTIONAL_FLOAT32_SCHEMA, null);
        assertSchemaMatches(OPTIONAL_FLOAT64_SCHEMA, null);
        assertSchemaMatches(OPTIONAL_STRING_SCHEMA, null);
        assertSchemaMatches(BOOLEAN_SCHEMA, true);
        assertSchemaMatches(BYTES_SCHEMA, new byte[]{  });
        assertSchemaMatches(INT8_SCHEMA, ((byte) (0)));
        assertSchemaMatches(INT16_SCHEMA, ((short) (0)));
        assertSchemaMatches(INT32_SCHEMA, 0);
        assertSchemaMatches(INT64_SCHEMA, 0L);
        assertSchemaMatches(FLOAT32_SCHEMA, 1.0F);
        assertSchemaMatches(FLOAT64_SCHEMA, 1.0);
        assertSchemaMatches(STRING_SCHEMA, "value");
        assertSchemaMatches(SchemaBuilder.array(STRING_SCHEMA), new ArrayList<String>());
        assertSchemaMatches(SchemaBuilder.array(STRING_SCHEMA), Collections.singletonList("value"));
        assertSchemaMatches(SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA), new HashMap<String, Integer>());
        assertSchemaMatches(SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA), Collections.singletonMap("a", 0));
        Schema emptyStructSchema = SchemaBuilder.struct();
        assertSchemaMatches(emptyStructSchema, new org.apache.kafka.connect.data.Struct(emptyStructSchema));
        Schema structSchema = SchemaBuilder.struct().field("foo", OPTIONAL_BOOLEAN_SCHEMA).field("bar", STRING_SCHEMA).schema();
        assertSchemaMatches(structSchema, put("foo", true).put("bar", "v"));
    }

    @Test
    public void shouldValidateLogicalTypes() {
        assertSchemaMatches(Decimal.schema(3), new BigDecimal(100.0));
        assertSchemaMatches(SCHEMA, new Date());
        assertSchemaMatches(Date.SCHEMA, new Date());
        assertSchemaMatches(Timestamp.SCHEMA, new Date());
    }

    @Test
    public void shouldNotValidateNullValuesWithBuiltInTypes() {
        assertSchemaDoesNotMatch(BOOLEAN_SCHEMA, null);
        assertSchemaDoesNotMatch(BYTES_SCHEMA, null);
        assertSchemaDoesNotMatch(INT8_SCHEMA, null);
        assertSchemaDoesNotMatch(INT16_SCHEMA, null);
        assertSchemaDoesNotMatch(INT32_SCHEMA, null);
        assertSchemaDoesNotMatch(INT64_SCHEMA, null);
        assertSchemaDoesNotMatch(FLOAT32_SCHEMA, null);
        assertSchemaDoesNotMatch(FLOAT64_SCHEMA, null);
        assertSchemaDoesNotMatch(STRING_SCHEMA, null);
        assertSchemaDoesNotMatch(SchemaBuilder.array(STRING_SCHEMA), null);
        assertSchemaDoesNotMatch(SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA), null);
        assertSchemaDoesNotMatch(SchemaBuilder.struct(), null);
    }

    @Test
    public void shouldNotValidateMismatchedValuesWithBuiltInTypes() {
        assertSchemaDoesNotMatch(BOOLEAN_SCHEMA, 0L);
        assertSchemaDoesNotMatch(BYTES_SCHEMA, "oops");
        assertSchemaDoesNotMatch(INT8_SCHEMA, 1.0F);
        assertSchemaDoesNotMatch(INT16_SCHEMA, 1.0F);
        assertSchemaDoesNotMatch(INT32_SCHEMA, 0L);
        assertSchemaDoesNotMatch(INT64_SCHEMA, 1.0F);
        assertSchemaDoesNotMatch(FLOAT32_SCHEMA, 1L);
        assertSchemaDoesNotMatch(FLOAT64_SCHEMA, 1L);
        assertSchemaDoesNotMatch(STRING_SCHEMA, true);
        assertSchemaDoesNotMatch(SchemaBuilder.array(STRING_SCHEMA), "value");
        assertSchemaDoesNotMatch(SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA), "value");
        assertSchemaDoesNotMatch(SchemaBuilder.struct(), new ArrayList<String>());
    }

    @Test
    public void shouldAddDate() {
        Date dateObj = ConnectHeadersTest.EPOCH_PLUS_TEN_THOUSAND_DAYS.getTime();
        int days = org.apache.kafka.connect.data.Date.fromLogical(Date.SCHEMA, dateObj);
        headers.addDate(key, dateObj);
        Header header = headers.lastWithName(key);
        Assert.assertEquals(days, ((int) (Values.convertToInteger(header.schema(), header.value()))));
        Assert.assertSame(dateObj, Values.convertToDate(header.schema(), header.value()));
        headers.addInt(other, days);
        header = headers.lastWithName(other);
        Assert.assertEquals(days, ((int) (Values.convertToInteger(header.schema(), header.value()))));
        Assert.assertEquals(dateObj, Values.convertToDate(header.schema(), header.value()));
    }

    @Test
    public void shouldAddTime() {
        Date dateObj = ConnectHeadersTest.EPOCH_PLUS_TEN_THOUSAND_MILLIS.getTime();
        long millis = Time.fromLogical(SCHEMA, dateObj);
        headers.addTime(key, dateObj);
        Header header = headers.lastWithName(key);
        Assert.assertEquals(millis, ((long) (Values.convertToLong(header.schema(), header.value()))));
        Assert.assertSame(dateObj, Values.convertToTime(header.schema(), header.value()));
        headers.addLong(other, millis);
        header = headers.lastWithName(other);
        Assert.assertEquals(millis, ((long) (Values.convertToLong(header.schema(), header.value()))));
        Assert.assertEquals(dateObj, Values.convertToTime(header.schema(), header.value()));
    }

    @Test
    public void shouldAddTimestamp() {
        Date dateObj = ConnectHeadersTest.EPOCH_PLUS_TEN_THOUSAND_MILLIS.getTime();
        long millis = Timestamp.fromLogical(Timestamp.SCHEMA, dateObj);
        headers.addTimestamp(key, dateObj);
        Header header = headers.lastWithName(key);
        Assert.assertEquals(millis, ((long) (Values.convertToLong(header.schema(), header.value()))));
        Assert.assertSame(dateObj, Values.convertToTimestamp(header.schema(), header.value()));
        headers.addLong(other, millis);
        header = headers.lastWithName(other);
        Assert.assertEquals(millis, ((long) (Values.convertToLong(header.schema(), header.value()))));
        Assert.assertEquals(dateObj, Values.convertToTimestamp(header.schema(), header.value()));
    }

    @Test
    public void shouldAddDecimal() {
        BigDecimal value = new BigDecimal("3.038573478e+3");
        headers.addDecimal(key, value);
        Header header = headers.lastWithName(key);
        Assert.assertEquals(value.doubleValue(), Values.convertToDouble(header.schema(), header.value()), 1.0E-5);
        Assert.assertEquals(value, Values.convertToDecimal(header.schema(), header.value(), value.scale()));
        value = value.setScale(3, RoundingMode.DOWN);
        BigDecimal decimal = Values.convertToDecimal(header.schema(), header.value(), value.scale());
        Assert.assertEquals(value, decimal.setScale(value.scale(), RoundingMode.DOWN));
    }

    @Test
    public void shouldDuplicateAndAlwaysReturnEquivalentButDifferentObject() {
        Assert.assertEquals(headers, headers.duplicate());
        Assert.assertNotSame(headers, headers.duplicate());
    }
}

