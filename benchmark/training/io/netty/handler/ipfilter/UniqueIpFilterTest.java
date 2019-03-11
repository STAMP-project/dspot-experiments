/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.ipfilter;


import io.netty.channel.embedded.EmbeddedChannel;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


public class UniqueIpFilterTest {
    @Test
    public void testUniqueIpFilterHandler() throws InterruptedException, ExecutionException {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            for (int round = 0; round < 10000; round++) {
                final UniqueIpFilter ipFilter = new UniqueIpFilter();
                Future<EmbeddedChannel> future1 = UniqueIpFilterTest.newChannelAsync(barrier, executorService, ipFilter);
                Future<EmbeddedChannel> future2 = UniqueIpFilterTest.newChannelAsync(barrier, executorService, ipFilter);
                EmbeddedChannel ch1 = future1.get();
                EmbeddedChannel ch2 = future2.get();
                Assert.assertTrue(((ch1.isActive()) || (ch2.isActive())));
                Assert.assertFalse(((ch1.isActive()) && (ch2.isActive())));
                barrier.reset();
                ch1.close().await();
                ch2.close().await();
            }
        } finally {
            executorService.shutdown();
        }
    }
}

