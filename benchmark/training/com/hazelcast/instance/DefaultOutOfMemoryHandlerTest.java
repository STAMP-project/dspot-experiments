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
package com.hazelcast.instance;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static DefaultOutOfMemoryHandler.GC_OVERHEAD_LIMIT_EXCEEDED;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DefaultOutOfMemoryHandlerTest extends AbstractOutOfMemoryHandlerTest {
    private HazelcastInstance[] instances;

    private DefaultOutOfMemoryHandler outOfMemoryHandler;

    @Test
    public void testShouldHandle() {
        Assert.assertTrue(outOfMemoryHandler.shouldHandle(new OutOfMemoryError(GC_OVERHEAD_LIMIT_EXCEEDED)));
    }

    @Test
    public void testOnOutOfMemory() {
        outOfMemoryHandler.onOutOfMemory(new OutOfMemoryError(), instances);
        Assert.assertFalse("The member should be shutdown", hazelcastInstance.getLifecycleService().isRunning());
    }
}

