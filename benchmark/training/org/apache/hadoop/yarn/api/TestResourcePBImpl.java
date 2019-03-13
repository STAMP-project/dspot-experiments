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
package org.apache.hadoop.yarn.api;


import ResourceInformation.MEMORY_MB;
import ResourceInformation.VCORES;
import YarnProtos.ResourceInformationProto;
import YarnProtos.ResourceProto;
import YarnProtos.ResourceTypeInfoProto;
import YarnProtos.ResourceTypesProto.COUNTABLE;
import YarnProtos.StringStringMapProto;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.util.resource.ResourceUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class to handle various proto related tests for resources.
 */
public class TestResourcePBImpl {
    @Test
    public void testEmptyResourcePBInit() throws Exception {
        Resource res = new ResourcePBImpl();
        // Assert to check it sets resource value and unit to default.
        Assert.assertEquals(0, res.getMemorySize());
        Assert.assertEquals(MEMORY_MB.getUnits(), res.getResourceInformation(MEMORY_MB.getName()).getUnits());
        Assert.assertEquals(VCORES.getUnits(), res.getResourceInformation(VCORES.getName()).getUnits());
    }

    @Test
    public void testResourcePBInitFromOldPB() throws Exception {
        YarnProtos.ResourceProto proto = ResourceProto.newBuilder().setMemory(1024).setVirtualCores(3).build();
        // Assert to check it sets resource value and unit to default.
        Resource res = new ResourcePBImpl(proto);
        Assert.assertEquals(1024, res.getMemorySize());
        Assert.assertEquals(3, res.getVirtualCores());
        Assert.assertEquals(MEMORY_MB.getUnits(), res.getResourceInformation(MEMORY_MB.getName()).getUnits());
        Assert.assertEquals(VCORES.getUnits(), res.getResourceInformation(VCORES.getName()).getUnits());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetMemory() {
        Resource res = new ResourcePBImpl();
        long memorySize = (Integer.MAX_VALUE) + 1L;
        res.setMemorySize(memorySize);
        Assert.assertEquals("No need to cast if both are long", memorySize, res.getMemorySize());
        Assert.assertEquals(("Cast to Integer.MAX_VALUE if the long is greater than " + "Integer.MAX_VALUE"), Integer.MAX_VALUE, res.getMemory());
    }

    @Test
    public void testGetVirtualCores() {
        Resource res = new ResourcePBImpl();
        long vcores = (Integer.MAX_VALUE) + 1L;
        res.getResourceInformation("vcores").setValue(vcores);
        Assert.assertEquals("No need to cast if both are long", vcores, res.getResourceInformation("vcores").getValue());
        Assert.assertEquals(("Cast to Integer.MAX_VALUE if the long is greater than " + "Integer.MAX_VALUE"), Integer.MAX_VALUE, res.getVirtualCores());
    }

    @Test
    public void testResourcePBWithExtraResources() throws Exception {
        // Resource 'resource1' has been passed as 4T
        // 4T should be converted to 4000G
        YarnProtos.ResourceInformationProto riProto = ResourceInformationProto.newBuilder().setType(ResourceTypeInfoProto.newBuilder().setName("resource1").setType(COUNTABLE).getType()).setValue(4).setUnits("T").setKey("resource1").build();
        YarnProtos.ResourceProto proto = ResourceProto.newBuilder().setMemory(1024).setVirtualCores(3).addResourceValueMap(riProto).build();
        Resource res = new ResourcePBImpl(proto);
        Assert.assertEquals(4000, res.getResourceInformation("resource1").getValue());
        Assert.assertEquals("G", res.getResourceInformation("resource1").getUnits());
        // Resource 'resource2' has been passed as 4M
        // 4M should be converted to 4000000000m
        YarnProtos.ResourceInformationProto riProto1 = ResourceInformationProto.newBuilder().setType(ResourceTypeInfoProto.newBuilder().setName("resource2").setType(COUNTABLE).getType()).setValue(4).setUnits("M").setKey("resource2").build();
        YarnProtos.ResourceProto proto1 = ResourceProto.newBuilder().setMemory(1024).setVirtualCores(3).addResourceValueMap(riProto1).build();
        Resource res1 = new ResourcePBImpl(proto1);
        Assert.assertEquals(4000000000L, res1.getResourceInformation("resource2").getValue());
        Assert.assertEquals("m", res1.getResourceInformation("resource2").getUnits());
        // Resource 'resource1' has been passed as 3M
        // 3M should be converted to 0G
        YarnProtos.ResourceInformationProto riProto2 = ResourceInformationProto.newBuilder().setType(ResourceTypeInfoProto.newBuilder().setName("resource1").setType(COUNTABLE).getType()).setValue(3).setUnits("M").setKey("resource1").build();
        YarnProtos.ResourceProto proto2 = ResourceProto.newBuilder().setMemory(1024).setVirtualCores(3).addResourceValueMap(riProto2).build();
        Resource res2 = new ResourcePBImpl(proto2);
        Assert.assertEquals(0, res2.getResourceInformation("resource1").getValue());
        Assert.assertEquals("G", res2.getResourceInformation("resource1").getUnits());
    }

    @Test
    public void testResourceTags() {
        YarnProtos.ResourceInformationProto riProto = ResourceInformationProto.newBuilder().setType(ResourceTypeInfoProto.newBuilder().setName("yarn.io/test-volume").setType(COUNTABLE).getType()).setValue(10).setUnits("G").setKey("yarn.io/test-volume").addTags("tag_A").addTags("tag_B").addTags("tag_C").build();
        YarnProtos.ResourceProto proto = ResourceProto.newBuilder().setMemory(1024).setVirtualCores(3).addResourceValueMap(riProto).build();
        Resource res = new ResourcePBImpl(proto);
        Assert.assertNotNull(res.getResourceInformation("yarn.io/test-volume"));
        Assert.assertEquals(10, res.getResourceInformation("yarn.io/test-volume").getValue());
        Assert.assertEquals("G", res.getResourceInformation("yarn.io/test-volume").getUnits());
        Assert.assertEquals(3, res.getResourceInformation("yarn.io/test-volume").getTags().size());
        Assert.assertFalse(res.getResourceInformation("yarn.io/test-volume").getTags().isEmpty());
        Assert.assertTrue(res.getResourceInformation("yarn.io/test-volume").getAttributes().isEmpty());
        boolean protoConvertExpected = false;
        YarnProtos.ResourceProto protoFormat = ProtoUtils.convertToProtoFormat(res);
        for (YarnProtos.ResourceInformationProto pf : protoFormat.getResourceValueMapList()) {
            if (pf.getKey().equals("yarn.io/test-volume")) {
                protoConvertExpected = ((pf.getAttributesCount()) == 0) && ((pf.getTagsCount()) == 3);
            }
        }
        Assert.assertTrue(("Expecting resource's protobuf message" + " contains 0 attributes and 3 tags"), protoConvertExpected);
    }

    @Test
    public void testResourceAttributes() {
        YarnProtos.ResourceInformationProto riProto = ResourceInformationProto.newBuilder().setType(ResourceTypeInfoProto.newBuilder().setName("yarn.io/test-volume").setType(COUNTABLE).getType()).setValue(10).setUnits("G").setKey("yarn.io/test-volume").addAttributes(StringStringMapProto.newBuilder().setKey("driver").setValue("test-driver").build()).addAttributes(StringStringMapProto.newBuilder().setKey("mount").setValue("/mnt/data").build()).build();
        YarnProtos.ResourceProto proto = ResourceProto.newBuilder().setMemory(1024).setVirtualCores(3).addResourceValueMap(riProto).build();
        Resource res = new ResourcePBImpl(proto);
        Assert.assertNotNull(res.getResourceInformation("yarn.io/test-volume"));
        Assert.assertEquals(10, res.getResourceInformation("yarn.io/test-volume").getValue());
        Assert.assertEquals("G", res.getResourceInformation("yarn.io/test-volume").getUnits());
        Assert.assertEquals(2, res.getResourceInformation("yarn.io/test-volume").getAttributes().size());
        Assert.assertTrue(res.getResourceInformation("yarn.io/test-volume").getTags().isEmpty());
        Assert.assertFalse(res.getResourceInformation("yarn.io/test-volume").getAttributes().isEmpty());
        boolean protoConvertExpected = false;
        YarnProtos.ResourceProto protoFormat = ProtoUtils.convertToProtoFormat(res);
        for (YarnProtos.ResourceInformationProto pf : protoFormat.getResourceValueMapList()) {
            if (pf.getKey().equals("yarn.io/test-volume")) {
                protoConvertExpected = ((pf.getAttributesCount()) == 2) && ((pf.getTagsCount()) == 0);
            }
        }
        Assert.assertTrue(("Expecting resource's protobuf message" + " contains 2 attributes and 0 tags"), protoConvertExpected);
    }

    @Test
    public void testParsingResourceTags() {
        ResourceInformation info = ResourceUtils.getResourceTypes().get("resource3");
        Assert.assertTrue(info.getAttributes().isEmpty());
        Assert.assertFalse(info.getTags().isEmpty());
        Assert.assertEquals(info.getTags().size(), 2);
        info.getTags().remove("resource3_tag_1");
        info.getTags().remove("resource3_tag_2");
        Assert.assertTrue(info.getTags().isEmpty());
    }
}

