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
package com.hazelcast.client.txn;


import ClientConnectionStrategyConfig.ReconnectMode.ASYNC;
import ClientConnectionStrategyConfig.ReconnectMode.ON;
import ClientProperty.INVOCATION_TIMEOUT_SECONDS;
import com.hazelcast.client.HazelcastClientNotActiveException;
import com.hazelcast.client.HazelcastClientOfflineException;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientTxnReconnectModeTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Parameterized.Parameter
    public boolean smartRouting;

    @Test(expected = OperationTimeoutException.class)
    public void testNewTransactionContext_ReconnectMode_ON() throws Throwable {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().setConnectionAttemptLimit(Integer.MAX_VALUE);
        config.getNetworkConfig().setSmartRouting(smartRouting);
        config.getConnectionStrategyConfig().setAsyncStart(true);
        config.setProperty(INVOCATION_TIMEOUT_SECONDS.getName(), "3");
        config.getConnectionStrategyConfig().setReconnectMode(ON);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(config);
        try {
            client.newTransactionContext();
        } catch (TransactionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = HazelcastClientOfflineException.class)
    public void testNewTransactionContext_ReconnectMode_ASYNC() throws Throwable {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().setConnectionAttemptLimit(Integer.MAX_VALUE);
        config.getNetworkConfig().setSmartRouting(smartRouting);
        config.getConnectionStrategyConfig().setAsyncStart(true);
        config.getConnectionStrategyConfig().setReconnectMode(ASYNC);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(config);
        try {
            client.newTransactionContext();
        } catch (TransactionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = HazelcastClientNotActiveException.class)
    public void testNewTransactionContext_After_shutdown() throws Throwable {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().setConnectionAttemptLimit(Integer.MAX_VALUE);
        config.getNetworkConfig().setSmartRouting(smartRouting);
        config.getConnectionStrategyConfig().setAsyncStart(true);
        config.getConnectionStrategyConfig().setReconnectMode(ASYNC);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(config);
        client.shutdown();
        try {
            client.newTransactionContext();
        } catch (TransactionException e) {
            throw e.getCause();
        }
    }
}

