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
package com.hazelcast.ringbuffer.impl;


import MapService.SERVICE_NAME;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RingbufferWaitNotifyKeyTest {
    @Test
    public void test_equals() {
        test_equals(waitNotifyKey("peter"), waitNotifyKey("peter"), true);
        test_equals(waitNotifyKey("peter"), waitNotifyKey("talip"), false);
        test_equals(waitNotifyKey("peter"), waitNotifyKey(SERVICE_NAME, "peter"), false);
        test_equals(waitNotifyKey("peter"), waitNotifyKey(SERVICE_NAME, "talip"), false);
        test_equals(waitNotifyKey("peter"), "", false);
        test_equals(waitNotifyKey("peter"), null, false);
        test_equals(waitNotifyKey(RingbufferService.SERVICE_NAME, "peter", 1), waitNotifyKey(SERVICE_NAME, "peter", 1), false);
        test_equals(waitNotifyKey(RingbufferService.SERVICE_NAME, "peter", 1), waitNotifyKey(RingbufferService.SERVICE_NAME, "peter", 2), false);
        final RingbufferWaitNotifyKey key = waitNotifyKey("peter");
        test_equals(key, key, true);
    }
}

