/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.sql.meta.provider.pubsub;


import FieldType.INT32;
import FieldType.STRING;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import org.apache.beam.sdk.extensions.sql.impl.utils.CalciteUtils;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.sdk.values.TupleTagList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unit tests for {@link PubsubMessageToRow}.
 */
public class PubsubMessageToRowTest implements Serializable {
    @Rule
    public transient TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testConvertsMessages() {
        Schema payloadSchema = Schema.builder().addNullableField("id", INT32).addNullableField("name", STRING).build();
        Schema messageSchema = Schema.builder().addDateTimeField("event_timestamp").addMapField("attributes", CalciteUtils.VARCHAR, CalciteUtils.VARCHAR).addRowField("payload", payloadSchema).build();
        PCollection<Row> rows = pipeline.apply("create", Create.timestamped(message(1, map("attr", "val"), "{ \"id\" : 3, \"name\" : \"foo\" }"), message(2, map("bttr", "vbl"), "{ \"name\" : \"baz\", \"id\" : 5 }"), message(3, map("cttr", "vcl"), "{ \"id\" : 7, \"name\" : \"bar\" }"), message(4, map("dttr", "vdl"), "{ \"name\" : \"qaz\", \"id\" : 8 }"), message(4, map("dttr", "vdl"), "{ \"name\" : null, \"id\" : null }"))).apply("convert", ParDo.of(PubsubMessageToRow.builder().messageSchema(messageSchema).useDlq(false).build()));
        PAssert.that(rows).containsInAnyOrder(Row.withSchema(messageSchema).addValues(ts(1), map("attr", "val"), row(payloadSchema, 3, "foo")).build(), Row.withSchema(messageSchema).addValues(ts(2), map("bttr", "vbl"), row(payloadSchema, 5, "baz")).build(), Row.withSchema(messageSchema).addValues(ts(3), map("cttr", "vcl"), row(payloadSchema, 7, "bar")).build(), Row.withSchema(messageSchema).addValues(ts(4), map("dttr", "vdl"), row(payloadSchema, 8, "qaz")).build(), Row.withSchema(messageSchema).addValues(ts(4), map("dttr", "vdl"), row(payloadSchema, null, null)).build());
        pipeline.run();
    }

    @Test
    public void testSendsInvalidToDLQ() {
        Schema payloadSchema = Schema.builder().addInt32Field("id").addStringField("name").build();
        Schema messageSchema = Schema.builder().addDateTimeField("event_timestamp").addMapField("attributes", CalciteUtils.VARCHAR, CalciteUtils.VARCHAR).addRowField("payload", payloadSchema).build();
        PCollectionTuple outputs = pipeline.apply("create", Create.timestamped(message(1, map("attr1", "val1"), "{ \"invalid1\" : \"sdfsd\" }"), message(2, map("attr2", "val2"), "{ \"invalid2"), message(3, map("attr", "val"), "{ \"id\" : 3, \"name\" : \"foo\" }"), message(4, map("bttr", "vbl"), "{ \"name\" : \"baz\", \"id\" : 5 }"))).apply("convert", ParDo.of(PubsubMessageToRow.builder().messageSchema(messageSchema).useDlq(true).build()).withOutputTags(PubsubMessageToRow.MAIN_TAG, TupleTagList.of(PubsubMessageToRow.DLQ_TAG)));
        PCollection<Row> rows = outputs.get(PubsubMessageToRow.MAIN_TAG);
        PCollection<PubsubMessage> dlqMessages = outputs.get(PubsubMessageToRow.DLQ_TAG);
        PAssert.that(dlqMessages).satisfies(( messages) -> {
            assertEquals(2, size(messages));
            assertEquals(ImmutableSet.of(map("attr1", "val1"), map("attr2", "val2")), convertToSet(messages, ( m) -> m.getAttributeMap()));
            assertEquals(ImmutableSet.of("{ \"invalid1\" : \"sdfsd\" }", "{ \"invalid2"), convertToSet(messages, ( m) -> new String(m.getPayload(), StandardCharsets.UTF_8)));
            return null;
        });
        PAssert.that(rows).containsInAnyOrder(Row.withSchema(messageSchema).addValues(ts(3), map("attr", "val"), row(payloadSchema, 3, "foo")).build(), Row.withSchema(messageSchema).addValues(ts(4), map("bttr", "vbl"), row(payloadSchema, 5, "baz")).build());
        pipeline.run();
    }
}

