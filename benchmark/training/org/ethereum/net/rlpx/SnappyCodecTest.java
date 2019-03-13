/**
 * Copyright (c) [2017] [ <ether.camp> ]
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
package org.ethereum.net.rlpx;


import java.util.List;
import org.ethereum.net.server.Channel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * EIP-706 provides snappy samples. FrameCodec should be
 * able to decode these samples.
 */
public class SnappyCodecTest {
    @Test
    public void testDecodeGoGeneratedSnappy() throws Exception {
        Resource golangGeneratedSnappy = new ClassPathResource("/rlp/block.go.snappy");
        List<Object> result = snappyDecode(golangGeneratedSnappy);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testDecodePythonGeneratedSnappy() throws Exception {
        Resource pythonGeneratedSnappy = new ClassPathResource("/rlp/block.py.snappy");
        List<Object> result = snappyDecode(pythonGeneratedSnappy);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testFramedDecodeDisconnect() throws Exception {
        byte[] frameBytes = new byte[]{ ((byte) (255)), 6, 0, 0, 115, 78, 97, 80, 112, 89 };
        Channel shouldBeDropped = Mockito.mock(Channel.class);
        Mockito.when(shouldBeDropped.getNodeStatistics()).thenReturn(new org.ethereum.net.rlpx.discover.NodeStatistics(new Node(new byte[0], "", 0)));
        Mockito.doAnswer(( invocation) -> {
            shouldBeDropped.getNodeStatistics().nodeDisconnectedLocal(invocation.getArgument(0));
            return null;
        }).when(shouldBeDropped).disconnect(ArgumentMatchers.any());
        snappyDecode(frameBytes, shouldBeDropped);
        Assert.assertTrue(shouldBeDropped.getNodeStatistics().wasDisconnected());
        String stats = shouldBeDropped.getNodeStatistics().toString();
        Assert.assertTrue(stats.contains(BAD_PROTOCOL.toString()));
    }
}

