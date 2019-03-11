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
package org.openhab.binding.network.internal.dhcp;


import DHCPListenService.instance;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openhab.binding.network.internal.dhcp.DHCPPacket.BadPacketException;

import static DHCPPacket.DHCPREQUEST;


/**
 * Tests cases for DHCP related functionality
 *
 * @author David Graeff - Initial contribution
 */
public class DHCPTest {
    @Test
    public void testService() throws SocketException {
        String testIP = "10.1.2.3";
        IPRequestReceivedCallback dhcpListener = Mockito.mock(IPRequestReceivedCallback.class);
        Assert.assertThat(instance, CoreMatchers.is(CoreMatchers.nullValue()));
        DHCPListenService.register(testIP, dhcpListener);
        Assert.assertThat(instance, CoreMatchers.is(CoreMatchers.notNullValue()));
        DHCPListenService.unregister(testIP);
        Assert.assertThat(instance, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testReceivePacketCallback() throws IOException, BadPacketException {
        String testIP = "10.1.2.3";
        InetAddress testAddress = InetAddress.getByName(testIP);
        IPRequestReceivedCallback dhcpListener = Mockito.mock(IPRequestReceivedCallback.class);
        DHCPPacketListenerServer s = new DHCPPacketListenerServer(dhcpListener);
        s.receivePacket(new DHCPPacket(new byte[]{ DHCPREQUEST }, testAddress.getAddress()), testAddress);
        // Test case if DHCP packet does not contain a DHO_DHCP_REQUESTED_ADDRESS option.
        // The destination IP should be deducted by the UDP address in this case
        s.receivePacket(new DHCPPacket(new byte[]{ DHCPREQUEST }, null), testAddress);
        Mockito.verify(dhcpListener, Mockito.times(2)).dhcpRequestReceived(ArgumentMatchers.eq(testIP));
    }
}

