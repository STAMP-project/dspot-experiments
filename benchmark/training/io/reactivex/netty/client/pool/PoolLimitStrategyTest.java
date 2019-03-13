/**
 * Copyright 2015 Netflix, Inc.
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
package io.reactivex.netty.client.pool;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class PoolLimitStrategyTest {
    @Test(timeout = 60000)
    public void testMaxConnectionLimit() throws Exception {
        MaxConnectionsBasedStrategy strategy = new MaxConnectionsBasedStrategy(3);
        long startTime = System.currentTimeMillis();
        Assert.assertTrue("Invalid permit acquire failure.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available permits.", 2, strategy.getAvailablePermits());
        Assert.assertTrue("Invalid permit acquire failure.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available permits.", 1, strategy.getAvailablePermits());
        Assert.assertTrue("Invalid permit acquire failure.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available permits.", 0, strategy.getAvailablePermits());
        Assert.assertFalse("Invalid permit acquire success.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        strategy.releasePermit();
        Assert.assertEquals("Unexpected available permits.", 1, strategy.getAvailablePermits());
        Assert.assertTrue("Permit not available after release.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testCompositeStrategy() throws Exception {
        long startTime = System.currentTimeMillis();
        MaxConnectionsBasedStrategy global = new MaxConnectionsBasedStrategy(1);
        MaxConnectionsBasedStrategy local = new MaxConnectionsBasedStrategy(2);
        CompositePoolLimitDeterminationStrategy strategy = new CompositePoolLimitDeterminationStrategy(local, global);
        Assert.assertTrue("Invalid permit acquire failure.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available global permits.", 0, global.getAvailablePermits());
        Assert.assertEquals("Unexpected available local permits.", 1, local.getAvailablePermits());
        Assert.assertEquals("Unexpected available composite permits.", 0, strategy.getAvailablePermits());// Should be min. of all strategies

        Assert.assertFalse("Invalid permit acquire success.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available global permits.", 0, global.getAvailablePermits());
        Assert.assertEquals("Unexpected available local permits.", 1, local.getAvailablePermits());
        Assert.assertEquals("Unexpected available composite permits.", 0, strategy.getAvailablePermits());// Should be min. of all strategies

        strategy.releasePermit();
        Assert.assertEquals("Unexpected available global permits.", 1, global.getAvailablePermits());
        Assert.assertEquals("Unexpected available local permits.", 2, local.getAvailablePermits());
        Assert.assertEquals("Unexpected available composite permits.", 1, strategy.getAvailablePermits());// Should be min. of all strategies

        Assert.assertTrue("Invalid permit acquire failure.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available global permits.", 0, global.getAvailablePermits());
        Assert.assertEquals("Unexpected available local permits.", 1, local.getAvailablePermits());
        Assert.assertEquals("Unexpected available composite permits.", 0, strategy.getAvailablePermits());// Should be min. of all strategies

    }

    @Test(timeout = 60000)
    public void testFirstStrategyHasMorePermits() throws Exception {
        long startTime = System.currentTimeMillis();
        MaxConnectionsBasedStrategy global = new MaxConnectionsBasedStrategy(2);
        MaxConnectionsBasedStrategy local = new MaxConnectionsBasedStrategy(1);
        CompositePoolLimitDeterminationStrategy strategy = new CompositePoolLimitDeterminationStrategy(local, global);
        Assert.assertTrue("Invalid permit acquire failure.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available global permits.", 1, global.getAvailablePermits());
        Assert.assertEquals("Unexpected available local permits.", 0, local.getAvailablePermits());
        Assert.assertEquals("Unexpected available composite permits.", 0, strategy.getAvailablePermits());// Should be min. of all strategies

        Assert.assertFalse("Invalid permit acquire success.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        strategy.releasePermit();
        Assert.assertTrue("Invalid permit acquire failure.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available global permits.", 1, global.getAvailablePermits());
        Assert.assertEquals("Unexpected available local permits.", 0, local.getAvailablePermits());
        Assert.assertEquals("Unexpected available composite permits.", 0, strategy.getAvailablePermits());// Should be min. of all strategies

    }

    @Test(timeout = 60000)
    public void testIncrementDecrementMaxConnections() throws Exception {
        long startTime = System.currentTimeMillis();
        MaxConnectionsBasedStrategy strategy = new MaxConnectionsBasedStrategy(1);
        Assert.assertTrue("Invalid permit acquire failure.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Unexpected available permits.", 0, strategy.getAvailablePermits());
        Assert.assertFalse("Invalid permit acquire success.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        strategy.incrementMaxConnections(1);
        Assert.assertEquals("Unexpected available permits.", 1, strategy.getAvailablePermits());
        Assert.assertTrue("Permit not available after release.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
        strategy.releasePermit();
        strategy.decrementMaxConnections(1);
        Assert.assertEquals("Unexpected available permits.", 0, strategy.getAvailablePermits());
        Assert.assertFalse("Invalid permit acquire success.", strategy.acquireCreationPermit(startTime, TimeUnit.MILLISECONDS));
    }
}

