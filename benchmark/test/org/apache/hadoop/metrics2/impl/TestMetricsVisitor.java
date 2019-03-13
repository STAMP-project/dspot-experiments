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


import java.util.List;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.MetricsVisitor;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test the metric visitor interface
 */
@RunWith(MockitoJUnitRunner.class)
public class TestMetricsVisitor {
    @Captor
    private ArgumentCaptor<MetricsInfo> c1;

    @Captor
    private ArgumentCaptor<MetricsInfo> c2;

    @Captor
    private ArgumentCaptor<MetricsInfo> g1;

    @Captor
    private ArgumentCaptor<MetricsInfo> g2;

    @Captor
    private ArgumentCaptor<MetricsInfo> g3;

    @Captor
    private ArgumentCaptor<MetricsInfo> g4;

    /**
     * Test the common use cases
     */
    @Test
    public void testCommon() {
        MetricsVisitor visitor = Mockito.mock(MetricsVisitor.class);
        MetricsRegistry registry = new MetricsRegistry("test");
        List<AbstractMetric> metrics = MetricsLists.builder("test").addCounter(info("c1", "int counter"), 1).addCounter(info("c2", "long counter"), 2L).addGauge(info("g1", "int gauge"), 5).addGauge(info("g2", "long gauge"), 6L).addGauge(info("g3", "float gauge"), 7.0F).addGauge(info("g4", "double gauge"), 8.0).metrics();
        for (AbstractMetric metric : metrics) {
            metric.visit(visitor);
        }
        Mockito.verify(visitor).counter(c1.capture(), ArgumentMatchers.eq(1));
        Assert.assertEquals("c1 name", "c1", c1.getValue().name());
        Assert.assertEquals("c1 description", "int counter", c1.getValue().description());
        Mockito.verify(visitor).counter(c2.capture(), ArgumentMatchers.eq(2L));
        Assert.assertEquals("c2 name", "c2", c2.getValue().name());
        Assert.assertEquals("c2 description", "long counter", c2.getValue().description());
        Mockito.verify(visitor).gauge(g1.capture(), ArgumentMatchers.eq(5));
        Assert.assertEquals("g1 name", "g1", g1.getValue().name());
        Assert.assertEquals("g1 description", "int gauge", g1.getValue().description());
        Mockito.verify(visitor).gauge(g2.capture(), ArgumentMatchers.eq(6L));
        Assert.assertEquals("g2 name", "g2", g2.getValue().name());
        Assert.assertEquals("g2 description", "long gauge", g2.getValue().description());
        Mockito.verify(visitor).gauge(g3.capture(), ArgumentMatchers.eq(7.0F));
        Assert.assertEquals("g3 name", "g3", g3.getValue().name());
        Assert.assertEquals("g3 description", "float gauge", g3.getValue().description());
        Mockito.verify(visitor).gauge(g4.capture(), ArgumentMatchers.eq(8.0));
        Assert.assertEquals("g4 name", "g4", g4.getValue().name());
        Assert.assertEquals("g4 description", "double gauge", g4.getValue().description());
    }
}

