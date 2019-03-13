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
package org.apache.ignite.internal.client.impl;


import org.apache.ignite.internal.client.GridClientException;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Grid client future implementation self test.
 */
public class ClientFutureAdapterSelfTest extends GridCommonAbstractTest {
    /**
     * Test finished futures.
     */
    @Test
    public void testFinished() {
        GridClientFutureAdapter<Integer> fut = new GridClientFutureAdapter();
        assertFalse(fut.isDone());
        fut.onDone(0);
        assertTrue(fut.isDone());
        assertTrue(new GridClientFutureAdapter(0).isDone());
        assertTrue(new GridClientFutureAdapter<Integer>(new GridClientException("Test grid exception.")).isDone());
        assertTrue(new GridClientFutureAdapter<Integer>(new RuntimeException("Test runtime exception.")).isDone());
    }

    /**
     * Test chained futures behaviour.
     *
     * @throws org.apache.ignite.internal.client.GridClientException
     * 		On any exception.
     */
    @Test
    public void testChains() throws GridClientException {
        // Synchronous notifications.
        testChains(1, 100);
        testChains(10, 10);
        testChains(100, 1);
        testChains(1000, 0);
    }
}

