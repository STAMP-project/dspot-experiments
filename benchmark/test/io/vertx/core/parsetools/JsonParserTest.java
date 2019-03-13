/**
 * Copyright 2017 Red Hat, Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.parsetools;


import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class JsonParserTest {
    @Test
    public void testParseEmptyObject() {
        JsonParser parser = JsonParser.newParser();
        AtomicInteger status = new AtomicInteger();
        parser.handler(( event) -> {
            assertNull(event.fieldName());
            assertNull(event.value());
            switch (status.getAndIncrement()) {
                case 0 :
                    assertEquals(JsonEventType.START_OBJECT, event.type());
                    break;
                case 1 :
                    assertEquals(JsonEventType.END_OBJECT, event.type());
                    break;
                default :
                    fail();
            }
        });
        AtomicInteger count = new AtomicInteger();
        parser.endHandler(( v) -> count.incrementAndGet());
        parser.handle(Buffer.buffer("{}"));
        Assert.assertEquals(2, status.get());
        Assert.assertEquals(0, count.get());
        parser.end();
        Assert.assertEquals(1, count.get());
        Assert.assertEquals(2, status.get());
        try {
            parser.end();
            Assert.fail();
        } catch (IllegalStateException ignore) {
            // expected
        }
    }

    @Test
    public void testParseEmptyArray() {
        JsonParser parser = JsonParser.newParser();
        AtomicInteger status = new AtomicInteger();
        parser.handler(( event) -> {
            assertNull(event.fieldName());
            assertNull(event.value());
            switch (status.getAndIncrement()) {
                case 0 :
                    assertEquals(JsonEventType.START_ARRAY, event.type());
                    break;
                case 1 :
                    assertEquals(JsonEventType.END_ARRAY, event.type());
                    break;
                default :
                    fail();
            }
        });
        parser.handle(Buffer.buffer("[]"));
        Assert.assertEquals(2, status.get());
    }

    @Test
    public void parseUnfinished() {
        Buffer data = Buffer.buffer("{\"un\":\"finished\"");
        try {
            JsonParser parser = JsonParser.newParser();
            parser.handle(data);
            parser.end();
            Assert.fail();
        } catch (DecodeException expected) {
        }
        JsonParser parser = JsonParser.newParser();
        List<Throwable> errors = new ArrayList<>();
        parser.exceptionHandler(errors::add);
        parser.handle(data);
        parser.end();
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void parseNumberFormatException() {
        Buffer data = Buffer.buffer(((Long.MAX_VALUE) + "0"));
        try {
            JsonParser.newParser().handler(( val) -> {
            }).write(data).end();
            Assert.fail();
        } catch (DecodeException expected) {
        }
        List<Throwable> errors = new ArrayList<>();
        JsonParser.newParser().exceptionHandler(errors::add).handler(( val) -> {
        }).write(data).end();
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testParseObjectValue() {
        JsonParser parser = JsonParser.newParser();
        AtomicInteger status = new AtomicInteger();
        parser.objectValueMode();
        JsonObject expected = new JsonObject().put("number", 3).put("floating", 3.5).put("true", true).put("false", false).put("string", "s").put("object", new JsonObject().put("foo", "bar")).put("array", new JsonArray().add(0).add(1).add(2)).putNull("null").put("bytes", new byte[]{ 1, 2, 3 });
        parser.handler(( event) -> {
            assertEquals(0, status.getAndIncrement());
            assertEquals(JsonEventType.VALUE, event.type());
            assertEquals(expected, event.value());
        });
        parser.handle(expected.toBuffer());
        Assert.assertEquals(1, status.get());
    }

    @Test
    public void testParseArrayValue() {
        JsonParser parser = JsonParser.newParser();
        AtomicInteger status = new AtomicInteger();
        parser.arrayValueMode();
        JsonArray expected = new JsonArray().add(3).add(3.5).add(true).add(false).add("s").addNull().add(new JsonObject().put("foo", "bar")).add(new JsonArray().add(0).add(1).add(2)).add(new byte[]{ 1, 2, 3 });
        parser.handler(( event) -> {
            assertEquals(expected, event.value());
            assertEquals(0, status.getAndIncrement());
        });
        parser.handle(expected.toBuffer());
        Assert.assertEquals(1, status.get());
    }

    @Test
    public void testStringValue() {
        testValue("\"bar\"", ( event) -> {
            assertEquals("bar", event.value());
            assertFalse(event.isArray());
            assertFalse(event.isObject());
            assertFalse(event.isNumber());
            assertFalse(event.isNull());
            assertFalse(event.isBoolean());
            assertTrue(event.isString());
            assertEquals("bar", event.stringValue());
            assertThrowCCE(event, JsonEvent::integerValue, JsonEvent::longValue, JsonEvent::floatValue, JsonEvent::doubleValue, JsonEvent::booleanValue, JsonEvent::objectValue, JsonEvent::arrayValue);
            try {
                event.instantValue();
                fail();
            } catch ( ignore) {
                // Expected
            }
        });
    }

    @Test
    public void testInstantValue() {
        Instant value = Instant.now();
        String encoded = DateTimeFormatter.ISO_INSTANT.format(value);
        testValue((('"' + encoded) + '"'), ( event) -> {
            assertEquals(encoded, event.value());
            assertFalse(event.isArray());
            assertFalse(event.isObject());
            assertFalse(event.isNumber());
            assertFalse(event.isNull());
            assertFalse(event.isBoolean());
            assertTrue(event.isString());
            assertEquals(encoded, event.stringValue());
            assertEquals(value, event.instantValue());
            assertThrowCCE(event, JsonEvent::integerValue, JsonEvent::longValue, JsonEvent::floatValue, JsonEvent::doubleValue, JsonEvent::booleanValue, JsonEvent::objectValue, JsonEvent::arrayValue);
        });
    }

    @Test
    public void testBinaryValue() {
        byte[] value = TestUtils.randomByteArray(10);
        String encoded = Base64.getEncoder().encodeToString(value);
        testValue((('"' + encoded) + '"'), ( event) -> {
            assertEquals(encoded, event.value());
            assertFalse(event.isArray());
            assertFalse(event.isObject());
            assertFalse(event.isNumber());
            assertFalse(event.isNull());
            assertFalse(event.isBoolean());
            assertTrue(event.isString());
            assertEquals(encoded, event.stringValue());
            assertEquals(Buffer.buffer(value), event.binaryValue());
            assertThrowCCE(event, JsonEvent::integerValue, JsonEvent::longValue, JsonEvent::floatValue, JsonEvent::doubleValue, JsonEvent::booleanValue, JsonEvent::objectValue, JsonEvent::arrayValue);
            try {
                event.instantValue();
                fail();
            } catch ( ignore) {
                // Expected
            }
        });
    }

    @Test
    public void testNullValue() {
        testValue("null", ( event) -> {
            assertEquals(null, event.value());
            assertFalse(event.isArray());
            assertFalse(event.isObject());
            assertFalse(event.isNumber());
            assertTrue(event.isNull());
            assertFalse(event.isBoolean());
            assertFalse(event.isString());
            assertNull(event.integerValue());
            assertNull(event.longValue());
            assertNull(event.floatValue());
            assertNull(event.doubleValue());
            assertNull(event.binaryValue());
            assertNull(event.instantValue());
            assertNull(event.objectValue());
            assertNull(event.arrayValue());
            assertNull(event.stringValue());
            assertNull(event.binaryValue());
        });
    }

    @Test
    public void testLongValue() {
        testValue("567", ( event) -> {
            assertEquals(567L, event.value());
            assertFalse(event.isArray());
            assertFalse(event.isObject());
            assertTrue(event.isNumber());
            assertFalse(event.isNull());
            assertFalse(event.isBoolean());
            assertFalse(event.isString());
            assertEquals(567, ((long) (event.integerValue())));
            assertEquals(567L, ((long) (event.longValue())));
            assertEquals(567.0F, ((float) (event.floatValue())), 0.01F);
            assertEquals(567.0, ((double) (event.doubleValue())), 0.01);
            assertThrowCCE(event, JsonEvent::stringValue, JsonEvent::booleanValue, JsonEvent::binaryValue, JsonEvent::instantValue, JsonEvent::objectValue, JsonEvent::arrayValue);
        });
    }

    @Test
    public void testDoubleValue() {
        testValue("567.45", ( event) -> {
            assertEquals(567.45, event.value());
            assertFalse(event.isArray());
            assertFalse(event.isObject());
            assertTrue(event.isNumber());
            assertFalse(event.isNull());
            assertFalse(event.isBoolean());
            assertFalse(event.isString());
            assertEquals(567, ((long) (event.integerValue())));
            assertEquals(567L, ((long) (event.longValue())));
            assertEquals(567.45F, ((float) (event.floatValue())), 0.01F);
            assertEquals(567.45, ((double) (event.doubleValue())), 0.01);
            assertThrowCCE(event, JsonEvent::stringValue, JsonEvent::booleanValue, JsonEvent::binaryValue, JsonEvent::instantValue, JsonEvent::objectValue, JsonEvent::arrayValue);
        });
    }

    @Test
    public void testBooleanValue() {
        testValue("true", ( event) -> {
            assertEquals(true, event.value());
            assertFalse(event.isArray());
            assertFalse(event.isObject());
            assertFalse(event.isNumber());
            assertFalse(event.isNull());
            assertTrue(event.isBoolean());
            assertFalse(event.isString());
            assertTrue(event.booleanValue());
            assertThrowCCE(event, JsonEvent::integerValue, JsonEvent::longValue, JsonEvent::floatValue, JsonEvent::doubleValue, JsonEvent::stringValue, JsonEvent::binaryValue, JsonEvent::instantValue, JsonEvent::objectValue, JsonEvent::arrayValue);
        });
        testValue("false", ( event) -> {
            assertEquals(false, event.value());
            assertFalse(event.isArray());
            assertFalse(event.isObject());
            assertFalse(event.isNumber());
            assertFalse(event.isNull());
            assertTrue(event.isBoolean());
            assertFalse(event.isString());
            assertFalse(event.booleanValue());
            assertThrowCCE(event, JsonEvent::integerValue, JsonEvent::longValue, JsonEvent::floatValue, JsonEvent::doubleValue, JsonEvent::stringValue, JsonEvent::binaryValue, JsonEvent::instantValue, JsonEvent::objectValue, JsonEvent::arrayValue);
        });
    }

    @Test
    public void testParseObjectValueMembers() {
        JsonParser parser = JsonParser.newParser();
        AtomicInteger status = new AtomicInteger();
        parser.handler(( event) -> {
            switch (status.getAndIncrement()) {
                case 0 :
                    assertEquals(JsonEventType.START_OBJECT, event.type());
                    parser.objectValueMode();
                    break;
                case 1 :
                    assertEquals(JsonEventType.VALUE, event.type());
                    assertTrue(event.isObject());
                    assertEquals(new JsonObject(), event.value());
                    assertEquals("foo", event.fieldName());
                    break;
                case 2 :
                    assertEquals("bar", event.fieldName());
                    assertTrue(event.isObject());
                    assertEquals(JsonEventType.VALUE, event.type());
                    assertEquals(new JsonObject(), event.value());
                    break;
                case 3 :
                    assertEquals(JsonEventType.END_OBJECT, event.type());
                    break;
                default :
                    fail();
                    break;
            }
        });
        parser.handle(Buffer.buffer("{\"foo\":{},\"bar\":{}}"));
        Assert.assertEquals(4, status.get());
    }

    @Test
    public void testParseObjectValueList() {
        JsonParser parser = JsonParser.newParser();
        AtomicInteger status = new AtomicInteger();
        parser.objectValueMode();
        parser.handler(( event) -> {
            switch (status.getAndIncrement()) {
                case 0 :
                    assertEquals(JsonEventType.START_ARRAY, event.type());
                    break;
                case 1 :
                    assertEquals(JsonEventType.VALUE, event.type());
                    assertTrue(event.isObject());
                    assertEquals(new JsonObject().put("one", 1), event.value());
                    break;
                case 2 :
                    assertEquals(JsonEventType.VALUE, event.type());
                    assertTrue(event.isObject());
                    assertEquals(new JsonObject().put("two", 2), event.value());
                    break;
                case 3 :
                    assertEquals(JsonEventType.VALUE, event.type());
                    assertTrue(event.isObject());
                    assertEquals(new JsonObject().put("three", 3), event.value());
                    break;
                case 4 :
                    assertEquals(JsonEventType.END_ARRAY, event.type());
                    break;
            }
        });
        parser.handle(Buffer.buffer(("[" + ((("{\"one\":1}," + "{\"two\":2},") + "{\"three\":3}") + "]"))));
        Assert.assertEquals(5, status.get());
    }

    @Test
    public void testObjectHandlerScope() {
        JsonParser parser = JsonParser.newParser();
        List<JsonObject> objects = new ArrayList<>();
        AtomicInteger ends = new AtomicInteger();
        AtomicBoolean obj = new AtomicBoolean();
        parser.handler(( event) -> {
            switch (event.type()) {
                case START_OBJECT :
                    parser.objectValueMode();
                    break;
                case VALUE :
                    if (obj.get()) {
                        objects.add(((JsonObject) (event.value())));
                    }
                    break;
                case END_OBJECT :
                    ends.incrementAndGet();
                    obj.set(true);
                    break;
            }
        });
        parser.handle(Buffer.buffer(("[" + ((("{\"one\":1}," + "{\"two\":2},") + "{\"three\":3}") + "]"))));
        Assert.assertEquals(1, ends.get());
        Assert.assertEquals(Arrays.asList(new JsonObject().put("two", 2), new JsonObject().put("three", 3)), objects);
    }

    @Test
    public void testParseTopValues() {
        Map<String, Object> tests = new HashMap<>();
        tests.put("\"a-string\"", "a-string");
        tests.put("true", true);
        tests.put("false", false);
        tests.put("1234", 1234L);
        tests.put(("" + (Long.MAX_VALUE)), Long.MAX_VALUE);
        tests.forEach(( test, expected) -> {
            JsonParser parser = JsonParser.newParser();
            List<Object> values = new ArrayList<>();
            parser.handler(( event) -> values.add(event.value()));
            parser.handle(Buffer.buffer(test));
            parser.end();
            Assert.assertEquals(Collections.singletonList(expected), values);
        });
    }

    @Test
    public void testObjectMapping() {
        JsonParser parser = JsonParser.newParser();
        List<Object> values = new ArrayList<>();
        parser.objectValueMode();
        parser.handler(( event) -> values.add(event.mapTo(.class)));
        parser.handle(new JsonObject().put("f", "the-value").toBuffer());
        Assert.assertEquals(Collections.singletonList(new JsonParserTest.TheObject("the-value")), values);
    }

    @Test
    public void testObjectMappingError() {
        List<Object> values = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        JsonParser.newParser().objectValueMode().handler(( event) -> values.add(event.mapTo(.class))).exceptionHandler(errors::add).write(Buffer.buffer("{\"destination\":\"unknown\"}")).end();
        Assert.assertEquals(Collections.emptyList(), values);
        Assert.assertEquals(1, errors.size());
        try {
            JsonParser.newParser().objectValueMode().handler(( event) -> values.add(event.mapTo(.class))).write(Buffer.buffer("{\"destination\":\"unknown\"}")).end();
            Assert.fail();
        } catch (DecodeException expected) {
        }
        Assert.assertEquals(Collections.emptyList(), values);
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testObjectMappingWithTypeReference() {
        JsonParser parser = JsonParser.newParser();
        List<Object> values = new ArrayList<>();
        parser.objectValueMode();
        parser.handler(( event) -> values.add(event.mapTo(new TypeReference<io.vertx.core.parsetools.TheObject>() {})));
        parser.handle(new JsonObject().put("f", "the-value").toBuffer());
        Assert.assertEquals(Collections.singletonList(new JsonParserTest.TheObject("the-value")), values);
    }

    @Test
    public void testArrayMapping() {
        JsonParser parser = JsonParser.newParser();
        List<Object> values = new ArrayList<>();
        parser.arrayValueMode();
        parser.handler(( event) -> values.add(event.mapTo(.class)));
        parser.handle(new JsonArray().add(0).add(1).add(2).toBuffer());
        Assert.assertEquals(Collections.singletonList(Arrays.asList(0L, 1L, 2L)), values);
        Assert.assertEquals(LinkedList.class, values.get(0).getClass());
    }

    @Test
    public void testArrayMappingError() {
        List<Object> values = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        JsonParser.newParser().arrayValueMode().handler(( event) -> values.add(event.mapTo(.class))).exceptionHandler(errors::add).write(Buffer.buffer("[]")).end();
        Assert.assertEquals(Collections.emptyList(), values);
        Assert.assertEquals(1, errors.size());
        try {
            JsonParser.newParser().arrayValueMode().handler(( event) -> values.add(event.mapTo(.class))).write(Buffer.buffer("[]")).end();
            Assert.fail();
        } catch (DecodeException expected) {
        }
        Assert.assertEquals(Collections.emptyList(), values);
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testArrayMappingWithTypeReference() {
        JsonParser parser = JsonParser.newParser();
        List<Object> values = new ArrayList<>();
        parser.arrayValueMode();
        parser.handler(( event) -> values.add(event.mapTo(new TypeReference<LinkedList<Long>>() {})));
        parser.handle(new JsonArray().add(0).add(1).add(2).toBuffer());
        Assert.assertEquals(Collections.singletonList(Arrays.asList(0L, 1L, 2L)), values);
        Assert.assertEquals(LinkedList.class, values.get(0).getClass());
    }

    public static class TheObject {
        private String f;

        public TheObject() {
        }

        public TheObject(String f) {
            this.f = f;
        }

        public void setF(String f) {
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            JsonParserTest.TheObject that = ((JsonParserTest.TheObject) (obj));
            return Objects.equals(f, that.f);
        }
    }

    @Test
    public void testParseConcatedJSONStream() {
        JsonParser parser = JsonParser.newParser();
        AtomicInteger startCount = new AtomicInteger();
        AtomicInteger endCount = new AtomicInteger();
        parser.handler(( event) -> {
            switch (event.type()) {
                case START_OBJECT :
                    startCount.incrementAndGet();
                    break;
                case END_OBJECT :
                    endCount.incrementAndGet();
                    break;
                default :
                    fail();
                    break;
            }
        });
        parser.handle(Buffer.buffer("{}{}"));
        Assert.assertEquals(2, startCount.get());
        Assert.assertEquals(2, endCount.get());
    }

    @Test
    public void testParseLineDelimitedJSONStream() {
        JsonParser parser = JsonParser.newParser();
        AtomicInteger startCount = new AtomicInteger();
        AtomicInteger endCount = new AtomicInteger();
        AtomicInteger numberCount = new AtomicInteger();
        AtomicInteger nullCount = new AtomicInteger();
        AtomicInteger stringCount = new AtomicInteger();
        parser.handler(( event) -> {
            switch (event.type()) {
                case START_OBJECT :
                    startCount.incrementAndGet();
                    break;
                case END_OBJECT :
                    endCount.incrementAndGet();
                    break;
                case VALUE :
                    if (event.isNull()) {
                        nullCount.incrementAndGet();
                    } else
                        if (event.isNumber()) {
                            numberCount.incrementAndGet();
                        } else
                            if (event.isString()) {
                                stringCount.incrementAndGet();
                            } else {
                                fail(("Unexpected " + (event.type())));
                            }


                    break;
                default :
                    fail(("Unexpected " + (event.type())));
                    break;
            }
        });
        parser.handle(Buffer.buffer("{}\r\n1\r\nnull\r\n\"foo\""));
        Assert.assertEquals(1, startCount.get());
        Assert.assertEquals(1, endCount.get());
        Assert.assertEquals(1, numberCount.get());
        Assert.assertEquals(1, nullCount.get());
        Assert.assertEquals(1, stringCount.get());
    }

    @Test
    public void testStreamHandle() {
        FakeStream stream = new FakeStream();
        JsonParser parser = JsonParser.newParser(stream);
        List<JsonEvent> events = new ArrayList<>();
        parser.handler(events::add);
        stream.handle("{}");
        TestCase.assertFalse(stream.isPaused());
        Assert.assertEquals(2, events.size());
    }

    @Test
    public void testStreamPause() {
        FakeStream stream = new FakeStream();
        JsonParser parser = JsonParser.newParser(stream);
        List<JsonEvent> events = new ArrayList<>();
        parser.handler(events::add);
        parser.pause();
        stream.handle("1234");
        Assert.assertTrue(stream.isPaused());
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void testStreamResume() {
        FakeStream stream = new FakeStream();
        JsonParser parser = JsonParser.newParser(stream);
        List<JsonEvent> events = new ArrayList<>();
        parser.handler(events::add);
        parser.pause();
        stream.handle("{}");
        parser.resume();
        Assert.assertEquals(2, events.size());
        TestCase.assertFalse(stream.isPaused());
    }

    @Test
    public void testStreamFetch() {
        FakeStream stream = new FakeStream();
        JsonParser parser = JsonParser.newParser(stream);
        List<JsonEvent> events = new ArrayList<>();
        parser.handler(events::add);
        parser.pause();
        stream.handle("{}");
        parser.fetch(1);
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(stream.isPaused());
    }

    @Test
    public void testStreamPauseInHandler() {
        FakeStream stream = new FakeStream();
        JsonParser parser = JsonParser.newParser(stream);
        List<JsonEvent> events = new ArrayList<>();
        parser.handler(( event) -> {
            assertTrue(events.isEmpty());
            events.add(event);
            parser.pause();
        });
        stream.handle("{}");
        Assert.assertEquals(1, events.size());
        Assert.assertTrue(stream.isPaused());
    }

    @Test
    public void testStreamFetchInHandler() {
        FakeStream stream = new FakeStream();
        JsonParser parser = JsonParser.newParser(stream);
        List<JsonEvent> events = new ArrayList<>();
        parser.handler(( event) -> {
            events.add(event);
            stream.fetch(1);
        });
        stream.pause();
        stream.fetch(1);
        stream.handle("{}");
        Assert.assertEquals(2, events.size());
        TestCase.assertFalse(stream.isPaused());
    }

    @Test
    public void testStreamEnd() {
        FakeStream stream = new FakeStream();
        JsonParser parser = JsonParser.newParser(stream);
        List<JsonEvent> events = new ArrayList<>();
        parser.handler(events::add);
        AtomicInteger ended = new AtomicInteger();
        parser.endHandler(( v) -> ended.incrementAndGet());
        stream.end();
        Assert.assertEquals(0, events.size());
        Assert.assertEquals(1, ended.get());
        // regression check for #2790 - ensure that by accident resume method is not called.
        Assert.assertEquals(0, stream.pauseCount());
        Assert.assertEquals(0, stream.resumeCount());
    }

    @Test
    public void testStreamPausedEnd() {
        FakeStream stream = new FakeStream();
        JsonParser parser = JsonParser.newParser(stream);
        List<JsonEvent> events = new ArrayList<>();
        parser.handler(events::add);
        AtomicInteger ended = new AtomicInteger();
        parser.endHandler(( v) -> ended.incrementAndGet());
        parser.pause();
        stream.handle("{}");
        stream.end();
        Assert.assertEquals(0, ended.get());
        Assert.assertEquals(0, events.size());
        parser.fetch(1);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(0, ended.get());
        parser.fetch(1);
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(1, ended.get());
    }
}

