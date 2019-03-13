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
package org.apache.hadoop.hbase.metrics;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MetricsTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test of default BaseSource for hadoop 2
 */
@Category({ MetricsTests.class, SmallTests.class })
public class TestBaseSourceImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBaseSourceImpl.class);

    private static BaseSourceImpl bmsi;

    @Test
    public void testSetGauge() throws Exception {
        TestBaseSourceImpl.bmsi.setGauge("testset", 100);
        Assert.assertEquals(100, value());
        TestBaseSourceImpl.bmsi.setGauge("testset", 300);
        Assert.assertEquals(300, value());
    }

    @Test
    public void testIncGauge() throws Exception {
        TestBaseSourceImpl.bmsi.incGauge("testincgauge", 100);
        Assert.assertEquals(100, value());
        TestBaseSourceImpl.bmsi.incGauge("testincgauge", 100);
        Assert.assertEquals(200, value());
    }

    @Test
    public void testDecGauge() throws Exception {
        TestBaseSourceImpl.bmsi.decGauge("testdec", 100);
        Assert.assertEquals((-100), value());
        TestBaseSourceImpl.bmsi.decGauge("testdec", 100);
        Assert.assertEquals((-200), value());
    }

    @Test
    public void testIncCounters() throws Exception {
        TestBaseSourceImpl.bmsi.incCounters("testinccounter", 100);
        Assert.assertEquals(100, value());
        TestBaseSourceImpl.bmsi.incCounters("testinccounter", 100);
        Assert.assertEquals(200, value());
    }

    @Test
    public void testRemoveMetric() throws Exception {
        TestBaseSourceImpl.bmsi.setGauge("testrmgauge", 100);
        TestBaseSourceImpl.bmsi.removeMetric("testrmgauge");
        Assert.assertNull(TestBaseSourceImpl.bmsi.metricsRegistry.get("testrmgauge"));
    }
}

