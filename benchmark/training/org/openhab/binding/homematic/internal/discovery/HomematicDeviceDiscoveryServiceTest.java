/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.homematic.internal.discovery;


import java.io.IOException;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.test.java.JavaTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openhab.binding.homematic.internal.handler.HomematicBridgeHandler;
import org.openhab.binding.homematic.internal.model.HmDevice;
import org.openhab.binding.homematic.test.util.DimmerHelper;
import org.openhab.binding.homematic.test.util.SimpleDiscoveryListener;


/**
 * Tests for {@link HomematicDeviceDiscoveryServiceTest}.
 *
 * @author Florian Stolte - Initial Contribution
 */
public class HomematicDeviceDiscoveryServiceTest extends JavaTest {
    private HomematicDeviceDiscoveryService homematicDeviceDiscoveryService;

    private HomematicBridgeHandler homematicBridgeHandler;

    @Test
    public void testDiscoveryResultIsReportedForNewDevice() {
        SimpleDiscoveryListener discoveryListener = new SimpleDiscoveryListener();
        homematicDeviceDiscoveryService.addDiscoveryListener(discoveryListener);
        HmDevice hmDevice = DimmerHelper.createDimmerHmDevice();
        homematicDeviceDiscoveryService.deviceDiscovered(hmDevice);
        MatcherAssert.assertThat(discoveryListener.discoveredResults.size(), CoreMatchers.is(1));
        discoveryResultMatchesHmDevice(discoveryListener.discoveredResults.element(), hmDevice);
    }

    @Test
    public void testDevicesAreLoadedFromBridgeDuringDiscovery() throws IOException {
        startScanAndWaitForLoadedDevices();
        Mockito.verify(homematicBridgeHandler.getGateway()).loadAllDeviceMetadata();
    }

    @Test
    public void testInstallModeIsNotActiveDuringInitialDiscovery() throws IOException {
        startScanAndWaitForLoadedDevices();
        Mockito.verify(homematicBridgeHandler.getGateway(), Mockito.never()).setInstallMode(ArgumentMatchers.eq(true), ArgumentMatchers.anyInt());
    }

    @Test
    public void testInstallModeIsActiveDuringSubsequentDiscovery() throws IOException {
        homematicBridgeHandler.getThing().setStatusInfo(new org.eclipse.smarthome.core.thing.ThingStatusInfo(ThingStatus.ONLINE, ThingStatusDetail.NONE, ""));
        startScanAndWaitForLoadedDevices();
        Mockito.verify(homematicBridgeHandler.getGateway()).setInstallMode(true, 60);
    }

    @Test
    public void testStoppingDiscoveryDisablesInstallMode() throws IOException {
        homematicBridgeHandler.getThing().setStatusInfo(new org.eclipse.smarthome.core.thing.ThingStatusInfo(ThingStatus.ONLINE, ThingStatusDetail.NONE, ""));
        homematicDeviceDiscoveryService.startScan();
        homematicDeviceDiscoveryService.stopScan();
        Mockito.verify(homematicBridgeHandler.getGateway()).setInstallMode(false, 0);
    }
}

