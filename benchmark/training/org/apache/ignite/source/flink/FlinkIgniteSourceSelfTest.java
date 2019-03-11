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
package org.apache.ignite.source.flink;


import EventType.EVT_CACHE_OBJECT_PUT;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.util.lang.GridAbsPredicate;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link IgniteSource}.
 */
public class FlinkIgniteSourceSelfTest extends GridCommonAbstractTest {
    /**
     * Cache name.
     */
    private static final String TEST_CACHE = "testCache";

    /**
     * Flink source context.
     */
    private SourceFunction.SourceContext<CacheEvent> ctx;

    /**
     * Ignite instance.
     */
    private Ignite ignite;

    /**
     * Cluster Group
     */
    private ClusterGroup clsGrp;

    /**
     * Ignite Source instance
     */
    private IgniteSource igniteSrc;

    /**
     * Tests Ignite source start operation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIgniteSourceStart() throws Exception {
        igniteSrc.start(null, EVT_CACHE_OBJECT_PUT);
        Mockito.verify(ignite.events(clsGrp), Mockito.times(1));
    }

    /**
     * Tests Ignite source run operation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIgniteSourceRun() throws Exception {
        IgniteInternalFuture f = GridTestUtils.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    igniteSrc.start(null, EVT_CACHE_OBJECT_PUT);
                    igniteSrc.run(ctx);
                } catch (Throwable e) {
                    igniteSrc.cancel();
                    throw new AssertionError("Unexpected failure.", e);
                }
            }
        });
        long endTime = (System.currentTimeMillis()) + 2000;
        GridTestUtils.waitForCondition(new GridAbsPredicate() {
            @Override
            public boolean apply() {
                return (f.isDone()) || ((System.currentTimeMillis()) > endTime);
            }
        }, 3000);
        igniteSrc.cancel();
        f.get(3000);
    }
}

