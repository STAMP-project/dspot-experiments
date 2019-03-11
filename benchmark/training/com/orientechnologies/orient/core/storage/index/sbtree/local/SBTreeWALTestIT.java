package com.orientechnologies.orient.core.storage.index.sbtree.local;


import OGlobalConfiguration.FILE_LOCK;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.storage.cache.OReadCache;
import com.orientechnologies.orient.core.storage.cache.OWriteCache;
import com.orientechnologies.orient.core.storage.disk.OLocalPaginatedStorage;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 8/27/13
 */
public class SBTreeWALTestIT extends SBTreeTestIT {
    static {
        FILE_LOCK.setValue(false);
    }

    private OLocalPaginatedStorage actualStorage;

    private OWriteCache actualWriteCache;

    private ODatabaseSession expectedDatabaseDocumentTx;

    private OLocalPaginatedStorage expectedStorage;

    private OReadCache expectedReadCache;

    private OWriteCache expectedWriteCache;

    private String expectedStorageDir;

    private String actualStorageDir;

    private static final String DIR_NAME = SBTreeWALTestIT.class.getSimpleName();

    private static final String ACTUAL_DB_NAME = "sbtreeWithWALTestActual";

    private static final String EXPECTED_DB_NAME = "sbtreeWithWALTestExpected";

    @Override
    @Test
    public void testKeyPut() throws Exception {
        super.testKeyPut();
        assertFileRestoreFromWAL();
    }

    @Override
    @Test
    public void testKeyPutRandomUniform() throws Exception {
        super.testKeyPutRandomUniform();
        assertFileRestoreFromWAL();
    }

    @Override
    @Test
    public void testKeyPutRandomGaussian() throws Exception {
        super.testKeyPutRandomGaussian();
        assertFileRestoreFromWAL();
    }

    @Override
    @Test
    public void testKeyDeleteRandomUniform() throws Exception {
        super.testKeyDeleteRandomUniform();
        assertFileRestoreFromWAL();
    }

    @Test
    @Override
    public void testKeyDeleteRandomGaussian() throws Exception {
        super.testKeyDeleteRandomGaussian();
        assertFileRestoreFromWAL();
    }

    @Test
    @Override
    public void testKeyDelete() throws Exception {
        super.testKeyDelete();
        assertFileRestoreFromWAL();
    }

    @Test
    @Override
    public void testKeyAddDelete() throws Exception {
        super.testKeyAddDelete();
        assertFileRestoreFromWAL();
    }

    @Test
    @Override
    public void testAddKeyValuesInTwoBucketsAndMakeFirstEmpty() throws Exception {
        super.testAddKeyValuesInTwoBucketsAndMakeFirstEmpty();
        assertFileRestoreFromWAL();
    }

    @Test
    @Override
    public void testAddKeyValuesInTwoBucketsAndMakeLastEmpty() throws Exception {
        super.testAddKeyValuesInTwoBucketsAndMakeLastEmpty();
        assertFileRestoreFromWAL();
    }

    @Test
    @Override
    public void testAddKeyValuesAndRemoveFirstMiddleAndLastPages() throws Exception {
        super.testAddKeyValuesAndRemoveFirstMiddleAndLastPages();
        assertFileRestoreFromWAL();
    }
}

