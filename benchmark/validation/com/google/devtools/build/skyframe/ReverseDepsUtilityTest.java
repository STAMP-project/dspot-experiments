/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.skyframe;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interner;
import com.google.devtools.build.lib.concurrent.BlazeInterners;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static ReverseDepsUtility.MAYBE_CHECK_THRESHOLD;
import static SkyFunctionName.FOR_TESTING;


/**
 * Test for {@code ReverseDepsUtility}.
 */
@RunWith(Parameterized.class)
public class ReverseDepsUtilityTest {
    private final int numElements;

    public ReverseDepsUtilityTest(int numElements) {
        this.numElements = numElements;
    }

    @Test
    public void testAddAndRemove() {
        for (int numRemovals = 0; numRemovals <= (numElements); numRemovals++) {
            InMemoryNodeEntry example = new InMemoryNodeEntry();
            for (int j = 0; j < (numElements); j++) {
                ReverseDepsUtility.addReverseDeps(example, Collections.singleton(ReverseDepsUtilityTest.Key.create(j)));
            }
            // Not a big test but at least check that it does not blow up.
            assertThat(ReverseDepsUtility.toString(example)).isNotEmpty();
            assertThat(ReverseDepsUtility.getReverseDeps(example)).hasSize(numElements);
            for (int i = 0; i < numRemovals; i++) {
                ReverseDepsUtility.removeReverseDep(example, ReverseDepsUtilityTest.Key.create(i));
            }
            assertThat(ReverseDepsUtility.getReverseDeps(example)).hasSize(((numElements) - numRemovals));
            assertThat(example.getReverseDepsDataToConsolidateForReverseDepsUtil()).isNull();
        }
    }

    // Same as testAdditionAndRemoval but we add all the reverse deps in one call.
    @Test
    public void testAddAllAndRemove() {
        for (int numRemovals = 0; numRemovals <= (numElements); numRemovals++) {
            InMemoryNodeEntry example = new InMemoryNodeEntry();
            List<SkyKey> toAdd = new ArrayList<>();
            for (int j = 0; j < (numElements); j++) {
                toAdd.add(ReverseDepsUtilityTest.Key.create(j));
            }
            ReverseDepsUtility.addReverseDeps(example, toAdd);
            assertThat(ReverseDepsUtility.getReverseDeps(example)).hasSize(numElements);
            for (int i = 0; i < numRemovals; i++) {
                ReverseDepsUtility.removeReverseDep(example, ReverseDepsUtilityTest.Key.create(i));
            }
            assertThat(ReverseDepsUtility.getReverseDeps(example)).hasSize(((numElements) - numRemovals));
            assertThat(example.getReverseDepsDataToConsolidateForReverseDepsUtil()).isNull();
        }
    }

    @Test
    public void testDuplicateCheckOnGetReverseDeps() {
        InMemoryNodeEntry example = new InMemoryNodeEntry();
        for (int i = 0; i < (numElements); i++) {
            ReverseDepsUtility.addReverseDeps(example, Collections.singleton(ReverseDepsUtilityTest.Key.create(i)));
        }
        // Should only fail when we call getReverseDeps().
        ReverseDepsUtility.addReverseDeps(example, Collections.singleton(ReverseDepsUtilityTest.Key.create(0)));
        try {
            ReverseDepsUtility.getReverseDeps(example);
            assertThat(numElements).isEqualTo(0);
        } catch (Exception expected) {
        }
    }

    @Test
    public void doubleAddThenRemove() {
        InMemoryNodeEntry example = new InMemoryNodeEntry();
        SkyKey key = ReverseDepsUtilityTest.Key.create(0);
        ReverseDepsUtility.addReverseDeps(example, Collections.singleton(key));
        // Should only fail when we call getReverseDeps().
        ReverseDepsUtility.addReverseDeps(example, Collections.singleton(key));
        ReverseDepsUtility.removeReverseDep(example, key);
        try {
            ReverseDepsUtility.getReverseDeps(example);
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void doubleAddThenRemoveCheckedOnSize() {
        InMemoryNodeEntry example = new InMemoryNodeEntry();
        SkyKey fixedKey = ReverseDepsUtilityTest.Key.create(0);
        SkyKey key = ReverseDepsUtilityTest.Key.create(1);
        ReverseDepsUtility.addReverseDeps(example, ImmutableList.of(fixedKey, key));
        // Should only fail when we reach the limit.
        ReverseDepsUtility.addReverseDeps(example, Collections.singleton(key));
        ReverseDepsUtility.removeReverseDep(example, key);
        ReverseDepsUtility.checkReverseDep(example, fixedKey);
        try {
            ReverseDepsUtility.checkReverseDep(example, fixedKey);
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void addRemoveAdd() {
        InMemoryNodeEntry example = new InMemoryNodeEntry();
        SkyKey fixedKey = ReverseDepsUtilityTest.Key.create(0);
        SkyKey key = ReverseDepsUtilityTest.Key.create(1);
        ReverseDepsUtility.addReverseDeps(example, ImmutableList.of(fixedKey, key));
        ReverseDepsUtility.removeReverseDep(example, key);
        ReverseDepsUtility.addReverseDeps(example, Collections.singleton(key));
        assertThat(ReverseDepsUtility.getReverseDeps(example)).containsExactly(fixedKey, key);
    }

    @Test
    public void testMaybeCheck() {
        InMemoryNodeEntry example = new InMemoryNodeEntry();
        for (int i = 0; i < (numElements); i++) {
            ReverseDepsUtility.addReverseDeps(example, Collections.singleton(ReverseDepsUtilityTest.Key.create(i)));
            // This should always succeed, since the next element is still not present.
            ReverseDepsUtility.maybeCheckReverseDepNotPresent(example, ReverseDepsUtilityTest.Key.create((i + 1)));
        }
        try {
            ReverseDepsUtility.maybeCheckReverseDepNotPresent(example, ReverseDepsUtilityTest.Key.create(0));
            // Should only fail if empty or above the checking threshold.
            assertThat((((numElements) == 0) || ((numElements) >= (MAYBE_CHECK_THRESHOLD)))).isTrue();
        } catch (Exception expected) {
        }
    }

    private static class Key extends AbstractSkyKey<Integer> {
        private static final Interner<ReverseDepsUtilityTest.Key> interner = BlazeInterners.newWeakInterner();

        private Key(Integer arg) {
            super(arg);
        }

        private static ReverseDepsUtilityTest.Key create(Integer arg) {
            return ReverseDepsUtilityTest.Key.interner.intern(new ReverseDepsUtilityTest.Key(arg));
        }

        @Override
        public SkyFunctionName functionName() {
            return FOR_TESTING;
        }
    }
}

