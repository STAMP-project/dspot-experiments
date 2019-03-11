package com.thinkaurelius.titan.diskstorage.locking;


import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.locking.consistentkey.ExpectedValueCheckingTransaction;
import com.thinkaurelius.titan.diskstorage.util.KeyColumn;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayBuffer;
import com.thinkaurelius.titan.diskstorage.util.time.TimestampProvider;
import com.thinkaurelius.titan.diskstorage.util.time.TimestampProviders;
import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;
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
    public void testLockExpiration() throws InterruptedException {
        TimestampProvider times = TimestampProviders.MICRO;
        LocalLockMediator<ExpectedValueCheckingTransaction> llm = new LocalLockMediator<ExpectedValueCheckingTransaction>(LocalLockMediatorTest.LOCK_NAMESPACE, times);
        Assert.assertTrue(llm.lock(LocalLockMediatorTest.kc, LocalLockMediatorTest.mockTx1, Instant.EPOCH));
        Assert.assertTrue(llm.lock(LocalLockMediatorTest.kc, LocalLockMediatorTest.mockTx2, Instant.MAX));
        llm = new LocalLockMediator<ExpectedValueCheckingTransaction>(LocalLockMediatorTest.LOCK_NAMESPACE, times);
        Assert.assertTrue(llm.lock(LocalLockMediatorTest.kc, LocalLockMediatorTest.mockTx1, Instant.MAX));
        Assert.assertFalse(llm.lock(LocalLockMediatorTest.kc, LocalLockMediatorTest.mockTx2, Instant.MAX));
    }
}

