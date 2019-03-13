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
package org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity;


import CapacitySchedulerConfiguration.MAX_WAIT_BEFORE_KILL_FOR_QUEUE_BALANCE_PREEMPTION;
import CapacitySchedulerConfiguration.PREEMPTION_TO_BALANCE_QUEUES_BEYOND_GUARANTEED;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestProportionalCapacityPreemptionPolicyPreemptToBalance extends ProportionalCapacityPreemptionPolicyMockFramework {
    @Test
    public void testPreemptionToBalanceDisabled() throws IOException {
        String labelsConfig = "=100,true";// default partition

        String nodesConfig = "n1=";// only one node

        // guaranteed,max,used,pending
        String queuesConfig = "root(=[100 100 100 100]);"// root
         + ((("-a(=[30 100 10 30]);"// a
         + "-b(=[30 100 40 30]);")// b
         + "-c(=[30 100 50 30]);")// c
         + "-d(=[10 100 0 0])");// d

        // queueName\t(priority,resource,host,expression,#repeat,reserved)
        String appsConfig = "a\t(1,1,n1,,10,false);"// app1 in a
         + ("b\t(1,1,n1,,40,false);"// app2 in b
         + "c\t(1,1,n1,,50,false)");// app3 in c

        buildEnv(labelsConfig, nodesConfig, queuesConfig, appsConfig);
        policy.editSchedule();
        // I_A: A:30 B:35 C:35, preempt 5 from B and 15 from C to A
        Mockito.verify(mDisp, Mockito.times(5)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(getAppAttemptId(2))));
        Mockito.verify(mDisp, Mockito.times(15)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(getAppAttemptId(3))));
        Assert.assertEquals(30, policy.getQueuePartitions().get("a").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(35, policy.getQueuePartitions().get("b").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(35, policy.getQueuePartitions().get("c").get("").getIdealAssigned().getMemorySize());
    }

    @Test
    public void testPreemptionToBalanceEnabled() throws IOException {
        String labelsConfig = "=100,true";// default partition

        String nodesConfig = "n1=";// only one node

        // guaranteed,max,used,pending
        String queuesConfig = "root(=[100 100 100 100]);"// root
         + ((("-a(=[30 100 10 30]);"// a
         + "-b(=[30 100 40 30]);")// b
         + "-c(=[30 100 50 30]);")// c
         + "-d(=[10 100 0 0])");// d

        // queueName\t(priority,resource,host,expression,#repeat,reserved)
        String appsConfig = "a\t(1,1,n1,,10,false);"// app1 in a
         + ("b\t(1,1,n1,,40,false);"// app2 in b
         + "c\t(1,1,n1,,50,false)");// app3 in c

        // enable preempt to balance and ideal assignment will change.
        boolean isPreemptionToBalanceEnabled = true;
        conf.setBoolean(PREEMPTION_TO_BALANCE_QUEUES_BEYOND_GUARANTEED, isPreemptionToBalanceEnabled);
        buildEnv(labelsConfig, nodesConfig, queuesConfig, appsConfig);
        policy.editSchedule();
        // I_A: A:33 B:33 C:33, preempt 7 from B and 17 from C to A
        Mockito.verify(mDisp, Mockito.times(7)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(getAppAttemptId(2))));
        Mockito.verify(mDisp, Mockito.times(17)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(getAppAttemptId(3))));
        Assert.assertEquals(33, policy.getQueuePartitions().get("a").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(33, policy.getQueuePartitions().get("b").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(33, policy.getQueuePartitions().get("c").get("").getIdealAssigned().getMemorySize());
    }

    @Test
    public void testPreemptionToBalanceUsedPlusPendingLessThanGuaranteed() throws IOException {
        String labelsConfig = "=100,true";// default partition

        String nodesConfig = "n1=";// only one node

        // guaranteed,max,used,pending
        String queuesConfig = "root(=[100 100 100 100]);"// root
         + ((("-a(=[30 100 10 6]);"// a
         + "-b(=[30 100 40 30]);")// b
         + "-c(=[30 100 50 30]);")// c
         + "-d(=[10 100 0 0])");// d

        // queueName\t(priority,resource,host,expression,#repeat,reserved)
        String appsConfig = "a\t(1,1,n1,,10,false);"// app1 in a
         + ("b\t(1,1,n1,,40,false);"// app2 in b
         + "c\t(1,1,n1,,50,false)");// app3 in c

        boolean isPreemptionToBalanceEnabled = true;
        conf.setBoolean(PREEMPTION_TO_BALANCE_QUEUES_BEYOND_GUARANTEED, isPreemptionToBalanceEnabled);
        buildEnv(labelsConfig, nodesConfig, queuesConfig, appsConfig);
        policy.editSchedule();
        // I_A: A:15 B:42 C:43, preempt 7 from B and 17 from C to A
        Mockito.verify(mDisp, Mockito.times(8)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(getAppAttemptId(3))));
        Assert.assertEquals(16, policy.getQueuePartitions().get("a").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(42, policy.getQueuePartitions().get("b").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(42, policy.getQueuePartitions().get("c").get("").getIdealAssigned().getMemorySize());
    }

    @Test
    public void testPreemptionToBalanceWithVcoreResource() throws IOException {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        String labelsConfig = "=100:100,true";// default partition

        String nodesConfig = "n1=";// only one node

        // guaranteed,max,used,pending
        String queuesConfig = "root(=[100:100 100:100 100:100 120:140]);"// root
         + ("-a(=[60:60 100:100 40:40 70:40]);"// a
         + "-b(=[40:40 100:100 60:60 50:100])");// b

        // queueName\t(priority,resource,host,expression,#repeat,reserved)
        String appsConfig = "a\t(1,1:1,n1,,40,false);"// app1 in a
         + "b\t(1,1:1,n1,,60,false)";// app2 in b

        boolean isPreemptionToBalanceEnabled = true;
        conf.setBoolean(PREEMPTION_TO_BALANCE_QUEUES_BEYOND_GUARANTEED, isPreemptionToBalanceEnabled);
        buildEnv(labelsConfig, nodesConfig, queuesConfig, appsConfig, true);
        policy.editSchedule();
        // 21 containers will be preempted here
        Mockito.verify(mDisp, Mockito.times(21)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(getAppAttemptId(2))));
        Assert.assertEquals(60, policy.getQueuePartitions().get("a").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(60, policy.getQueuePartitions().get("a").get("").getIdealAssigned().getVirtualCores());
        Assert.assertEquals(40, policy.getQueuePartitions().get("b").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(40, policy.getQueuePartitions().get("b").get("").getIdealAssigned().getVirtualCores());
    }

    @Test
    public void testPreemptionToBalanceWithConfiguredTimeout() throws IOException {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        String labelsConfig = "=100:100,true";// default partition

        String nodesConfig = "n1=";// only one node

        // guaranteed,max,used,pending
        String queuesConfig = "root(=[100:100 100:100 100:100 120:140]);"// root
         + ("-a(=[60:60 100:100 40:40 70:40]);"// a
         + "-b(=[40:40 100:100 60:60 50:100])");// b

        // queueName\t(priority,resource,host,expression,#repeat,reserved)
        String appsConfig = "a\t(1,1:1,n1,,40,false);"// app1 in a
         + "b\t(1,1:1,n1,,60,false)";// app2 in b

        boolean isPreemptionToBalanceEnabled = true;
        conf.setBoolean(PREEMPTION_TO_BALANCE_QUEUES_BEYOND_GUARANTEED, isPreemptionToBalanceEnabled);
        final long FB_MAX_BEFORE_KILL = 60 * 1000;
        conf.setLong(MAX_WAIT_BEFORE_KILL_FOR_QUEUE_BALANCE_PREEMPTION, FB_MAX_BEFORE_KILL);
        buildEnv(labelsConfig, nodesConfig, queuesConfig, appsConfig, true);
        policy.editSchedule();
        Map<PreemptionCandidatesSelector, Map<ApplicationAttemptId, Set<RMContainer>>> pcps = policy.getToPreemptCandidatesPerSelector();
        String FIFO_CANDIDATE_SELECTOR = "FifoCandidatesSelector";
        boolean hasFifoSelector = false;
        for (Map.Entry<PreemptionCandidatesSelector, Map<ApplicationAttemptId, Set<RMContainer>>> pc : pcps.entrySet()) {
            if (pc.getKey().getClass().getSimpleName().equals(FIFO_CANDIDATE_SELECTOR)) {
                FifoCandidatesSelector pcs = ((FifoCandidatesSelector) (pc.getKey()));
                if ((pcs.getAllowQueuesBalanceAfterAllQueuesSatisfied()) == true) {
                    hasFifoSelector = true;
                    Assert.assertEquals(pcs.getMaximumKillWaitTimeMs(), FB_MAX_BEFORE_KILL);
                }
            }
        }
        Assert.assertEquals(hasFifoSelector, true);
        // 21 containers will be preempted here
        Mockito.verify(mDisp, Mockito.times(21)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(getAppAttemptId(2))));
        Assert.assertEquals(60, policy.getQueuePartitions().get("a").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(60, policy.getQueuePartitions().get("a").get("").getIdealAssigned().getVirtualCores());
        Assert.assertEquals(40, policy.getQueuePartitions().get("b").get("").getIdealAssigned().getMemorySize());
        Assert.assertEquals(40, policy.getQueuePartitions().get("b").get("").getIdealAssigned().getVirtualCores());
    }
}

