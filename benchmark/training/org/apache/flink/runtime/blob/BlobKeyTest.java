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
package org.apache.flink.runtime.blob;


import org.apache.flink.util.AbstractID;
import org.apache.flink.util.TestLogger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static BlobKey.SIZE;


/**
 * This class contains unit tests for the {@link BlobKey} class.
 */
public final class BlobKeyTest extends TestLogger {
    /**
     * The first key array to be used during the unit tests.
     */
    private static final byte[] KEY_ARRAY_1 = new byte[SIZE];

    /**
     * The second key array to be used during the unit tests.
     */
    private static final byte[] KEY_ARRAY_2 = new byte[SIZE];

    /**
     * First byte array to use for the random component of a {@link BlobKey}.
     */
    private static final byte[] RANDOM_ARRAY_1 = new byte[AbstractID.SIZE];

    /**
     * Second byte array to use for the random component of a {@link BlobKey}.
     */
    private static final byte[] RANDOM_ARRAY_2 = new byte[AbstractID.SIZE];

    /* Initialize the key and random arrays. */
    static {
        for (int i = 0; i < (BlobKeyTest.KEY_ARRAY_1.length); ++i) {
            BlobKeyTest.KEY_ARRAY_1[i] = ((byte) (i));
            BlobKeyTest.KEY_ARRAY_2[i] = ((byte) (i + 1));
        }
        for (int i = 0; i < (BlobKeyTest.RANDOM_ARRAY_1.length); ++i) {
            BlobKeyTest.RANDOM_ARRAY_1[i] = ((byte) (i));
            BlobKeyTest.RANDOM_ARRAY_2[i] = ((byte) (i + 1));
        }
    }

    @Test
    public void testCreateKey() {
        BlobKey key = BlobKey.createKey(BlobType.PERMANENT_BLOB, BlobKeyTest.KEY_ARRAY_1);
        BlobKeyTest.verifyType(BlobType.PERMANENT_BLOB, key);
        Assert.assertArrayEquals(BlobKeyTest.KEY_ARRAY_1, key.getHash());
        key = BlobKey.createKey(BlobType.TRANSIENT_BLOB, BlobKeyTest.KEY_ARRAY_1);
        BlobKeyTest.verifyType(BlobType.TRANSIENT_BLOB, key);
        Assert.assertArrayEquals(BlobKeyTest.KEY_ARRAY_1, key.getHash());
    }

    @Test
    public void testSerializationTransient() throws Exception {
        testSerialization(BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testSerializationPermanent() throws Exception {
        testSerialization(BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testEqualsTransient() {
        testEquals(BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testEqualsPermanent() {
        testEquals(BlobType.PERMANENT_BLOB);
    }

    /**
     * Tests the equals method.
     */
    @Test
    public void testEqualsDifferentBlobType() {
        final BlobKey k1 = BlobKey.createKey(BlobType.TRANSIENT_BLOB, BlobKeyTest.KEY_ARRAY_1, BlobKeyTest.RANDOM_ARRAY_1);
        final BlobKey k2 = BlobKey.createKey(BlobType.PERMANENT_BLOB, BlobKeyTest.KEY_ARRAY_1, BlobKeyTest.RANDOM_ARRAY_1);
        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k2.equals(k1));
    }

    @Test
    public void testComparesTransient() {
        testCompares(BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testComparesPermanent() {
        testCompares(BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testComparesDifferentBlobType() {
        final BlobKey k1 = BlobKey.createKey(BlobType.TRANSIENT_BLOB, BlobKeyTest.KEY_ARRAY_1, BlobKeyTest.RANDOM_ARRAY_1);
        final BlobKey k2 = BlobKey.createKey(BlobType.PERMANENT_BLOB, BlobKeyTest.KEY_ARRAY_1, BlobKeyTest.RANDOM_ARRAY_1);
        MatcherAssert.assertThat(k1.compareTo(k2), Matchers.greaterThan(0));
        MatcherAssert.assertThat(k2.compareTo(k1), Matchers.lessThan(0));
    }

    @Test
    public void testStreamsTransient() throws Exception {
        testStreams(BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testStreamsPermanent() throws Exception {
        testStreams(BlobType.PERMANENT_BLOB);
    }
}

