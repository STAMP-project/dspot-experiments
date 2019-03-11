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
package org.apache.activemq.store.jdbc;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class TransactionContextTest {
    TransactionContext underTest;

    static JDBCPersistenceAdapter jdbcPersistenceAdapter;

    @Test
    public void testCompletionCalledOnceOnCommmit() throws Exception {
        final AtomicInteger called = new AtomicInteger();
        underTest.begin();
        underTest.onCompletion(new Runnable() {
            @Override
            public void run() {
                called.incrementAndGet();
            }
        });
        underTest.commit();
        Assert.assertEquals(1, called.get());
        underTest.begin();
        underTest.commit();
        Assert.assertEquals(1, called.get());
    }

    @Test
    public void testCompletionCalledOnceOnClose() throws Exception {
        underTest.getConnection();
        final AtomicInteger called = new AtomicInteger();
        underTest.onCompletion(new Runnable() {
            @Override
            public void run() {
                called.incrementAndGet();
            }
        });
        underTest.close();
        Assert.assertEquals(1, called.get());
        underTest.getConnection();
        underTest.close();
        Assert.assertEquals(1, called.get());
    }
}

