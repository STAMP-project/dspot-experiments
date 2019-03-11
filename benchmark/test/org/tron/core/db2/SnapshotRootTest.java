package org.tron.core.db2;


import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.SessionOptional;
import org.tron.core.capsule.ProtoCapsule;
import org.tron.core.db2.core.ISession;
import org.tron.core.db2.core.Snapshot;
import org.tron.core.db2.core.SnapshotManager;
import org.tron.core.db2.core.SnapshotRoot;


public class SnapshotRootTest {
    private RevokingDbWithCacheNewValueTest.TestRevokingTronStore tronDatabase;

    private TronApplicationContext context;

    private Application appT;

    private SnapshotManager revokingDatabase;

    @Test
    public synchronized void testRemove() {
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest("test".getBytes());
        tronDatabase = new RevokingDbWithCacheNewValueTest.TestRevokingTronStore("testSnapshotRoot-testRemove");
        put("test".getBytes(), testProtoCapsule);
        Assert.assertEquals(testProtoCapsule, tronDatabase.get("test".getBytes()));
        delete("test".getBytes());
        Assert.assertEquals(null, tronDatabase.get("test".getBytes()));
        close();
    }

    @Test
    public synchronized void testMerge() {
        tronDatabase = new RevokingDbWithCacheNewValueTest.TestRevokingTronStore("testSnapshotRoot-testMerge");
        revokingDatabase = new RevokingDbWithCacheNewValueTest.TestSnapshotManager();
        revokingDatabase.enable();
        revokingDatabase.add(getRevokingDB());
        SessionOptional dialog = SessionOptional.instance().setValue(revokingDatabase.buildSession());
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest("merge".getBytes());
        put(testProtoCapsule.getData(), testProtoCapsule);
        revokingDatabase.getDbs().forEach(( db) -> db.getHead().getRoot().merge(db.getHead()));
        dialog.reset();
        Assert.assertEquals(tronDatabase.get(testProtoCapsule.getData()), testProtoCapsule);
        close();
    }

    @Test
    public synchronized void testMergeList() {
        tronDatabase = new RevokingDbWithCacheNewValueTest.TestRevokingTronStore("testSnapshotRoot-testMergeList");
        revokingDatabase = new RevokingDbWithCacheNewValueTest.TestSnapshotManager();
        revokingDatabase.enable();
        revokingDatabase.add(getRevokingDB());
        SessionOptional.instance().setValue(revokingDatabase.buildSession());
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest("test".getBytes());
        put("merge".getBytes(), testProtoCapsule);
        for (int i = 1; i < 11; i++) {
            SnapshotRootTest.ProtoCapsuleTest tmpProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("mergeList" + i).getBytes());
            try (ISession tmpSession = revokingDatabase.buildSession()) {
                put(tmpProtoCapsule.getData(), tmpProtoCapsule);
                tmpSession.commit();
            }
        }
        revokingDatabase.getDbs().forEach(( db) -> {
            List<Snapshot> snapshots = new ArrayList<>();
            SnapshotRoot root = ((SnapshotRoot) (db.getHead().getRoot()));
            Snapshot next = root;
            for (int i = 0; i < 11; ++i) {
                next = next.getNext();
                snapshots.add(next);
            }
            root.merge(snapshots);
            root.resetSolidity();
            for (int i = 1; i < 11; i++) {
                org.tron.core.db2.ProtoCapsuleTest tmpProtoCapsule = new org.tron.core.db2.ProtoCapsuleTest(("mergeList" + i).getBytes());
                Assert.assertEquals(tmpProtoCapsule, tronDatabase.get(tmpProtoCapsule.getData()));
            }
        });
        revokingDatabase.updateSolidity(10);
        close();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ProtoCapsuleTest implements ProtoCapsule<Object> {
        private byte[] value;

        @Override
        public byte[] getData() {
            return value;
        }

        @Override
        public Object getInstance() {
            return value;
        }

        @Override
        public String toString() {
            return (((("ProtoCapsuleTest{" + "value=") + (Arrays.toString(value))) + ", string=") + ((value) == null ? "" : new String(value))) + '}';
        }
    }
}

