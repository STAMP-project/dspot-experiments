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
package org.nd4j.parameterserver.distributed.v2.util;


import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Optional;
import org.nd4j.parameterserver.distributed.v2.messages.impl.GradientsUpdateMessage;


@Slf4j
public class MessageSplitterTest {
    @Test
    public void testMessageSplit_1() throws Exception {
        val array = Nd4j.linspace(1, 100000, 100000).reshape((-1), 1000);
        val splitter = new MessageSplitter();
        val message = new GradientsUpdateMessage("123", array);
        val messages = splitter.split(message, 16384);
        Assert.assertNotNull(messages);
        Assert.assertFalse(messages.isEmpty());
        log.info("Number of messages: {}", messages.size());
        for (val m : messages)
            Assert.assertEquals("123", m.getOriginalId());

        Optional<GradientsUpdateMessage> dec = null;
        for (val m : messages)
            dec = splitter.merge(m);

        Assert.assertNotNull(dec);
        Assert.assertTrue(dec.isPresent());
    }

    @Test
    public void testSmallMessageSplit_1() throws Exception {
        val array = Nd4j.linspace(1, 15, 15).reshape((-1), 5);
        val splitter = new MessageSplitter();
        val message = new GradientsUpdateMessage("123", array);
        val messages = splitter.split(message, 16384);
        Assert.assertNotNull(messages);
        Assert.assertEquals(1, messages.size());
        for (val m : messages)
            Assert.assertEquals("123", m.getOriginalId());

        Optional<GradientsUpdateMessage> dec = splitter.merge(new java.util.ArrayList(messages).get(0));
        Assert.assertNotNull(dec);
        Assert.assertTrue(dec.isPresent());
    }

    @Test
    public void testConcurrentAppend_1() throws Exception {
        val splitter = new MessageSplitter();
        val array = Nd4j.linspace(1, 100000, 100000).reshape((-1), 1000);
        for (int e = 0; e < 100; e++) {
            val message = new GradientsUpdateMessage(UUID.randomUUID().toString(), array);
            val chunks = splitter.split(message, 16384);
            val ref = new org.nd4j.linalg.primitives.Atomic<GradientsUpdateMessage>();
            chunks.parallelStream().forEach(( c) -> {
                val o = splitter.merge(c);
                if (o.isPresent())
                    ref.set(((GradientsUpdateMessage) (o.get())));

            });
            Assert.assertNotNull(ref.get());
            Assert.assertEquals(array, ref.get().getPayload());
            Assert.assertEquals(0, splitter.memoryUse.intValue());
            Assert.assertEquals(false, splitter.isTrackedMessage(message.getMessageId()));
            Assert.assertEquals(0, splitter.trackers.size());
        }
    }
}

