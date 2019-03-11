/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.utilities;


import java.net.UnknownHostException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class IpSubnetTest {
    private final IpSubnet ipSubnet;

    private final String ipAddress;

    private final String networkAddress;

    private final String broadcastAddress;

    private final boolean isInSubnet;

    public IpSubnetTest(String cidr, String ipAddress, String networkAddress, String broadcastAddress, boolean isInSubnet) throws UnknownHostException {
        this.ipSubnet = new IpSubnet(cidr);
        this.ipAddress = ipAddress;
        this.networkAddress = networkAddress;
        this.broadcastAddress = broadcastAddress;
        this.isInSubnet = isInSubnet;
    }

    @Test
    public void getNetworkAddress() {
        assertThat(ipSubnet.getNetworkAddress()).isEqualTo(networkAddress);
    }

    @Test
    public void getBroadcastAddress() {
        assertThat(ipSubnet.getBroadcastAddress()).isEqualTo(broadcastAddress);
    }

    @Test
    public void contains() throws UnknownHostException {
        assertThat(ipSubnet.contains(ipAddress)).isEqualTo(isInSubnet);
    }
}

