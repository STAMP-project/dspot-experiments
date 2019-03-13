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
package com.hazelcast.client.spi.impl;


import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.spi.impl.ClientResponseHandlerSupplier.AsyncResponseHandler;
import com.hazelcast.client.spi.impl.ClientResponseHandlerSupplier.DynamicResponseHandler;
import com.hazelcast.client.spi.impl.ClientResponseHandlerSupplier.SyncResponseHandler;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.function.Consumer;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientResponseHandlerSupplierTest extends ClientTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test(expected = IllegalArgumentException.class)
    public void whenNegativeResponseThreads() {
        getResponseHandler((-1), false);
    }

    @Test
    public void whenZeroResponseThreads() {
        Consumer<ClientMessage> handler = getResponseHandler(0, false);
        assertInstanceOf(SyncResponseHandler.class, handler);
    }

    @Test
    public void whenOneResponseThreads_andStatic() {
        Consumer<ClientMessage> handler = getResponseHandler(1, false);
        assertInstanceOf(AsyncResponseHandler.class, handler);
    }

    @Test
    public void whenMultipleResponseThreads_andStatic() {
        Consumer<ClientMessage> handler = getResponseHandler(2, false);
        assertInstanceOf(AsyncResponseHandler.class, handler);
    }

    @Test
    public void whenOneResponseThreads_andDynamic() {
        Consumer<ClientMessage> handler = getResponseHandler(1, true);
        assertInstanceOf(DynamicResponseHandler.class, handler);
    }

    @Test
    public void whenMultipleResponseThreads_andDynamic() {
        Consumer<ClientMessage> handler = getResponseHandler(2, true);
        assertInstanceOf(DynamicResponseHandler.class, handler);
    }
}

