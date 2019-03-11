package com.orientechnologies.orient.core.storage.impl.local.paginated;


import com.orientechnologies.common.directmemory.OByteBufferPool;
import com.orientechnologies.common.directmemory.OPointer;
import com.orientechnologies.orient.core.storage.cache.OCacheEntry;
import com.orientechnologies.orient.core.storage.cache.OCachePointer;
import com.orientechnologies.orient.core.storage.cluster.OClusterPage;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 20.03.13
 */
public class ClusterPageTest {
    private static final int SYSTEM_OFFSET = 24;

    @Test
    public void testAddOneRecord() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            addOneRecord(localPage);
            addOneRecord(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testAddThreeRecords() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            addThreeRecords(localPage);
            addThreeRecords(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testAddFullPage() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            addFullPage(localPage);
            addFullPage(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testDeleteAddLowerVersion() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            deleteAddLowerVersion(localPage);
            deleteAddLowerVersion(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testDeleteAddBiggerVersion() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            deleteAddBiggerVersion(localPage);
            deleteAddBiggerVersion(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testDeleteAddEqualVersion() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            deleteAddEqualVersion(localPage);
            deleteAddEqualVersion(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testDeleteAddEqualVersionKeepTombstoneVersion() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            deleteAddEqualVersionKeepTombstoneVersion(localPage);
            deleteAddEqualVersionKeepTombstoneVersion(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testDeleteTwoOutOfFour() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            deleteTwoOutOfFour(localPage);
            deleteTwoOutOfFour(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testAddFullPageDeleteAndAddAgain() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            addFullPageDeleteAndAddAgain(localPage);
            addFullPageDeleteAndAddAgain(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testAddBigRecordDeleteAndAddSmallRecords() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            final long seed = System.currentTimeMillis();
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            addBigRecordDeleteAndAddSmallRecords(seed, localPage);
            addBigRecordDeleteAndAddSmallRecords(seed, directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testFindFirstRecord() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        final long seed = System.currentTimeMillis();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            findFirstRecord(seed, localPage);
            findFirstRecord(seed, directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testFindLastRecord() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        final long seed = System.currentTimeMillis();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            findLastRecord(seed, localPage);
            findLastRecord(seed, directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testSetGetNextPage() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            setGetNextPage(localPage);
            setGetNextPage(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testSetGetPrevPage() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            setGetPrevPage(localPage);
            setGetPrevPage(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testReplaceOneRecordWithBiggerSize() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            replaceOneRecordWithBiggerSize(localPage);
            replaceOneRecordWithBiggerSize(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testReplaceOneRecordWithEqualSize() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            replaceOneRecordWithEqualSize(localPage);
            replaceOneRecordWithEqualSize(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testReplaceOneRecordWithSmallerSize() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            replaceOneRecordWithSmallerSize(localPage);
            replaceOneRecordWithSmallerSize(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testReplaceOneRecordNoVersionUpdate() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            replaceOneRecordNoVersionUpdate(localPage);
            replaceOneRecordNoVersionUpdate(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }

    @Test
    public void testReplaceOneRecordLowerVersion() throws Exception {
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        OPointer pointer = bufferPool.acquireDirect(true);
        OCachePointer cachePointer = new OCachePointer(pointer, bufferPool, 0, 0);
        cachePointer.incrementReferrer();
        OCacheEntry cacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, cachePointer);
        cacheEntry.acquireExclusiveLock();
        OPointer directPointer = bufferPool.acquireDirect(true);
        OCachePointer directCachePointer = new OCachePointer(directPointer, bufferPool, 0, 0);
        directCachePointer.incrementReferrer();
        OCacheEntry directCacheEntry = new com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl(0, 0, directCachePointer);
        directCacheEntry.acquireExclusiveLock();
        try {
            OClusterPage localPage = new OClusterPage(new com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OCacheEntryChanges(cacheEntry), true);
            OClusterPage directLocalPage = new OClusterPage(directCacheEntry, true);
            replaceOneRecordLowerVersion(localPage);
            replaceOneRecordLowerVersion(directLocalPage);
            assertChangesTracking(localPage, directPointer, bufferPool);
        } finally {
            cacheEntry.releaseExclusiveLock();
            directCacheEntry.releaseExclusiveLock();
            cachePointer.decrementReferrer();
            directCachePointer.decrementReferrer();
        }
    }
}

