/**
 * Copyright 2017 Netflix, Inc.
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
package com.netflix.conductor.client.worker;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Viren
 */
public class TestPropertyFactory {
    @Test
    public void testIdentity() {
        Worker worker = Worker.create("Test2", TaskResult::new);
        Assert.assertNotNull(worker.getIdentity());
        boolean paused = worker.paused();
        Assert.assertFalse(("Paused? " + paused), paused);
    }

    @Test
    public void test() {
        int val = PropertyFactory.getInteger("workerB", "pollingInterval", 100);
        Assert.assertEquals(("got: " + val), 2, val);
        Assert.assertEquals(100, PropertyFactory.getInteger("workerB", "propWithoutValue", 100).intValue());
        Assert.assertFalse(PropertyFactory.getBoolean("workerB", "paused", true));// Global value set to 'false'

        Assert.assertTrue(PropertyFactory.getBoolean("workerA", "paused", false));// WorkerA value set to 'true'

        Assert.assertEquals(42, PropertyFactory.getInteger("workerA", "batchSize", 42).intValue());// No global value set, so will return the default value supplied

        Assert.assertEquals(84, PropertyFactory.getInteger("workerB", "batchSize", 42).intValue());// WorkerB's value set to 84

        Assert.assertEquals("domainA", PropertyFactory.getString("workerA", "domain", null));
        Assert.assertEquals("domainB", PropertyFactory.getString("workerB", "domain", null));
        Assert.assertNull(PropertyFactory.getString("workerC", "domain", null));// Non Existent

    }

    @Test
    public void testProperty() {
        Worker worker = Worker.create("Test", TaskResult::new);
        boolean paused = worker.paused();
        Assert.assertTrue(("Paused? " + paused), paused);
    }
}

