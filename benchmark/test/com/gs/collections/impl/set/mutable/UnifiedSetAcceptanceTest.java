/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.set.mutable;


import com.gs.collections.impl.CollidingInt;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test suite for {@link UnifiedSet}.
 */
public class UnifiedSetAcceptanceTest {
    @Test
    public void testUnifiedSetWithCollisions() {
        for (int removeStride = 2; removeStride < 9; removeStride++) {
            UnifiedSetAcceptanceTest.assertUnifiedSetWithCollisions(0, removeStride);
            UnifiedSetAcceptanceTest.assertUnifiedSetWithCollisions(1, removeStride);
            UnifiedSetAcceptanceTest.assertUnifiedSetWithCollisions(2, removeStride);
            UnifiedSetAcceptanceTest.assertUnifiedSetWithCollisions(3, removeStride);
            UnifiedSetAcceptanceTest.assertUnifiedSetWithCollisions(4, removeStride);
        }
    }

    @Test
    public void testUnifiedSetWithCollisionsAndNullKey() {
        for (int removeStride = 2; removeStride < 9; removeStride++) {
            UnifiedSetAcceptanceTest.setupAndAssertUnifiedSetWithCollisionsAndNullKey(0, removeStride);
            UnifiedSetAcceptanceTest.setupAndAssertUnifiedSetWithCollisionsAndNullKey(1, removeStride);
            UnifiedSetAcceptanceTest.setupAndAssertUnifiedSetWithCollisionsAndNullKey(2, removeStride);
            UnifiedSetAcceptanceTest.setupAndAssertUnifiedSetWithCollisionsAndNullKey(3, removeStride);
            UnifiedSetAcceptanceTest.setupAndAssertUnifiedSetWithCollisionsAndNullKey(4, removeStride);
        }
    }

    @Test
    public void testUnifiedSet() {
        UnifiedSet<Integer> set = UnifiedSet.newSet();
        int size = 100000;
        for (int i = 0; i < size; i++) {
            Assert.assertTrue(set.add(i));
        }
        Verify.assertSize(size, set);
        for (int i = 0; i < size; i++) {
            Verify.assertContains(i, set);
        }
        for (int i = 0; i < size; i += 2) {
            Assert.assertTrue(set.remove(i));
        }
        Verify.assertSize((size / 2), set);
        for (int i = 1; i < size; i += 2) {
            Verify.assertContains(i, set);
        }
    }

    @Test
    public void testUnifiedSetClear() {
        UnifiedSetAcceptanceTest.assertUnifiedSetClear(0);
        UnifiedSetAcceptanceTest.assertUnifiedSetClear(1);
        UnifiedSetAcceptanceTest.assertUnifiedSetClear(2);
        UnifiedSetAcceptanceTest.assertUnifiedSetClear(3);
    }

    @Test
    public void testUnifiedSetForEach() {
        UnifiedSetAcceptanceTest.assertUnifiedSetForEach(0);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEach(1);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEach(2);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEach(3);
    }

    @Test
    public void testUnifiedSetForEachWith() {
        UnifiedSetAcceptanceTest.assertUnifiedSetForEachWith(0);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEachWith(1);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEachWith(2);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEachWith(3);
    }

    @Test
    public void testUnifiedSetForEachWithIndex() {
        UnifiedSetAcceptanceTest.assertUnifiedSetForEachWithIndex(0);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEachWithIndex(1);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEachWithIndex(2);
        UnifiedSetAcceptanceTest.assertUnifiedSetForEachWithIndex(3);
    }

    @Test
    public void testUnifiedSetAddAll() {
        UnifiedSetAcceptanceTest.assertUnifiedSetAddAll(0);
        UnifiedSetAcceptanceTest.assertUnifiedSetAddAll(1);
        UnifiedSetAcceptanceTest.assertUnifiedSetAddAll(2);
        UnifiedSetAcceptanceTest.assertUnifiedSetAddAll(3);
    }

    @Test
    public void testUnifiedSetAddAllWithHashSet() {
        UnifiedSetAcceptanceTest.assertUnifiedSetAddAllWithHashSet(0);
        UnifiedSetAcceptanceTest.assertUnifiedSetAddAllWithHashSet(1);
        UnifiedSetAcceptanceTest.assertUnifiedSetAddAllWithHashSet(2);
        UnifiedSetAcceptanceTest.assertUnifiedSetAddAllWithHashSet(3);
    }

    @Test
    public void testUnifiedSetReplace() {
        UnifiedSetAcceptanceTest.assertUnifiedSetReplace(0);
        UnifiedSetAcceptanceTest.assertUnifiedSetReplace(1);
        UnifiedSetAcceptanceTest.assertUnifiedSetReplace(2);
        UnifiedSetAcceptanceTest.assertUnifiedSetReplace(3);
    }

    @Test
    public void testUnifiedSetRetainAllFromList() {
        UnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromList(0);
        UnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromList(1);
        UnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromList(2);
        UnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromList(3);
    }

    @Test
    public void testUnifiedSetRetainAllFromSet() {
        UnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromSet(0);
        UnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromSet(1);
        UnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromSet(2);
        UnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromSet(3);
    }

    @Test
    public void testUnifiedSetToArray() {
        UnifiedSetAcceptanceTest.runUnifiedSetToArray(0);
        UnifiedSetAcceptanceTest.runUnifiedSetToArray(1);
        UnifiedSetAcceptanceTest.runUnifiedSetToArray(2);
        UnifiedSetAcceptanceTest.runUnifiedSetToArray(3);
    }

    @Test
    public void testUnifiedSetIterator() {
        UnifiedSetAcceptanceTest.runUnifiedSetIterator(0);
        UnifiedSetAcceptanceTest.runUnifiedSetIterator(1);
        UnifiedSetAcceptanceTest.runUnifiedSetIterator(2);
        UnifiedSetAcceptanceTest.runUnifiedSetIterator(3);
    }

    @Test
    public void testUnifiedSetIteratorRemove() {
        for (int removeStride = 2; removeStride < 9; removeStride++) {
            UnifiedSetAcceptanceTest.runUnifiedSetIteratorRemove(0, removeStride);
            UnifiedSetAcceptanceTest.runUnifiedSetIteratorRemove(1, removeStride);
            UnifiedSetAcceptanceTest.runUnifiedSetIteratorRemove(2, removeStride);
            UnifiedSetAcceptanceTest.runUnifiedSetIteratorRemove(3, removeStride);
        }
    }

    @Test
    public void testUnifiedSetIteratorRemoveFlip() {
        for (int removeStride = 2; removeStride < 9; removeStride++) {
            UnifiedSetAcceptanceTest.runUnifiedSetKeySetIteratorRemoveFlip(0, removeStride);
            UnifiedSetAcceptanceTest.runUnifiedSetKeySetIteratorRemoveFlip(1, removeStride);
            UnifiedSetAcceptanceTest.runUnifiedSetKeySetIteratorRemoveFlip(2, removeStride);
            UnifiedSetAcceptanceTest.runUnifiedSetKeySetIteratorRemoveFlip(3, removeStride);
        }
    }

    @Test
    public void testUnifiedSetSerialize() {
        UnifiedSetAcceptanceTest.runUnifiedSetSerialize(0);
        UnifiedSetAcceptanceTest.runUnifiedSetSerialize(1);
        UnifiedSetAcceptanceTest.runUnifiedSetSerialize(2);
        UnifiedSetAcceptanceTest.runUnifiedSetSerialize(3);
    }

    @Test
    public void testUnifiedSetEqualsAndHashCode() {
        UnifiedSetAcceptanceTest.assertUnifiedSetEqualsAndHashCode(0);
        UnifiedSetAcceptanceTest.assertUnifiedSetEqualsAndHashCode(1);
        UnifiedSetAcceptanceTest.assertUnifiedSetEqualsAndHashCode(2);
        UnifiedSetAcceptanceTest.assertUnifiedSetEqualsAndHashCode(3);
    }

    @Test
    public void testUnifiedSetRemoveAll() {
        UnifiedSetAcceptanceTest.runUnifiedSetRemoveAll(0);
        UnifiedSetAcceptanceTest.runUnifiedSetRemoveAll(1);
        UnifiedSetAcceptanceTest.runUnifiedSetRemoveAll(2);
        UnifiedSetAcceptanceTest.runUnifiedSetRemoveAll(3);
    }

    @Test
    public void testUnifiedSetPutDoesNotReplace() {
        this.assertUnifiedSetPutDoesNotReplace(0);
        this.assertUnifiedSetPutDoesNotReplace(1);
        this.assertUnifiedSetPutDoesNotReplace(2);
        this.assertUnifiedSetPutDoesNotReplace(3);
        this.assertUnifiedSetPutDoesNotReplace(4);
    }

    @Test
    public void testUnifiedSetAsPool() {
        this.runUnifiedSetAsPool(0);
        this.runUnifiedSetAsPool(1);
        this.runUnifiedSetAsPool(2);
        this.runUnifiedSetAsPool(3);
    }

    @Test
    public void testUnifiedSetAsPoolRandomInput() {
        this.runUnifiedSetAsPoolRandomInput(0);
        this.runUnifiedSetAsPoolRandomInput(1);
        this.runUnifiedSetAsPoolRandomInput(2);
        this.runUnifiedSetAsPoolRandomInput(3);
    }

    private static final class CollidingIntWithFlag extends CollidingInt {
        private static final long serialVersionUID = 1L;

        private final boolean flag;

        private CollidingIntWithFlag(int value, int shift, boolean flag) {
            super(value, shift);
            this.flag = flag;
        }
    }
}

