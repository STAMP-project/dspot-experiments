package org.tron.core.db2;


import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.SessionOptional;
import org.tron.core.db.TronStoreWithRevoking;
import org.tron.core.db2.core.ISession;
import org.tron.core.db2.core.SnapshotManager;
import org.tron.core.exception.RevokingStoreIllegalStateException;

import static org.tron.core.db2.SnapshotRootTest.ProtoCapsuleTest.<init>;


@Slf4j
public class RevokingDbWithCacheNewValueTest {
    private SnapshotManager revokingDatabase;

    private TronApplicationContext context;

    private Application appT;

    private RevokingDbWithCacheNewValueTest.TestRevokingTronStore tronDatabase;

    @Test
    public synchronized void testPop() throws RevokingStoreIllegalStateException {
        revokingDatabase = new RevokingDbWithCacheNewValueTest.TestSnapshotManager();
        revokingDatabase.enable();
        tronDatabase = new RevokingDbWithCacheNewValueTest.TestRevokingTronStore("testRevokingDBWithCacheNewValue-testPop");
        revokingDatabase.add(getRevokingDB());
        while ((revokingDatabase.size()) != 0) {
            revokingDatabase.pop();
        } 
        for (int i = 1; i < 11; i++) {
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("pop" + i).getBytes());
            try (ISession tmpSession = revokingDatabase.buildSession()) {
                put(testProtoCapsule.getData(), testProtoCapsule);
                Assert.assertEquals(1, revokingDatabase.getActiveSession());
                tmpSession.commit();
                Assert.assertEquals(i, revokingDatabase.getSize());
                Assert.assertEquals(0, revokingDatabase.getActiveSession());
            }
        }
        for (int i = 1; i < 11; i++) {
            revokingDatabase.pop();
            Assert.assertEquals((10 - i), revokingDatabase.getSize());
        }
        Assert.assertEquals(0, revokingDatabase.getSize());
    }

    @Test
    public synchronized void testMerge() {
        revokingDatabase = new RevokingDbWithCacheNewValueTest.TestSnapshotManager();
        revokingDatabase.enable();
        tronDatabase = new RevokingDbWithCacheNewValueTest.TestRevokingTronStore("testRevokingDBWithCacheNewValue-testMerge");
        revokingDatabase.add(getRevokingDB());
        while ((revokingDatabase.size()) != 0) {
            revokingDatabase.pop();
        } 
        SessionOptional dialog = SessionOptional.instance().setValue(revokingDatabase.buildSession());
        dialog.setValue(revokingDatabase.buildSession());
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest("merge".getBytes());
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule2 = new SnapshotRootTest.ProtoCapsuleTest("merge2".getBytes());
        put(testProtoCapsule.getData(), testProtoCapsule);
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            put(testProtoCapsule.getData(), testProtoCapsule2);
            tmpSession.merge();
        }
        Assert.assertEquals(testProtoCapsule2, tronDatabase.get(testProtoCapsule.getData()));
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            delete(testProtoCapsule.getData());
            tmpSession.merge();
        }
        Assert.assertEquals(null, tronDatabase.get(testProtoCapsule.getData()));
        dialog.reset();
    }

    @Test
    public synchronized void testRevoke() {
        revokingDatabase = new RevokingDbWithCacheNewValueTest.TestSnapshotManager();
        revokingDatabase.enable();
        tronDatabase = new RevokingDbWithCacheNewValueTest.TestRevokingTronStore("testRevokingDBWithCacheNewValue-testRevoke");
        revokingDatabase.add(getRevokingDB());
        while ((revokingDatabase.size()) != 0) {
            revokingDatabase.pop();
        } 
        SessionOptional dialog = SessionOptional.instance().setValue(revokingDatabase.buildSession());
        for (int i = 0; i < 10; i++) {
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("undo" + i).getBytes());
            try (ISession tmpSession = revokingDatabase.buildSession()) {
                put(testProtoCapsule.getData(), testProtoCapsule);
                Assert.assertEquals(2, revokingDatabase.getSize());
                tmpSession.merge();
                Assert.assertEquals(1, revokingDatabase.getSize());
            }
        }
        Assert.assertEquals(1, revokingDatabase.getSize());
        dialog.reset();
        Assert.assertTrue(((revokingDatabase.getSize()) == 0));
        Assert.assertEquals(0, revokingDatabase.getActiveSession());
        dialog.setValue(revokingDatabase.buildSession());
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest("revoke".getBytes());
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule2 = new SnapshotRootTest.ProtoCapsuleTest("revoke2".getBytes());
        put(testProtoCapsule.getData(), testProtoCapsule);
        dialog.setValue(revokingDatabase.buildSession());
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            put(testProtoCapsule.getData(), testProtoCapsule2);
            tmpSession.merge();
        }
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            put(testProtoCapsule.getData(), new SnapshotRootTest.ProtoCapsuleTest("revoke22".getBytes()));
            tmpSession.merge();
        }
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            put(testProtoCapsule.getData(), new SnapshotRootTest.ProtoCapsuleTest("revoke222".getBytes()));
            tmpSession.merge();
        }
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            delete(testProtoCapsule.getData());
            tmpSession.merge();
        }
        dialog.reset();
        logger.info(("**********testProtoCapsule:" + (String.valueOf(getUnchecked(testProtoCapsule.getData())))));
        Assert.assertEquals(testProtoCapsule, tronDatabase.get(testProtoCapsule.getData()));
    }

    @Test
    public synchronized void testGetlatestValues() {
        revokingDatabase = new RevokingDbWithCacheNewValueTest.TestSnapshotManager();
        revokingDatabase.enable();
        tronDatabase = new RevokingDbWithCacheNewValueTest.TestRevokingTronStore("testSnapshotManager-testGetlatestValues");
        revokingDatabase.add(getRevokingDB());
        while ((revokingDatabase.size()) != 0) {
            revokingDatabase.pop();
        } 
        for (int i = 1; i < 10; i++) {
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("getLastestValues" + i).getBytes());
            try (ISession tmpSession = revokingDatabase.buildSession()) {
                put(testProtoCapsule.getData(), testProtoCapsule);
                tmpSession.commit();
            }
        }
        Set<SnapshotRootTest.ProtoCapsuleTest> result = getRevokingDB().getlatestValues(5).stream().map(SnapshotRootTest.ProtoCapsuleTest::new).collect(Collectors.toSet());
        for (int i = 9; i >= 5; i--) {
            Assert.assertEquals(true, result.contains(new SnapshotRootTest.ProtoCapsuleTest(("getLastestValues" + i).getBytes())));
        }
    }

    @Test
    public synchronized void testGetValuesNext() {
        revokingDatabase = new RevokingDbWithCacheNewValueTest.TestSnapshotManager();
        revokingDatabase.enable();
        tronDatabase = new RevokingDbWithCacheNewValueTest.TestRevokingTronStore("testSnapshotManager-testGetValuesNext");
        revokingDatabase.add(getRevokingDB());
        while ((revokingDatabase.size()) != 0) {
            revokingDatabase.pop();
        } 
        for (int i = 1; i < 10; i++) {
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("getValuesNext" + i).getBytes());
            try (ISession tmpSession = revokingDatabase.buildSession()) {
                put(testProtoCapsule.getData(), testProtoCapsule);
                tmpSession.commit();
            }
        }
        Set<SnapshotRootTest.ProtoCapsuleTest> result = getRevokingDB().getValuesNext(new SnapshotRootTest.ProtoCapsuleTest("getValuesNext2".getBytes()).getData(), 3).stream().map(SnapshotRootTest.ProtoCapsuleTest::new).collect(Collectors.toSet());
        for (int i = 2; i < 5; i++) {
            Assert.assertEquals(true, result.contains(new SnapshotRootTest.ProtoCapsuleTest(("getValuesNext" + i).getBytes())));
        }
    }

    public static class TestRevokingTronStore extends TronStoreWithRevoking<SnapshotRootTest.ProtoCapsuleTest> {
        protected TestRevokingTronStore(String dbName) {
            super(dbName);
        }

        @Override
        public SnapshotRootTest.ProtoCapsuleTest get(byte[] key) {
            byte[] value = this.revokingDB.getUnchecked(key);
            return ArrayUtils.isEmpty(value) ? null : new SnapshotRootTest.ProtoCapsuleTest(value);
        }
    }

    public static class TestSnapshotManager extends SnapshotManager {}
}

