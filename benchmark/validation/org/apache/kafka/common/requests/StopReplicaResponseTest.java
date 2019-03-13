/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.requests;


import Errors.CLUSTER_AUTHORIZATION_FAILED;
import Errors.NONE;
import Errors.NOT_LEADER_FOR_PARTITION;
import Errors.UNKNOWN_SERVER_ERROR;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.Errors;
import org.junit.Assert;
import org.junit.Test;


public class StopReplicaResponseTest {
    @Test
    public void testErrorCountsFromGetErrorResponse() {
        StopReplicaRequest request = build();
        StopReplicaResponse response = request.getErrorResponse(0, CLUSTER_AUTHORIZATION_FAILED.exception());
        Assert.assertEquals(Collections.singletonMap(CLUSTER_AUTHORIZATION_FAILED, 2), response.errorCounts());
    }

    @Test
    public void testErrorCountsWithTopLevelError() {
        Map<TopicPartition, Errors> errors = new HashMap<>();
        errors.put(new TopicPartition("foo", 0), NONE);
        errors.put(new TopicPartition("foo", 1), NOT_LEADER_FOR_PARTITION);
        StopReplicaResponse response = new StopReplicaResponse(Errors.UNKNOWN_SERVER_ERROR, errors);
        Assert.assertEquals(Collections.singletonMap(UNKNOWN_SERVER_ERROR, 2), response.errorCounts());
    }

    @Test
    public void testErrorCountsNoTopLevelError() {
        Map<TopicPartition, Errors> errors = new HashMap<>();
        errors.put(new TopicPartition("foo", 0), NONE);
        errors.put(new TopicPartition("foo", 1), CLUSTER_AUTHORIZATION_FAILED);
        StopReplicaResponse response = new StopReplicaResponse(Errors.NONE, errors);
        Map<Errors, Integer> errorCounts = response.errorCounts();
        Assert.assertEquals(2, errorCounts.size());
        Assert.assertEquals(1, errorCounts.get(NONE).intValue());
        Assert.assertEquals(1, errorCounts.get(CLUSTER_AUTHORIZATION_FAILED).intValue());
    }

    @Test
    public void testToString() {
        Map<TopicPartition, Errors> errors = new HashMap<>();
        errors.put(new TopicPartition("foo", 0), NONE);
        errors.put(new TopicPartition("foo", 1), CLUSTER_AUTHORIZATION_FAILED);
        StopReplicaResponse response = new StopReplicaResponse(Errors.NONE, errors);
        String responseStr = response.toString();
        Assert.assertTrue(responseStr.contains(StopReplicaResponse.class.getSimpleName()));
        Assert.assertTrue(responseStr.contains(errors.toString()));
        Assert.assertTrue(responseStr.contains(NONE.name()));
    }
}

