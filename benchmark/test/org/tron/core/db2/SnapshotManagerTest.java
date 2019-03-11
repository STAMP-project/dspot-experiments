package org.tron.core.db2;


import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.TronApplicationContext;
import org.tron.core.db2.core.ISession;
import org.tron.core.db2.core.SnapshotManager;
import org.tron.core.exception.BadItemException;
import org.tron.core.exception.ItemNotFoundException;

import static org.tron.core.db2.SnapshotRootTest.ProtoCapsuleTest.<init>;


@Slf4j
public class SnapshotManagerTest {
    private SnapshotManager revokingDatabase;

    private TronApplicationContext context;

    private Application appT;

    private RevokingDbWithCacheNewValueTest.TestRevokingTronStore tronDatabase;

    @Test
    public synchronized void testRefresh() throws BadItemException, ItemNotFoundException {
        while ((revokingDatabase.size()) != 0) {
            revokingDatabase.pop();
        } 
        revokingDatabase.setMaxFlushCount(0);
        revokingDatabase.setUnChecked(false);
        revokingDatabase.setMaxSize(5);
        SnapshotRootTest.ProtoCapsuleTest protoCapsule = new SnapshotRootTest.ProtoCapsuleTest("refresh".getBytes());
        for (int i = 1; i < 11; i++) {
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("refresh" + i).getBytes());
            try (ISession tmpSession = revokingDatabase.buildSession()) {
                put(protoCapsule.getData(), testProtoCapsule);
                tmpSession.commit();
            }
        }
        revokingDatabase.flush();
        Assert.assertEquals(new SnapshotRootTest.ProtoCapsuleTest("refresh10".getBytes()), tronDatabase.get(protoCapsule.getData()));
    }

    @Test
    public synchronized void testClose() {
        while ((revokingDatabase.size()) != 0) {
            revokingDatabase.pop();
        } 
        revokingDatabase.setMaxFlushCount(0);
        revokingDatabase.setUnChecked(false);
        revokingDatabase.setMaxSize(5);
        SnapshotRootTest.ProtoCapsuleTest protoCapsule = new SnapshotRootTest.ProtoCapsuleTest("close".getBytes());
        for (int i = 1; i < 11; i++) {
            SnapshotRootTest.ProtoCapsuleTest testProtoCapsule = new SnapshotRootTest.ProtoCapsuleTest(("close" + i).getBytes());
            try (ISession _ = revokingDatabase.buildSession()) {
                put(protoCapsule.getData(), testProtoCapsule);
            }
        }
        Assert.assertEquals(null, tronDatabase.get(protoCapsule.getData()));
    }
}

