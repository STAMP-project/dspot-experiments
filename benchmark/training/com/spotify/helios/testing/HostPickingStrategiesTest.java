/**
 * -
 * -\-\-
 * Helios Testing Library
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.testing;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class HostPickingStrategiesTest {
    private static final int NUM_ITERATIONS = 1000;

    private static final ImmutableList<String> HOSTS = ImmutableList.of("hosta", "hostb", "hostc", "hostd");

    @Test
    public void testDeterministicOneHost() {
        final Set<String> chosenHosts = Sets.newHashSet();
        final HostPickingStrategy strategy1 = HostPickingStrategies.deterministicOneHost("");
        for (int i = 0; i < (HostPickingStrategiesTest.NUM_ITERATIONS); i++) {
            chosenHosts.add(strategy1.pickHost(HostPickingStrategiesTest.HOSTS));
        }
        final HostPickingStrategy strategy2 = HostPickingStrategies.deterministicOneHost("");
        for (int i = 0; i < (HostPickingStrategiesTest.NUM_ITERATIONS); i++) {
            chosenHosts.add(strategy2.pickHost(HostPickingStrategiesTest.HOSTS));
        }
        Assert.assertEquals(1, chosenHosts.size());
    }

    @Test
    public void testDeterministic() {
        final List<String> order = Lists.newArrayList();
        final Set<String> chosenHosts = Sets.newHashSet();
        final HostPickingStrategy strategy1 = HostPickingStrategies.deterministic("");
        for (int i = 0; i < (HostPickingStrategiesTest.NUM_ITERATIONS); i++) {
            final String picked = strategy1.pickHost(HostPickingStrategiesTest.HOSTS);
            order.add(picked);
            chosenHosts.add(picked);
        }
        // should've hit them all
        Assert.assertEquals(HostPickingStrategiesTest.HOSTS.size(), chosenHosts.size());
        final HostPickingStrategy strategy2 = HostPickingStrategies.deterministic("");
        for (int i = 0; i < (HostPickingStrategiesTest.NUM_ITERATIONS); i++) {
            Assert.assertEquals(("at index " + i), order.get(i), strategy2.pickHost(HostPickingStrategiesTest.HOSTS));
        }
    }

    @Test
    public void testRandomOneHost() {
        final Set<String> chosenHosts = Sets.newHashSet();
        final HostPickingStrategy strategy1 = HostPickingStrategies.randomOneHost();
        for (int i = 0; i < (HostPickingStrategiesTest.NUM_ITERATIONS); i++) {
            chosenHosts.add(strategy1.pickHost(HostPickingStrategiesTest.HOSTS));
        }
        Assert.assertEquals(1, chosenHosts.size());
    }

    @Test
    public void testRandom() {
        final List<String> order = Lists.newArrayList();
        final Set<String> chosenHosts = Sets.newHashSet();
        final HostPickingStrategy strategy1 = HostPickingStrategies.random();
        for (int i = 0; i < (HostPickingStrategiesTest.NUM_ITERATIONS); i++) {
            final String picked = strategy1.pickHost(HostPickingStrategiesTest.HOSTS);
            order.add(picked);
            chosenHosts.add(picked);
        }
        // should've hit them all
        Assert.assertEquals(HostPickingStrategiesTest.HOSTS.size(), chosenHosts.size());
        final HostPickingStrategy strategy2 = HostPickingStrategies.random();
        boolean different = false;
        for (int i = 0; i < (HostPickingStrategiesTest.NUM_ITERATIONS); i++) {
            if (!(order.get(i).equals(strategy2.pickHost(HostPickingStrategiesTest.HOSTS)))) {
                different = true;
                break;
            }
        }
        Assert.assertTrue(different);
    }
}

