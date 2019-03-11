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
package org.apache.flink.runtime.clusterframework.types;


import ResourceProfile.UNKNOWN;
import ResourceSpec.GPU_NAME;
import java.util.Collections;
import org.apache.flink.api.common.operators.ResourceSpec;
import org.junit.Assert;
import org.junit.Test;


public class ResourceProfileTest {
    @Test
    public void testMatchRequirement() throws Exception {
        ResourceProfile rp1 = new ResourceProfile(1.0, 100, 100, 100, 0, Collections.emptyMap());
        ResourceProfile rp2 = new ResourceProfile(1.0, 200, 200, 200, 0, Collections.emptyMap());
        ResourceProfile rp3 = new ResourceProfile(2.0, 100, 100, 100, 0, Collections.emptyMap());
        ResourceProfile rp4 = new ResourceProfile(2.0, 200, 200, 200, 0, Collections.emptyMap());
        Assert.assertFalse(rp1.isMatching(rp2));
        Assert.assertTrue(rp2.isMatching(rp1));
        Assert.assertFalse(rp1.isMatching(rp3));
        Assert.assertTrue(rp3.isMatching(rp1));
        Assert.assertFalse(rp2.isMatching(rp3));
        Assert.assertFalse(rp3.isMatching(rp2));
        Assert.assertTrue(rp4.isMatching(rp1));
        Assert.assertTrue(rp4.isMatching(rp2));
        Assert.assertTrue(rp4.isMatching(rp3));
        Assert.assertTrue(rp4.isMatching(rp4));
        ResourceProfile rp5 = new ResourceProfile(2.0, 100, 100, 100, 100, null);
        Assert.assertFalse(rp4.isMatching(rp5));
        ResourceSpec rs1 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(2.2).build();
        ResourceSpec rs2 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(1.1).build();
        Assert.assertFalse(rp1.isMatching(ResourceProfile.fromResourceSpec(rs1, 0)));
        Assert.assertTrue(ResourceProfile.fromResourceSpec(rs1, 0).isMatching(ResourceProfile.fromResourceSpec(rs2, 0)));
        Assert.assertFalse(ResourceProfile.fromResourceSpec(rs2, 0).isMatching(ResourceProfile.fromResourceSpec(rs1, 0)));
    }

    @Test
    public void testUnknownMatchesUnknown() {
        Assert.assertTrue(UNKNOWN.isMatching(UNKNOWN));
    }

    @Test
    public void testEquals() throws Exception {
        ResourceSpec rs1 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).build();
        ResourceSpec rs2 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).build();
        Assert.assertTrue(ResourceProfile.fromResourceSpec(rs1, 0).equals(ResourceProfile.fromResourceSpec(rs2, 0)));
        ResourceSpec rs3 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(2.2).build();
        ResourceSpec rs4 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(1.1).build();
        Assert.assertFalse(ResourceProfile.fromResourceSpec(rs3, 0).equals(ResourceProfile.fromResourceSpec(rs4, 0)));
        ResourceSpec rs5 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(2.2).build();
        Assert.assertTrue(ResourceProfile.fromResourceSpec(rs3, 100).equals(ResourceProfile.fromResourceSpec(rs5, 100)));
    }

    @Test
    public void testCompareTo() throws Exception {
        ResourceSpec rs1 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).build();
        ResourceSpec rs2 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).build();
        Assert.assertEquals(0, ResourceProfile.fromResourceSpec(rs1, 0).compareTo(ResourceProfile.fromResourceSpec(rs2, 0)));
        ResourceSpec rs3 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(2.2).build();
        Assert.assertEquals((-1), ResourceProfile.fromResourceSpec(rs1, 0).compareTo(ResourceProfile.fromResourceSpec(rs3, 0)));
        Assert.assertEquals(1, ResourceProfile.fromResourceSpec(rs3, 0).compareTo(ResourceProfile.fromResourceSpec(rs1, 0)));
        ResourceSpec rs4 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(1.1).build();
        Assert.assertEquals(1, ResourceProfile.fromResourceSpec(rs3, 0).compareTo(ResourceProfile.fromResourceSpec(rs4, 0)));
        Assert.assertEquals((-1), ResourceProfile.fromResourceSpec(rs4, 0).compareTo(ResourceProfile.fromResourceSpec(rs3, 0)));
        ResourceSpec rs5 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(2.2).build();
        Assert.assertEquals(0, ResourceProfile.fromResourceSpec(rs3, 0).compareTo(ResourceProfile.fromResourceSpec(rs5, 0)));
    }

    @Test
    public void testGet() throws Exception {
        ResourceSpec rs = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).setGPUResource(1.6).build();
        ResourceProfile rp = ResourceProfile.fromResourceSpec(rs, 50);
        Assert.assertEquals(1.0, rp.getCpuCores(), 1.0E-6);
        Assert.assertEquals(150, rp.getMemoryInMB());
        Assert.assertEquals(100, rp.getOperatorsMemoryInMB());
        Assert.assertEquals(1.6, rp.getExtendedResources().get(GPU_NAME).getValue(), 1.0E-6);
    }
}

