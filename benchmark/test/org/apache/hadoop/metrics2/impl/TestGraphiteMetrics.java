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
package org.apache.hadoop.metrics2.impl;


import GraphiteSink.Graphite;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.sink.GraphiteSink;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static MsInfo.Context;
import static MsInfo.Hostname;


public class TestGraphiteMetrics {
    @Test
    public void testPutMetrics() {
        GraphiteSink sink = new GraphiteSink();
        List<MetricsTag> tags = new ArrayList<MetricsTag>();
        tags.add(new MetricsTag(Context, "all"));
        tags.add(new MetricsTag(Hostname, "host"));
        Set<AbstractMetric> metrics = new HashSet<AbstractMetric>();
        metrics.add(makeMetric("foo1", 1.25));
        metrics.add(makeMetric("foo2", 2.25));
        MetricsRecord record = new MetricsRecordImpl(Context, ((long) (10000)), tags, metrics);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        final GraphiteSink.Graphite mockGraphite = makeGraphite();
        Whitebox.setInternalState(sink, "graphite", mockGraphite);
        sink.putMetrics(record);
        try {
            Mockito.verify(mockGraphite).write(argument.capture());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = argument.getValue();
        Assert.assertEquals(true, ((result.equals(("null.all.Context.Context=all.Hostname=host.foo1 1.25 10\n" + "null.all.Context.Context=all.Hostname=host.foo2 2.25 10\n"))) || (result.equals(("null.all.Context.Context=all.Hostname=host.foo2 2.25 10\n" + "null.all.Context.Context=all.Hostname=host.foo1 1.25 10\n")))));
    }

    @Test
    public void testPutMetrics2() {
        GraphiteSink sink = new GraphiteSink();
        List<MetricsTag> tags = new ArrayList<MetricsTag>();
        tags.add(new MetricsTag(Context, "all"));
        tags.add(new MetricsTag(Hostname, null));
        Set<AbstractMetric> metrics = new HashSet<AbstractMetric>();
        metrics.add(makeMetric("foo1", 1));
        metrics.add(makeMetric("foo2", 2));
        MetricsRecord record = new MetricsRecordImpl(Context, ((long) (10000)), tags, metrics);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        final GraphiteSink.Graphite mockGraphite = makeGraphite();
        Whitebox.setInternalState(sink, "graphite", mockGraphite);
        sink.putMetrics(record);
        try {
            Mockito.verify(mockGraphite).write(argument.capture());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = argument.getValue();
        Assert.assertEquals(true, ((result.equals(("null.all.Context.Context=all.foo1 1 10\n" + "null.all.Context.Context=all.foo2 2 10\n"))) || (result.equals(("null.all.Context.Context=all.foo2 2 10\n" + "null.all.Context.Context=all.foo1 1 10\n")))));
    }

    /**
     * Assert that timestamps are converted correctly, ticket HADOOP-11182
     */
    @Test
    public void testPutMetrics3() {
        // setup GraphiteSink
        GraphiteSink sink = new GraphiteSink();
        final GraphiteSink.Graphite mockGraphite = makeGraphite();
        Whitebox.setInternalState(sink, "graphite", mockGraphite);
        // given two metrics records with timestamps 1000 milliseconds apart.
        List<MetricsTag> tags = Collections.emptyList();
        Set<AbstractMetric> metrics = new HashSet<AbstractMetric>();
        metrics.add(makeMetric("foo1", 1));
        MetricsRecord record1 = new MetricsRecordImpl(Context, 1000000000000L, tags, metrics);
        MetricsRecord record2 = new MetricsRecordImpl(Context, 1000000001000L, tags, metrics);
        sink.putMetrics(record1);
        sink.putMetrics(record2);
        sink.flush();
        try {
            sink.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // then the timestamps in the graphite stream should differ by one second.
        try {
            Mockito.verify(mockGraphite).write(ArgumentMatchers.eq("null.default.Context.foo1 1 1000000000\n"));
            Mockito.verify(mockGraphite).write(ArgumentMatchers.eq("null.default.Context.foo1 1 1000000001\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFailureAndPutMetrics() throws IOException {
        GraphiteSink sink = new GraphiteSink();
        List<MetricsTag> tags = new ArrayList<MetricsTag>();
        tags.add(new MetricsTag(Context, "all"));
        tags.add(new MetricsTag(Hostname, "host"));
        Set<AbstractMetric> metrics = new HashSet<AbstractMetric>();
        metrics.add(makeMetric("foo1", 1.25));
        metrics.add(makeMetric("foo2", 2.25));
        MetricsRecord record = new MetricsRecordImpl(Context, ((long) (10000)), tags, metrics);
        final GraphiteSink.Graphite mockGraphite = makeGraphite();
        Whitebox.setInternalState(sink, "graphite", mockGraphite);
        // throw exception when first try
        Mockito.doThrow(new IOException("IO exception")).when(mockGraphite).write(ArgumentMatchers.anyString());
        sink.putMetrics(record);
        Mockito.verify(mockGraphite).write(ArgumentMatchers.anyString());
        Mockito.verify(mockGraphite).close();
        // reset mock and try again
        Mockito.reset(mockGraphite);
        Mockito.when(mockGraphite.isConnected()).thenReturn(false);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        sink.putMetrics(record);
        Mockito.verify(mockGraphite).write(argument.capture());
        String result = argument.getValue();
        Assert.assertEquals(true, ((result.equals(("null.all.Context.Context=all.Hostname=host.foo1 1.25 10\n" + "null.all.Context.Context=all.Hostname=host.foo2 2.25 10\n"))) || (result.equals(("null.all.Context.Context=all.Hostname=host.foo2 2.25 10\n" + "null.all.Context.Context=all.Hostname=host.foo1 1.25 10\n")))));
    }

    @Test
    public void testClose() {
        GraphiteSink sink = new GraphiteSink();
        final GraphiteSink.Graphite mockGraphite = makeGraphite();
        Whitebox.setInternalState(sink, "graphite", mockGraphite);
        try {
            sink.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        try {
            Mockito.verify(mockGraphite).close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

