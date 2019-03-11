/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal;


import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.Ignite;
import org.apache.ignite.events.Event;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test to reproduce gg-2852.
 */
public class GridTaskUriDeploymentDeadlockSelfTest extends GridCommonAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeadlock() throws Exception {
        try {
            Ignite g = startGrid(1);
            final CountDownLatch latch = new CountDownLatch(1);
            g.events().localListen(new org.apache.ignite.lang.IgnitePredicate<Event>() {
                @Override
                public boolean apply(Event evt) {
                    assert (evt.type()) == (EVT_NODE_JOINED);
                    latch.countDown();
                    return true;
                }
            }, EVT_NODE_JOINED);
            IgniteInternalFuture<?> f = multithreadedAsync(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    startGrid(2);
                    return null;
                }
            }, 1);
            assert latch.await(5, TimeUnit.SECONDS);
            info(">>> Starting task.");
            assert "2".equals(executeAsync(compute(g.cluster().forPredicate(F.equalTo(F.first(g.cluster().forRemotes().nodes())))), "GarHelloWorldTask", "HELLOWORLD.MSG").get(60000));
            f.get();
        } finally {
            stopAllGrids();
        }
    }
}

