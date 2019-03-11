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


import bisq.core.btc.nodes.BtcNodes.BtcNode;
import com.google.common.collect.Lists;
import com.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import java.util.Collections;
import java.util.List;
import org.bitcoinj.core.PeerAddress;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BtcNodesRepositoryTest {
    @Test
    public void testGetPeerAddressesWhenClearNodes() {
        BtcNode node = Mockito.mock(BtcNode.class);
        Mockito.when(node.hasClearNetAddress()).thenReturn(true);
        BtcNodeConverter converter = Mockito.mock(BtcNodeConverter.class, Mockito.RETURNS_DEEP_STUBS);
        BtcNodesRepository repository = new BtcNodesRepository(converter, Collections.singletonList(node));
        List<PeerAddress> peers = repository.getPeerAddresses(null, false);
        Assert.assertFalse(peers.isEmpty());
    }

    @Test
    public void testGetPeerAddressesWhenConverterReturnsNull() {
        BtcNodeConverter converter = Mockito.mock(BtcNodeConverter.class);
        Mockito.when(converter.convertClearNode(ArgumentMatchers.any())).thenReturn(null);
        BtcNode node = Mockito.mock(BtcNode.class);
        Mockito.when(node.hasClearNetAddress()).thenReturn(true);
        BtcNodesRepository repository = new BtcNodesRepository(converter, Collections.singletonList(node));
        List<PeerAddress> peers = repository.getPeerAddresses(null, false);
        Mockito.verify(converter).convertClearNode(ArgumentMatchers.any());
        Assert.assertTrue(peers.isEmpty());
    }

    @Test
    public void testGetPeerAddressesWhenProxyAndClearNodes() {
        BtcNode node = Mockito.mock(BtcNode.class);
        Mockito.when(node.hasClearNetAddress()).thenReturn(true);
        BtcNode onionNode = Mockito.mock(BtcNode.class);
        Mockito.when(node.hasOnionAddress()).thenReturn(true);
        BtcNodeConverter converter = Mockito.mock(BtcNodeConverter.class, Mockito.RETURNS_DEEP_STUBS);
        BtcNodesRepository repository = new BtcNodesRepository(converter, Lists.newArrayList(node, onionNode));
        List<PeerAddress> peers = repository.getPeerAddresses(Mockito.mock(Socks5Proxy.class), true);
        Assert.assertEquals(2, peers.size());
    }

    @Test
    public void testGetPeerAddressesWhenOnionNodesOnly() {
        BtcNode node = Mockito.mock(BtcNode.class);
        Mockito.when(node.hasClearNetAddress()).thenReturn(true);
        BtcNode onionNode = Mockito.mock(BtcNode.class);
        Mockito.when(node.hasOnionAddress()).thenReturn(true);
        BtcNodeConverter converter = Mockito.mock(BtcNodeConverter.class, Mockito.RETURNS_DEEP_STUBS);
        BtcNodesRepository repository = new BtcNodesRepository(converter, Lists.newArrayList(node, onionNode));
        List<PeerAddress> peers = repository.getPeerAddresses(Mockito.mock(Socks5Proxy.class), false);
        Assert.assertEquals(1, peers.size());
    }
}

