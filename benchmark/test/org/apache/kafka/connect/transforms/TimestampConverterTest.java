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
package org.apache.kafka.connect.transforms;


import Schema.INT64_SCHEMA;
import Schema.STRING_SCHEMA;
import Timestamp.SCHEMA;
import TimestampConverter.FIELD_CONFIG;
import TimestampConverter.FORMAT_CONFIG;
import TimestampConverter.TARGET_TYPE_CONFIG;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Assert;
import org.junit.Test;


public class TimestampConverterTest {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private static final Calendar EPOCH;

    private static final Calendar TIME;

    private static final Calendar DATE;

    private static final Calendar DATE_PLUS_TIME;

    private static final long DATE_PLUS_TIME_UNIX;

    private static final String STRING_DATE_FMT = "yyyy MM dd HH mm ss SSS z";

    private static final String DATE_PLUS_TIME_STRING;

    private final TimestampConverter<SourceRecord> xformKey = new TimestampConverter.Key<>();

    private final TimestampConverter<SourceRecord> xformValue = new TimestampConverter.Value<>();

    static {
        EPOCH = GregorianCalendar.getInstance(TimestampConverterTest.UTC);
        TimestampConverterTest.EPOCH.setTimeInMillis(0L);
        TIME = GregorianCalendar.getInstance(TimestampConverterTest.UTC);
        TimestampConverterTest.TIME.setTimeInMillis(0L);
        TimestampConverterTest.TIME.add(Calendar.MILLISECOND, 1234);
        DATE = GregorianCalendar.getInstance(TimestampConverterTest.UTC);
        TimestampConverterTest.DATE.setTimeInMillis(0L);
        TimestampConverterTest.DATE.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        TimestampConverterTest.DATE.add(Calendar.DATE, 1);
        DATE_PLUS_TIME = GregorianCalendar.getInstance(TimestampConverterTest.UTC);
        TimestampConverterTest.DATE_PLUS_TIME.setTimeInMillis(0L);
        TimestampConverterTest.DATE_PLUS_TIME.add(Calendar.DATE, 1);
        TimestampConverterTest.DATE_PLUS_TIME.add(Calendar.MILLISECOND, 1234);
        DATE_PLUS_TIME_UNIX = TimestampConverterTest.DATE_PLUS_TIME.getTime().getTime();
        DATE_PLUS_TIME_STRING = "1970 01 02 00 00 01 234 UTC";
    }

    @Test(expected = ConfigException.class)
    public void testConfigNoTargetType() {
        xformValue.configure(Collections.<String, String>emptyMap());
    }

    @Test(expected = ConfigException.class)
    public void testConfigInvalidTargetType() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "invalid"));
    }

    @Test(expected = ConfigException.class)
    public void testConfigMissingFormat() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "string"));
    }

    @Test(expected = ConfigException.class)
    public void testConfigInvalidFormat() {
        Map<String, String> config = new HashMap<>();
        config.put(TARGET_TYPE_CONFIG, "string");
        config.put(FORMAT_CONFIG, "bad-format");
        xformValue.configure(config);
    }

    // Conversions without schemas (most flexible Timestamp -> other types)
    @Test
    public void testSchemalessIdentity() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME.getTime(), transformed.value());
    }

    @Test
    public void testSchemalessTimestampToDate() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Date"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE.getTime(), transformed.value());
    }

    @Test
    public void testSchemalessTimestampToTime() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Time"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.TIME.getTime(), transformed.value());
    }

    @Test
    public void testSchemalessTimestampToUnix() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "unix"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME_UNIX, transformed.value());
    }

    @Test
    public void testSchemalessTimestampToString() {
        Map<String, String> config = new HashMap<>();
        config.put(TARGET_TYPE_CONFIG, "string");
        config.put(FORMAT_CONFIG, TimestampConverterTest.STRING_DATE_FMT);
        xformValue.configure(config);
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME_STRING, transformed.value());
    }

    // Conversions without schemas (core types -> most flexible Timestamp format)
    @Test
    public void testSchemalessDateToTimestamp() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE.getTime()));
        Assert.assertNull(transformed.valueSchema());
        // No change expected since the source type is coarser-grained
        Assert.assertEquals(TimestampConverterTest.DATE.getTime(), transformed.value());
    }

    @Test
    public void testSchemalessTimeToTimestamp() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.TIME.getTime()));
        Assert.assertNull(transformed.valueSchema());
        // No change expected since the source type is coarser-grained
        Assert.assertEquals(TimestampConverterTest.TIME.getTime(), transformed.value());
    }

    @Test
    public void testSchemalessUnixToTimestamp() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE_PLUS_TIME_UNIX));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME.getTime(), transformed.value());
    }

    @Test
    public void testSchemalessStringToTimestamp() {
        Map<String, String> config = new HashMap<>();
        config.put(TARGET_TYPE_CONFIG, "Timestamp");
        config.put(FORMAT_CONFIG, TimestampConverterTest.STRING_DATE_FMT);
        xformValue.configure(config);
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE_PLUS_TIME_STRING));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME.getTime(), transformed.value());
    }

    // Conversions with schemas (most flexible Timestamp -> other types)
    @Test
    public void testWithSchemaIdentity() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Timestamp.SCHEMA, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertEquals(SCHEMA, transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME.getTime(), transformed.value());
    }

    @Test
    public void testWithSchemaTimestampToDate() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Date"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Timestamp.SCHEMA, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertEquals(Date.SCHEMA, transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE.getTime(), transformed.value());
    }

    @Test
    public void testWithSchemaTimestampToTime() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Time"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Timestamp.SCHEMA, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertEquals(Time.SCHEMA, transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.TIME.getTime(), transformed.value());
    }

    @Test
    public void testWithSchemaTimestampToUnix() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "unix"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Timestamp.SCHEMA, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertEquals(INT64_SCHEMA, transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME_UNIX, transformed.value());
    }

    @Test
    public void testWithSchemaTimestampToString() {
        Map<String, String> config = new HashMap<>();
        config.put(TARGET_TYPE_CONFIG, "string");
        config.put(FORMAT_CONFIG, TimestampConverterTest.STRING_DATE_FMT);
        xformValue.configure(config);
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Timestamp.SCHEMA, TimestampConverterTest.DATE_PLUS_TIME.getTime()));
        Assert.assertEquals(STRING_SCHEMA, transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME_STRING, transformed.value());
    }

    // Conversions with schemas (core types -> most flexible Timestamp format)
    @Test
    public void testWithSchemaDateToTimestamp() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Date.SCHEMA, TimestampConverterTest.DATE.getTime()));
        Assert.assertEquals(SCHEMA, transformed.valueSchema());
        // No change expected since the source type is coarser-grained
        Assert.assertEquals(TimestampConverterTest.DATE.getTime(), transformed.value());
    }

    @Test
    public void testWithSchemaTimeToTimestamp() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Time.SCHEMA, TimestampConverterTest.TIME.getTime()));
        Assert.assertEquals(SCHEMA, transformed.valueSchema());
        // No change expected since the source type is coarser-grained
        Assert.assertEquals(TimestampConverterTest.TIME.getTime(), transformed.value());
    }

    @Test
    public void testWithSchemaUnixToTimestamp() {
        xformValue.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT64_SCHEMA, TimestampConverterTest.DATE_PLUS_TIME_UNIX));
        Assert.assertEquals(SCHEMA, transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME.getTime(), transformed.value());
    }

    @Test
    public void testWithSchemaStringToTimestamp() {
        Map<String, String> config = new HashMap<>();
        config.put(TARGET_TYPE_CONFIG, "Timestamp");
        config.put(FORMAT_CONFIG, TimestampConverterTest.STRING_DATE_FMT);
        xformValue.configure(config);
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.STRING_SCHEMA, TimestampConverterTest.DATE_PLUS_TIME_STRING));
        Assert.assertEquals(SCHEMA, transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME.getTime(), transformed.value());
    }

    // Convert field instead of entire key/value
    @Test
    public void testSchemalessFieldConversion() {
        Map<String, String> config = new HashMap<>();
        config.put(TARGET_TYPE_CONFIG, "Date");
        config.put(FIELD_CONFIG, "ts");
        xformValue.configure(config);
        Object value = Collections.singletonMap("ts", TimestampConverterTest.DATE_PLUS_TIME.getTime());
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, value));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(Collections.singletonMap("ts", TimestampConverterTest.DATE.getTime()), transformed.value());
    }

    @Test
    public void testWithSchemaFieldConversion() {
        Map<String, String> config = new HashMap<>();
        config.put(TARGET_TYPE_CONFIG, "Timestamp");
        config.put(FIELD_CONFIG, "ts");
        xformValue.configure(config);
        // ts field is a unix timestamp
        Schema structWithTimestampFieldSchema = SchemaBuilder.struct().field("ts", INT64_SCHEMA).field("other", STRING_SCHEMA).build();
        Struct original = new Struct(structWithTimestampFieldSchema);
        original.put("ts", TimestampConverterTest.DATE_PLUS_TIME_UNIX);
        original.put("other", "test");
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, structWithTimestampFieldSchema, original));
        Schema expectedSchema = SchemaBuilder.struct().field("ts", SCHEMA).field("other", STRING_SCHEMA).build();
        Assert.assertEquals(expectedSchema, transformed.valueSchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME.getTime(), get("ts"));
        Assert.assertEquals("test", get("other"));
    }

    // Validate Key implementation in addition to Value
    @Test
    public void testKey() {
        xformKey.configure(Collections.singletonMap(TARGET_TYPE_CONFIG, "Timestamp"));
        SourceRecord transformed = xformKey.apply(new SourceRecord(null, null, "topic", 0, null, TimestampConverterTest.DATE_PLUS_TIME.getTime(), null, null));
        Assert.assertNull(transformed.keySchema());
        Assert.assertEquals(TimestampConverterTest.DATE_PLUS_TIME.getTime(), transformed.key());
    }
}

