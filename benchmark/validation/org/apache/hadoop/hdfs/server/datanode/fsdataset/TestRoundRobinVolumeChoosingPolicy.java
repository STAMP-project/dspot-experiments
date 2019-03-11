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
package org.apache.hadoop.hdfs.server.datanode.fsdataset;


import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Test;


public class TestRoundRobinVolumeChoosingPolicy {
    // Test the Round-Robin block-volume choosing algorithm.
    @Test
    public void testRR() throws Exception {
        @SuppressWarnings("unchecked")
        final RoundRobinVolumeChoosingPolicy<FsVolumeSpi> policy = ReflectionUtils.newInstance(RoundRobinVolumeChoosingPolicy.class, null);
        TestRoundRobinVolumeChoosingPolicy.testRR(policy);
    }

    // ChooseVolume should throw DiskOutOfSpaceException
    // with volume and block sizes in exception message.
    @Test
    public void testRRPolicyExceptionMessage() throws Exception {
        final RoundRobinVolumeChoosingPolicy<FsVolumeSpi> policy = new RoundRobinVolumeChoosingPolicy<FsVolumeSpi>();
        TestRoundRobinVolumeChoosingPolicy.testRRPolicyExceptionMessage(policy);
    }

    // Test Round-Robin choosing algorithm with heterogeneous storage.
    @Test
    public void testRRPolicyWithStorageTypes() throws Exception {
        final RoundRobinVolumeChoosingPolicy<FsVolumeSpi> policy = new RoundRobinVolumeChoosingPolicy<FsVolumeSpi>();
        TestRoundRobinVolumeChoosingPolicy.testRRPolicyWithStorageTypes(policy);
    }
}

