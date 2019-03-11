/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.mongo;


import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.drill.categories.MongoStorageTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.exceptions.ExecutionSetupException;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.apache.drill.exec.store.mongo.common.ChunkInfo;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class, MongoStorageTest.class })
public class TestMongoChunkAssignment {
    static final String HOST_A = "A";

    static final String HOST_B = "B";

    static final String HOST_C = "C";

    static final String HOST_D = "D";

    static final String HOST_E = "E";

    static final String HOST_F = "F";

    static final String HOST_G = "G";

    static final String HOST_H = "H";

    static final String HOST_I = "I";

    static final String HOST_J = "J";

    static final String HOST_K = "K";

    static final String HOST_L = "L";

    static final String HOST_M = "M";

    static final String HOST_X = "X";

    static final String dbName = "testDB";

    static final String collectionName = "testCollection";

    private Map<String, Set<ServerAddress>> chunksMapping;

    private Map<String, List<ChunkInfo>> chunksInverseMapping;

    private MongoGroupScan mongoGroupScan;

    @Test
    public void testMongoGroupScanAssignmentMix() throws UnknownHostException, ExecutionSetupException {
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        final DrillbitEndpoint DB_A = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_A).setControlPort(1234).build();
        endpoints.add(DB_A);
        endpoints.add(DB_A);
        final DrillbitEndpoint DB_B = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_B).setControlPort(1234).build();
        endpoints.add(DB_B);
        final DrillbitEndpoint DB_D = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_D).setControlPort(1234).build();
        endpoints.add(DB_D);
        final DrillbitEndpoint DB_X = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_X).setControlPort(1234).build();
        endpoints.add(DB_X);
        mongoGroupScan.applyAssignments(endpoints);
        // assignments for chunks on host A, assign on drill bit A
        Assert.assertEquals(1, mongoGroupScan.getSpecificScan(0).getChunkScanSpecList().size());
        // assignments for chunks on host A, assign on drill bit A
        Assert.assertEquals(1, mongoGroupScan.getSpecificScan(1).getChunkScanSpecList().size());
        // assignments for chunks on host B, assign on drill bit B
        Assert.assertEquals(1, mongoGroupScan.getSpecificScan(2).getChunkScanSpecList().size());
        // assignments for chunks on host D, assign on drill bit D
        Assert.assertEquals(1, mongoGroupScan.getSpecificScan(3).getChunkScanSpecList().size());
        // assignments for chunks on host C, assign on drill bit X
        Assert.assertEquals(2, mongoGroupScan.getSpecificScan(4).getChunkScanSpecList().size());
    }

    @Test
    public void testMongoGroupScanAssignmentAllAffinity() throws UnknownHostException, ExecutionSetupException {
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        final DrillbitEndpoint DB_A = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_A).setControlPort(1234).build();
        endpoints.add(DB_A);
        final DrillbitEndpoint DB_B = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_B).setControlPort(1234).build();
        endpoints.add(DB_B);
        final DrillbitEndpoint DB_C = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_C).setControlPort(1234).build();
        endpoints.add(DB_C);
        final DrillbitEndpoint DB_D = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_D).setControlPort(1234).build();
        endpoints.add(DB_D);
        mongoGroupScan.applyAssignments(endpoints);
        // assignments for chunks on host A, assign on drill bit A
        Assert.assertEquals(2, mongoGroupScan.getSpecificScan(0).getChunkScanSpecList().size());
        // assignments for chunks on host B, assign on drill bit B
        Assert.assertEquals(1, mongoGroupScan.getSpecificScan(1).getChunkScanSpecList().size());
        // assignments for chunks on host C, assign on drill bit C
        Assert.assertEquals(2, mongoGroupScan.getSpecificScan(2).getChunkScanSpecList().size());
        // assignments for chunks on host D, assign on drill bit D
        Assert.assertEquals(1, mongoGroupScan.getSpecificScan(3).getChunkScanSpecList().size());
    }

    @Test
    public void testMongoGroupScanAssignmentNoAffinity() throws UnknownHostException, ExecutionSetupException {
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        final DrillbitEndpoint DB_M = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_M).setControlPort(1234).build();
        endpoints.add(DB_M);
        endpoints.add(DB_M);
        final DrillbitEndpoint DB_L = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_L).setControlPort(1234).build();
        endpoints.add(DB_L);
        final DrillbitEndpoint DB_X = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_X).setControlPort(1234).build();
        endpoints.add(DB_X);
        mongoGroupScan.applyAssignments(endpoints);
        // assignments for chunks on host A, assign on drill bit M
        Assert.assertEquals(1, mongoGroupScan.getSpecificScan(0).getChunkScanSpecList().size());
        // assignments for chunks on host B, assign on drill bit M
        Assert.assertEquals(2, mongoGroupScan.getSpecificScan(1).getChunkScanSpecList().size());
        // assignments for chunks on host C, assign on drill bit L
        Assert.assertEquals(2, mongoGroupScan.getSpecificScan(2).getChunkScanSpecList().size());
        // assignments for chunks on host D, assign on drill bit X
        Assert.assertEquals(1, mongoGroupScan.getSpecificScan(3).getChunkScanSpecList().size());
    }

    @Test
    public void testMongoGroupScanAssignmentWhenOnlyOneDrillBit() throws UnknownHostException, ExecutionSetupException {
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        final DrillbitEndpoint DB_A = DrillbitEndpoint.newBuilder().setAddress(TestMongoChunkAssignment.HOST_A).setControlPort(1234).build();
        endpoints.add(DB_A);
        mongoGroupScan.applyAssignments(endpoints);
        // All the assignments should be given to drill bit A.
        Assert.assertEquals(6, mongoGroupScan.getSpecificScan(0).getChunkScanSpecList().size());
    }
}

