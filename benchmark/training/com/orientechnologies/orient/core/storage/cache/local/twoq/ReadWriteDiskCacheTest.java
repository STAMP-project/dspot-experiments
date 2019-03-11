package com.orientechnologies.orient.core.storage.cache.local.twoq;


import OIntegerSerializer.INSTANCE;
import com.orientechnologies.common.collection.closabledictionary.OClosableLinkedContainer;
import com.orientechnologies.common.directmemory.OByteBufferPool;
import com.orientechnologies.common.directmemory.OPointer;
import com.orientechnologies.common.serialization.types.OIntegerSerializer;
import com.orientechnologies.common.serialization.types.OLongSerializer;
import com.orientechnologies.common.types.OModifiableBoolean;
import com.orientechnologies.orient.core.exception.OAllCacheEntriesAreUsedException;
import com.orientechnologies.orient.core.storage.cache.OCacheEntry;
import com.orientechnologies.orient.core.storage.cache.OCachePointer;
import com.orientechnologies.orient.core.storage.cache.local.OWOWCache;
import com.orientechnologies.orient.core.storage.fs.OFileClassic;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OAbstractWALRecord;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OLogSequenceNumber;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.cas.OCASDiskWriteAheadLog;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class ReadWriteDiskCacheTest {
    private static final int userDataSize = 8;

    private static final int writeCacheAmountOfPages = 15000;

    private static final int systemOffset = (OIntegerSerializer.INT_SIZE) + (3 * (OLongSerializer.LONG_SIZE));

    private static final int PAGE_SIZE = (ReadWriteDiskCacheTest.userDataSize) + (ReadWriteDiskCacheTest.systemOffset);

    private static final int READ_CACHE_MAX_MEMORY = 4 * (ReadWriteDiskCacheTest.PAGE_SIZE);

    private static final int WRITE_CACHE_MAX_SIZE = (ReadWriteDiskCacheTest.writeCacheAmountOfPages) * (ReadWriteDiskCacheTest.PAGE_SIZE);

    private static O2QCache readBuffer;

    private static OWOWCache writeBuffer;

    private static OCASDiskWriteAheadLog writeAheadLog;

    private static final OClosableLinkedContainer<Long, OFileClassic> files = new OClosableLinkedContainer(1024);

    private static String fileName;

    private static Path storagePath;

    private static String storageName;

    private byte seed;

    private static final OByteBufferPool BUFFER_POOL = new OByteBufferPool(ReadWriteDiskCacheTest.PAGE_SIZE);

    @Test
    public void testAddFourItems() throws IOException {
        long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileId);
        OCacheEntry[] entries = new OCacheEntry[4];
        for (int i = 0; i < 4; i++) {
            entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
            if ((entries[i]) == null) {
                entries[i] = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
                Assert.assertEquals(entries[i].getPageIndex(), i);
            }
            final ByteBuffer buffer = entries[i].getCachePointer().getBuffer();
            buffer.position(ReadWriteDiskCacheTest.systemOffset);
            buffer.put(new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, 6, ((byte) (i)) });
            ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(entries[i], ReadWriteDiskCacheTest.writeBuffer);
        }
        LRUList am = ReadWriteDiskCacheTest.readBuffer.getAm();
        LRUList a1in = ReadWriteDiskCacheTest.readBuffer.getA1in();
        LRUList a1out = ReadWriteDiskCacheTest.readBuffer.getA1out();
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        final OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        for (int i = 0; i < 4; i++) {
            OCacheEntry entry = generateEntry(fileId, i, entries[i].getCachePointer().getPointer(), bufferPool);
            Assert.assertEquals(a1in.get(entry.getFileId(), entry.getPageIndex()), entry);
        }
        Assert.assertEquals(ReadWriteDiskCacheTest.writeBuffer.getFilledUpTo(fileId), 4);
        ReadWriteDiskCacheTest.writeBuffer.flush();
        for (int i = 0; i < 4; i++) {
            assertFile(i, new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, 6, ((byte) (i)) }, new OLogSequenceNumber(0, 0), nativeFileName);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testAddFourItemsInFourDifferentFiles() throws IOException {
        List<Long> fileIds = new ArrayList<>();
        List<String> nativeFileNames = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        fileNames.add("readWriteDiskCacheTest.tst");
        fileNames.add("readwRitedIskCacheTest.tst");
        fileNames.add("ReadwRitedIskCacheTesT.tst");
        fileNames.add("readwritediskcachetest.tst");
        addFileName(fileNames.get(0), fileIds, nativeFileNames);
        addFileName(fileNames.get(1), fileIds, nativeFileNames);
        addFileName(fileNames.get(2), fileIds, nativeFileNames);
        addFileName(fileNames.get(3), fileIds, nativeFileNames);
        OCacheEntry[] entries = new OCacheEntry[4];
        for (int i = 0; i < 4; i++) {
            for (int n = 0; n < 4; n++) {
                final long fileId = fileIds.get(n);
                entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
                if ((entries[i]) == null) {
                    entries[i] = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
                    Assert.assertEquals(entries[i].getPageIndex(), i);
                }
                entries[i].getCachePointer().acquireExclusiveLock();
                final ByteBuffer buffer = entries[i].getCachePointer().getBufferDuplicate();
                assert buffer != null;
                buffer.position(ReadWriteDiskCacheTest.systemOffset);
                buffer.put(new byte[]{ ((byte) (i)), 1, 2, ((byte) ((seed) + n)), 4, 5, 6, ((byte) (i + n)) });
                entries[i].getCachePointer().releaseExclusiveLock();
                ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(entries[i], ReadWriteDiskCacheTest.writeBuffer);
            }
        }
        for (int n = 0; n < 4; n++) {
            final long fileId = fileIds.get(n);
            Assert.assertEquals(ReadWriteDiskCacheTest.writeBuffer.getFilledUpTo(fileId), 4);
        }
        ReadWriteDiskCacheTest.writeBuffer.flush();
        for (int n = 0; n < 4; n++) {
            final String nativeFileName = nativeFileNames.get(n);
            for (int i = 0; i < 4; i++) {
                assertFile(i, new byte[]{ ((byte) (i)), 1, 2, ((byte) ((seed) + n)), 4, 5, 6, ((byte) (i + n)) }, new OLogSequenceNumber(0, 0), nativeFileName);
            }
        }
        for (int n = 3; n >= 0; n--) {
            for (int i = 3; i >= 0; i--) {
                final String fileNativeName = nativeFileNames.get(i);
                final File file = ReadWriteDiskCacheTest.storagePath.resolve(fileNativeName).toFile();
                if (i > n) {
                    Assert.assertFalse(file.exists());
                } else {
                    Assert.assertTrue(file.exists());
                }
            }
            ReadWriteDiskCacheTest.writeBuffer.deleteFile(fileIds.get(n));
            for (int i = 3; i >= 0; i--) {
                final String fileNativeName = nativeFileNames.get(i);
                final File file = ReadWriteDiskCacheTest.storagePath.resolve(fileNativeName).toFile();
                if (i >= n) {
                    Assert.assertFalse(file.exists());
                } else {
                    Assert.assertTrue(file.exists());
                }
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testAddFourItemsInFourDifferentFilesCloseAndOpen() throws Exception {
        List<Long> fileIds = new ArrayList<>();
        List<String> nativeFileNames = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        fileNames.add("readWriteDiskCacheTest.tst");
        fileNames.add("readwRitedIskCacheTest.tst");
        fileNames.add("ReadwRitedIskCacheTesT.tst");
        fileNames.add("readwritediskcachetest.tst");
        addFileName(fileNames.get(0), fileIds, nativeFileNames);
        addFileName(fileNames.get(1), fileIds, nativeFileNames);
        addFileName(fileNames.get(2), fileIds, nativeFileNames);
        addFileName(fileNames.get(3), fileIds, nativeFileNames);
        OCacheEntry[] entries = new OCacheEntry[4];
        for (int i = 0; i < 4; i++) {
            for (int n = 0; n < 4; n++) {
                final long fileId = fileIds.get(n);
                entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
                if ((entries[i]) == null) {
                    entries[i] = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
                }
                entries[i].getCachePointer().acquireExclusiveLock();
                final ByteBuffer buffer = entries[i].getCachePointer().getBufferDuplicate();
                assert buffer != null;
                buffer.position(ReadWriteDiskCacheTest.systemOffset);
                buffer.put(new byte[]{ ((byte) (i)), 1, 2, ((byte) ((seed) + n)), 4, 5, 6, ((byte) (i + n)) });
                entries[i].getCachePointer().releaseExclusiveLock();
                ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(entries[i], ReadWriteDiskCacheTest.writeBuffer);
            }
        }
        ReadWriteDiskCacheTest.readBuffer.closeStorage(ReadWriteDiskCacheTest.writeBuffer);
        ReadWriteDiskCacheTest.writeAheadLog.close();
        initBuffer();
        fileIds.clear();
        for (int n = 0; n < 4; n++) {
            fileIds.add(ReadWriteDiskCacheTest.writeBuffer.loadFile(fileNames.get(n)));
            final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileIds.get(n));
            Assert.assertEquals(nativeFileName, nativeFileNames.get(n));
        }
        for (int n = 0; n < 4; n++) {
            final long fileId = fileIds.get(n);
            Assert.assertEquals(ReadWriteDiskCacheTest.writeBuffer.getFilledUpTo(fileId), 4);
        }
        for (int n = 3; n >= 0; n--) {
            for (int i = 3; i >= 0; i--) {
                final String fileNativeName = nativeFileNames.get(i);
                final File file = ReadWriteDiskCacheTest.storagePath.resolve(fileNativeName).toFile();
                if (i > n) {
                    Assert.assertFalse(file.exists());
                } else {
                    Assert.assertTrue(file.exists());
                }
            }
            ReadWriteDiskCacheTest.writeBuffer.deleteFile(fileIds.get(n));
            for (int i = 3; i >= 0; i--) {
                final String fileNativeName = nativeFileNames.get(i);
                final File file = ReadWriteDiskCacheTest.storagePath.resolve(fileNativeName).toFile();
                if (i >= n) {
                    Assert.assertFalse(file.exists());
                } else {
                    Assert.assertTrue(file.exists());
                }
            }
        }
    }

    @Test
    public void testCacheShouldCreateFileIfItIsNotExisted() throws Exception {
        final long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileId);
        File file = ReadWriteDiskCacheTest.storagePath.resolve(nativeFileName).toFile();
        Assert.assertTrue(file.exists());
        Assert.assertTrue(file.isFile());
    }

    @Test
    public void testReadFourItems() throws IOException {
        long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileId);
        OCacheEntry[] entries = new OCacheEntry[4];
        for (int i = 0; i < 4; i++) {
            entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
            if ((entries[i]) == null) {
                entries[i] = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
                Assert.assertEquals(entries[i].getPageIndex(), i);
            }
            final ByteBuffer buffer = entries[i].getCachePointer().getBufferDuplicate();
            assert buffer != null;
            buffer.position(ReadWriteDiskCacheTest.systemOffset);
            buffer.put(new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, 6, ((byte) (i)) });
            setLsn(buffer, new OLogSequenceNumber(1, i));
            ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(entries[i], ReadWriteDiskCacheTest.writeBuffer);
        }
        ReadWriteDiskCacheTest.readBuffer.clear();
        ReadWriteDiskCacheTest.writeBuffer.flush();
        for (int i = 0; i < 4; i++) {
            assertFile(i, new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, 6, ((byte) (i)) }, new OLogSequenceNumber(1, i), nativeFileName);
        }
        for (int i = 0; i < 4; i++) {
            entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForRead(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true);
            ReadWriteDiskCacheTest.readBuffer.releaseFromRead(entries[i], ReadWriteDiskCacheTest.writeBuffer);
        }
        LRUList am = ReadWriteDiskCacheTest.readBuffer.getAm();
        LRUList a1in = ReadWriteDiskCacheTest.readBuffer.getA1in();
        LRUList a1out = ReadWriteDiskCacheTest.readBuffer.getA1out();
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        for (int i = 0; i < 4; i++) {
            OCacheEntry entry = generateEntry(fileId, i, entries[i].getCachePointer().getPointer(), bufferPool);
            Assert.assertEquals(a1in.get(entry.getFileId(), entry.getPageIndex()), entry);
        }
        Assert.assertEquals(ReadWriteDiskCacheTest.writeBuffer.getFilledUpTo(fileId), 4);
    }

    @Test
    public void testPrefetchPagesInA1inQueue() throws Exception {
        final long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileId);
        for (int i = 0; i < 4; i++) {
            OCacheEntry cacheEntry = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
            try {
                byte[] userData = new byte[ReadWriteDiskCacheTest.userDataSize];
                for (int n = 0; n < (userData.length); n++) {
                    userData[n] = ((byte) (i + 1));
                }
                final ByteBuffer buffer = cacheEntry.getCachePointer().getBufferDuplicate();
                assert buffer != null;
                buffer.position(ReadWriteDiskCacheTest.systemOffset);
                buffer.put(userData);
                setLsn(buffer, new OLogSequenceNumber(1, i));
            } finally {
                ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(cacheEntry, ReadWriteDiskCacheTest.writeBuffer);
            }
        }
        ReadWriteDiskCacheTest.readBuffer.clear();
        ReadWriteDiskCacheTest.writeBuffer.flush();
        for (int i = 0; i < 4; i++) {
            byte[] userData = new byte[ReadWriteDiskCacheTest.userDataSize];
            for (int n = 0; n < (userData.length); n++) {
                userData[n] = ((byte) (i + 1));
            }
            assertFile(i, userData, new OLogSequenceNumber(1, i), nativeFileName);
        }
        LRUList am = ReadWriteDiskCacheTest.readBuffer.getAm();
        LRUList a1in = ReadWriteDiskCacheTest.readBuffer.getA1in();
        LRUList a1out = ReadWriteDiskCacheTest.readBuffer.getA1out();
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        Assert.assertEquals(a1in.size(), 0);
        OCacheEntry cacheEntry = ReadWriteDiskCacheTest.readBuffer.loadForRead(fileId, 0, false, ReadWriteDiskCacheTest.writeBuffer, 1, true);
        ReadWriteDiskCacheTest.readBuffer.releaseFromRead(cacheEntry, ReadWriteDiskCacheTest.writeBuffer);
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        Assert.assertEquals(a1in.size(), 1);
        ReadWriteDiskCacheTest.readBuffer.clear();
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        Assert.assertEquals(a1in.size(), 0);
        cacheEntry = ReadWriteDiskCacheTest.readBuffer.loadForRead(fileId, 0, false, ReadWriteDiskCacheTest.writeBuffer, 4, true);
        ReadWriteDiskCacheTest.readBuffer.releaseFromRead(cacheEntry, ReadWriteDiskCacheTest.writeBuffer);
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        Assert.assertEquals(a1in.size(), 4);
    }

    @Test
    public void testLoadAndLockForReadShouldHitCache() throws Exception {
        long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        OCacheEntry cacheEntry = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, 0, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
        if (cacheEntry == null) {
            cacheEntry = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
            Assert.assertEquals(cacheEntry.getPageIndex(), 0);
        }
        ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(cacheEntry, ReadWriteDiskCacheTest.writeBuffer);
        LRUList am = ReadWriteDiskCacheTest.readBuffer.getAm();
        LRUList a1in = ReadWriteDiskCacheTest.readBuffer.getA1in();
        LRUList a1out = ReadWriteDiskCacheTest.readBuffer.getA1out();
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        final OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        final OCacheEntry entry = generateEntry(fileId, 0, cacheEntry.getCachePointer().getPointer(), bufferPool);
        Assert.assertEquals(a1in.size(), 1);
        Assert.assertEquals(a1in.get(entry.getFileId(), entry.getPageIndex()), entry);
    }

    @Test
    public void testCloseFileShouldFlushData() throws Exception {
        long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileId);
        OCacheEntry[] entries = new OCacheEntry[4];
        for (int i = 0; i < 4; i++) {
            entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
            if ((entries[i]) == null) {
                entries[i] = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
                Assert.assertEquals(entries[i].getPageIndex(), i);
            }
            ByteBuffer buffer = entries[i].getCachePointer().getBufferDuplicate();
            assert buffer != null;
            buffer.position(ReadWriteDiskCacheTest.systemOffset);
            buffer.put(new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, 6, ((byte) (i)) });
            ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(entries[i], ReadWriteDiskCacheTest.writeBuffer);
        }
        LRUList am = ReadWriteDiskCacheTest.readBuffer.getAm();
        LRUList a1in = ReadWriteDiskCacheTest.readBuffer.getA1in();
        LRUList a1out = ReadWriteDiskCacheTest.readBuffer.getA1out();
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        final OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        for (int i = 0; i < 4; i++) {
            OCacheEntry entry = generateEntry(fileId, i, entries[i].getCachePointer().getPointer(), bufferPool);
            Assert.assertEquals(a1in.get(entry.getFileId(), entry.getPageIndex()), entry);
        }
        Assert.assertEquals(ReadWriteDiskCacheTest.writeBuffer.getFilledUpTo(fileId), 4);
        ReadWriteDiskCacheTest.readBuffer.closeFile(fileId, true, ReadWriteDiskCacheTest.writeBuffer);
        for (int i = 0; i < 4; i++) {
            assertFile(i, new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, 6, ((byte) (i)) }, new OLogSequenceNumber(0, 0), nativeFileName);
        }
    }

    @Test
    public void testDeleteFileShouldDeleteFileFromHardDrive() throws Exception {
        long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileId);
        OCacheEntry[] entries = new OCacheEntry[4];
        byte[][] content = new byte[4][];
        for (int i = 0; i < 4; i++) {
            entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
            if ((entries[i]) == null) {
                entries[i] = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
                Assert.assertEquals(entries[i].getPageIndex(), i);
            }
            final ByteBuffer buffer = entries[i].getCachePointer().getBufferDuplicate();
            assert buffer != null;
            buffer.position(ReadWriteDiskCacheTest.systemOffset);
            content[i] = new byte[8];
            buffer.get(content[i]);
            ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(entries[i], ReadWriteDiskCacheTest.writeBuffer);
        }
        ReadWriteDiskCacheTest.readBuffer.deleteFile(fileId, ReadWriteDiskCacheTest.writeBuffer);
        ReadWriteDiskCacheTest.writeBuffer.flush();
        for (int i = 0; i < 4; i++) {
            File file = ReadWriteDiskCacheTest.storagePath.resolve(nativeFileName).toFile();
            Assert.assertFalse(file.exists());
        }
    }

    @Test
    public void testFileContentReplacement() throws IOException {
        // Add a file.
        final long fileId = ReadWriteDiskCacheTest.writeBuffer.addFile(ReadWriteDiskCacheTest.fileName);
        final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileId);
        final Path path = ReadWriteDiskCacheTest.storagePath.resolve(nativeFileName);
        Assert.assertTrue(Files.exists(path));
        // Set the file content to random.
        ReadWriteDiskCacheTest.writeBuffer.allocateNewPage(fileId);
        final OCachePointer cachePointer = ReadWriteDiskCacheTest.writeBuffer.load(fileId, 0, 1, new OModifiableBoolean(), false)[0];
        cachePointer.acquireExclusiveLock();
        final Random random = new Random(seed);
        final ByteBuffer buffer = cachePointer.getBufferDuplicate();
        assert buffer != null;
        Assert.assertTrue(((buffer.limit()) > (ReadWriteDiskCacheTest.systemOffset)));
        for (int i = ReadWriteDiskCacheTest.systemOffset; i < (buffer.limit()); ++i)
            buffer.put(i, ((byte) (random.nextInt())));

        cachePointer.releaseExclusiveLock();
        ReadWriteDiskCacheTest.writeBuffer.store(fileId, 0, cachePointer);
        cachePointer.decrementReadersReferrer();
        // Create a copy.
        ReadWriteDiskCacheTest.writeBuffer.flush();
        final Path copyPath = Files.createTempFile("ReadWriteDiskCacheTest", "testFileContentReplacement");
        Files.copy(path, copyPath, StandardCopyOption.REPLACE_EXISTING);
        // Truncate the file.
        ReadWriteDiskCacheTest.writeBuffer.truncateFile(fileId);
        ReadWriteDiskCacheTest.writeBuffer.flush();// just in case

        Assert.assertTrue(((Files.size(path)) < (Files.size(copyPath))));
        // Replace the file content back.
        ReadWriteDiskCacheTest.writeBuffer.replaceFileContentWith(fileId, copyPath);
        Files.delete(copyPath);// cleanup

        // Verify the content.
        final OCachePointer verificationCachePointer = ReadWriteDiskCacheTest.writeBuffer.load(fileId, 0, 1, new OModifiableBoolean(), true)[0];
        verificationCachePointer.acquireSharedLock();
        final Random verificationRandom = new Random(seed);
        final ByteBuffer verificationBuffer = verificationCachePointer.getBufferDuplicate();
        assert verificationBuffer != null;
        Assert.assertTrue(((verificationBuffer.limit()) > (ReadWriteDiskCacheTest.systemOffset)));
        for (int i = ReadWriteDiskCacheTest.systemOffset; i < (verificationBuffer.limit()); ++i)
            Assert.assertEquals(("at " + i), ((byte) (verificationRandom.nextInt())), verificationBuffer.get(i));

        verificationCachePointer.releaseSharedLock();
        verificationCachePointer.decrementReadersReferrer();
    }

    @Test
    public void testFlushData() throws Exception {
        long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        final String nativeFileName = ReadWriteDiskCacheTest.writeBuffer.nativeFileNameById(fileId);
        OCacheEntry[] entries = new OCacheEntry[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; ++j) {
                entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
                if ((entries[i]) == null) {
                    entries[i] = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
                    Assert.assertEquals(entries[i].getPageIndex(), i);
                }
                final ByteBuffer buffer = entries[i].getCachePointer().getBufferDuplicate();
                assert buffer != null;
                buffer.position(ReadWriteDiskCacheTest.systemOffset);
                buffer.put(new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, ((byte) (j)), ((byte) (i)) });
                ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(entries[i], ReadWriteDiskCacheTest.writeBuffer);
            }
        }
        LRUList am = ReadWriteDiskCacheTest.readBuffer.getAm();
        LRUList a1in = ReadWriteDiskCacheTest.readBuffer.getA1in();
        LRUList a1out = ReadWriteDiskCacheTest.readBuffer.getA1out();
        Assert.assertEquals(am.size(), 0);
        Assert.assertEquals(a1out.size(), 0);
        final OByteBufferPool bufferPool = OByteBufferPool.instance(null);
        for (int i = 0; i < 4; i++) {
            final OPointer pointer = entries[i].getCachePointer().getPointer();
            OCacheEntry entry = generateEntry(fileId, i, pointer, bufferPool);
            Assert.assertEquals(a1in.get(entry.getFileId(), entry.getPageIndex()), entry);
        }
        Assert.assertEquals(ReadWriteDiskCacheTest.writeBuffer.getFilledUpTo(fileId), 4);
        ReadWriteDiskCacheTest.writeBuffer.flush(fileId);
        for (int i = 0; i < 4; i++) {
            assertFile(i, new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, 3, ((byte) (i)) }, new OLogSequenceNumber(0, 0), nativeFileName);
        }
    }

    @Test(expected = OAllCacheEntriesAreUsedException.class)
    public void testIfAllPagesAreUsedExceptionShouldBeThrown() throws Exception {
        long fileId = ReadWriteDiskCacheTest.readBuffer.addFile(ReadWriteDiskCacheTest.fileName, ReadWriteDiskCacheTest.writeBuffer);
        OCacheEntry[] entries = new OCacheEntry[5];
        try {
            for (int i = 0; i < 5; i++) {
                entries[i] = ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, i, false, ReadWriteDiskCacheTest.writeBuffer, 1, true, null);
                if ((entries[i]) == null) {
                    entries[i] = ReadWriteDiskCacheTest.readBuffer.allocateNewPage(fileId, ReadWriteDiskCacheTest.writeBuffer, null);
                    Assert.assertEquals(entries[i].getPageIndex(), i);
                }
                ByteBuffer buffer = entries[i].getCachePointer().getBufferDuplicate();
                assert buffer != null;
                buffer.position(ReadWriteDiskCacheTest.systemOffset);
                buffer.put(new byte[]{ ((byte) (i)), 1, 2, seed, 4, 5, 6, 7 });
                if ((i - 4) >= 0) {
                    ReadWriteDiskCacheTest.readBuffer.loadForWrite(fileId, (i - 4), false, ReadWriteDiskCacheTest.writeBuffer, 0, true, null);
                    buffer = entries[(i - 4)].getCachePointer().getBufferDuplicate();
                    assert buffer != null;
                    buffer.position(ReadWriteDiskCacheTest.systemOffset);
                    buffer.put(new byte[]{ ((byte) (i - 4)), 1, 2, seed, 4, 5, 6, 7 });
                }
            }
        } finally {
            for (int i = 0; i < 4; i++) {
                assert (entries[i]) != null;
                ReadWriteDiskCacheTest.readBuffer.releaseFromWrite(entries[i], ReadWriteDiskCacheTest.writeBuffer);
            }
        }
    }

    public static final class TestRecord extends OAbstractWALRecord {
        private byte[] data;

        @SuppressWarnings("unused")
        public TestRecord() {
        }

        @SuppressWarnings("unused")
        public TestRecord(byte[] data) {
            this.data = data;
        }

        @Override
        public int toStream(byte[] content, int offset) {
            INSTANCE.serializeNative(data.length, content, offset);
            offset += OIntegerSerializer.INT_SIZE;
            System.arraycopy(data, 0, content, offset, data.length);
            offset += data.length;
            return offset;
        }

        @Override
        public void toStream(ByteBuffer buffer) {
            buffer.putInt(data.length);
            buffer.put(data);
        }

        @Override
        public int fromStream(byte[] content, int offset) {
            int len = INSTANCE.deserializeNative(content, offset);
            offset += OIntegerSerializer.INT_SIZE;
            data = new byte[len];
            System.arraycopy(content, offset, data, 0, len);
            offset += len;
            return offset;
        }

        @Override
        public int serializedSize() {
            return (data.length) + (OIntegerSerializer.INT_SIZE);
        }

        @Override
        public boolean isUpdateMasterRecord() {
            return false;
        }

        @Override
        public byte getId() {
            return ((byte) (128));
        }
    }
}

