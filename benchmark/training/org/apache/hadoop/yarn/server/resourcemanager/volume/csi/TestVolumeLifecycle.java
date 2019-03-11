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


import VolumeState.NEW;
import VolumeState.UNAVAILABLE;
import VolumeState.VALIDATED;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.CsiAdaptorProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.ValidateVolumeCapabilitiesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ValidateVolumeCapabilitiesResponse;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.volume.csi.lifecycle.VolumeImpl;
import org.apache.hadoop.yarn.server.volume.csi.exception.VolumeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test cases for volume lifecycle management.
 */
public class TestVolumeLifecycle {
    @Test
    public void testValidation() throws IOException, YarnException {
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        Mockito.doReturn(ValidateVolumeCapabilitiesResponse.newInstance(true, "")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        VolumeImpl volume = ((VolumeImpl) (VolumeBuilder.newBuilder().volumeId("test_vol_00000001").maxCapability(5L).unit("Gi").mountPoint("/path/to/mount").driverName("test-driver-name").build()));
        volume.setClient(mockedClient);
        Assert.assertEquals(NEW, volume.getVolumeState());
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
        Assert.assertEquals(VALIDATED, volume.getVolumeState());
    }

    @Test
    public void testVolumeCapacityNotSupported() throws Exception {
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        VolumeImpl volume = ((VolumeImpl) (VolumeBuilder.newBuilder().volumeId("test_vol_00000001").build()));
        volume.setClient(mockedClient);
        // NEW -> UNAVAILABLE
        // Simulate a failed API call to the adaptor
        Mockito.doReturn(ValidateVolumeCapabilitiesResponse.newInstance(false, "")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
        try {
            // Verify the countdown did not happen
            GenericTestUtils.waitFor(() -> (volume.getVolumeState()) == VolumeState.VALIDATED, 10, 50);
            Assert.fail(("Validate state not reached," + " it should keep waiting until timeout"));
        } catch (Exception e) {
            Assert.assertTrue((e instanceof TimeoutException));
            Assert.assertEquals(UNAVAILABLE, volume.getVolumeState());
        }
    }

    @Test
    public void testValidationFailure() throws IOException, YarnException {
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        Mockito.doThrow(new VolumeException("fail")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        VolumeImpl volume = ((VolumeImpl) (VolumeBuilder.newBuilder().volumeId("test_vol_00000001").build()));
        volume.setClient(mockedClient);
        // NEW -> UNAVAILABLE
        // Simulate a failed API call to the adaptor
        Mockito.doThrow(new VolumeException("failed")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
    }

    @Test
    public void testValidated() throws IOException, YarnException {
        VolumeImpl volume = ((VolumeImpl) (VolumeBuilder.newBuilder().volumeId("test_vol_00000001").build()));
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        // The client has a count to memorize how many times being called
        volume.setClient(mockedClient);
        // NEW -> VALIDATED
        Mockito.doReturn(ValidateVolumeCapabilitiesResponse.newInstance(true, "")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        Assert.assertEquals(NEW, volume.getVolumeState());
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
        Assert.assertEquals(VALIDATED, volume.getVolumeState());
        Mockito.verify(mockedClient, Mockito.times(1)).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        // VALIDATED -> VALIDATED
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
        Assert.assertEquals(VALIDATED, volume.getVolumeState());
        Mockito.verify(mockedClient, Mockito.times(1)).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
    }

    @Test
    public void testUnavailableState() throws IOException, YarnException {
        VolumeImpl volume = ((VolumeImpl) (VolumeBuilder.newBuilder().volumeId("test_vol_00000001").build()));
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        volume.setClient(mockedClient);
        // NEW -> UNAVAILABLE
        Mockito.doThrow(new VolumeException("failed")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        Assert.assertEquals(NEW, volume.getVolumeState());
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
        Assert.assertEquals(UNAVAILABLE, volume.getVolumeState());
        // UNAVAILABLE -> UNAVAILABLE
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
        Assert.assertEquals(UNAVAILABLE, volume.getVolumeState());
        // UNAVAILABLE -> VALIDATED
        Mockito.doReturn(ValidateVolumeCapabilitiesResponse.newInstance(true, "")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        volume.setClient(mockedClient);
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
        Assert.assertEquals(VALIDATED, volume.getVolumeState());
    }

    @Test
    public void testPublishUnavailableVolume() throws IOException, YarnException {
        VolumeImpl volume = ((VolumeImpl) (VolumeBuilder.newBuilder().volumeId("test_vol_00000001").build()));
        CsiAdaptorProtocol mockedClient = Mockito.mock(CsiAdaptorProtocol.class);
        volume.setClient(mockedClient);
        // NEW -> UNAVAILABLE (on validateVolume)
        Mockito.doThrow(new VolumeException("failed")).when(mockedClient).validateVolumeCapacity(ArgumentMatchers.any(ValidateVolumeCapabilitiesRequest.class));
        Assert.assertEquals(NEW, volume.getVolumeState());
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ValidateVolumeEvent(volume));
        Assert.assertEquals(UNAVAILABLE, volume.getVolumeState());
        // UNAVAILABLE -> UNAVAILABLE (on publishVolume)
        volume.handle(new org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event.ControllerPublishVolumeEvent(volume));
        // controller publish is not called since the state is UNAVAILABLE
        // verify(mockedClient, times(0)).controllerPublishVolume();
        // state remains to UNAVAILABLE
        Assert.assertEquals(UNAVAILABLE, volume.getVolumeState());
    }
}

