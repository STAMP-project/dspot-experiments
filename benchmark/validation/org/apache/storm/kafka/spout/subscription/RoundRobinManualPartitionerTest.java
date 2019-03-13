/**
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.kafka.spout.subscription;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.task.TopologyContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RoundRobinManualPartitionerTest {
    @Test
    public void testRoundRobinPartitioning() {
        List<TopicPartition> allPartitions = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            allPartitions.add(createTp(i));
        }
        List<TopologyContext> contextMocks = new ArrayList<>();
        String thisComponentId = "A spout";
        List<Integer> allTasks = Arrays.asList(new Integer[]{ 0, 1, 2 });
        for (int i = 0; i < 3; i++) {
            TopologyContext contextMock = Mockito.mock(TopologyContext.class);
            Mockito.when(contextMock.getThisTaskIndex()).thenReturn(i);
            Mockito.when(contextMock.getThisComponentId()).thenReturn(thisComponentId);
            Mockito.when(contextMock.getComponentTasks(thisComponentId)).thenReturn(allTasks);
            contextMocks.add(contextMock);
        }
        RoundRobinManualPartitioner partitioner = new RoundRobinManualPartitioner();
        Set<TopicPartition> partitionsForFirstTask = partitioner.getPartitionsForThisTask(allPartitions, contextMocks.get(0));
        Assert.assertThat(partitionsForFirstTask, CoreMatchers.is(partitionsToTps(new int[]{ 0, 3, 6, 9 })));
        Set<TopicPartition> partitionsForSecondTask = partitioner.getPartitionsForThisTask(allPartitions, contextMocks.get(1));
        Assert.assertThat(partitionsForSecondTask, CoreMatchers.is(partitionsToTps(new int[]{ 1, 4, 7, 10 })));
        Set<TopicPartition> partitionsForThirdTask = partitioner.getPartitionsForThisTask(allPartitions, contextMocks.get(2));
        Assert.assertThat(partitionsForThirdTask, CoreMatchers.is(partitionsToTps(new int[]{ 2, 5, 8 })));
    }
}

