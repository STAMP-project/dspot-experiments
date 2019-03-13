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
package org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection;


import ConnectionStatus.RUNNING;
import ConnectionStatus.TERMINATED;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import static ConnectionStatus.RUNNING;


public final class ConnectionStateHandlerTest {
    private ResourceSynchronizer resourceSynchronizer = new ResourceSynchronizer();

    private ConnectionStateHandler connectionStateHandler = new ConnectionStateHandler(resourceSynchronizer);

    @Test
    public void assertWaitUntilConnectionReleaseForNoneTransaction() throws InterruptedException {
        final AtomicBoolean flag = new AtomicBoolean(true);
        Thread waitThread = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                connectionStateHandler.setStatus(RUNNING);
                connectionStateHandler.waitUntilConnectionReleasedIfNecessary();
                if ((RUNNING) != (connectionStateHandler.getStatus())) {
                    flag.getAndSet(false);
                }
            }
        });
        Thread notifyThread = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                Thread.sleep(2000);
                connectionStateHandler.doNotifyIfNecessary();
            }
        });
        waitThread.start();
        notifyThread.start();
        waitThread.join();
        notifyThread.join();
        Assert.assertTrue(flag.get());
    }

    @Test
    public void assertWaitUntilConnectionReleaseForTransaction() throws InterruptedException {
        final AtomicBoolean flag = new AtomicBoolean(true);
        Thread waitThread = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                connectionStateHandler.setStatus(TERMINATED);
                connectionStateHandler.waitUntilConnectionReleasedIfNecessary();
                if ((ConnectionStatus.RUNNING) != (connectionStateHandler.getStatus())) {
                    flag.getAndSet(false);
                }
            }
        });
        Thread notifyThread = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                Thread.sleep(2000);
                connectionStateHandler.doNotifyIfNecessary();
            }
        });
        waitThread.start();
        notifyThread.start();
        waitThread.join();
        notifyThread.join();
        Assert.assertTrue(flag.get());
    }
}

