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
package com.gs.collections.impl.concurrent;


import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.CollidingInt;
import com.gs.collections.impl.set.mutable.MultiReaderUnifiedSet;
import com.gs.collections.impl.test.Verify;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class MultiReaderUnifiedSetAcceptanceTest {
    @Test
    public void testUnifiedSet() {
        MultiReaderUnifiedSet<Integer> set = MultiReaderUnifiedSet.newSet();
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
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetClear(0);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetClear(1);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetClear(2);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetClear(3);
    }

    @Test
    public void testUnifiedSetForEach() {
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEach(0);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEach(1);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEach(2);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEach(3);
    }

    @Test
    public void testUnifiedSetForEachWith() {
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEachWith(0);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEachWith(1);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEachWith(2);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEachWith(3);
    }

    @Test
    public void testUnifiedSetForEachWithIndex() {
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEachWithIndex(0);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEachWithIndex(1);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEachWithIndex(2);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetForEachWithIndex(3);
    }

    @Test
    public void testUnifiedSetAddAll() {
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetAddAll(0);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetAddAll(1);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetAddAll(2);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetAddAll(3);
    }

    @Test
    public void testUnifiedSetAddAllWithHashSet() {
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetAddAllWithHashSet(0);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetAddAllWithHashSet(1);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetAddAllWithHashSet(2);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetAddAllWithHashSet(3);
    }

    @Test
    public void testUnifiedSetReplace() {
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetReplace(0);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetReplace(1);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetReplace(2);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetReplace(3);
    }

    @Test
    public void testUnifiedSetRetainAllFromList() {
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromList(0);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromList(1);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromList(2);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromList(3);
    }

    @Test
    public void testUnifiedSetRetainAllFromSet() {
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromSet(0);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromSet(1);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromSet(2);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRetainAllFromSet(3);
    }

    @Test
    public void testUnifiedSetToArray() {
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetToArray(0);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetToArray(1);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetToArray(2);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetToArray(3);
    }

    @Test
    public void testUnifiedSetSerialize() {
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetSerialize(0);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetSerialize(1);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetSerialize(2);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetSerialize(3);
    }

    @Test
    public void testUnifiedSetKeySetToArrayDest() {
        MutableSet<Integer> set = MultiReaderUnifiedSet.newSetWith(1, 2, 3, 4);
        // deliberately to small to force the method to allocate one of the correct size
        Integer[] dest = new Integer[2];
        Integer[] result = set.toArray(dest);
        Verify.assertSize(4, result);
        Arrays.sort(result);
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3, 4 }, result);
    }

    @Test
    public void testUnifiedSetEqualsAndHashCode() {
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetEqualsAndHashCode(0);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetEqualsAndHashCode(1);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetEqualsAndHashCode(2);
        MultiReaderUnifiedSetAcceptanceTest.assertUnifiedSetEqualsAndHashCode(3);
    }

    @Test
    public void testUnifiedSetRemoveAll() {
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRemoveAll(0);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRemoveAll(1);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRemoveAll(2);
        MultiReaderUnifiedSetAcceptanceTest.runUnifiedSetRemoveAll(3);
    }

    @Test
    public void testUnifiedSetPutDoesNotReplace() {
        this.assertUnifiedSetPutDoesNotReplace(0);
        this.assertUnifiedSetPutDoesNotReplace(1);
        this.assertUnifiedSetPutDoesNotReplace(2);
        this.assertUnifiedSetPutDoesNotReplace(3);
        this.assertUnifiedSetPutDoesNotReplace(4);
    }

    private static final class CollidingIntWithFlag extends CollidingInt {
        private static final long serialVersionUID = 1L;

        private final boolean flag;

        private CollidingIntWithFlag(int value, int shift, boolean flag) {
            super(value, shift);
            this.flag = flag;
        }

        public boolean isFlag() {
            return this.flag;
        }
    }
}

