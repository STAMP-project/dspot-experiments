/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.transaction;


import DatabaseType.H2;
import TransactionType.XA;
import java.util.Map;
import org.apache.shardingsphere.transaction.core.fixture.ShardingTransactionManagerFixture;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ShardingTransactionManagerEngineTest {
    private ShardingTransactionManagerEngine shardingTransactionManagerEngine = new ShardingTransactionManagerEngine();

    @Test
    public void assertGetEngine() {
        Assert.assertThat(shardingTransactionManagerEngine.getTransactionManager(XA), CoreMatchers.instanceOf(ShardingTransactionManagerFixture.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertRegisterTransactionResource() {
        Runnable caller = Mockito.mock(Runnable.class);
        ShardingTransactionManagerFixture shardingTransactionManager = ((ShardingTransactionManagerFixture) (shardingTransactionManagerEngine.getTransactionManager(XA)));
        setCaller(caller);
        shardingTransactionManagerEngine.init(H2, Mockito.mock(Map.class));
        Mockito.verify(caller).run();
    }
}

