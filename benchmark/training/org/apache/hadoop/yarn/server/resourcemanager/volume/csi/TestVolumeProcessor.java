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
package org.apache.hadoop.yarn.server.resourcemanager.volume.csi;


import CsiConstants.CSI_DRIVER_NAME;
import CsiConstants.CSI_VOLUME_ID;
import CsiConstants.CSI_VOLUME_MOUNT;
import CsiConstants.CSI_VOLUME_NAME;
import CsiConstants.CSI_VOLUME_RESOURCE_TAG;
import ResourceTypes.COUNTABLE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.yarn.api.CsiAdaptorProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ValidateVolumeCapabilitiesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ValidateVolumeCapabilitiesResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.api.records.ResourceSizing;
import org.apache.hadoop.yarn.api.records.SchedulingRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.volume.csi.lifecycle.Volume;
import org.apache.hadoop.yarn.server.resourcemanager.volume.csi.lifecycle.VolumeState;
import org.apache.hadoop.yarn.server.volume.csi.VolumeId;
import org.apache.hadoop.yarn.server.volume.csi.exception.InvalidVolumeException;
import org.apache.hadoop.yarn.server.volume.csi.exception.VolumeException;
import org.apache.hadoop.yarn.server.volume.csi.exception.VolumeProvisioningException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test cases for volume processor.
 */
public class TestVolumeProcessor {
    private static final int GB = 1024;

    private YarnConfiguration conf;

    private RMNodeLabelsManager mgr;

    private MockRM rm;

    private MockNM[] mockNMS;

    private RMNode[] rmNodes;

    private static final int NUM_OF_NMS = 4;

    // resource-types.xml will be created under target/test-classes/ dir,
    // it must be deleted after the test is done, to avoid it from reading
    // by other UT classes.
    private File resourceTypesFile = null;

    private static final String VOLUME_RESOURCE_NAME = "yarn.io/csi-volume";

    @Test(timeout = 10000L)
    public void testVolumeProvisioning() throws Exception {
        RMApp app1 = rm.submitApp((1 * (TestVolumeProcessor.GB)), "app", "user", null, "default");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, mockNMS[0]);
        Resource resource = Resource.newInstance(1024, 1);
        ResourceInformation volumeResource = ResourceInformation.newInstance(TestVolumeProcessor.VOLUME_RESOURCE_NAME, "Mi", 1024, COUNTABLE, 0, Long.MAX_VALUE, ImmutableSet.of(CSI_VOLUME_RESOURCE_TAG), ImmutableMap.of(CSI_VOLUME_ID, "test-vol-000001", CSI_DRIVER_NAME, "hostpath", CSI_VOLUME_MOUNT, "/mnt/data"));
        resource.setResourceInformation(TestVolumeProcessor.VOLUME_RESOURCE_NAME, volumeResource);
        SchedulingRequest sc = SchedulingRequest.newBuilder().allocationRequestId(0L).resourceSizing(ResourceSizing.newInstance(1, resource)).build();
        AllocateRequest ar = AllocateRequest.newBuilder().schedulingRequests(Arrays.asList(sc)).build();
        // inject adaptor client for testing
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        getRMContext().getVolumeManager().registerCsiDriverAdaptor("hostpath", mockedClient);
        // simulate validation succeed
        Mockito.doReturn(ValidateVolumeCapabilitiesResponse.newInstance(true, "")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        am1.allocate(ar);
        VolumeStates volumeStates = getRMContext().getVolumeManager().getVolumeStates();
        Assert.assertNotNull(volumeStates);
        VolumeState volumeState = VolumeState.NEW;
        while (volumeState != (VolumeState.NODE_READY)) {
            Volume volume = volumeStates.getVolume(new VolumeId("test-vol-000001"));
            if (volume != null) {
                volumeState = volume.getVolumeState();
            }
            am1.doHeartbeat();
            mockNMS[0].nodeHeartbeat(true);
            Thread.sleep(500);
        } 
        stop();
    }

    @Test(timeout = 30000L)
    public void testInvalidRequest() throws Exception {
        RMApp app1 = rm.submitApp((1 * (TestVolumeProcessor.GB)), "app", "user", null, "default");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, mockNMS[0]);
        Resource resource = Resource.newInstance(1024, 1);
        ResourceInformation volumeResource = ResourceInformation.newInstance(TestVolumeProcessor.VOLUME_RESOURCE_NAME, "Mi", 1024, COUNTABLE, 0, Long.MAX_VALUE, ImmutableSet.of(CSI_VOLUME_RESOURCE_TAG), // volume ID is missing...
        ImmutableMap.of(CSI_VOLUME_NAME, "test-vol-000001", CSI_DRIVER_NAME, "hostpath", CSI_VOLUME_MOUNT, "/mnt/data"));
        resource.setResourceInformation(TestVolumeProcessor.VOLUME_RESOURCE_NAME, volumeResource);
        SchedulingRequest sc = SchedulingRequest.newBuilder().allocationRequestId(0L).resourceSizing(ResourceSizing.newInstance(1, resource)).build();
        AllocateRequest ar = AllocateRequest.newBuilder().schedulingRequests(Arrays.asList(sc)).build();
        try {
            am1.allocate(ar);
            Assert.fail("allocate should fail because invalid request received");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof InvalidVolumeException));
        }
        stop();
    }

    @Test(timeout = 30000L)
    public void testProvisioningFailures() throws Exception {
        RMApp app1 = rm.submitApp((1 * (TestVolumeProcessor.GB)), "app", "user", null, "default");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, mockNMS[0]);
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        // inject adaptor client
        getRMContext().getVolumeManager().registerCsiDriverAdaptor("hostpath", mockedClient);
        Mockito.doThrow(new VolumeException("failed")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        Resource resource = Resource.newInstance(1024, 1);
        ResourceInformation volumeResource = ResourceInformation.newInstance(TestVolumeProcessor.VOLUME_RESOURCE_NAME, "Mi", 1024, COUNTABLE, 0, Long.MAX_VALUE, ImmutableSet.of(CSI_VOLUME_RESOURCE_TAG), ImmutableMap.of(CSI_VOLUME_ID, "test-vol-000001", CSI_DRIVER_NAME, "hostpath", CSI_VOLUME_MOUNT, "/mnt/data"));
        resource.setResourceInformation(TestVolumeProcessor.VOLUME_RESOURCE_NAME, volumeResource);
        SchedulingRequest sc = SchedulingRequest.newBuilder().allocationRequestId(0L).resourceSizing(ResourceSizing.newInstance(1, resource)).build();
        AllocateRequest ar = AllocateRequest.newBuilder().schedulingRequests(Arrays.asList(sc)).build();
        try {
            am1.allocate(ar);
            Assert.fail("allocate should fail");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof VolumeProvisioningException));
        }
        stop();
    }

    @Test(timeout = 10000L)
    public void testVolumeResourceAllocate() throws Exception {
        RMApp app1 = rm.submitApp((1 * (TestVolumeProcessor.GB)), "app", "user", null, "default");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, mockNMS[0]);
        Resource resource = Resource.newInstance(1024, 1);
        ResourceInformation volumeResource = ResourceInformation.newInstance(TestVolumeProcessor.VOLUME_RESOURCE_NAME, "Mi", 1024, COUNTABLE, 0, Long.MAX_VALUE, ImmutableSet.of(CSI_VOLUME_RESOURCE_TAG), ImmutableMap.of(CSI_VOLUME_ID, "test-vol-000001", CSI_DRIVER_NAME, "hostpath", CSI_VOLUME_MOUNT, "/mnt/data"));
        resource.setResourceInformation(TestVolumeProcessor.VOLUME_RESOURCE_NAME, volumeResource);
        SchedulingRequest sc = SchedulingRequest.newBuilder().allocationRequestId(0L).resourceSizing(ResourceSizing.newInstance(1, resource)).build();
        // inject adaptor client for testing
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        getRMContext().getVolumeManager().registerCsiDriverAdaptor("hostpath", mockedClient);
        // simulate validation succeed
        Mockito.doReturn(ValidateVolumeCapabilitiesResponse.newInstance(true, "")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        am1.addSchedulingRequest(ImmutableList.of(sc));
        List<Container> allocated = new ArrayList<>();
        while ((allocated.size()) != 1) {
            AllocateResponse response = am1.schedule();
            mockNMS[0].nodeHeartbeat(true);
            allocated.addAll(response.getAllocatedContainers());
            Thread.sleep(500);
        } 
        Assert.assertEquals(1, allocated.size());
        Container alloc = allocated.get(0);
        Assert.assertEquals(alloc.getResource().getMemorySize(), 1024);
        Assert.assertEquals(alloc.getResource().getVirtualCores(), 1);
        ResourceInformation allocatedVolume = alloc.getResource().getResourceInformation(TestVolumeProcessor.VOLUME_RESOURCE_NAME);
        Assert.assertNotNull(allocatedVolume);
        Assert.assertEquals(allocatedVolume.getValue(), 1024);
        Assert.assertEquals(allocatedVolume.getUnits(), "Mi");
        stop();
    }
}

