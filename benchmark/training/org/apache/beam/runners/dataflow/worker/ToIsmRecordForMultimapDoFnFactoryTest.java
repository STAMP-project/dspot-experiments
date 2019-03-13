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
package org.apache.beam.runners.dataflow.worker;


import GlobalWindow.INSTANCE;
import PropertyNames.ENCODING;
import PropertyNames.OBJECT_TYPE_NAME;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecord;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ParDoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ToIsmRecordForMultimapDoFnFactory}.
 */
@RunWith(JUnit4.class)
public class ToIsmRecordForMultimapDoFnFactoryTest {
    @Test
    public void testConversionOfRecord() throws Exception {
        ParDoFn parDoFn = /* pipeline options */
        /* side input infos */
        /* main output tag */
        /* output tag to receiver index */
        /* exection context */
        /* operation context */
        new ToIsmRecordForMultimapDoFnFactory().create(null, CloudObject.fromSpec(ImmutableMap.of(OBJECT_TYPE_NAME, "ToIsmRecordForMultimapDoFn", ENCODING, createIsmRecordEncoding())), null, null, null, null, null);
        List<Object> outputReceiver = new ArrayList<>();
        parDoFn.startBundle(outputReceiver::add);
        parDoFn.processElement(valueInGlobalWindow(/* shard key */
        KV.of(12, ImmutableList.of(/* sort key */
        /* value */
        KV.of(/* user key */
        /* window */
        KV.of(42, INSTANCE), 4), /* sort key */
        /* value */
        KV.of(/* user key */
        /* window */
        KV.of(42, INSTANCE), 5), /* sort key */
        /* value */
        KV.of(/* user key */
        /* window */
        KV.of(43, INSTANCE), 6), /* sort key */
        /* value */
        KV.of(/* user key */
        /* window */
        KV.of(44, INSTANCE), 7), /* sort key */
        /* value */
        KV.of(/* user key */
        /* window */
        KV.of(44, INSTANCE), 8)))));
        Assert.assertThat(outputReceiver, /* same structural value  as above */
        /* same structural value as above and final value */
        Matchers.contains(valueInGlobalWindow(IsmRecord.of(ImmutableList.of(42, INSTANCE, 0L), 4)), valueInGlobalWindow(IsmRecord.of(ImmutableList.of(42, INSTANCE, 1L), 5)), valueInGlobalWindow(IsmRecord.of(ImmutableList.of(43, INSTANCE, 0L), 6)), valueInGlobalWindow(IsmRecord.of(ImmutableList.of(44, INSTANCE, 0L), 7)), valueInGlobalWindow(IsmRecord.of(ImmutableList.of(44, INSTANCE, 1L), 8))));
    }
}

