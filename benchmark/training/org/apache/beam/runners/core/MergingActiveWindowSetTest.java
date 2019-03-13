/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.core;


import java.util.Map;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test NonMergingActiveWindowSet.
 */
@RunWith(JUnit4.class)
public class MergingActiveWindowSetTest {
    private Sessions windowFn;

    private StateInternals state;

    private MergingActiveWindowSet<IntervalWindow> set;

    private ActiveWindowSet.MergeCallback<IntervalWindow> callback;

    @Test
    public void testLifecycle() throws Exception {
        // Step 1: New elements show up, introducing NEW windows which are partially merged.
        // NEW 1+10
        // NEW 2+10
        // NEW 15+10
        // =>
        // ACTIVE 1+11 (target 1+11)
        // ACTIVE 15+10 (target 15+10)
        add(1, 2, 15);
        Assert.assertEquals(ImmutableSet.of(window(1, 10), window(2, 10), window(15, 10)), set.getActiveAndNewWindows());
        Map<IntervalWindow, IntervalWindow> map = merge(ImmutableList.of(window(1, 10), window(2, 10)), window(1, 11));
        activate(map, 1, 2, 15);
        Assert.assertEquals(ImmutableSet.of(window(1, 11), window(15, 10)), set.getActiveAndNewWindows());
        Assert.assertEquals(ImmutableSet.of(window(1, 11)), set.readStateAddresses(window(1, 11)));
        Assert.assertEquals(ImmutableSet.of(window(15, 10)), set.readStateAddresses(window(15, 10)));
        cleanup();
        // Step 2: Another element, merged into an existing ACTIVE window.
        // NEW 3+10
        // =>
        // ACTIVE 1+12 (target 1+11)
        // ACTIVE 15+10 (target 15+10)
        add(3);
        Assert.assertEquals(ImmutableSet.of(window(3, 10), window(1, 11), window(15, 10)), set.getActiveAndNewWindows());
        map = merge(ImmutableList.of(window(1, 11), window(3, 10)), window(1, 12));
        activate(map, 3);
        Assert.assertEquals(ImmutableSet.of(window(1, 12), window(15, 10)), set.getActiveAndNewWindows());
        Assert.assertEquals(ImmutableSet.of(window(1, 11)), set.readStateAddresses(window(1, 12)));
        Assert.assertEquals(ImmutableSet.of(window(15, 10)), set.readStateAddresses(window(15, 10)));
        cleanup();
        // Step 3: Another element, causing two ACTIVE windows to be merged.
        // NEW 8+10
        // =>
        // ACTIVE 1+24 (target 1+11)
        add(8);
        Assert.assertEquals(ImmutableSet.of(window(8, 10), window(1, 12), window(15, 10)), set.getActiveAndNewWindows());
        map = merge(ImmutableList.of(window(1, 12), window(8, 10), window(15, 10)), window(1, 24));
        activate(map, 8);
        Assert.assertEquals(ImmutableSet.of(window(1, 24)), set.getActiveAndNewWindows());
        Assert.assertEquals(ImmutableSet.of(window(1, 11)), set.readStateAddresses(window(1, 24)));
        cleanup();
        // Step 4: Another element, merged into an existing ACTIVE window.
        // NEW 9+10
        // =>
        // ACTIVE 1+24 (target 1+11)
        add(9);
        Assert.assertEquals(ImmutableSet.of(window(9, 10), window(1, 24)), set.getActiveAndNewWindows());
        map = merge(ImmutableList.of(window(1, 24), window(9, 10)), window(1, 24));
        activate(map, 9);
        Assert.assertEquals(ImmutableSet.of(window(1, 24)), set.getActiveAndNewWindows());
        Assert.assertEquals(ImmutableSet.of(window(1, 11)), set.readStateAddresses(window(1, 24)));
        cleanup();
        // Step 5: Another element reusing earlier window, merged into an existing ACTIVE window.
        // NEW 1+10
        // =>
        // ACTIVE 1+24 (target 1+11)
        add(1);
        Assert.assertEquals(ImmutableSet.of(window(1, 10), window(1, 24)), set.getActiveAndNewWindows());
        map = merge(ImmutableList.of(window(1, 10), window(1, 24)), window(1, 24));
        activate(map, 1);
        Assert.assertEquals(ImmutableSet.of(window(1, 24)), set.getActiveAndNewWindows());
        Assert.assertEquals(ImmutableSet.of(window(1, 11)), set.readStateAddresses(window(1, 24)));
        cleanup();
        // Step 6: Window is closed.
        set.remove(window(1, 24));
        cleanup();
        Assert.assertTrue(set.getActiveAndNewWindows().isEmpty());
    }

    @Test
    public void testLegacyState() {
        // Pre 1.4 we merged window state lazily.
        // Simulate loading an active window set with multiple state address windows.
        set.addActiveForTesting(window(1, 12), ImmutableList.of(window(1, 10), window(2, 10), window(3, 10)));
        // Make sure we can detect and repair the state.
        Assert.assertTrue(set.isActive(window(1, 12)));
        Assert.assertEquals(ImmutableSet.of(window(1, 10), window(2, 10), window(3, 10)), set.readStateAddresses(window(1, 12)));
        Assert.assertEquals(window(1, 10), set.mergedWriteStateAddress(ImmutableList.of(window(1, 10), window(2, 10), window(3, 10)), window(1, 12)));
        set.merged(window(1, 12));
        cleanup();
        // For then on we are back to the eager case.
        Assert.assertEquals(ImmutableSet.of(window(1, 10)), set.readStateAddresses(window(1, 12)));
    }
}

