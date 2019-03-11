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
package org.apache.beam.runners.flink.streaming;


import java.io.Serializable;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.TestFlinkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Instant;
import org.junit.Test;

import static org.junit.Assert.assertNull;


/**
 * Tests grouping with null values.
 */
public class GroupByWithNullValuesTest implements Serializable {
    @Test
    public void testGroupByWithNullValues() {
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        options.setRunner(TestFlinkRunner.class);
        options.setStreaming(true);
        options.setShutdownSourcesOnFinalWatermark(true);
        Pipeline pipeline = Pipeline.create(options);
        PCollection<Integer> result = pipeline.apply(GenerateSequence.from(0).to(100).withTimestampFn(new org.apache.beam.sdk.transforms.SerializableFunction<Long, Instant>() {
            @Override
            public Instant apply(Long input) {
                return new Instant(input);
            }
        })).apply(org.apache.beam.sdk.transforms.windowing.Window.into(org.apache.beam.sdk.transforms.windowing.FixedWindows.of(org.joda.time.Duration.millis(10)))).apply(org.apache.beam.sdk.transforms.ParDo.of(new org.apache.beam.sdk.transforms.DoFn<Long, org.apache.beam.sdk.values.KV<String, Void>>() {
            @ProcessElement
            public void processElement(ProcessContext pc) {
                pc.output(org.apache.beam.sdk.values.KV.of("hello", null));
            }
        })).apply(org.apache.beam.sdk.transforms.GroupByKey.create()).apply(org.apache.beam.sdk.transforms.ParDo.of(new org.apache.beam.sdk.transforms.DoFn<org.apache.beam.sdk.values.KV<String, Iterable<Void>>, Integer>() {
            @ProcessElement
            public void processElement(ProcessContext pc) {
                int count = 0;
                for (Void aVoid : pc.element().getValue()) {
                    assertNull("Element should be null", aVoid);
                    count++;
                }
                pc.output(count);
            }
        }));
        PAssert.that(result).containsInAnyOrder(10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        pipeline.run();
    }
}

