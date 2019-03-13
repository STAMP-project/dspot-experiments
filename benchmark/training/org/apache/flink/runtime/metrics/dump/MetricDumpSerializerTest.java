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
package org.apache.flink.runtime.metrics.dump;


import MetricDump.CounterDump;
import MetricDump.GaugeDump;
import MetricDump.HistogramDump;
import MetricDump.MeterDump;
import MetricDumpSerialization.MetricDumpDeserializer;
import MetricDumpSerialization.MetricDumpSerializer;
import MetricDumpSerialization.MetricSerializationResult;
import QueryScopeInfo.JobQueryScopeInfo;
import QueryScopeInfo.OperatorQueryScopeInfo;
import QueryScopeInfo.TaskManagerQueryScopeInfo;
import QueryScopeInfo.TaskQueryScopeInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.SimpleCounter;
import org.apache.flink.metrics.util.TestHistogram;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link MetricDumpSerialization}.
 */
public class MetricDumpSerializerTest {
    @Test
    public void testNullGaugeHandling() throws IOException {
        MetricDumpSerialization.MetricDumpSerializer serializer = new MetricDumpSerialization.MetricDumpSerializer();
        MetricDumpSerialization.MetricDumpDeserializer deserializer = new MetricDumpSerialization.MetricDumpDeserializer();
        Map<Gauge<?>, Tuple2<QueryScopeInfo, String>> gauges = new HashMap<>();
        gauges.put(new Gauge<Object>() {
            @Override
            public Object getValue() {
                return null;
            }
        }, new Tuple2<QueryScopeInfo, String>(new QueryScopeInfo.JobManagerQueryScopeInfo("A"), "g"));
        MetricDumpSerialization.MetricSerializationResult output = serializer.serialize(Collections.<Counter, Tuple2<QueryScopeInfo, String>>emptyMap(), gauges, Collections.<Histogram, Tuple2<QueryScopeInfo, String>>emptyMap(), Collections.<Meter, Tuple2<QueryScopeInfo, String>>emptyMap());
        // no metrics should be serialized
        Assert.assertEquals(0, output.serializedCounters.length);
        Assert.assertEquals(0, output.serializedGauges.length);
        Assert.assertEquals(0, output.serializedHistograms.length);
        Assert.assertEquals(0, output.serializedMeters.length);
        List<MetricDump> deserialized = deserializer.deserialize(output);
        Assert.assertEquals(0, deserialized.size());
    }

    @Test
    public void testJavaSerialization() throws IOException {
        MetricDumpSerialization.MetricDumpSerializer serializer = new MetricDumpSerialization.MetricDumpSerializer();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(serializer.serialize(new HashMap<Counter, Tuple2<QueryScopeInfo, String>>(), new HashMap<Gauge<?>, Tuple2<QueryScopeInfo, String>>(), new HashMap<Histogram, Tuple2<QueryScopeInfo, String>>(), new HashMap<Meter, Tuple2<QueryScopeInfo, String>>()));
    }

    @Test
    public void testSerialization() throws IOException {
        MetricDumpSerialization.MetricDumpSerializer serializer = new MetricDumpSerialization.MetricDumpSerializer();
        MetricDumpSerialization.MetricDumpDeserializer deserializer = new MetricDumpSerialization.MetricDumpDeserializer();
        Map<Counter, Tuple2<QueryScopeInfo, String>> counters = new HashMap<>();
        Map<Gauge<?>, Tuple2<QueryScopeInfo, String>> gauges = new HashMap<>();
        Map<Histogram, Tuple2<QueryScopeInfo, String>> histograms = new HashMap<>();
        Map<Meter, Tuple2<QueryScopeInfo, String>> meters = new HashMap<>();
        SimpleCounter c1 = new SimpleCounter();
        SimpleCounter c2 = new SimpleCounter();
        c1.inc(1);
        c2.inc(2);
        Gauge<Integer> g1 = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 4;
            }
        };
        Histogram h1 = new TestHistogram();
        Meter m1 = new Meter() {
            @Override
            public void markEvent() {
            }

            @Override
            public void markEvent(long n) {
            }

            @Override
            public double getRate() {
                return 5;
            }

            @Override
            public long getCount() {
                return 10;
            }
        };
        counters.put(c1, new Tuple2<QueryScopeInfo, String>(new QueryScopeInfo.JobManagerQueryScopeInfo("A"), "c1"));
        counters.put(c2, new Tuple2<QueryScopeInfo, String>(new QueryScopeInfo.TaskManagerQueryScopeInfo("tmid", "B"), "c2"));
        meters.put(m1, new Tuple2<QueryScopeInfo, String>(new QueryScopeInfo.JobQueryScopeInfo("jid", "C"), "c3"));
        gauges.put(g1, new Tuple2<QueryScopeInfo, String>(new QueryScopeInfo.TaskQueryScopeInfo("jid", "vid", 2, "D"), "g1"));
        histograms.put(h1, new Tuple2<QueryScopeInfo, String>(new QueryScopeInfo.OperatorQueryScopeInfo("jid", "vid", 2, "opname", "E"), "h1"));
        MetricDumpSerialization.MetricSerializationResult serialized = serializer.serialize(counters, gauges, histograms, meters);
        List<MetricDump> deserialized = deserializer.deserialize(serialized);
        // ===== Counters ==============================================================================================
        Assert.assertEquals(5, deserialized.size());
        for (MetricDump metric : deserialized) {
            switch (metric.getCategory()) {
                case MetricDump.METRIC_CATEGORY_COUNTER :
                    MetricDump.CounterDump counterDump = ((MetricDump.CounterDump) (metric));
                    switch (((byte) (counterDump.count))) {
                        case 1 :
                            Assert.assertTrue(((counterDump.scopeInfo) instanceof QueryScopeInfo.JobManagerQueryScopeInfo));
                            Assert.assertEquals("A", counterDump.scopeInfo.scope);
                            Assert.assertEquals("c1", counterDump.name);
                            counters.remove(c1);
                            break;
                        case 2 :
                            Assert.assertTrue(((counterDump.scopeInfo) instanceof QueryScopeInfo.TaskManagerQueryScopeInfo));
                            Assert.assertEquals("B", counterDump.scopeInfo.scope);
                            Assert.assertEquals("c2", counterDump.name);
                            Assert.assertEquals("tmid", ((QueryScopeInfo.TaskManagerQueryScopeInfo) (counterDump.scopeInfo)).taskManagerID);
                            counters.remove(c2);
                            break;
                        default :
                            Assert.fail();
                    }
                    break;
                case MetricDump.METRIC_CATEGORY_GAUGE :
                    MetricDump.GaugeDump gaugeDump = ((MetricDump.GaugeDump) (metric));
                    Assert.assertEquals("4", gaugeDump.value);
                    Assert.assertEquals("g1", gaugeDump.name);
                    Assert.assertTrue(((gaugeDump.scopeInfo) instanceof QueryScopeInfo.TaskQueryScopeInfo));
                    QueryScopeInfo.TaskQueryScopeInfo taskInfo = ((QueryScopeInfo.TaskQueryScopeInfo) (gaugeDump.scopeInfo));
                    Assert.assertEquals("D", taskInfo.scope);
                    Assert.assertEquals("jid", taskInfo.jobID);
                    Assert.assertEquals("vid", taskInfo.vertexID);
                    Assert.assertEquals(2, taskInfo.subtaskIndex);
                    gauges.remove(g1);
                    break;
                case MetricDump.METRIC_CATEGORY_HISTOGRAM :
                    MetricDump.HistogramDump histogramDump = ((MetricDump.HistogramDump) (metric));
                    Assert.assertEquals("h1", histogramDump.name);
                    Assert.assertEquals(0.5, histogramDump.median, 0.1);
                    Assert.assertEquals(0.75, histogramDump.p75, 0.1);
                    Assert.assertEquals(0.9, histogramDump.p90, 0.1);
                    Assert.assertEquals(0.95, histogramDump.p95, 0.1);
                    Assert.assertEquals(0.98, histogramDump.p98, 0.1);
                    Assert.assertEquals(0.99, histogramDump.p99, 0.1);
                    Assert.assertEquals(0.999, histogramDump.p999, 0.1);
                    Assert.assertEquals(4, histogramDump.mean, 0.1);
                    Assert.assertEquals(5, histogramDump.stddev, 0.1);
                    Assert.assertEquals(6, histogramDump.max);
                    Assert.assertEquals(7, histogramDump.min);
                    Assert.assertTrue(((histogramDump.scopeInfo) instanceof QueryScopeInfo.OperatorQueryScopeInfo));
                    QueryScopeInfo.OperatorQueryScopeInfo opInfo = ((QueryScopeInfo.OperatorQueryScopeInfo) (histogramDump.scopeInfo));
                    Assert.assertEquals("E", opInfo.scope);
                    Assert.assertEquals("jid", opInfo.jobID);
                    Assert.assertEquals("vid", opInfo.vertexID);
                    Assert.assertEquals(2, opInfo.subtaskIndex);
                    Assert.assertEquals("opname", opInfo.operatorName);
                    histograms.remove(h1);
                    break;
                case MetricDump.METRIC_CATEGORY_METER :
                    MetricDump.MeterDump meterDump = ((MetricDump.MeterDump) (metric));
                    Assert.assertEquals(5.0, meterDump.rate, 0.1);
                    Assert.assertTrue(((meterDump.scopeInfo) instanceof QueryScopeInfo.JobQueryScopeInfo));
                    Assert.assertEquals("C", meterDump.scopeInfo.scope);
                    Assert.assertEquals("c3", meterDump.name);
                    Assert.assertEquals("jid", ((QueryScopeInfo.JobQueryScopeInfo) (meterDump.scopeInfo)).jobID);
                    break;
                default :
                    Assert.fail();
            }
        }
        Assert.assertTrue(counters.isEmpty());
        Assert.assertTrue(gauges.isEmpty());
        Assert.assertTrue(histograms.isEmpty());
    }
}

