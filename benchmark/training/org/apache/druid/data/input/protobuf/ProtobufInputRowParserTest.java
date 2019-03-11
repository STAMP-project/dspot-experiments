/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.data.input.protobuf;


import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.JavaScriptParseSpec;
import org.apache.druid.data.input.impl.ParseSpec;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.java.util.common.parsers.ParseException;
import org.apache.druid.js.JavaScriptConfig;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.apache.druid.data.input.protobuf.ProtoTestEventWrapper.ProtoTestEvent.EventCategory.CATEGORY_ONE;
import static org.apache.druid.data.input.protobuf.ProtoTestEventWrapper.ProtoTestEvent.Foo.newBuilder;


public class ProtobufInputRowParserTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ParseSpec parseSpec;

    @Test
    public void testShortMessageType() {
        // configure parser with desc file, and specify which file name to use
        // expected to create parser without exception
        @SuppressWarnings("unused")
        ProtobufInputRowParser parser = new ProtobufInputRowParser(parseSpec, "prototest.desc", "ProtoTestEvent");
        parser.initDescriptor();
    }

    @Test
    public void testLongMessageType() {
        // configure parser with desc file, and specify which file name to use
        // expected to create parser without exception
        @SuppressWarnings("unused")
        ProtobufInputRowParser parser = new ProtobufInputRowParser(parseSpec, "prototest.desc", "prototest.ProtoTestEvent");
        parser.initDescriptor();
    }

    @Test(expected = ParseException.class)
    public void testBadProto() {
        // configure parser with desc file
        // expected exception
        @SuppressWarnings("unused")
        ProtobufInputRowParser parser = new ProtobufInputRowParser(parseSpec, "prototest.desc", "BadName");
        parser.initDescriptor();
    }

    @Test(expected = ParseException.class)
    public void testMalformedDescriptorUrl() {
        // configure parser with non existent desc file
        // expected exception
        @SuppressWarnings("unused")
        ProtobufInputRowParser parser = new ProtobufInputRowParser(parseSpec, "file:/nonexist.desc", "BadName");
        parser.initDescriptor();
    }

    @Test
    public void testSingleDescriptorNoMessageType() {
        // For the backward compatibility, protoMessageType allows null when the desc file has only one message type.
        // expected to create parser without exception
        @SuppressWarnings("unused")
        ProtobufInputRowParser parser = new ProtobufInputRowParser(parseSpec, "prototest.desc", null);
        parser.initDescriptor();
    }

    @Test
    public void testParse() throws Exception {
        // configure parser with desc file
        ProtobufInputRowParser parser = new ProtobufInputRowParser(parseSpec, "prototest.desc", "ProtoTestEvent");
        // create binary of proto test event
        DateTime dateTime = new DateTime(2012, 7, 12, 9, 30, ISOChronology.getInstanceUTC());
        ProtoTestEventWrapper.ProtoTestEvent event = ProtoTestEventWrapper.ProtoTestEvent.newBuilder().setDescription("description").setEventType(CATEGORY_ONE).setId(4711L).setIsValid(true).setSomeOtherId(4712).setTimestamp(dateTime.toString()).setSomeFloatColumn(47.11F).setSomeIntColumn(815).setSomeLongColumn(816L).setFoo(newBuilder().setBar("baz")).addBar(newBuilder().setBar("bar0")).addBar(newBuilder().setBar("bar1")).build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        event.writeTo(out);
        InputRow row = parser.parseBatch(ByteBuffer.wrap(out.toByteArray())).get(0);
        System.out.println(row);
        Assert.assertEquals(dateTime.getMillis(), row.getTimestampFromEpoch());
        assertDimensionEquals(row, "id", "4711");
        assertDimensionEquals(row, "isValid", "true");
        assertDimensionEquals(row, "someOtherId", "4712");
        assertDimensionEquals(row, "description", "description");
        assertDimensionEquals(row, "eventType", CATEGORY_ONE.name());
        assertDimensionEquals(row, "foobar", "baz");
        assertDimensionEquals(row, "bar0", "bar0");
        Assert.assertEquals(47.11F, row.getMetric("someFloatColumn").floatValue(), 0.0);
        Assert.assertEquals(815.0F, row.getMetric("someIntColumn").floatValue(), 0.0);
        Assert.assertEquals(816.0F, row.getMetric("someLongColumn").floatValue(), 0.0);
    }

    @Test
    public void testDisableJavaScript() {
        final JavaScriptParseSpec parseSpec = new JavaScriptParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(ImmutableList.of("dim1", "dim2")), null, null), "func", new JavaScriptConfig(false));
        final ProtobufInputRowParser parser = new ProtobufInputRowParser(parseSpec, "prototest.desc", "ProtoTestEvent");
        expectedException.expect(CoreMatchers.instanceOf(IllegalStateException.class));
        expectedException.expectMessage("JavaScript is disabled");
        parser.parseBatch(ByteBuffer.allocate(1)).get(0);
    }
}

