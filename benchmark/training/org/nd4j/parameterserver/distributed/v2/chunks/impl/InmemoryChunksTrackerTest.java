/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.parameterserver.distributed.v2.chunks.impl;


import java.util.ArrayList;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.distributed.v2.util.MessageSplitter;


public class InmemoryChunksTrackerTest {
    @Test
    public void testTracker_1() throws Exception {
        val array = Nd4j.linspace(1, 100000, 10000).reshape((-1), 1000);
        val splitter = MessageSplitter.getInstance();
        val message = new org.nd4j.parameterserver.distributed.v2.messages.impl.GradientsUpdateMessage("123", array);
        val messages = new ArrayList<org.nd4j.parameterserver.distributed.v2.chunks.VoidChunk>(splitter.split(message, 16384));
        val tracker = new InmemoryChunksTracker<org.nd4j.parameterserver.distributed.v2.messages.impl.GradientsUpdateMessage>(messages.get(0));
        Assert.assertFalse(tracker.isComplete());
        for (val m : messages)
            tracker.append(m);

        Assert.assertTrue(tracker.isComplete());
        val des = tracker.getMessage();
        Assert.assertNotNull(des);
        val restored = des.getPayload();
        Assert.assertNotNull(restored);
        Assert.assertEquals(array, restored);
    }
}

