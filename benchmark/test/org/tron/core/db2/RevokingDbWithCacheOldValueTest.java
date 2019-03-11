package org.tron.core.db2;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.SessionOptional;
import org.tron.core.db.AbstractRevokingStore;
import org.tron.core.db.RevokingDatabase;
import org.tron.core.db.TronStoreWithRevoking;
import org.tron.core.db2.core.ISession;
import org.tron.core.exception.RevokingStoreIllegalStateException;

import static org.tron.core.db2.SnapshotRootTest.ProtoCapsuleTest.<init>;


@Slf4j
public class RevokingDbWithCacheOldValueTest {
    private AbstractRevokingStore revokingDatabase;

    private TronApplicationContext context;

    private Application appT;

    @Test
    public synchronized void testReset() {
        revokingDatabase.getStack().clear();
        RevokingDbWithCacheOldValueTest.TestRevokingTronStore tronDatabase = new RevokingDbWithCacheOldValueTest.TestRevokingTronStore("testrevokingtronstore-testReset", revokingDatabase);
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest("reset".getBytes());
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            put(testProtoCapsule.getData(), testProtoCapsule);
            tmpSession.commit();
        }
        Assert.assertEquals(true, has(testProtoCapsule.getData()));
        reset();
        Assert.assertEquals(false, has(testProtoCapsule.getData()));
    }

    @Test
    public synchronized void testPop() throws RevokingStoreIllegalStateException {
        revokingDatabase.getStack().clear();
        RevokingDbWithCacheOldValueTest.TestRevokingTronStore tronDatabase = new RevokingDbWithCacheOldValueTest.TestRevokingTronStore("testrevokingtronstore-testPop", revokingDatabase);
        for (int i = 1; i < 11; i++) {
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("pop" + i).getBytes());
            try (ISession tmpSession = revokingDatabase.buildSession()) {
                put(testProtoCapsule.getData(), testProtoCapsule);
                Assert.assertEquals(1, revokingDatabase.getActiveDialog());
                tmpSession.commit();
                Assert.assertEquals(i, revokingDatabase.getStack().size());
                Assert.assertEquals(0, revokingDatabase.getActiveDialog());
            }
        }
        for (int i = 1; i < 11; i++) {
            revokingDatabase.pop();
            Assert.assertEquals((10 - i), revokingDatabase.getStack().size());
        }
        close();
        Assert.assertEquals(0, revokingDatabase.getStack().size());
    }

    @Test
    public synchronized void testUndo() throws RevokingStoreIllegalStateException {
        revokingDatabase.getStack().clear();
        RevokingDbWithCacheOldValueTest.TestRevokingTronStore tronDatabase = new RevokingDbWithCacheOldValueTest.TestRevokingTronStore("testrevokingtronstore-testUndo", revokingDatabase);
        SessionOptional dialog = SessionOptional.instance().setValue(revokingDatabase.buildSession());
        for (int i = 0; i < 10; i++) {
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("undo" + i).getBytes());
            try (ISession tmpSession = revokingDatabase.buildSession()) {
                put(testProtoCapsule.getData(), testProtoCapsule);
                Assert.assertEquals(2, revokingDatabase.getStack().size());
                tmpSession.merge();
                Assert.assertEquals(1, revokingDatabase.getStack().size());
            }
        }
        Assert.assertEquals(1, revokingDatabase.getStack().size());
        dialog.reset();
        Assert.assertTrue(revokingDatabase.getStack().isEmpty());
        Assert.assertEquals(0, revokingDatabase.getActiveDialog());
        dialog = SessionOptional.instance().setValue(revokingDatabase.buildSession());
        revokingDatabase.disable();
        SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest("del".getBytes());
        put(testProtoCapsule.getData(), testProtoCapsule);
        revokingDatabase.enable();
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            put(testProtoCapsule.getData(), new SnapshotRootTest.ProtoCapsuleTest("del2".getBytes()));
            tmpSession.merge();
        }
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            put(testProtoCapsule.getData(), new SnapshotRootTest.ProtoCapsuleTest("del22".getBytes()));
            tmpSession.merge();
        }
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            put(testProtoCapsule.getData(), new SnapshotRootTest.ProtoCapsuleTest("del222".getBytes()));
            tmpSession.merge();
        }
        try (ISession tmpSession = revokingDatabase.buildSession()) {
            delete(testProtoCapsule.getData());
            tmpSession.merge();
        }
        dialog.reset();
        logger.info(("**********testProtoCapsule:" + (String.valueOf(getUnchecked(testProtoCapsule.getData())))));
        Assert.assertArrayEquals("del".getBytes(), getUnchecked(testProtoCapsule.getData()).getData());
        Assert.assertEquals(testProtoCapsule, getUnchecked(testProtoCapsule.getData()));
        close();
    }

    @Test
    public synchronized void testGetlatestValues() {
        revokingDatabase.getStack().clear();
        RevokingDbWithCacheOldValueTest.TestRevokingTronStore tronDatabase = new RevokingDbWithCacheOldValueTest.TestRevokingTronStore("testrevokingtronstore-testGetlatestValues", revokingDatabase);
        for (int i = 0; i < 10; i++) {
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
        close();
    }

    @Test
    public synchronized void testGetValuesNext() {
        revokingDatabase.getStack().clear();
        RevokingDbWithCacheOldValueTest.TestRevokingTronStore tronDatabase = new RevokingDbWithCacheOldValueTest.TestRevokingTronStore("testrevokingtronstore-testGetValuesNext", revokingDatabase);
        for (int i = 0; i < 10; i++) {
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
        close();
    }

    @Test
    public void shutdown() throws RevokingStoreIllegalStateException {
        revokingDatabase.getStack().clear();
        RevokingDbWithCacheOldValueTest.TestRevokingTronStore tronDatabase = new RevokingDbWithCacheOldValueTest.TestRevokingTronStore("testrevokingtronstore-shutdown", revokingDatabase);
        List<SnapshotRootTest.ProtoCapsuleTest> capsules = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            revokingDatabase.buildSession();
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("test" + i).getBytes());
            capsules.add(testProtoCapsule);
            put(testProtoCapsule.getData(), testProtoCapsule);
            Assert.assertEquals(revokingDatabase.getActiveDialog(), i);
            Assert.assertEquals(revokingDatabase.getStack().size(), i);
        }
        for (SnapshotRootTest.ProtoCapsuleTest capsule : capsules) {
            logger.info(new String(capsule.getData()));
            Assert.assertEquals(capsule, getUnchecked(capsule.getData()));
        }
        revokingDatabase.shutdown();
        for (SnapshotRootTest.ProtoCapsuleTest capsule : capsules) {
            logger.info(getUnchecked(capsule.getData()).toString());
            Assert.assertEquals(null, getUnchecked(capsule.getData()).getData());
        }
        Assert.assertEquals(0, revokingDatabase.getStack().size());
        close();
    }

    private static class TestRevokingTronStore extends TronStoreWithRevoking<SnapshotRootTest.ProtoCapsuleTest> {
        protected TestRevokingTronStore(String dbName, RevokingDatabase revokingDatabase) {
            super(dbName, revokingDatabase);
        }

        @Override
        public SnapshotRootTest.ProtoCapsuleTest get(byte[] key) {
            byte[] value = this.revokingDB.getUnchecked(key);
            return ArrayUtils.isEmpty(value) ? null : new SnapshotRootTest.ProtoCapsuleTest(value);
        }
    }

    private static class TestRevokingTronDatabase extends AbstractRevokingStore {}
}

