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
package com.hazelcast.test;


import LifecycleEvent.LifecycleState;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.merge.MergingValue;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;


/**
 * A support class for high-level split-brain tests.
 * <p>
 * Forms a cluster, creates a split-brain situation and then heals the cluster again.
 * <p>
 * Implementing tests are supposed to subclass this class and use its hooks to be notified about state transitions.
 * See {@link #onBeforeSplitBrainCreated(HazelcastInstance[])},
 * {@link #onAfterSplitBrainCreated(HazelcastInstance[], HazelcastInstance[])}
 * and {@link #onAfterSplitBrainHealed(HazelcastInstance[])}
 * <p>
 * The current implementation always isolates the first member of the cluster, but it should be simple to customize this
 * class to support mode advanced split-brain scenarios.
 * <p>
 * See {@link SplitBrainTestSupportTest} for an example test.
 */
@SuppressWarnings({ "RedundantThrows", "WeakerAccess" })
public abstract class SplitBrainTestSupport extends HazelcastTestSupport {
    protected TestHazelcastInstanceFactory factory;

    // per default the second half should merge into the first half
    private static final int[] DEFAULT_BRAINS = new int[]{ 2, 1 };

    private static final int DEFAULT_ITERATION_COUNT = 1;

    private HazelcastInstance[] instances;

    private int[] brains;

    /**
     * If new nodes have been created during split brain via {@link #createHazelcastInstanceInBrain(int)}, then their joiners
     * are initialized with the other brain's addresses being blacklisted.
     */
    private boolean unblacklistHint = false;

    private static final SplitBrainTestSupport.SplitBrainAction BLOCK_COMMUNICATION = new SplitBrainTestSupport.SplitBrainAction() {
        @Override
        public void apply(HazelcastInstance h1, HazelcastInstance h2) {
            SplitBrainTestSupport.blockCommunicationBetween(h1, h2);
        }
    };

    private static final SplitBrainTestSupport.SplitBrainAction UNBLOCK_COMMUNICATION = new SplitBrainTestSupport.SplitBrainAction() {
        @Override
        public void apply(HazelcastInstance h1, HazelcastInstance h2) {
            SplitBrainTestSupport.unblockCommunicationBetween(h1, h2);
        }
    };

    private static final SplitBrainTestSupport.SplitBrainAction CLOSE_CONNECTION = new SplitBrainTestSupport.SplitBrainAction() {
        @Override
        public void apply(HazelcastInstance h1, HazelcastInstance h2) {
            HazelcastTestSupport.closeConnectionBetween(h1, h2);
        }
    };

    private static final SplitBrainTestSupport.SplitBrainAction UNBLACKLIST_MEMBERS = new SplitBrainTestSupport.SplitBrainAction() {
        @Override
        public void apply(HazelcastInstance h1, HazelcastInstance h2) {
            SplitBrainTestSupport.unblacklistJoinerBetween(h1, h2);
        }
    };

    @Test
    public void testSplitBrain() throws Exception {
        for (int i = 0; i < (iterations()); i++) {
            doIteration();
        }
    }

    private interface SplitBrainAction {
        void apply(HazelcastInstance h1, HazelcastInstance h2);
    }

    /**
     * Contains the {@link HazelcastInstance} from the both sub-clusters (first and second brain).
     */
    protected static class Brains {
        private final HazelcastInstance[] firstHalf;

        private final HazelcastInstance[] secondHalf;

        private Brains(HazelcastInstance[] firstHalf, HazelcastInstance[] secondHalf) {
            this.firstHalf = firstHalf;
            this.secondHalf = secondHalf;
        }

        public HazelcastInstance[] getFirstHalf() {
            return firstHalf;
        }

        public HazelcastInstance[] getSecondHalf() {
            return secondHalf;
        }
    }

    /**
     * Listener to wait for the split-brain healing to be finished.
     */
    protected static class MergeLifecycleListener implements LifecycleListener {
        private final CountDownLatch latch;

        public MergeLifecycleListener(int mergingClusterSize) {
            latch = new CountDownLatch(mergingClusterSize);
        }

        @Override
        public void stateChanged(LifecycleEvent event) {
            if ((event.getState()) == (LifecycleState.MERGED)) {
                latch.countDown();
            }
        }

        public void await() {
            HazelcastTestSupport.assertOpenEventually(latch);
        }
    }

    /**
     * Always returns {@code null} as merged value.
     * <p>
     * Used to test the removal of all values from a data structure.
     */
    protected static class RemoveValuesMergePolicy implements SplitBrainMergePolicy<Object, MergingValue<Object>> {
        @Override
        public Object merge(MergingValue<Object> mergingValue, MergingValue<Object> existingValue) {
            return null;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    /**
     * Always returns {@link Math#PI} as merged value.
     * <p>
     * Used to test that data structures can deal with user created data in OBJECT format.
     */
    protected static class ReturnPiMergePolicy implements SplitBrainMergePolicy<Object, MergingValue<Object>> {
        @Override
        public Object merge(MergingValue<Object> mergingValue, MergingValue<Object> existingValue) {
            return Math.PI;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    /**
     * Always returns a collection of {@link Math#PI} digits as merged value.
     * <p>
     * Used to test that data structures can deal with user created data in OBJECT format.
     */
    protected static class ReturnPiCollectionMergePolicy implements SplitBrainMergePolicy<Collection<Object>, MergingValue<Collection<Object>>> {
        private static final Collection<Object> PI_COLLECTION;

        private static final Set<Object> PI_SET;

        static {
            PI_COLLECTION = new ArrayList<Object>(5);
            SplitBrainTestSupport.ReturnPiCollectionMergePolicy.PI_COLLECTION.add(3);
            SplitBrainTestSupport.ReturnPiCollectionMergePolicy.PI_COLLECTION.add(1);
            SplitBrainTestSupport.ReturnPiCollectionMergePolicy.PI_COLLECTION.add(4);
            SplitBrainTestSupport.ReturnPiCollectionMergePolicy.PI_COLLECTION.add(1);
            SplitBrainTestSupport.ReturnPiCollectionMergePolicy.PI_COLLECTION.add(5);
            PI_SET = new HashSet<Object>(SplitBrainTestSupport.ReturnPiCollectionMergePolicy.PI_COLLECTION);
        }

        @Override
        public Collection<Object> merge(MergingValue<Collection<Object>> mergingValue, MergingValue<Collection<Object>> existingValue) {
            return SplitBrainTestSupport.ReturnPiCollectionMergePolicy.PI_COLLECTION;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    /**
     * Merges only {@link Integer} values of the given values (preferring the merging value).
     * <p>
     * Used to test the deserialization of values.
     */
    protected static class MergeIntegerValuesMergePolicy<V, T extends MergingValue<V>> implements SplitBrainMergePolicy<V, T> {
        @Override
        public V merge(T mergingValue, T existingValue) {
            if ((getDeserializedValue()) instanceof Integer) {
                return getValue();
            }
            if ((existingValue != null) && ((getDeserializedValue()) instanceof Integer)) {
                return getValue();
            }
            return null;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    /**
     * Merges only {@link Integer} values of the given collections.
     * <p>
     * Used to test the deserialization of values.
     */
    protected static class MergeCollectionOfIntegerValuesMergePolicy implements SplitBrainMergePolicy<Collection<Object>, MergingValue<Collection<Object>>> {
        @Override
        public Collection<Object> merge(MergingValue<Collection<Object>> mergingValue, MergingValue<Collection<Object>> existingValue) {
            Collection<Object> result = new ArrayList<Object>();
            for (Object value : mergingValue.<Collection<Object>>getDeserializedValue()) {
                if (value instanceof Integer) {
                    result.add(value);
                }
            }
            if (existingValue != null) {
                for (Object value : existingValue.<Collection<Object>>getDeserializedValue()) {
                    if (value instanceof Integer) {
                        result.add(value);
                    }
                }
            }
            return result;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }
}

