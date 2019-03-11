/**
 * Copyright (C) 2017 The Android Open Source Project
 *
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
 */
package com.google.android.exoplayer2.source;


import C.INDEX_UNSET;
import com.google.android.exoplayer2.source.ShuffleOrder.DefaultShuffleOrder;
import com.google.android.exoplayer2.source.ShuffleOrder.UnshuffledShuffleOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link ShuffleOrder}.
 */
@RunWith(RobolectricTestRunner.class)
public final class ShuffleOrderTest {
    public static final long RANDOM_SEED = 1234567890L;

    @Test
    public void testDefaultShuffleOrder() {
        ShuffleOrderTest.assertShuffleOrderCorrectness(new DefaultShuffleOrder(0, ShuffleOrderTest.RANDOM_SEED), 0);
        ShuffleOrderTest.assertShuffleOrderCorrectness(new DefaultShuffleOrder(1, ShuffleOrderTest.RANDOM_SEED), 1);
        ShuffleOrderTest.assertShuffleOrderCorrectness(new DefaultShuffleOrder(5, ShuffleOrderTest.RANDOM_SEED), 5);
        for (int initialLength = 0; initialLength < 4; initialLength++) {
            for (int insertionPoint = 0; insertionPoint <= initialLength; insertionPoint += 2) {
                ShuffleOrderTest.testCloneAndInsert(new DefaultShuffleOrder(initialLength, ShuffleOrderTest.RANDOM_SEED), insertionPoint, 0);
                ShuffleOrderTest.testCloneAndInsert(new DefaultShuffleOrder(initialLength, ShuffleOrderTest.RANDOM_SEED), insertionPoint, 1);
                ShuffleOrderTest.testCloneAndInsert(new DefaultShuffleOrder(initialLength, ShuffleOrderTest.RANDOM_SEED), insertionPoint, 5);
            }
        }
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(5, ShuffleOrderTest.RANDOM_SEED), 0, 1);
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(5, ShuffleOrderTest.RANDOM_SEED), 2, 3);
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(5, ShuffleOrderTest.RANDOM_SEED), 4, 5);
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(1, ShuffleOrderTest.RANDOM_SEED), 0, 1);
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(1000, ShuffleOrderTest.RANDOM_SEED), 0, 1000);
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(1000, ShuffleOrderTest.RANDOM_SEED), 0, 999);
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(1000, ShuffleOrderTest.RANDOM_SEED), 0, 500);
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(1000, ShuffleOrderTest.RANDOM_SEED), 100, 600);
        ShuffleOrderTest.testCloneAndRemove(new DefaultShuffleOrder(1000, ShuffleOrderTest.RANDOM_SEED), 500, 1000);
    }

    @Test
    public void testDefaultShuffleOrderSideloaded() {
        int[] shuffledIndices = new int[]{ 2, 1, 0, 4, 3 };
        ShuffleOrder shuffleOrder = new DefaultShuffleOrder(shuffledIndices, ShuffleOrderTest.RANDOM_SEED);
        assertThat(shuffleOrder.getFirstIndex()).isEqualTo(2);
        assertThat(shuffleOrder.getLastIndex()).isEqualTo(3);
        for (int i = 0; i < 4; i++) {
            assertThat(shuffleOrder.getNextIndex(shuffledIndices[i])).isEqualTo(shuffledIndices[(i + 1)]);
        }
        assertThat(shuffleOrder.getNextIndex(3)).isEqualTo(INDEX_UNSET);
        for (int i = 4; i > 0; i--) {
            assertThat(shuffleOrder.getPreviousIndex(shuffledIndices[i])).isEqualTo(shuffledIndices[(i - 1)]);
        }
        assertThat(shuffleOrder.getPreviousIndex(2)).isEqualTo(INDEX_UNSET);
    }

    @Test
    public void testUnshuffledShuffleOrder() {
        ShuffleOrderTest.assertShuffleOrderCorrectness(new UnshuffledShuffleOrder(0), 0);
        ShuffleOrderTest.assertShuffleOrderCorrectness(new UnshuffledShuffleOrder(1), 1);
        ShuffleOrderTest.assertShuffleOrderCorrectness(new UnshuffledShuffleOrder(5), 5);
        for (int initialLength = 0; initialLength < 4; initialLength++) {
            for (int insertionPoint = 0; insertionPoint <= initialLength; insertionPoint += 2) {
                ShuffleOrderTest.testCloneAndInsert(new UnshuffledShuffleOrder(initialLength), insertionPoint, 0);
                ShuffleOrderTest.testCloneAndInsert(new UnshuffledShuffleOrder(initialLength), insertionPoint, 1);
                ShuffleOrderTest.testCloneAndInsert(new UnshuffledShuffleOrder(initialLength), insertionPoint, 5);
            }
        }
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(5), 0, 1);
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(5), 2, 3);
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(5), 4, 5);
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(1), 0, 1);
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(1000), 0, 1000);
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(1000), 0, 999);
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(1000), 0, 500);
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(1000), 100, 600);
        ShuffleOrderTest.testCloneAndRemove(new UnshuffledShuffleOrder(1000), 500, 1000);
    }

    @Test
    public void testUnshuffledShuffleOrderIsUnshuffled() {
        ShuffleOrder shuffleOrder = new UnshuffledShuffleOrder(5);
        assertThat(shuffleOrder.getFirstIndex()).isEqualTo(0);
        assertThat(shuffleOrder.getLastIndex()).isEqualTo(4);
        for (int i = 0; i < 4; i++) {
            assertThat(shuffleOrder.getNextIndex(i)).isEqualTo((i + 1));
        }
    }
}

