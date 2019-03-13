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
package com.hazelcast.util;


import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PreconditionsTest {
    // =====================================================
    @Test
    public void checkNotNull_whenNull() {
        String msg = "Can't be null";
        try {
            Preconditions.checkNotNull(null, msg);
            Assert.fail();
        } catch (NullPointerException expected) {
            Assert.assertEquals(msg, expected.getMessage());
        }
    }

    @Test
    public void checkNotNull_whenNotNull() {
        Object o = "foobar";
        Object result = Preconditions.checkNotNull(o, "");
        Assert.assertSame(o, result);
    }

    // =====================================================
    @Test
    public void checkBackupCount() {
        checkBackupCount((-1), 0, false);
        checkBackupCount((-1), (-1), false);
        checkBackupCount(0, (-1), false);
        checkBackupCount(0, 0, true);
        checkBackupCount(0, 1, true);
        checkBackupCount(1, 1, true);
        checkBackupCount(2, 1, true);
        checkBackupCount(1, 2, true);
        checkBackupCount(InternalPartition.MAX_BACKUP_COUNT, 0, true);
        checkBackupCount(0, InternalPartition.MAX_BACKUP_COUNT, true);
        checkBackupCount(InternalPartition.MAX_BACKUP_COUNT, 1, false);
        checkBackupCount(((InternalPartition.MAX_BACKUP_COUNT) + 1), 0, false);
        checkBackupCount(0, ((InternalPartition.MAX_BACKUP_COUNT) + 1), false);
    }

    @Test
    public void checkAsyncBackupCount() {
        checkAsyncBackupCount((-1), 0, false);
        checkAsyncBackupCount((-1), (-1), false);
        checkAsyncBackupCount(0, (-1), false);
        checkAsyncBackupCount(0, 0, true);
        checkAsyncBackupCount(0, 1, true);
        checkAsyncBackupCount(1, 1, true);
        checkAsyncBackupCount(2, 1, true);
        checkAsyncBackupCount(1, 2, true);
        checkAsyncBackupCount(InternalPartition.MAX_BACKUP_COUNT, 0, true);
        checkAsyncBackupCount(0, InternalPartition.MAX_BACKUP_COUNT, true);
        checkAsyncBackupCount(InternalPartition.MAX_BACKUP_COUNT, 1, false);
        checkAsyncBackupCount(((InternalPartition.MAX_BACKUP_COUNT) + 1), 0, false);
        checkAsyncBackupCount(0, ((InternalPartition.MAX_BACKUP_COUNT) + 1), false);
    }

    // =====================================================
    @Test
    public void checkNegative_long() {
        checkNegative_long((-1), true);
        checkNegative_long(0, false);
        checkNegative_long(1, false);
    }

    @Test
    public void checkNotNegative_long() {
        checkNotNegative_long((-1), false);
        checkNotNegative_long(0, true);
        checkNotNegative_long(1, true);
    }

    @Test
    public void checkNotNegative_int() {
        checkNotNegative_int((-1), false);
        checkNotNegative_int(0, true);
        checkNotNegative_int(1, true);
    }

    @Test
    public void checkPositive_long() {
        checkPositive_long((-1), false);
        checkPositive_long(0, false);
        checkPositive_long(1, true);
    }

    @Test
    public void checkPositive_int() {
        checkPositive_int((-1), false);
        checkPositive_int(0, false);
        checkPositive_int(1, true);
    }

    @Test
    public void checkHasText() {
        checkHasText(null, false);
        checkHasText("", false);
        checkHasText("foobar", true);
    }

    @Test
    public void test_checkInstanceOf() throws Exception {
        Number value = Preconditions.checkInstanceOf(Number.class, Integer.MAX_VALUE, "argumentName");
        Assert.assertEquals(("Returned value should be " + (Integer.MAX_VALUE)), Integer.MAX_VALUE, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkInstanceOf_whenSuppliedObjectIsNotInstanceOfExpectedType() throws Exception {
        Preconditions.checkInstanceOf(Integer.class, BigInteger.ONE, "argumentName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkInstanceOf_withNullType() throws Exception {
        Preconditions.checkInstanceOf(null, Integer.MAX_VALUE, "argumentName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkInstanceOf_withNullObject() throws Exception {
        Preconditions.checkInstanceOf(Number.class, null, "argumentName");
    }

    @Test
    public void test_checkNotInstanceOf() throws Exception {
        BigInteger value = Preconditions.checkNotInstanceOf(Integer.class, BigInteger.ONE, "argumentName");
        Assert.assertEquals("Returned value should be equal to BigInteger.ONE", BigInteger.ONE, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkNotInstanceOf_whenSuppliedObjectIsInstanceOfExpectedType() throws Exception {
        Preconditions.checkNotInstanceOf(Integer.class, Integer.MAX_VALUE, "argumentName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkNotInstanceOf_withNullType() throws Exception {
        Preconditions.checkNotInstanceOf(null, BigInteger.ONE, "argumentName");
    }

    @Test
    public void test_checkNotInstanceOf_withNullObject() throws Exception {
        Object value = Preconditions.checkNotInstanceOf(Integer.class, null, "argumentName");
        Assert.assertNull(value);
    }

    @Test
    public void test_checkFalse_whenFalse() throws Exception {
        Preconditions.checkFalse(false, "comparison cannot be true");
    }

    @Test
    public void test_checkFalse_whenTrue() throws Exception {
        String errorMessage = "foobar";
        try {
            Preconditions.checkFalse(true, errorMessage);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertSame(errorMessage, e.getMessage());
        }
    }

    @Test
    public void test_checkTrue_whenTrue() throws Exception {
        Preconditions.checkTrue(true, "must be true");
    }

    @Test
    public void test_checkTrue_whenFalse() throws Exception {
        String errorMessage = "foobar";
        try {
            Preconditions.checkTrue(false, errorMessage);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertSame(errorMessage, e.getMessage());
        }
    }

    @Test
    public void test_checkFalse() throws Exception {
        try {
            Preconditions.checkFalse(Boolean.FALSE, "comparison cannot be true");
        } catch (Exception e) {
            Assert.fail(("checkFalse method should not throw this exception " + e));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkFalse_whenComparisonTrue() throws Exception {
        Preconditions.checkFalse(Boolean.TRUE, "comparison cannot be true");
    }

    @Test(expected = NoSuchElementException.class)
    public void test_hasNextThrowsException_whenEmptyIteratorGiven() throws Exception {
        Preconditions.checkHasNext(Collections.emptyList().iterator(), "");
    }

    @Test
    public void test_hasNextReturnsIterator_whenNonEmptyIteratorGiven() throws Exception {
        Iterator<Integer> iterator = Arrays.asList(1, 2).iterator();
        Assert.assertEquals(iterator, Preconditions.checkHasNext(iterator, ""));
    }
}

