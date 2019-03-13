package com.orientechnologies.orient.core.storage.impl.local.paginated;


import OGlobalConfiguration.STORAGE_TRACK_CHANGED_RECORDS_IN_WAL;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.junit.Test;


public class LocalPaginatedStorageIncrementalSync {
    private ODatabaseDocumentTx originalDB;

    private ODatabaseDocumentTx syncDB;

    @Test
    public void testIncrementalSynch() throws Exception {
        STORAGE_TRACK_CHANGED_RECORDS_IN_WAL.setValue(true);
        final String buildDirectory = System.getProperty("buildDirectory", ".");
        createOriginalDB(buildDirectory);
        createSyncDB(buildDirectory);
        assertDatabasesAreInSynch();
        for (int i = 0; i < 10; i++) {
            System.out.println((("Iteration " + (i + 1)) + "-----------------------------------------------"));
            incrementalSyncIteration(buildDirectory);
        }
    }
}

