/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.cache.disk;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Test for the score-based eviction comparator.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ System.class })
public class ScoreBasedEvictionComparatorSupplierTest {
    private static final long RANDOM_SEED = 42;

    private List<DiskStorage.Entry> entries;

    @Test
    public void testTimestampOnlyOrder() {
        doTest(1.0F, 0.0F);
        for (int i = 0; i < ((entries.size()) - 1); i++) {
            Assert.assertTrue(((entries.get(i).getTimestamp()) < (entries.get((i + 1)).getTimestamp())));
        }
    }

    @Test
    public void testSizeOnlyOrder() {
        doTest(0.0F, 1.0F);
        for (int i = 0; i < ((entries.size()) - 1); i++) {
            Assert.assertTrue(((entries.get(i).getSize()) > (entries.get((i + 1)).getSize())));
        }
    }

    @Test
    public void testEqualOrder() {
        doTest(1.0F, 1.0F);
    }

    @Test
    public void testWeightedOrder() {
        doTest(2.0F, 3.0F);
    }
}

