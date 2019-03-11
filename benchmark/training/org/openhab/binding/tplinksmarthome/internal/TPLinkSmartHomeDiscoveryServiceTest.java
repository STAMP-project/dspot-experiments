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
package org.openhab.binding.tplinksmarthome.internal;


import TPLinkSmartHomeBindingConstants.BINDING_ID;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.List;
import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Test class for {@link TPLinkSmartHomeDiscoveryService} class.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
@RunWith(Parameterized.class)
public class TPLinkSmartHomeDiscoveryServiceTest {
    private static final List<Object[]> TESTS = Arrays.asList(new Object[][]{ new Object[]{ "bulb_get_sysinfo_response_on", 11 }, new Object[]{ "rangeextender_get_sysinfo_response", 11 } });

    @Mock
    private DatagramSocket discoverSocket;

    @Mock
    private DiscoveryListener discoveryListener;

    private TPLinkSmartHomeDiscoveryService discoveryService;

    private final String filename;

    private final int propertiesSize;

    public TPLinkSmartHomeDiscoveryServiceTest(String filename, int propertiesSize) {
        this.filename = filename;
        this.propertiesSize = propertiesSize;
    }

    /**
     * Test if startScan method finds a device with expected properties.
     */
    @Test
    public void testScan() {
        discoveryService.startScan();
        ArgumentCaptor<DiscoveryResult> discoveryResultCaptor = ArgumentCaptor.forClass(DiscoveryResult.class);
        Mockito.verify(discoveryListener).thingDiscovered(ArgumentMatchers.any(), discoveryResultCaptor.capture());
        DiscoveryResult discoveryResult = discoveryResultCaptor.getValue();
        Assert.assertEquals("Check if correct binding id found", BINDING_ID, discoveryResult.getBindingId());
        Assert.assertEquals("Check if expected number of properties found", propertiesSize, discoveryResult.getProperties().size());
    }
}

