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
package org.apache.beam.runners.dataflow.transforms;


import PCollection.IsBounded.UNBOUNDED;
import com.google.api.services.dataflow.Dataflow;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link GroupByKey} for the {@link DataflowRunner}.
 */
@RunWith(JUnit4.class)
public class DataflowGroupByKeyTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Dataflow dataflow;

    @Test
    public void testInvalidWindowsService() {
        Pipeline p = createTestServiceRunner();
        List<KV<String, Integer>> ungroupedPairs = Arrays.asList();
        PCollection<KV<String, Integer>> input = p.apply(Create.of(ungroupedPairs).withCoder(KvCoder.of(StringUtf8Coder.of(), BigEndianIntegerCoder.of()))).apply(Window.into(Sessions.withGapDuration(Duration.standardMinutes(1))));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("GroupByKey must have a valid Window merge function");
        input.apply("GroupByKey", GroupByKey.create()).apply("GroupByKeyAgain", GroupByKey.create());
    }

    @Test
    public void testGroupByKeyServiceUnbounded() {
        Pipeline p = createTestServiceRunner();
        PCollection<KV<String, Integer>> input = p.apply(new org.apache.beam.sdk.transforms.PTransform<PBegin, PCollection<KV<String, Integer>>>() {
            @Override
            public PCollection<KV<String, Integer>> expand(PBegin input) {
                return PCollection.createPrimitiveOutputInternal(input.getPipeline(), WindowingStrategy.globalDefault(), UNBOUNDED, KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()));
            }
        });
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(("GroupByKey cannot be applied to non-bounded PCollection in the GlobalWindow without " + "a trigger. Use a Window.into or Window.triggering transform prior to GroupByKey."));
        input.apply("GroupByKey", GroupByKey.create());
    }
}

