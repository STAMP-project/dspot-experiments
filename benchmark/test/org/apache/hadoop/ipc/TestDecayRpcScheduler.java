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
package org.apache.hadoop.ipc;


import DecayRpcScheduler.IPC_SCHEDULER_DECAYSCHEDULER_FACTOR_DEFAULT;
import DecayRpcScheduler.IPC_SCHEDULER_DECAYSCHEDULER_PERIOD_DEFAULT;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.junit.Assert;
import org.junit.Test;

import static DecayRpcScheduler.IPC_FCQ_DECAYSCHEDULER_FACTOR_KEY;
import static DecayRpcScheduler.IPC_FCQ_DECAYSCHEDULER_PERIOD_KEY;
import static DecayRpcScheduler.IPC_FCQ_DECAYSCHEDULER_THRESHOLDS_KEY;


public class TestDecayRpcScheduler {
    private DecayRpcScheduler scheduler;

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeScheduler() {
        scheduler = new DecayRpcScheduler((-1), "", new Configuration());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroScheduler() {
        scheduler = new DecayRpcScheduler(0, "", new Configuration());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testParsePeriod() {
        // By default
        scheduler = new DecayRpcScheduler(1, "", new Configuration());
        Assert.assertEquals(IPC_SCHEDULER_DECAYSCHEDULER_PERIOD_DEFAULT, scheduler.getDecayPeriodMillis());
        // Custom
        Configuration conf = new Configuration();
        conf.setLong(("ns." + (IPC_FCQ_DECAYSCHEDULER_PERIOD_KEY)), 1058);
        scheduler = new DecayRpcScheduler(1, "ns", conf);
        Assert.assertEquals(1058L, scheduler.getDecayPeriodMillis());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testParseFactor() {
        // Default
        scheduler = new DecayRpcScheduler(1, "", new Configuration());
        Assert.assertEquals(IPC_SCHEDULER_DECAYSCHEDULER_FACTOR_DEFAULT, scheduler.getDecayFactor(), 1.0E-5);
        // Custom
        Configuration conf = new Configuration();
        conf.set(("prefix." + (IPC_FCQ_DECAYSCHEDULER_FACTOR_KEY)), "0.125");
        scheduler = new DecayRpcScheduler(1, "prefix", conf);
        Assert.assertEquals(0.125, scheduler.getDecayFactor(), 1.0E-5);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testParseThresholds() {
        // Defaults vary by number of queues
        Configuration conf = new Configuration();
        scheduler = new DecayRpcScheduler(1, "", conf);
        assertEqualDecimalArrays(new double[]{  }, scheduler.getThresholds());
        scheduler = new DecayRpcScheduler(2, "", conf);
        assertEqualDecimalArrays(new double[]{ 0.5 }, scheduler.getThresholds());
        scheduler = new DecayRpcScheduler(3, "", conf);
        assertEqualDecimalArrays(new double[]{ 0.25, 0.5 }, scheduler.getThresholds());
        scheduler = new DecayRpcScheduler(4, "", conf);
        assertEqualDecimalArrays(new double[]{ 0.125, 0.25, 0.5 }, scheduler.getThresholds());
        // Custom
        conf = new Configuration();
        conf.set(("ns." + (IPC_FCQ_DECAYSCHEDULER_THRESHOLDS_KEY)), "1, 10, 20, 50, 85");
        scheduler = new DecayRpcScheduler(6, "ns", conf);
        assertEqualDecimalArrays(new double[]{ 0.01, 0.1, 0.2, 0.5, 0.85 }, scheduler.getThresholds());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAccumulate() {
        Configuration conf = new Configuration();
        conf.set(("ns." + (IPC_FCQ_DECAYSCHEDULER_PERIOD_KEY)), "99999999");// Never flush

        scheduler = new DecayRpcScheduler(1, "ns", conf);
        Assert.assertEquals(0, scheduler.getCallCountSnapshot().size());// empty first

        scheduler.getPriorityLevel(mockCall("A"));
        Assert.assertEquals(1, scheduler.getCallCountSnapshot().get("A").longValue());
        Assert.assertEquals(1, scheduler.getCallCountSnapshot().get("A").longValue());
        scheduler.getPriorityLevel(mockCall("A"));
        scheduler.getPriorityLevel(mockCall("B"));
        scheduler.getPriorityLevel(mockCall("A"));
        Assert.assertEquals(3, scheduler.getCallCountSnapshot().get("A").longValue());
        Assert.assertEquals(1, scheduler.getCallCountSnapshot().get("B").longValue());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testDecay() throws Exception {
        Configuration conf = new Configuration();
        conf.set(("ns." + (IPC_FCQ_DECAYSCHEDULER_PERIOD_KEY)), "999999999");// Never

        conf.set(("ns." + (IPC_FCQ_DECAYSCHEDULER_FACTOR_KEY)), "0.5");
        scheduler = new DecayRpcScheduler(1, "ns", conf);
        Assert.assertEquals(0, scheduler.getTotalCallSnapshot());
        for (int i = 0; i < 4; i++) {
            scheduler.getPriorityLevel(mockCall("A"));
        }
        Thread.sleep(1000);
        for (int i = 0; i < 8; i++) {
            scheduler.getPriorityLevel(mockCall("B"));
        }
        Assert.assertEquals(12, scheduler.getTotalCallSnapshot());
        Assert.assertEquals(4, scheduler.getCallCountSnapshot().get("A").longValue());
        Assert.assertEquals(8, scheduler.getCallCountSnapshot().get("B").longValue());
        scheduler.forceDecay();
        Assert.assertEquals(6, scheduler.getTotalCallSnapshot());
        Assert.assertEquals(2, scheduler.getCallCountSnapshot().get("A").longValue());
        Assert.assertEquals(4, scheduler.getCallCountSnapshot().get("B").longValue());
        scheduler.forceDecay();
        Assert.assertEquals(3, scheduler.getTotalCallSnapshot());
        Assert.assertEquals(1, scheduler.getCallCountSnapshot().get("A").longValue());
        Assert.assertEquals(2, scheduler.getCallCountSnapshot().get("B").longValue());
        scheduler.forceDecay();
        Assert.assertEquals(1, scheduler.getTotalCallSnapshot());
        Assert.assertEquals(null, scheduler.getCallCountSnapshot().get("A"));
        Assert.assertEquals(1, scheduler.getCallCountSnapshot().get("B").longValue());
        scheduler.forceDecay();
        Assert.assertEquals(0, scheduler.getTotalCallSnapshot());
        Assert.assertEquals(null, scheduler.getCallCountSnapshot().get("A"));
        Assert.assertEquals(null, scheduler.getCallCountSnapshot().get("B"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPriority() throws Exception {
        Configuration conf = new Configuration();
        final String namespace = "ns";
        conf.set(((namespace + ".") + (IPC_FCQ_DECAYSCHEDULER_PERIOD_KEY)), "99999999");// Never flush

        conf.set(((namespace + ".") + (IPC_FCQ_DECAYSCHEDULER_THRESHOLDS_KEY)), "25, 50, 75");
        scheduler = new DecayRpcScheduler(4, namespace, conf);
        Assert.assertEquals(0, scheduler.getPriorityLevel(mockCall("A")));
        Assert.assertEquals(2, scheduler.getPriorityLevel(mockCall("A")));
        Assert.assertEquals(0, scheduler.getPriorityLevel(mockCall("B")));
        Assert.assertEquals(1, scheduler.getPriorityLevel(mockCall("B")));
        Assert.assertEquals(0, scheduler.getPriorityLevel(mockCall("C")));
        Assert.assertEquals(0, scheduler.getPriorityLevel(mockCall("C")));
        Assert.assertEquals(1, scheduler.getPriorityLevel(mockCall("A")));
        Assert.assertEquals(1, scheduler.getPriorityLevel(mockCall("A")));
        Assert.assertEquals(1, scheduler.getPriorityLevel(mockCall("A")));
        Assert.assertEquals(2, scheduler.getPriorityLevel(mockCall("A")));
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName mxbeanName = new ObjectName((("Hadoop:service=" + namespace) + ",name=DecayRpcScheduler"));
        String cvs1 = ((String) (mbs.getAttribute(mxbeanName, "CallVolumeSummary")));
        Assert.assertTrue("Get expected JMX of CallVolumeSummary before decay", cvs1.equals("{\"A\":6,\"B\":2,\"C\":2}"));
        scheduler.forceDecay();
        String cvs2 = ((String) (mbs.getAttribute(mxbeanName, "CallVolumeSummary")));
        Assert.assertTrue("Get expected JMX for CallVolumeSummary after decay", cvs2.equals("{\"A\":3,\"B\":1,\"C\":1}"));
    }

    @Test(timeout = 2000)
    @SuppressWarnings("deprecation")
    public void testPeriodic() throws InterruptedException {
        Configuration conf = new Configuration();
        conf.set(("ns." + (IPC_FCQ_DECAYSCHEDULER_PERIOD_KEY)), "10");
        conf.set(("ns." + (IPC_FCQ_DECAYSCHEDULER_FACTOR_KEY)), "0.5");
        scheduler = new DecayRpcScheduler(1, "ns", conf);
        Assert.assertEquals(10, scheduler.getDecayPeriodMillis());
        Assert.assertEquals(0, scheduler.getTotalCallSnapshot());
        for (int i = 0; i < 64; i++) {
            scheduler.getPriorityLevel(mockCall("A"));
        }
        // It should eventually decay to zero
        while ((scheduler.getTotalCallSnapshot()) > 0) {
            Thread.sleep(10);
        } 
    }

    @Test(timeout = 60000)
    public void testNPEatInitialization() throws InterruptedException {
        // redirect the LOG to and check if there is NPE message while initializing
        // the DecayRpcScheduler
        PrintStream output = System.out;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bytes));
            // initializing DefaultMetricsSystem here would set "monitoring" flag in
            // MetricsSystemImpl to true
            DefaultMetricsSystem.initialize("NameNode");
            Configuration conf = new Configuration();
            scheduler = new DecayRpcScheduler(1, "ns", conf);
            // check if there is npe in log
            Assert.assertFalse(bytes.toString().contains("NullPointerException"));
        } finally {
            // set systout back
            System.setOut(output);
        }
    }
}

