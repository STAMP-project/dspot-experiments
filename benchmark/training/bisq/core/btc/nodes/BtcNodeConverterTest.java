/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.core.btc.nodes;


import bisq.core.btc.nodes.BtcNodeConverter.Facade;
import bisq.core.btc.nodes.BtcNodes.BtcNode;
import bisq.network.DnsLookupException;
import com.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.bitcoinj.core.PeerAddress;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BtcNodeConverterTest {
    @Test
    public void testConvertOnionHost() throws UnknownHostException {
        BtcNode node = Mockito.mock(BtcNode.class);
        Mockito.when(node.getOnionAddress()).thenReturn("aaa.onion");
        InetAddress inetAddress = Mockito.mock(InetAddress.class);
        Facade facade = Mockito.mock(Facade.class);
        Mockito.when(facade.onionHostToInetAddress(ArgumentMatchers.any())).thenReturn(inetAddress);
        PeerAddress peerAddress = new BtcNodeConverter(facade).convertOnionHost(node);
        // noinspection ConstantConditions
        Assert.assertEquals(inetAddress, peerAddress.getAddr());
    }

    @Test
    public void testConvertOnionHostOnFailure() throws UnknownHostException {
        BtcNode node = Mockito.mock(BtcNode.class);
        Mockito.when(node.getOnionAddress()).thenReturn("aaa.onion");
        Facade facade = Mockito.mock(Facade.class);
        Mockito.when(facade.onionHostToInetAddress(ArgumentMatchers.any())).thenThrow(UnknownHostException.class);
        PeerAddress peerAddress = new BtcNodeConverter(facade).convertOnionHost(node);
        Assert.assertNull(peerAddress);
    }

    @Test
    public void testConvertWithTor() throws DnsLookupException {
        InetAddress expected = Mockito.mock(InetAddress.class);
        Facade facade = Mockito.mock(Facade.class);
        Mockito.when(facade.torLookup(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(expected);
        BtcNode node = Mockito.mock(BtcNode.class);
        Mockito.when(node.getHostNameOrAddress()).thenReturn("aaa.onion");
        PeerAddress peerAddress = new BtcNodeConverter(facade).convertWithTor(node, Mockito.mock(Socks5Proxy.class));
        // noinspection ConstantConditions
        Assert.assertEquals(expected, peerAddress.getAddr());
    }
}

