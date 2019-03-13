/**
 * -
 * -\-\-
 * Helios Services
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
package com.spotify.helios.agent;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.spotify.helios.common.descriptors.PortMapping;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PortAllocatorTest {
    @Test
    public void testAllocate() throws Exception {
        final PortAllocator sut = new PortAllocator(20000, 20010);
        final Map<String, PortMapping> mapping = ImmutableMap.of("p1", PortMapping.of(17), "p2", PortMapping.of(18, 18));
        final Set<Integer> used = ImmutableSet.of(10, 11);
        final Map<String, Integer> allocation = sut.allocate(mapping, used);
        Assert.assertThat(allocation, Matchers.hasEntry(Matchers.is("p1"), Matchers.allOf(Matchers.greaterThanOrEqualTo(20000), Matchers.lessThanOrEqualTo(20010))));
        Assert.assertThat(allocation, Matchers.hasEntry("p2", 18));
    }

    @Test
    public void testInsufficientPortsFail1() throws Exception {
        final PortAllocator sut = new PortAllocator(10, 11);
        final Map<String, PortMapping> mapping = ImmutableMap.of("p1", PortMapping.of(17), "p2", PortMapping.of(18, 18));
        final Set<Integer> used = ImmutableSet.of(10, 11);
        final Map<String, Integer> allocation = sut.allocate(mapping, used);
        Assert.assertNull(allocation);
    }

    @Test
    public void testInsufficientPortsFail2() throws Exception {
        final PortAllocator sut = new PortAllocator(10, 11);
        final Map<String, PortMapping> mapping = ImmutableMap.of("p1", PortMapping.of(1), "p2", PortMapping.of(2), "p3", PortMapping.of(4), "p4", PortMapping.of(18, 18));
        final Set<Integer> used = ImmutableSet.of();
        final Map<String, Integer> allocation = sut.allocate(mapping, used);
        Assert.assertNull(allocation);
    }
}

