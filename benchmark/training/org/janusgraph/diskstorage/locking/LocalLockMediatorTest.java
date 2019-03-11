/**
 * Copyright 2017 JanusGraph Authors
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
package org.janusgraph.diskstorage.locking;


import java.time.Instant;
import org.janusgraph.diskstorage.StaticBuffer;
import org.janusgraph.diskstorage.locking.consistentkey.ExpectedValueCheckingTransaction;
import org.janusgraph.diskstorage.util.KeyColumn;
import org.janusgraph.diskstorage.util.StaticArrayBuffer;
import org.janusgraph.diskstorage.util.time.TimestampProvider;
import org.janusgraph.diskstorage.util.time.TimestampProviders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class LocalLockMediatorTest {
    private static final String LOCK_NAMESPACE = "test";

    private static final StaticBuffer LOCK_ROW = StaticArrayBuffer.of(new byte[]{ 1 });

    private static final StaticBuffer LOCK_COL = StaticArrayBuffer.of(new byte[]{ 1 });

    private static final KeyColumn kc = new KeyColumn(LocalLockMediatorTest.LOCK_ROW, LocalLockMediatorTest.LOCK_COL);

    // private static final long LOCK_EXPIRATION_TIME_MS = 1;
    // private static final long SLEEP_MS = LOCK_EXPIRATION_TIME_MS * 1000;
    private static final ExpectedValueCheckingTransaction mockTx1 = Mockito.mock(ExpectedValueCheckingTransaction.class);

    private static final ExpectedValueCheckingTransaction mockTx2 = Mockito.mock(ExpectedValueCheckingTransaction.class);

    @Test
    public void testLockExpiration() {
        TimestampProvider times = TimestampProviders.MICRO;
        LocalLockMediator<ExpectedValueCheckingTransaction> llm = new LocalLockMediator(LocalLockMediatorTest.LOCK_NAMESPACE, times);
        Assertions.assertTrue(llm.lock(LocalLockMediatorTest.kc, LocalLockMediatorTest.mockTx1, Instant.EPOCH));
        Assertions.assertTrue(llm.lock(LocalLockMediatorTest.kc, LocalLockMediatorTest.mockTx2, Instant.MAX));
        llm = new LocalLockMediator(LocalLockMediatorTest.LOCK_NAMESPACE, times);
        Assertions.assertTrue(llm.lock(LocalLockMediatorTest.kc, LocalLockMediatorTest.mockTx1, Instant.MAX));
        Assertions.assertFalse(llm.lock(LocalLockMediatorTest.kc, LocalLockMediatorTest.mockTx2, Instant.MAX));
    }
}

