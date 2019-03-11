/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.rest;


import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RestServiceTest {
    private List<PartitionDefinition> pds;

    private PartitionDefinition pd1;

    private PartitionDefinition pd2;

    private PartitionDefinition pd3;

    private PartitionDefinition pd4;

    private PartitionDefinition pd5;

    private PartitionDefinition pd6;

    @Test
    public void testAssignmentOnlyOneTask() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 0, 1);
        Assert.assertThat(results.size(), Matchers.is(6));
        Assert.assertEquals(pds, results);
    }

    @Test
    public void testAssignmentOptimalNumberOfTasks() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 1, 6);
        Assert.assertThat(results.size(), Matchers.is(1));
        Assert.assertThat(results.get(0), Matchers.is(pd2));
    }

    @Test
    public void testAssignmentDividingTasks() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 0, 2);
        Assert.assertThat(results.size(), Matchers.is(3));
        Assert.assertThat(results.get(0), Matchers.is(pd1));
        Assert.assertThat(results.get(1), Matchers.is(pd2));
        Assert.assertThat(results.get(2), Matchers.is(pd3));
    }

    @Test
    public void testAssignmentRemainderTasksGroup1() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 0, 4);
        Assert.assertThat(results.size(), Matchers.is(2));
        Assert.assertThat(results.get(0), Matchers.is(pd1));
        Assert.assertThat(results.get(1), Matchers.is(pd2));
    }

    @Test
    public void testAssignmentRemainderTasksGroup2() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 1, 4);
        Assert.assertThat(results.size(), Matchers.is(2));
        Assert.assertThat(results.get(0), Matchers.is(pd3));
        Assert.assertThat(results.get(1), Matchers.is(pd4));
    }

    @Test
    public void testAssignmentRemainderTasksGroup3() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 2, 4);
        Assert.assertThat(results.size(), Matchers.is(1));
        Assert.assertThat(results.get(0), Matchers.is(pd5));
    }

    @Test
    public void testAssignmentRemainderTasksGroup4() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 3, 4);
        Assert.assertThat(results.size(), Matchers.is(1));
        Assert.assertThat(results.get(0), Matchers.is(pd6));
    }

    @Test
    public void testAssignmentRemainderTasksGroup11() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 0, 5);
        Assert.assertThat(results.size(), Matchers.is(2));
        Assert.assertThat(results.get(0), Matchers.is(pd1));
        Assert.assertThat(results.get(1), Matchers.is(pd2));
    }

    @Test
    public void testAssignmentRemainderTasksGroup12() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 3, 5);
        Assert.assertThat(results.size(), Matchers.is(1));
        Assert.assertThat(results.get(0), Matchers.is(pd5));
    }

    @Test
    public void testAssignmentMoreTasksThanNeeded() throws Exception {
        List<PartitionDefinition> results = RestService.assignPartitions(pds, 6, 7);
        Assert.assertThat(results.size(), Matchers.is(0));
    }
}

