/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.impl.protocol.util;


import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.RequireAssertEnabled;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMessageSplitterTest extends HazelcastTestSupport {
    private ClientMessage clientMessage;

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(ClientMessageSplitter.class);
    }

    @Test
    @RequireAssertEnabled
    public void testGetSubFrames() {
        List<ClientMessage> frames = ClientMessageSplitter.getSubFrames(128, clientMessage);
        Assert.assertEquals(10, frames.size());
    }

    @Test
    @RequireAssertEnabled
    public void testGetSubFrame_whenFrameSizeGreaterThanFrameLength_thenReturnOriginalMessage() {
        List<ClientMessage> frame = ClientMessageSplitter.getSubFrames(1025, clientMessage);
        Assert.assertEquals(1, frame.size());
        Assert.assertEquals(clientMessage, frame.get(0));
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testGetSubFrames_whenInvalidFrameSize_thenThrowAssertionError() {
        ClientMessageSplitter.getSubFrames(((ClientMessage.HEADER_SIZE) - 1), clientMessage);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testGetNumberOfSubFrames_whenInvalidFrameSize_thenThrowAssertionError() {
        ClientMessageSplitter.getNumberOfSubFrames(((ClientMessage.HEADER_SIZE) - 1), clientMessage);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testGetSubFrame_whenNegativeFrameIndex_thenThrowAssertionError() {
        ClientMessageSplitter.getSubFrame(((ClientMessage.HEADER_SIZE) + 1), (-1), 10, clientMessage);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testGetSubFrame_whenFrameIndexGreaterNumberOfFrames_thenThrowAssertionError() {
        ClientMessageSplitter.getSubFrame(((ClientMessage.HEADER_SIZE) + 1), 10, 5, clientMessage);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testGetSubFrame_whenInvalidFrameSize_thenThrowAssertionError() {
        ClientMessageSplitter.getSubFrame(((ClientMessage.HEADER_SIZE) - 1), 0, 5, clientMessage);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testGetSubFrame_whenFrameSizeGreaterThanFrameLength_withInvalidFrameIndex_thenThrowAssertionError() {
        ClientMessageSplitter.getSubFrame(1025, 1, 5, clientMessage);
    }
}

