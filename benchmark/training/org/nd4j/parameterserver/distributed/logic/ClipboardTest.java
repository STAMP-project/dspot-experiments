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
package org.nd4j.parameterserver.distributed.logic;


import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.distributed.logic.completion.Clipboard;
import org.nd4j.parameterserver.distributed.messages.VoidAggregation;
import org.nd4j.parameterserver.distributed.messages.aggregations.InitializationAggregation;
import org.nd4j.parameterserver.distributed.messages.aggregations.VectorAggregation;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@Ignore
@Deprecated
public class ClipboardTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @Test
    public void testPin1() throws Exception {
        Clipboard clipboard = new Clipboard();
        Random rng = new Random(12345L);
        for (int i = 0; i < 100; i++) {
            VectorAggregation aggregation = new VectorAggregation(rng.nextLong(), ((short) (100)), ((short) (i)), Nd4j.create(5));
            clipboard.pin(aggregation);
        }
        Assert.assertEquals(false, clipboard.hasCandidates());
        Assert.assertEquals(0, clipboard.getNumberOfCompleteStacks());
        Assert.assertEquals(100, clipboard.getNumberOfPinnedStacks());
    }

    @Test
    public void testPin2() throws Exception {
        Clipboard clipboard = new Clipboard();
        Random rng = new Random(12345L);
        Long validId = 123L;
        short shardIdx = 0;
        for (int i = 0; i < 300; i++) {
            VectorAggregation aggregation = new VectorAggregation(rng.nextLong(), ((short) (100)), ((short) (1)), Nd4j.create(5));
            // imitating valid
            if (((i % 2) == 0) && (shardIdx < 100)) {
                aggregation.setTaskId(validId);
                aggregation.setShardIndex((shardIdx++));
            }
            clipboard.pin(aggregation);
        }
        VoidAggregation aggregation = clipboard.getStackFromClipboard(0L, validId);
        Assert.assertNotEquals(null, aggregation);
        Assert.assertEquals(0, aggregation.getMissingChunks());
        Assert.assertEquals(true, clipboard.hasCandidates());
        Assert.assertEquals(1, clipboard.getNumberOfCompleteStacks());
    }

    /**
     * This test checks how clipboard handles singular aggregations
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPin3() throws Exception {
        Clipboard clipboard = new Clipboard();
        Random rng = new Random(12345L);
        Long validId = 123L;
        InitializationAggregation aggregation = new InitializationAggregation(1, 0);
        clipboard.pin(aggregation);
        Assert.assertTrue(clipboard.isTracking(0L, aggregation.getTaskId()));
        Assert.assertTrue(clipboard.isReady(0L, aggregation.getTaskId()));
    }
}

