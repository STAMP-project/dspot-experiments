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
package org.ethereum.net.wire;


import EthMessageCodes.BLOCK_BODIES;
import EthMessageCodes.BLOCK_HEADERS;
import EthMessageCodes.GET_BLOCK_BODIES;
import EthMessageCodes.GET_BLOCK_HEADERS;
import EthMessageCodes.NEW_BLOCK;
import EthMessageCodes.NEW_BLOCK_HASHES;
import EthMessageCodes.STATUS;
import EthMessageCodes.TRANSACTIONS;
import P2pMessageCodes.DISCONNECT;
import P2pMessageCodes.GET_PEERS;
import P2pMessageCodes.HELLO;
import P2pMessageCodes.PEERS;
import P2pMessageCodes.PING;
import P2pMessageCodes.PONG;
import P2pMessageCodes.USER;
import ShhMessageCodes.FILTER;
import ShhMessageCodes.MESSAGE;
import java.util.Arrays;
import java.util.List;
import org.ethereum.net.client.Capability;
import org.ethereum.net.eth.message.EthMessageCodes;
import org.ethereum.net.p2p.P2pMessageCodes;
import org.ethereum.net.rlpx.MessageCodesResolver;
import org.ethereum.net.shh.ShhHandler;
import org.ethereum.net.shh.ShhMessageCodes;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Roman Mandeleil
 * @since 15.10.2014
 */
public class AdaptiveMessageIdsTest {
    private MessageCodesResolver messageCodesResolver;

    @Test
    public void test1() {
        Assert.assertEquals(7, P2pMessageCodes.values().length);
        Assert.assertEquals(0, messageCodesResolver.withP2pOffset(HELLO.asByte()));
        Assert.assertEquals(1, messageCodesResolver.withP2pOffset(DISCONNECT.asByte()));
        Assert.assertEquals(2, messageCodesResolver.withP2pOffset(PING.asByte()));
        Assert.assertEquals(3, messageCodesResolver.withP2pOffset(PONG.asByte()));
        Assert.assertEquals(4, messageCodesResolver.withP2pOffset(GET_PEERS.asByte()));
        Assert.assertEquals(5, messageCodesResolver.withP2pOffset(PEERS.asByte()));
        Assert.assertEquals(15, messageCodesResolver.withP2pOffset(USER.asByte()));
    }

    @Test
    public void test2() {
        Assert.assertEquals(8, EthMessageCodes.values(V62).length);
        Assert.assertEquals(0, STATUS.asByte());
        Assert.assertEquals(1, NEW_BLOCK_HASHES.asByte());
        Assert.assertEquals(2, TRANSACTIONS.asByte());
        Assert.assertEquals(3, GET_BLOCK_HEADERS.asByte());
        Assert.assertEquals(4, BLOCK_HEADERS.asByte());
        Assert.assertEquals(5, GET_BLOCK_BODIES.asByte());
        Assert.assertEquals(6, BLOCK_BODIES.asByte());
        Assert.assertEquals(7, NEW_BLOCK.asByte());
        messageCodesResolver.setEthOffset(16);
        Assert.assertEquals((16 + 0), messageCodesResolver.withEthOffset(STATUS.asByte()));
        Assert.assertEquals((16 + 1), messageCodesResolver.withEthOffset(NEW_BLOCK_HASHES.asByte()));
        Assert.assertEquals((16 + 2), messageCodesResolver.withEthOffset(TRANSACTIONS.asByte()));
        Assert.assertEquals((16 + 3), messageCodesResolver.withEthOffset(GET_BLOCK_HEADERS.asByte()));
        Assert.assertEquals((16 + 4), messageCodesResolver.withEthOffset(BLOCK_HEADERS.asByte()));
        Assert.assertEquals((16 + 5), messageCodesResolver.withEthOffset(GET_BLOCK_BODIES.asByte()));
        Assert.assertEquals((16 + 6), messageCodesResolver.withEthOffset(BLOCK_BODIES.asByte()));
        Assert.assertEquals((16 + 7), messageCodesResolver.withEthOffset(NEW_BLOCK.asByte()));
    }

    @Test
    public void test3() {
        Assert.assertEquals(3, ShhMessageCodes.values().length);
        Assert.assertEquals(0, ShhMessageCodes.STATUS.asByte());
        Assert.assertEquals(1, MESSAGE.asByte());
        Assert.assertEquals(2, FILTER.asByte());
        messageCodesResolver.setShhOffset(32);
        Assert.assertEquals((32 + 0), messageCodesResolver.withShhOffset(ShhMessageCodes.STATUS.asByte()));
        Assert.assertEquals((32 + 1), messageCodesResolver.withShhOffset(MESSAGE.asByte()));
        Assert.assertEquals((32 + 2), messageCodesResolver.withShhOffset(FILTER.asByte()));
    }

    @Test
    public void test4() {
        List<Capability> capabilities = Arrays.asList(new Capability(Capability.ETH, EthVersion.V62.getCode()), new Capability(Capability.SHH, ShhHandler.VERSION));
        messageCodesResolver.init(capabilities);
        Assert.assertEquals((16 + 0), messageCodesResolver.withEthOffset(STATUS.asByte()));
        Assert.assertEquals((16 + 1), messageCodesResolver.withEthOffset(NEW_BLOCK_HASHES.asByte()));
        Assert.assertEquals((16 + 2), messageCodesResolver.withEthOffset(TRANSACTIONS.asByte()));
        Assert.assertEquals((16 + 3), messageCodesResolver.withEthOffset(GET_BLOCK_HEADERS.asByte()));
        Assert.assertEquals((16 + 4), messageCodesResolver.withEthOffset(BLOCK_HEADERS.asByte()));
        Assert.assertEquals((16 + 5), messageCodesResolver.withEthOffset(GET_BLOCK_BODIES.asByte()));
        Assert.assertEquals((16 + 6), messageCodesResolver.withEthOffset(BLOCK_BODIES.asByte()));
        Assert.assertEquals((16 + 7), messageCodesResolver.withEthOffset(NEW_BLOCK.asByte()));
        Assert.assertEquals((24 + 0), messageCodesResolver.withShhOffset(ShhMessageCodes.STATUS.asByte()));
        Assert.assertEquals((24 + 1), messageCodesResolver.withShhOffset(MESSAGE.asByte()));
        Assert.assertEquals((24 + 2), messageCodesResolver.withShhOffset(FILTER.asByte()));
    }

    // Capabilities should be read in alphabetical order
    @Test
    public void test5() {
        List<Capability> capabilities = Arrays.asList(new Capability(Capability.SHH, ShhHandler.VERSION), new Capability(Capability.ETH, EthVersion.V62.getCode()));
        messageCodesResolver.init(capabilities);
        Assert.assertEquals((16 + 0), messageCodesResolver.withEthOffset(STATUS.asByte()));
        Assert.assertEquals((16 + 1), messageCodesResolver.withEthOffset(NEW_BLOCK_HASHES.asByte()));
        Assert.assertEquals((16 + 2), messageCodesResolver.withEthOffset(TRANSACTIONS.asByte()));
        Assert.assertEquals((16 + 3), messageCodesResolver.withEthOffset(GET_BLOCK_HEADERS.asByte()));
        Assert.assertEquals((16 + 4), messageCodesResolver.withEthOffset(BLOCK_HEADERS.asByte()));
        Assert.assertEquals((16 + 5), messageCodesResolver.withEthOffset(GET_BLOCK_BODIES.asByte()));
        Assert.assertEquals((16 + 6), messageCodesResolver.withEthOffset(BLOCK_BODIES.asByte()));
        Assert.assertEquals((16 + 7), messageCodesResolver.withEthOffset(NEW_BLOCK.asByte()));
        Assert.assertEquals((24 + 0), messageCodesResolver.withShhOffset(ShhMessageCodes.STATUS.asByte()));
        Assert.assertEquals((24 + 1), messageCodesResolver.withShhOffset(MESSAGE.asByte()));
        Assert.assertEquals((24 + 2), messageCodesResolver.withShhOffset(FILTER.asByte()));
    }
}

