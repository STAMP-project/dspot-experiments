/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.control;


import MemoryState.CRITICAL;
import MemoryState.CRITICAL_DISABLED;
import MemoryState.DISABLED;
import MemoryState.EVICTION;
import MemoryState.EVICTION_CRITICAL;
import MemoryState.EVICTION_CRITICAL_DISABLED;
import MemoryState.EVICTION_DISABLED;
import MemoryState.EVICTION_DISABLED_CRITICAL;
import MemoryState.NORMAL;
import org.junit.Assert;
import org.junit.Test;


public class MemoryThresholdsJUnitTest {
    @Test
    public void testDefaults() {
        MemoryThresholds thresholds = new MemoryThresholds(1000);
        Assert.assertFalse(thresholds.isEvictionThresholdEnabled());
        Assert.assertFalse(thresholds.isCriticalThresholdEnabled());
        Assert.assertEquals(1000L, thresholds.getMaxMemoryBytes());
        Assert.assertEquals(0.0F, thresholds.getEvictionThreshold(), 0.01);
        Assert.assertEquals(0.0F, thresholds.getCriticalThreshold(), 0.01);
    }

    @Test
    public void testSetAndGetters() {
        try {
            new MemoryThresholds(1000, 49.8F, 84.2F);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        try {
            new MemoryThresholds(1000, 100.1F, 0.0F);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        try {
            new MemoryThresholds(1000, (-0.1F), 0.0F);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        try {
            new MemoryThresholds(1000, 0.0F, 100.1F);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        try {
            new MemoryThresholds(1000, 0.0F, (-0.1F));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        MemoryThresholds thresholds = new MemoryThresholds(1000, 84.2F, 49.8F);
        Assert.assertTrue(thresholds.isEvictionThresholdEnabled());
        Assert.assertTrue(thresholds.isCriticalThresholdEnabled());
        Assert.assertEquals(1000L, thresholds.getMaxMemoryBytes());
        Assert.assertEquals(49.8F, thresholds.getEvictionThreshold(), 0.01);
        Assert.assertTrue(((Math.abs((498 - (thresholds.getEvictionThresholdBytes())))) <= 1));// Allow for rounding

        Assert.assertEquals(84.2F, thresholds.getCriticalThreshold(), 0.01);
        Assert.assertTrue(((Math.abs((842 - (thresholds.getCriticalThresholdBytes())))) <= 1));// Allow for rounding

    }

    @Test
    public void testTransitionsNoThresholds() {
        MemoryThresholds thresholds = new MemoryThresholds(1000, 0.0F, 0.0F);
        Assert.assertEquals(DISABLED, thresholds.computeNextState(DISABLED, 100));
        Assert.assertEquals(DISABLED, thresholds.computeNextState(EVICTION_DISABLED, 100));
        Assert.assertEquals(DISABLED, thresholds.computeNextState(EVICTION_DISABLED_CRITICAL, 100));
        Assert.assertEquals(DISABLED, thresholds.computeNextState(CRITICAL_DISABLED, 100));
        Assert.assertEquals(DISABLED, thresholds.computeNextState(EVICTION_CRITICAL_DISABLED, 100));
        Assert.assertEquals(DISABLED, thresholds.computeNextState(NORMAL, 100));
        Assert.assertEquals(DISABLED, thresholds.computeNextState(EVICTION, 100));
        Assert.assertEquals(DISABLED, thresholds.computeNextState(CRITICAL, 100));
        Assert.assertEquals(DISABLED, thresholds.computeNextState(EVICTION_CRITICAL, 100));
    }

    @Test
    public void testTransitionsEvictionSet() {
        MemoryThresholds thresholds = new MemoryThresholds(1000, 0.0F, 50.0F);
        Assert.assertEquals(CRITICAL_DISABLED, thresholds.computeNextState(DISABLED, 499));
        Assert.assertEquals(CRITICAL_DISABLED, thresholds.computeNextState(EVICTION, 450));
        Assert.assertEquals(CRITICAL_DISABLED, thresholds.computeNextState(CRITICAL, 499));
        Assert.assertEquals(EVICTION_CRITICAL_DISABLED, thresholds.computeNextState(DISABLED, 500));
        Assert.assertEquals(EVICTION_CRITICAL_DISABLED, thresholds.computeNextState(EVICTION, 499));
        Assert.assertEquals(EVICTION_CRITICAL_DISABLED, thresholds.computeNextState(CRITICAL, 500));
    }

    @Test
    public void testTransitionsCriticalSet() {
        MemoryThresholds thresholds = new MemoryThresholds(1000, 50.0F, 0.0F);
        Assert.assertEquals(EVICTION_DISABLED, thresholds.computeNextState(DISABLED, 499));
        Assert.assertEquals(EVICTION_DISABLED, thresholds.computeNextState(EVICTION, 499));
        Assert.assertEquals(EVICTION_DISABLED_CRITICAL, thresholds.computeNextState(DISABLED, 500));
        Assert.assertEquals(EVICTION_DISABLED_CRITICAL, thresholds.computeNextState(EVICTION, 500));
        Assert.assertEquals(EVICTION_DISABLED_CRITICAL, thresholds.computeNextState(CRITICAL, 499));
    }

    @Test
    public void testTransitionsEvictionAndCriticalSet() {
        MemoryThresholds thresholds = new MemoryThresholds(1000, 80.0F, 50.0F);
        Assert.assertEquals(NORMAL, thresholds.computeNextState(DISABLED, 0));
        Assert.assertEquals(NORMAL, thresholds.computeNextState(DISABLED, 499));
        Assert.assertEquals(NORMAL, thresholds.computeNextState(NORMAL, 499));
        Assert.assertEquals(NORMAL, thresholds.computeNextState(CRITICAL, 499));
        Assert.assertEquals(EVICTION, thresholds.computeNextState(DISABLED, 500));
        Assert.assertEquals(EVICTION, thresholds.computeNextState(NORMAL, 500));
        Assert.assertEquals(EVICTION, thresholds.computeNextState(EVICTION, 499));
        Assert.assertEquals(EVICTION, thresholds.computeNextState(EVICTION, 500));
        Assert.assertEquals(EVICTION, thresholds.computeNextState(EVICTION, 799));
        Assert.assertEquals(EVICTION_CRITICAL, thresholds.computeNextState(DISABLED, 800));
        Assert.assertEquals(EVICTION_CRITICAL, thresholds.computeNextState(NORMAL, 800));
        Assert.assertEquals(EVICTION_CRITICAL, thresholds.computeNextState(EVICTION, 800));
        Assert.assertEquals(EVICTION_CRITICAL, thresholds.computeNextState(CRITICAL, 800));
        Assert.assertEquals(EVICTION_CRITICAL, thresholds.computeNextState(CRITICAL, 799));
        Assert.assertEquals(EVICTION_CRITICAL, thresholds.computeNextState(EVICTION_CRITICAL, 800));
        Assert.assertEquals(EVICTION_CRITICAL, thresholds.computeNextState(EVICTION_CRITICAL, 799));
    }
}

