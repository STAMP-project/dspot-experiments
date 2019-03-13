/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.internal.executor;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.ehcache.impl.config.executor.PooledExecutionServiceConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Ludovic Orban
 */
public class PooledExecutionServiceTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    PooledExecutionService pooledExecutionService;

    @Test
    public void testEmptyConfigThrowsAtStart() throws Exception {
        PooledExecutionServiceConfiguration configuration = new PooledExecutionServiceConfiguration();
        pooledExecutionService = new PooledExecutionService(configuration);
        expectedException.expectMessage("Pool configuration is empty");
        pooledExecutionService.start(null);
    }

    @Test
    public void testGetOrderedExecutorFailsOnNonExistentPool() throws Exception {
        PooledExecutionServiceConfiguration configuration = new PooledExecutionServiceConfiguration();
        configuration.addPool("getOrderedExecutorFailsOnNonExistentPool", 0, 1);
        pooledExecutionService = new PooledExecutionService(configuration);
        pooledExecutionService.start(null);
        expectedException.expectMessage("Pool 'abc' is not in the set of available pools [getOrderedExecutorFailsOnNonExistentPool]");
        pooledExecutionService.getOrderedExecutor("abc", new LinkedBlockingDeque<>());
    }

    @Test
    public void testGetOrderedExecutorFailsOnNonExistentDefaultPool() throws Exception {
        PooledExecutionServiceConfiguration configuration = new PooledExecutionServiceConfiguration();
        configuration.addPool("getOrderedExecutorFailsOnNonExistentDefaultPool", 0, 1);
        pooledExecutionService = new PooledExecutionService(configuration);
        pooledExecutionService.start(null);
        expectedException.expectMessage("Null pool alias provided and no default pool configured");
        pooledExecutionService.getOrderedExecutor(null, new LinkedBlockingDeque<>());
    }

    @Test
    public void testGetOrderedExecutorSucceedsOnExistingPool() throws Exception {
        PooledExecutionServiceConfiguration configuration = new PooledExecutionServiceConfiguration();
        configuration.addPool("getOrderedExecutorSucceedsOnExistingPool", 0, 1);
        pooledExecutionService = new PooledExecutionService(configuration);
        pooledExecutionService.start(null);
        ExecutorService aaa = pooledExecutionService.getOrderedExecutor("getOrderedExecutorSucceedsOnExistingPool", new LinkedBlockingDeque<>());
        aaa.shutdown();
    }

    @Test
    public void testGetOrderedExecutorSucceedsOnExistingDefaultPool() throws Exception {
        PooledExecutionServiceConfiguration configuration = new PooledExecutionServiceConfiguration();
        configuration.addDefaultPool("getOrderedExecutorSucceedsOnExistingDefaultPool", 0, 1);
        pooledExecutionService = new PooledExecutionService(configuration);
        pooledExecutionService.start(null);
        ExecutorService dflt = pooledExecutionService.getOrderedExecutor(null, new LinkedBlockingDeque<>());
        dflt.shutdown();
    }

    @Test
    public void testAllThreadsAreStopped() throws Exception {
        PooledExecutionServiceConfiguration configuration = new PooledExecutionServiceConfiguration();
        configuration.addDefaultPool("allThreadsAreStopped", 0, 1);
        pooledExecutionService = new PooledExecutionService(configuration);
        pooledExecutionService.start(null);
        final CountDownLatch latch = new CountDownLatch(1);
        pooledExecutionService.getScheduledExecutor("allThreadsAreStopped").execute(latch::countDown);
        assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
        pooledExecutionService.stop();
        assertThat(Thread.currentThread().isInterrupted()).isFalse();
        assertThat(pooledExecutionService.isStopped()).isTrue();
    }
}

