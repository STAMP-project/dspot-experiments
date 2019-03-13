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
package com.hazelcast.client.map;


import com.hazelcast.client.helpers.SimpleClientInterceptor;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SimpleClientMapInterceptorTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    private SimpleClientInterceptor interceptor;

    @Test
    public void clientMapInterceptorTestIssue1238() throws InterruptedException {
        final IMap<Object, Object> map = client.getMap("clientMapInterceptorTest");
        String id = map.addInterceptor(interceptor);
        map.put(1, "New York");
        map.put(2, "Istanbul");
        map.put(3, "Tokyo");
        map.put(4, "London");
        map.put(5, "Paris");
        map.put(6, "Cairo");
        map.put(7, "Hong Kong");
        map.remove(1);
        try {
            map.remove(2);
            Assert.fail();
        } catch (Exception ignore) {
        }
        Assert.assertEquals(map.size(), 6);
        Assert.assertEquals(map.get(1), null);
        Assert.assertEquals(map.get(2), "ISTANBUL:");
        Assert.assertEquals(map.get(3), "TOKYO:");
        Assert.assertEquals(map.get(4), "LONDON:");
        Assert.assertEquals(map.get(5), "PARIS:");
        Assert.assertEquals(map.get(6), "CAIRO:");
        Assert.assertEquals(map.get(7), "HONG KONG:");
        map.removeInterceptor(id);
        map.put(8, "Moscow");
        Assert.assertEquals(map.get(8), "Moscow");
        Assert.assertEquals(map.get(1), null);
        Assert.assertEquals(map.get(2), "ISTANBUL");
        Assert.assertEquals(map.get(3), "TOKYO");
        Assert.assertEquals(map.get(4), "LONDON");
        Assert.assertEquals(map.get(5), "PARIS");
        Assert.assertEquals(map.get(6), "CAIRO");
        Assert.assertEquals(map.get(7), "HONG KONG");
    }
}

