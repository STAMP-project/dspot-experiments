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


import P2pMessageCodes.PING;
import P2pMessageCodes.PONG;
import org.ethereum.net.p2p.PingMessage;
import org.ethereum.net.p2p.PongMessage;
import org.junit.Assert;
import org.junit.Test;


public class PingPongMessageTest {
    /* PING_MESSAGE & PONG_MESSAGE */
    /* PingMessage */
    @Test
    public void testPing() {
        PingMessage pingMessage = new PingMessage();
        System.out.println(pingMessage);
        Assert.assertEquals(PongMessage.class, pingMessage.getAnswerMessage());
        Assert.assertEquals(PING, pingMessage.getCommand());
    }

    /* PongMessage */
    @Test
    public void testPong() {
        PongMessage pongMessage = new PongMessage();
        System.out.println(pongMessage);
        Assert.assertEquals(PONG, pongMessage.getCommand());
        Assert.assertEquals(null, pongMessage.getAnswerMessage());
    }
}

