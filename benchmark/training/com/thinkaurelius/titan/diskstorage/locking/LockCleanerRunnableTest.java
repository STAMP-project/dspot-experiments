package com.thinkaurelius.titan.diskstorage.locking;


import TimestampProviders.MILLI;
import com.google.common.collect.ImmutableList;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.Entry;
import com.thinkaurelius.titan.diskstorage.EntryList;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeySliceQuery;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;
import com.thinkaurelius.titan.diskstorage.locking.consistentkey.ConsistentKeyLockerSerializer;
import com.thinkaurelius.titan.diskstorage.locking.consistentkey.StandardLockCleanerRunnable;
import com.thinkaurelius.titan.diskstorage.util.BufferUtil;
import com.thinkaurelius.titan.diskstorage.util.time.TimestampProviders;
import java.time.Instant;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


public class LockCleanerRunnableTest {
    private IMocksControl ctrl;

    private IMocksControl relaxedCtrl;

    private StandardLockCleanerRunnable del;

    private KeyColumnValueStore store;

    private StoreTransaction tx;

    private final ConsistentKeyLockerSerializer codec = new ConsistentKeyLockerSerializer();

    private final KeyColumn kc = new KeyColumn(new StaticArrayBuffer(new byte[]{ ((byte) (1)) }), new StaticArrayBuffer(new byte[]{ ((byte) (2)) }));

    private final StaticBuffer key = codec.toLockKey(kc.getKey(), kc.getColumn());

    private final KeySliceQuery ksq = new KeySliceQuery(key, LOCK_COL_START, LOCK_COL_END);

    private final StaticBuffer defaultLockRid = new StaticArrayBuffer(new byte[]{ ((byte) (32)) });

    /**
     * Simplest case test of the lock cleaner.
     */
    @Test
    public void testDeleteSingleLock() throws BackendException {
        Instant now = Instant.ofEpochMilli(1L);
        Entry expiredLockCol = StaticArrayEntry.of(codec.toLockCol(now, defaultLockRid, MILLI), BufferUtil.getIntBuffer(0));
        EntryList expiredSingleton = StaticArrayEntryList.of(expiredLockCol);
        now = now.plusMillis(1);
        del = new StandardLockCleanerRunnable(store, kc, tx, codec, now, TimestampProviders.MILLI);
        expect(store.getSlice(eq(ksq), eq(tx))).andReturn(expiredSingleton);
        store.mutate(eq(key), eq(ImmutableList.<Entry>of()), eq(ImmutableList.<StaticBuffer>of(expiredLockCol.getColumn())), anyObject(StoreTransaction.class));
        ctrl.replay();
        del.run();
    }

    /**
     * Test the cleaner against a set of locks where some locks have timestamps
     * before the cutoff and others have timestamps after the cutoff. One lock
     * has a timestamp equal to the cutoff.
     */
    @Test
    public void testDeletionWithExpiredAndValidLocks() throws BackendException {
        final int lockCount = 10;
        final int expiredCount = 3;
        Assert.assertTrue(((expiredCount + 2) <= lockCount));
        final long timeIncr = 1L;
        final Instant timeStart = Instant.EPOCH;
        final Instant timeCutoff = timeStart.plusMillis((expiredCount * timeIncr));
        ImmutableList.Builder<Entry> locksBuilder = ImmutableList.builder();
        ImmutableList.Builder<Entry> delsBuilder = ImmutableList.builder();
        for (int i = 0; i < lockCount; i++) {
            final Instant ts = timeStart.plusMillis((timeIncr * i));
            Entry lock = StaticArrayEntry.of(codec.toLockCol(ts, defaultLockRid, MILLI), BufferUtil.getIntBuffer(0));
            if (ts.isBefore(timeCutoff)) {
                delsBuilder.add(lock);
            }
            locksBuilder.add(lock);
        }
        EntryList locks = StaticArrayEntryList.of(locksBuilder.build());
        EntryList dels = StaticArrayEntryList.of(delsBuilder.build());
        Assert.assertTrue((expiredCount == (dels.size())));
        del = new StandardLockCleanerRunnable(store, kc, tx, codec, timeCutoff, TimestampProviders.MILLI);
        expect(store.getSlice(eq(ksq), eq(tx))).andReturn(locks);
        store.mutate(eq(key), eq(ImmutableList.<Entry>of()), eq(LockCleanerRunnableTest.columnsOf(dels)), anyObject(StoreTransaction.class));
        ctrl.replay();
        del.run();
    }

    /**
     * Locks with timestamps equal to or numerically greater than the cleaner
     * cutoff timestamp must be preserved. Test that the cleaner reads locks by
     * slicing the store and then does <b>not</b> attempt to write.
     */
    @Test
    public void testPreservesLocksAtOrAfterCutoff() throws BackendException {
        final Instant cutoff = Instant.ofEpochMilli(10L);
        Entry currentLock = StaticArrayEntry.of(codec.toLockCol(cutoff, defaultLockRid, MILLI), BufferUtil.getIntBuffer(0));
        Entry futureLock = StaticArrayEntry.of(codec.toLockCol(cutoff.plusMillis(1), defaultLockRid, MILLI), BufferUtil.getIntBuffer(0));
        EntryList locks = StaticArrayEntryList.of(currentLock, futureLock);
        // Don't increment cutoff: lockCol is exactly at the cutoff timestamp
        del = new StandardLockCleanerRunnable(store, kc, tx, codec, cutoff, TimestampProviders.MILLI);
        expect(store.getSlice(eq(ksq), eq(tx))).andReturn(locks);
        ctrl.replay();
        del.run();
    }
}

