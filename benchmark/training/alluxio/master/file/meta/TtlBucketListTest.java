/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master.file.meta;


import com.google.common.collect.Sets;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Unit tests for {@link TtlBucketList}.
 */
public final class TtlBucketListTest {
    private static final long BUCKET_INTERVAL = 10;

    private static final long BUCKET1_START = 0;

    private static final long BUCKET1_END = (TtlBucketListTest.BUCKET1_START) + (TtlBucketListTest.BUCKET_INTERVAL);

    private static final long BUCKET2_START = TtlBucketListTest.BUCKET1_END;

    private static final long BUCKET2_END = (TtlBucketListTest.BUCKET2_START) + (TtlBucketListTest.BUCKET_INTERVAL);

    private static final Inode BUCKET1_FILE1 = TtlTestUtils.createFileWithIdAndTtl(0, TtlBucketListTest.BUCKET1_START);

    private static final Inode BUCKET1_FILE2 = TtlTestUtils.createFileWithIdAndTtl(1, ((TtlBucketListTest.BUCKET1_END) - 1));

    private static final Inode BUCKET2_FILE = TtlTestUtils.createFileWithIdAndTtl(2, TtlBucketListTest.BUCKET2_START);

    private TtlBucketList mBucketList;

    @ClassRule
    public static TtlIntervalRule sTtlIntervalRule = new TtlIntervalRule(TtlBucketListTest.BUCKET_INTERVAL);

    /**
     * Tests the {@link TtlBucketList#insert(Inode)} method.
     */
    @Test
    public void insert() {
        // No bucket should expire.
        List<TtlBucket> expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET1_START);
        Assert.assertTrue(expired.isEmpty());
        mBucketList.insert(TtlBucketListTest.BUCKET1_FILE1);
        // The first bucket should expire.
        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET1_END);
        assertExpired(expired, 0, TtlBucketListTest.BUCKET1_FILE1);
        mBucketList.insert(TtlBucketListTest.BUCKET1_FILE2);
        // Only the first bucket should expire.
        for (long end = TtlBucketListTest.BUCKET2_START; end < (TtlBucketListTest.BUCKET2_END); end++) {
            expired = getSortedExpiredBuckets(end);
            assertExpired(expired, 0, TtlBucketListTest.BUCKET1_FILE1, TtlBucketListTest.BUCKET1_FILE2);
        }
        mBucketList.insert(TtlBucketListTest.BUCKET2_FILE);
        // All buckets should expire.
        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET2_END);
        assertExpired(expired, 0, TtlBucketListTest.BUCKET1_FILE1, TtlBucketListTest.BUCKET1_FILE2);
        assertExpired(expired, 1, TtlBucketListTest.BUCKET2_FILE);
    }

    /**
     * Tests the {@link TtlBucketList#remove(InodeView)} method.
     */
    @Test
    public void remove() {
        mBucketList.insert(TtlBucketListTest.BUCKET1_FILE1);
        mBucketList.insert(TtlBucketListTest.BUCKET1_FILE2);
        mBucketList.insert(TtlBucketListTest.BUCKET2_FILE);
        List<TtlBucket> expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET1_END);
        assertExpired(expired, 0, TtlBucketListTest.BUCKET1_FILE1, TtlBucketListTest.BUCKET1_FILE2);
        mBucketList.remove(TtlBucketListTest.BUCKET1_FILE1);
        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET1_END);
        // Only the first bucket should expire, and there should be only one BUCKET1_FILE2 in it.
        assertExpired(expired, 0, TtlBucketListTest.BUCKET1_FILE2);
        mBucketList.remove(TtlBucketListTest.BUCKET1_FILE2);
        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET1_END);
        // Only the first bucket should expire, and there should be no files in it.
        assertExpired(expired, 0);// nothing in bucket 0.

        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET2_END);
        // All buckets should expire.
        assertExpired(expired, 0);// nothing in bucket 0.

        assertExpired(expired, 1, TtlBucketListTest.BUCKET2_FILE);
        // Remove bucket 0.
        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET1_END);
        mBucketList.removeBuckets(Sets.newHashSet(expired));
        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET2_END);
        // The only remaining bucket is bucket 1, it should expire.
        assertExpired(expired, 0, TtlBucketListTest.BUCKET2_FILE);
        mBucketList.remove(TtlBucketListTest.BUCKET2_FILE);
        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET2_END);
        assertExpired(expired, 0);// nothing in bucket.

        mBucketList.removeBuckets(Sets.newHashSet(expired));
        // No bucket should exist now.
        expired = getSortedExpiredBuckets(TtlBucketListTest.BUCKET2_END);
        Assert.assertEquals(0, expired.size());
    }
}

