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


import bisq.core.btc.setup.WalletConfig;
import bisq.network.Socks5MultiDiscovery;
import com.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import java.util.Collections;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BtcNetworkConfigTest {
    private static final int MODE = 0;

    private WalletConfig delegate;

    @Test
    public void testProposePeersWhenProxyPresentAndNoPeers() {
        BtcNetworkConfig config = new BtcNetworkConfig(delegate, Mockito.mock(NetworkParameters.class), BtcNetworkConfigTest.MODE, Mockito.mock(Socks5Proxy.class));
        config.proposePeers(Collections.emptyList());
        Mockito.verify(delegate, Mockito.never()).setPeerNodes(ArgumentMatchers.any());
        Mockito.verify(delegate).setDiscovery(ArgumentMatchers.any(Socks5MultiDiscovery.class));
    }

    @Test
    public void testProposePeersWhenProxyNotPresentAndNoPeers() {
        BtcNetworkConfig config = new BtcNetworkConfig(delegate, Mockito.mock(NetworkParameters.class), BtcNetworkConfigTest.MODE, null);
        config.proposePeers(Collections.emptyList());
        Mockito.verify(delegate, Mockito.never()).setDiscovery(ArgumentMatchers.any(Socks5MultiDiscovery.class));
        Mockito.verify(delegate, Mockito.never()).setPeerNodes(ArgumentMatchers.any());
    }

    @Test
    public void testProposePeersWhenPeersPresent() {
        BtcNetworkConfig config = new BtcNetworkConfig(delegate, Mockito.mock(NetworkParameters.class), BtcNetworkConfigTest.MODE, null);
        config.proposePeers(Collections.singletonList(Mockito.mock(PeerAddress.class)));
        Mockito.verify(delegate, Mockito.never()).setDiscovery(ArgumentMatchers.any(Socks5MultiDiscovery.class));
        Mockito.verify(delegate).setPeerNodes(ArgumentMatchers.any());
    }
}

