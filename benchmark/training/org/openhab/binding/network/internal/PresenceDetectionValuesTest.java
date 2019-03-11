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
package org.openhab.binding.network.internal;


import PresenceDetectionType.ARP_PING;
import PresenceDetectionType.ICMP_PING;
import PresenceDetectionType.TCP_CONNECTION;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cases for {@see PresenceDetectionValue}
 *
 * @author David Graeff - Initial contribution
 */
public class PresenceDetectionValuesTest {
    @Test
    public void updateLatencyTests() {
        PresenceDetectionValue value = new PresenceDetectionValue("127.0.0.1", 10.0);
        Assert.assertThat(value.getLowestLatency(), CoreMatchers.is(10.0));
        value.updateLatency(20.0);
        Assert.assertThat(value.getLowestLatency(), CoreMatchers.is(10.0));
        value.updateLatency(0.0);
        Assert.assertThat(value.getLowestLatency(), CoreMatchers.is(10.0));
        value.updateLatency(5.0);
        Assert.assertThat(value.getLowestLatency(), CoreMatchers.is(5.0));
    }

    @Test
    public void tcpTests() {
        PresenceDetectionValue value = new PresenceDetectionValue("127.0.0.1", 10.0);
        Assert.assertFalse(value.isTCPServiceReachable());
        value.addReachableTcpService(1010);
        Assert.assertThat(value.getReachableTCPports(), CoreMatchers.hasItem(1010));
        value.addType(TCP_CONNECTION);
        Assert.assertTrue(value.isTCPServiceReachable());
        Assert.assertThat(value.getSuccessfulDetectionTypes(), CoreMatchers.is("TCP_CONNECTION"));
    }

    @Test
    public void isFinishedTests() {
        PresenceDetectionValue value = new PresenceDetectionValue("127.0.0.1", 10.0);
        Assert.assertFalse(value.isFinished());
        value.setDetectionIsFinished(true);
        Assert.assertTrue(value.isFinished());
    }

    @Test
    public void pingTests() {
        PresenceDetectionValue value = new PresenceDetectionValue("127.0.0.1", 10.0);
        Assert.assertFalse(value.isPingReachable());
        value.addType(ICMP_PING);
        Assert.assertTrue(value.isPingReachable());
        value.addType(ARP_PING);
        value.addType(TCP_CONNECTION);
        Assert.assertThat(value.getSuccessfulDetectionTypes(), CoreMatchers.is("ARP_PING, ICMP_PING, TCP_CONNECTION"));
    }
}

