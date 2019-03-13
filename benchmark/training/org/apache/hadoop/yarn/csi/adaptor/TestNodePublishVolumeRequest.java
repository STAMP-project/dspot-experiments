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
package org.apache.hadoop.yarn.csi.adaptor;


import AccessMode.MULTI_NODE_READER_ONLY;
import CsiAdaptorProtos.NodePublishVolumeRequest;
import CsiAdaptorProtos.VolumeCapability;
import VolumeType.FILE_SYSTEM;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.NodePublishVolumeRequestPBImpl;
import org.apache.hadoop.yarn.proto.CsiAdaptorProtos;
import org.junit.Assert;
import org.junit.Test;


/**
 * UT for NodePublishVolumeRequest.
 */
public class TestNodePublishVolumeRequest {
    @Test
    public void testPBRecord() {
        CsiAdaptorProtos.VolumeCapability capability = VolumeCapability.newBuilder().setAccessMode(MULTI_NODE_READER_ONLY).setVolumeType(FILE_SYSTEM).build();
        CsiAdaptorProtos.NodePublishVolumeRequest proto = NodePublishVolumeRequest.newBuilder().setReadonly(false).setVolumeId("test-vol-000001").setTargetPath("/mnt/data").setStagingTargetPath("/mnt/staging").setVolumeCapability(capability).build();
        NodePublishVolumeRequestPBImpl pbImpl = new NodePublishVolumeRequestPBImpl(proto);
        Assert.assertEquals("test-vol-000001", pbImpl.getVolumeId());
        Assert.assertEquals("/mnt/data", pbImpl.getTargetPath());
        Assert.assertEquals("/mnt/staging", pbImpl.getStagingPath());
        Assert.assertFalse(pbImpl.getReadOnly());
    }
}

