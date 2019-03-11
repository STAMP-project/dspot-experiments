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
package org.apache.hadoop.mapreduce.v2.app;


import AMPreemptionPolicy.Context;
import TaskType.MAP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.mapred.TaskAttemptListenerImpl;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.mapreduce.v2.app.MRAppMaster.RunningAppContext;
import org.apache.hadoop.mapreduce.v2.app.rm.RMContainerAllocator;
import org.apache.hadoop.mapreduce.v2.app.rm.preemption.AMPreemptionPolicy;
import org.apache.hadoop.mapreduce.v2.app.rm.preemption.CheckpointAMPreemptionPolicy;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.PreemptionMessage;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.junit.Test;


public class TestCheckpointPreemptionPolicy {
    TaskAttemptListenerImpl pel = null;

    RMContainerAllocator r;

    JobId jid;

    RunningAppContext mActxt;

    Set<ContainerId> preemptedContainers = new HashSet<ContainerId>();

    Map<ContainerId, TaskAttemptId> assignedContainers = new HashMap<ContainerId, TaskAttemptId>();

    private final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    HashMap<ContainerId, Resource> contToResourceMap = new HashMap<ContainerId, Resource>();

    private int minAlloc = 1024;

    @Test
    public void testStrictPreemptionContract() {
        final Map<ContainerId, TaskAttemptId> containers = assignedContainers;
        AMPreemptionPolicy.Context mPctxt = new AMPreemptionPolicy.Context() {
            @Override
            public TaskAttemptId getTaskAttempt(ContainerId cId) {
                return containers.get(cId);
            }

            @Override
            public List<Container> getContainers(TaskType t) {
                List<Container> p = new ArrayList<Container>();
                for (Map.Entry<ContainerId, TaskAttemptId> ent : assignedContainers.entrySet()) {
                    if (ent.getValue().getTaskId().getTaskType().equals(t)) {
                        p.add(Container.newInstance(ent.getKey(), null, null, contToResourceMap.get(ent.getKey()), Priority.newInstance(0), null));
                    }
                }
                return p;
            }
        };
        PreemptionMessage pM = generatePreemptionMessage(preemptedContainers, contToResourceMap, Resource.newInstance(1024, 1), true);
        CheckpointAMPreemptionPolicy policy = new CheckpointAMPreemptionPolicy();
        policy.init(mActxt);
        policy.preempt(mPctxt, pM);
        for (ContainerId c : preemptedContainers) {
            TaskAttemptId t = assignedContainers.get(c);
            if (MAP.equals(t.getTaskId().getTaskType())) {
                assert (policy.isPreempted(t)) == false;
            } else {
                assert policy.isPreempted(t);
            }
        }
    }

    @Test
    public void testPreemptionContract() {
        final Map<ContainerId, TaskAttemptId> containers = assignedContainers;
        AMPreemptionPolicy.Context mPctxt = new AMPreemptionPolicy.Context() {
            @Override
            public TaskAttemptId getTaskAttempt(ContainerId cId) {
                return containers.get(cId);
            }

            @Override
            public List<Container> getContainers(TaskType t) {
                List<Container> p = new ArrayList<Container>();
                for (Map.Entry<ContainerId, TaskAttemptId> ent : assignedContainers.entrySet()) {
                    if (ent.getValue().getTaskId().getTaskType().equals(t)) {
                        p.add(Container.newInstance(ent.getKey(), null, null, contToResourceMap.get(ent.getKey()), Priority.newInstance(0), null));
                    }
                }
                return p;
            }
        };
        PreemptionMessage pM = generatePreemptionMessage(preemptedContainers, contToResourceMap, Resource.newInstance(minAlloc, 1), false);
        CheckpointAMPreemptionPolicy policy = new CheckpointAMPreemptionPolicy();
        policy.init(mActxt);
        int supposedMemPreemption = ((int) (pM.getContract().getResourceRequest().get(0).getResourceRequest().getCapability().getMemorySize())) * (pM.getContract().getResourceRequest().get(0).getResourceRequest().getNumContainers());
        // first round of preemption
        policy.preempt(mPctxt, pM);
        List<TaskAttemptId> preempting = validatePreemption(pM, policy, supposedMemPreemption);
        // redundant message
        policy.preempt(mPctxt, pM);
        List<TaskAttemptId> preempting2 = validatePreemption(pM, policy, supposedMemPreemption);
        // check that nothing got added
        assert preempting2.equals(preempting);
        // simulate 2 task completions/successful preemption
        policy.handleCompletedContainer(preempting.get(0));
        policy.handleCompletedContainer(preempting.get(1));
        // remove from assignedContainers
        Iterator<Map.Entry<ContainerId, TaskAttemptId>> it = assignedContainers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ContainerId, TaskAttemptId> ent = it.next();
            if ((ent.getValue().equals(preempting.get(0))) || (ent.getValue().equals(preempting.get(1))))
                it.remove();

        } 
        // one more message asking for preemption
        policy.preempt(mPctxt, pM);
        // triggers preemption of 2 more containers (i.e., the preemption set changes)
        List<TaskAttemptId> preempting3 = validatePreemption(pM, policy, supposedMemPreemption);
        assert (preempting3.equals(preempting2)) == false;
    }
}

