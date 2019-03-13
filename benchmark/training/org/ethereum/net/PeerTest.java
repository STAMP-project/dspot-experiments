/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.net;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.ethereum.net.client.Capability;
import org.ethereum.net.p2p.Peer;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;


public class PeerTest {
    /* PEER */
    @Test
    public void testPeer() {
        // Init
        InetAddress address = InetAddress.getLoopbackAddress();
        List<Capability> capabilities = new ArrayList<>();
        int port = 1010;
        String peerId = "1010";
        Peer peerCopy = new Peer(address, port, peerId);
        // Peer
        Peer peer = new Peer(address, port, peerId);
        // getAddress
        Assert.assertEquals("127.0.0.1", peer.getAddress().getHostAddress());
        // getPort
        Assert.assertEquals(port, peer.getPort());
        // getPeerId
        Assert.assertEquals(peerId, peer.getPeerId());
        // getCapabilities
        Assert.assertEquals(capabilities, peer.getCapabilities());
        // getEncoded
        Assert.assertEquals("CC847F0000018203F2821010C0", Hex.toHexString(peer.getEncoded()).toUpperCase());
        // toString
        Assert.assertEquals((((((("[ip=" + (address.getHostAddress())) + " port=") + (Integer.toString(port))) + " peerId=") + peerId) + "]"), peer.toString());
        // equals
        Assert.assertEquals(true, peer.equals(peerCopy));
        Assert.assertEquals(false, peer.equals(null));
        // hashCode
        Assert.assertEquals((-1218913009), peer.hashCode());
    }
}

