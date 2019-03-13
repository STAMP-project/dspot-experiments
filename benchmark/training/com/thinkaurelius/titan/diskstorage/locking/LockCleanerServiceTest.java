package com.thinkaurelius.titan.diskstorage.locking;


import TimestampProviders.MILLI;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;
import com.thinkaurelius.titan.diskstorage.locking.consistentkey.ConsistentKeyLockerSerializer;
import com.thinkaurelius.titan.diskstorage.locking.consistentkey.LockCleanerService;
import com.thinkaurelius.titan.diskstorage.util.KeyColumn;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayBuffer;
import com.thinkaurelius.titan.diskstorage.util.time.TimestampProviders;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import org.easymock.IMocksControl;
import org.junit.Test;


public class LockCleanerServiceTest {
    private IMocksControl ctrl;

    private IMocksControl relaxedCtrl;

    private KeyColumnValueStore store;

    private StoreTransaction tx;

    private ExecutorService exec;

    private LockCleanerService svc;

    private final ConsistentKeyLockerSerializer codec = new ConsistentKeyLockerSerializer();

    private final KeyColumn kc = new KeyColumn(new StaticArrayBuffer(new byte[]{ ((byte) (1)) }), new StaticArrayBuffer(new byte[]{ ((byte) (2)) }));

    @Test
    public void testCleanCooldownBlocksRapidRequests() {
        final Instant cutoff = Instant.ofEpochMilli(1L);
        svc = new com.thinkaurelius.titan.diskstorage.locking.consistentkey.StandardLockCleanerService(store, codec, exec, Duration.ofSeconds(60L), TimestampProviders.MILLI);
        expect(exec.submit(eq(new com.thinkaurelius.titan.diskstorage.locking.consistentkey.StandardLockCleanerRunnable(store, kc, tx, codec, cutoff, TimestampProviders.MILLI)))).andReturn(null);
        ctrl.replay();
        for (int i = 0; i < 500; i++) {
            svc.clean(kc, cutoff, tx);
        }
    }

    @Test
    public void testCleanCooldownElapses() throws InterruptedException {
        final Instant cutoff = Instant.ofEpochMilli(1L);
        Duration wait = Duration.ofMillis(500L);
        svc = new com.thinkaurelius.titan.diskstorage.locking.consistentkey.StandardLockCleanerService(store, codec, exec, wait, TimestampProviders.MILLI);
        expect(exec.submit(eq(new com.thinkaurelius.titan.diskstorage.locking.consistentkey.StandardLockCleanerRunnable(store, kc, tx, codec, cutoff, TimestampProviders.MILLI)))).andReturn(null);
        expect(exec.submit(eq(new com.thinkaurelius.titan.diskstorage.locking.consistentkey.StandardLockCleanerRunnable(store, kc, tx, codec, cutoff.plusMillis(1), TimestampProviders.MILLI)))).andReturn(null);
        ctrl.replay();
        for (int i = 0; i < 2; i++) {
            svc.clean(kc, cutoff, tx);
        }
        MILLI.sleepFor(wait.plusMillis(1));
        for (int i = 0; i < 2; i++) {
            svc.clean(kc, cutoff.plusMillis(1), tx);
        }
    }
}

