package com.orientechnologies.orient.core.storage.index.hashindex.local.cache;


import OChecksumMode.Off;
import OChecksumMode.Store;
import OChecksumMode.StoreAndThrow;
import OIntegerSerializer.INSTANCE;
import com.orientechnologies.common.collection.closabledictionary.OClosableLinkedContainer;
import com.orientechnologies.common.directmemory.OByteBufferPool;
import com.orientechnologies.common.serialization.types.OIntegerSerializer;
import com.orientechnologies.common.serialization.types.OLongSerializer;
import com.orientechnologies.common.types.OModifiableBoolean;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.core.storage.cache.OCachePointer;
import com.orientechnologies.orient.core.storage.cache.local.OWOWCache;
import com.orientechnologies.orient.core.storage.fs.OFileClassic;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OAbstractWALRecord;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OLogSequenceNumber;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.cas.OCASDiskWriteAheadLog;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 26.07.13
 */
public class WOWCacheTestIT {
    private static final int systemOffset = 2 * ((OIntegerSerializer.INT_SIZE) + (OLongSerializer.LONG_SIZE));

    private static final int pageSize = (WOWCacheTestIT.systemOffset) + 8;

    private static String fileName;

    private static OCASDiskWriteAheadLog writeAheadLog;

    private static final OByteBufferPool bufferPool = new OByteBufferPool(WOWCacheTestIT.pageSize);

    private static Path storagePath;

    private static OWOWCache wowCache;

    private static String storageName;

    private final OClosableLinkedContainer<Long, OFileClassic> files = new OClosableLinkedContainer(1024);

    @Test
    public void testLoadStore() throws IOException {
        Random random = new Random();
        byte[][] pageData = new byte[200][];
        long fileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        final String nativeFileName = WOWCacheTestIT.wowCache.nativeFileNameById(fileId);
        for (int i = 0; i < (pageData.length); i++) {
            byte[] data = new byte[8];
            random.nextBytes(data);
            pageData[i] = data;
            final int pageIndex = WOWCacheTestIT.wowCache.allocateNewPage(fileId);
            Assert.assertEquals(i, pageIndex);
            final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, i, 1, new OModifiableBoolean(), false)[0];
            cachePointer.acquireExclusiveLock();
            ByteBuffer buffer = cachePointer.getBufferDuplicate();
            buffer.position(WOWCacheTestIT.systemOffset);
            buffer.put(data);
            cachePointer.releaseExclusiveLock();
            WOWCacheTestIT.wowCache.store(fileId, i, cachePointer);
            cachePointer.decrementReadersReferrer();
        }
        for (int i = 0; i < (pageData.length); i++) {
            byte[] dataOne = pageData[i];
            OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, i, 1, new OModifiableBoolean(), true)[0];
            byte[] dataTwo = new byte[8];
            ByteBuffer buffer = cachePointer.getBufferDuplicate();
            buffer.position(WOWCacheTestIT.systemOffset);
            buffer.get(dataTwo);
            cachePointer.decrementReadersReferrer();
            Assert.assertArrayEquals(dataTwo, dataOne);
        }
        WOWCacheTestIT.wowCache.flush();
        for (int i = 0; i < (pageData.length); i++) {
            byte[] dataContent = pageData[i];
            assertFile(i, dataContent, new OLogSequenceNumber(0, 0), nativeFileName);
        }
    }

    @Test
    public void testDataUpdate() throws Exception {
        final NavigableMap<Long, byte[]> pageIndexDataMap = new TreeMap<>();
        long fileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        final String nativeFileName = WOWCacheTestIT.wowCache.nativeFileNameById(fileId);
        Random random = new Random();
        for (int i = 0; i < 2048; i++) {
            WOWCacheTestIT.wowCache.allocateNewPage(fileId);
        }
        for (int i = 0; i < 600; i++) {
            long pageIndex = random.nextInt(2048);
            byte[] data = new byte[8];
            random.nextBytes(data);
            pageIndexDataMap.put(pageIndex, data);
            final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, pageIndex, 1, new OModifiableBoolean(), false)[0];
            cachePointer.acquireExclusiveLock();
            ByteBuffer buffer = cachePointer.getBufferDuplicate();
            buffer.position(WOWCacheTestIT.systemOffset);
            buffer.put(data);
            cachePointer.releaseExclusiveLock();
            WOWCacheTestIT.wowCache.store(fileId, pageIndex, cachePointer);
            cachePointer.decrementReadersReferrer();
        }
        for (Map.Entry<Long, byte[]> entry : pageIndexDataMap.entrySet()) {
            long pageIndex = entry.getKey();
            byte[] dataOne = entry.getValue();
            OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, pageIndex, 1, new OModifiableBoolean(), true)[0];
            byte[] dataTwo = new byte[8];
            ByteBuffer buffer = cachePointer.getBufferDuplicate();
            buffer.position(WOWCacheTestIT.systemOffset);
            buffer.get(dataTwo);
            cachePointer.decrementReadersReferrer();
            Assert.assertArrayEquals(dataTwo, dataOne);
        }
        for (int i = 0; i < 300; i++) {
            long desiredIndex = random.nextInt(2048);
            Long pageIndex = pageIndexDataMap.ceilingKey(desiredIndex);
            if (pageIndex == null)
                pageIndex = pageIndexDataMap.floorKey(desiredIndex);

            byte[] data = new byte[8];
            random.nextBytes(data);
            pageIndexDataMap.put(pageIndex, data);
            final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, pageIndex, 1, new OModifiableBoolean(), true)[0];
            cachePointer.acquireExclusiveLock();
            ByteBuffer buffer = cachePointer.getBufferDuplicate();
            buffer.position(WOWCacheTestIT.systemOffset);
            buffer.put(data);
            cachePointer.releaseExclusiveLock();
            WOWCacheTestIT.wowCache.store(fileId, pageIndex, cachePointer);
            cachePointer.decrementReadersReferrer();
        }
        for (Map.Entry<Long, byte[]> entry : pageIndexDataMap.entrySet()) {
            long pageIndex = entry.getKey();
            byte[] dataOne = entry.getValue();
            OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, pageIndex, 1, new OModifiableBoolean(), true)[0];
            byte[] dataTwo = new byte[8];
            ByteBuffer buffer = cachePointer.getBufferDuplicate();
            buffer.position(WOWCacheTestIT.systemOffset);
            buffer.get(dataTwo);
            cachePointer.decrementReadersReferrer();
            Assert.assertArrayEquals(dataTwo, dataOne);
        }
        WOWCacheTestIT.wowCache.flush();
        for (Map.Entry<Long, byte[]> entry : pageIndexDataMap.entrySet()) {
            assertFile(entry.getKey(), entry.getValue(), new OLogSequenceNumber(0, 0), nativeFileName);
        }
    }

    @Test
    public void testFileRestore() throws IOException {
        final long nonDelFileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        final long fileId = WOWCacheTestIT.wowCache.addFile("removedFile.del");
        final String removedNativeFileName = WOWCacheTestIT.wowCache.nativeFileNameById(fileId);
        WOWCacheTestIT.wowCache.deleteFile(fileId);
        File deletedFile = WOWCacheTestIT.storagePath.resolve(removedNativeFileName).toFile();
        Assert.assertTrue((!(deletedFile.exists())));
        String fileName = WOWCacheTestIT.wowCache.restoreFileById(fileId);
        Assert.assertEquals(fileName, "removedFile.del");
        fileName = WOWCacheTestIT.wowCache.restoreFileById(nonDelFileId);
        Assert.assertNull(fileName);
        Assert.assertTrue(deletedFile.exists());
        fileName = WOWCacheTestIT.wowCache.restoreFileById(1525454L);
        Assert.assertNull(fileName);
        WOWCacheTestIT.wowCache.deleteFile(fileId);
        Assert.assertTrue((!(deletedFile.exists())));
    }

    @Test
    public void testFileRestoreAfterClose() throws Exception {
        final long nonDelFileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        final long fileId = WOWCacheTestIT.wowCache.addFile("removedFile.del");
        final String removedNativeFileName = WOWCacheTestIT.wowCache.nativeFileNameById(fileId);
        WOWCacheTestIT.wowCache.deleteFile(fileId);
        File deletedFile = WOWCacheTestIT.storagePath.resolve(removedNativeFileName).toFile();
        Assert.assertTrue((!(deletedFile.exists())));
        WOWCacheTestIT.wowCache.close();
        WOWCacheTestIT.writeAheadLog.close();
        initBuffer();
        String fileName = WOWCacheTestIT.wowCache.restoreFileById(fileId);
        Assert.assertEquals(fileName, "removedFile.del");
        fileName = WOWCacheTestIT.wowCache.restoreFileById(nonDelFileId);
        Assert.assertNull(fileName);
        Assert.assertTrue(deletedFile.exists());
        fileName = WOWCacheTestIT.wowCache.restoreFileById(1525454L);
        Assert.assertNull(fileName);
        WOWCacheTestIT.wowCache.deleteFile(fileId);
        Assert.assertTrue((!(deletedFile.exists())));
    }

    @Test
    public void testChecksumFailure() throws IOException {
        WOWCacheTestIT.wowCache.setChecksumMode(StoreAndThrow);
        final long fileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        Assert.assertEquals(0, WOWCacheTestIT.wowCache.allocateNewPage(fileId));
        final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), false)[0];
        cachePointer.acquireExclusiveLock();
        final ByteBuffer buffer = cachePointer.getBufferDuplicate();
        buffer.position(WOWCacheTestIT.systemOffset);
        buffer.put(new byte[buffer.remaining()]);
        cachePointer.releaseExclusiveLock();
        WOWCacheTestIT.wowCache.store(fileId, 0, cachePointer);
        cachePointer.decrementReadersReferrer();
        WOWCacheTestIT.wowCache.flush();
        final Path path = WOWCacheTestIT.storagePath.resolve(WOWCacheTestIT.wowCache.nativeFileNameById(fileId));
        final OFileClassic file = new OFileClassic(path);
        file.open();
        file.writeByte(WOWCacheTestIT.systemOffset, ((byte) (1)));
        file.close();
        try {
            WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), true);
            Assert.fail();
        } catch (OStorageException e) {
            // ok
        }
    }

    @Test
    public void testMagicFailure() throws IOException {
        WOWCacheTestIT.wowCache.setChecksumMode(StoreAndThrow);
        final long fileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        Assert.assertEquals(0, WOWCacheTestIT.wowCache.allocateNewPage(fileId));
        final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), false)[0];
        cachePointer.acquireExclusiveLock();
        final ByteBuffer buffer = cachePointer.getBufferDuplicate();
        buffer.position(WOWCacheTestIT.systemOffset);
        buffer.put(new byte[buffer.remaining()]);
        cachePointer.releaseExclusiveLock();
        WOWCacheTestIT.wowCache.store(fileId, 0, cachePointer);
        cachePointer.decrementReadersReferrer();
        WOWCacheTestIT.wowCache.flush();
        final Path path = WOWCacheTestIT.storagePath.resolve(WOWCacheTestIT.wowCache.nativeFileNameById(fileId));
        final OFileClassic file = new OFileClassic(path);
        file.open();
        file.writeByte(0, ((byte) (1)));
        file.close();
        try {
            WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), true);
            Assert.fail();
        } catch (OStorageException e) {
            // ok
        }
    }

    @Test
    public void testNoChecksumVerificationIfNotRequested() throws IOException {
        WOWCacheTestIT.wowCache.setChecksumMode(StoreAndThrow);
        final long fileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        Assert.assertEquals(0, WOWCacheTestIT.wowCache.allocateNewPage(fileId));
        final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), false)[0];
        cachePointer.acquireExclusiveLock();
        final ByteBuffer buffer = cachePointer.getBufferDuplicate();
        buffer.position(WOWCacheTestIT.systemOffset);
        buffer.put(new byte[buffer.remaining()]);
        cachePointer.releaseExclusiveLock();
        WOWCacheTestIT.wowCache.store(fileId, 0, cachePointer);
        cachePointer.decrementReadersReferrer();
        WOWCacheTestIT.wowCache.flush();
        final Path path = WOWCacheTestIT.storagePath.resolve(WOWCacheTestIT.wowCache.nativeFileNameById(fileId));
        final OFileClassic file = new OFileClassic(path);
        file.open();
        file.writeByte(WOWCacheTestIT.systemOffset, ((byte) (1)));
        file.close();
        WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), false)[0].decrementReadersReferrer();
    }

    @Test
    public void testNoChecksumFailureIfVerificationTurnedOff() throws IOException {
        WOWCacheTestIT.wowCache.setChecksumMode(Off);
        final long fileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        Assert.assertEquals(0, WOWCacheTestIT.wowCache.allocateNewPage(fileId));
        final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), true)[0];
        cachePointer.acquireExclusiveLock();
        final ByteBuffer buffer = cachePointer.getBufferDuplicate();
        buffer.position(WOWCacheTestIT.systemOffset);
        buffer.put(new byte[buffer.remaining()]);
        cachePointer.releaseExclusiveLock();
        WOWCacheTestIT.wowCache.store(fileId, 0, cachePointer);
        cachePointer.decrementReadersReferrer();
        WOWCacheTestIT.wowCache.flush();
        final Path path = WOWCacheTestIT.storagePath.resolve(WOWCacheTestIT.wowCache.nativeFileNameById(fileId));
        final OFileClassic file = new OFileClassic(path);
        file.open();
        file.writeByte(WOWCacheTestIT.systemOffset, ((byte) (1)));
        file.close();
        WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), true)[0].decrementReadersReferrer();
    }

    @Test
    public void testNoChecksumFailureIfVerificationTurnedOffOnLoad() throws IOException {
        WOWCacheTestIT.wowCache.setChecksumMode(Store);
        final long fileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        Assert.assertEquals(0, WOWCacheTestIT.wowCache.allocateNewPage(fileId));
        final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), true)[0];
        cachePointer.acquireExclusiveLock();
        final ByteBuffer buffer = cachePointer.getBufferDuplicate();
        buffer.position(WOWCacheTestIT.systemOffset);
        buffer.put(new byte[buffer.remaining()]);
        cachePointer.releaseExclusiveLock();
        WOWCacheTestIT.wowCache.store(fileId, 0, cachePointer);
        cachePointer.decrementReadersReferrer();
        WOWCacheTestIT.wowCache.flush();
        final Path path = WOWCacheTestIT.storagePath.resolve(WOWCacheTestIT.wowCache.nativeFileNameById(fileId));
        final OFileClassic file = new OFileClassic(path);
        file.open();
        file.writeByte(WOWCacheTestIT.systemOffset, ((byte) (1)));
        file.close();
        WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), true)[0].decrementReadersReferrer();
    }

    @Test
    public void testNoChecksumFailureIfNoChecksumProvided() throws IOException {
        WOWCacheTestIT.wowCache.setChecksumMode(Off);
        final long fileId = WOWCacheTestIT.wowCache.addFile(WOWCacheTestIT.fileName);
        Assert.assertEquals(0, WOWCacheTestIT.wowCache.allocateNewPage(fileId));
        final OCachePointer cachePointer = WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), true)[0];
        cachePointer.acquireExclusiveLock();
        final ByteBuffer buffer = cachePointer.getBufferDuplicate();
        buffer.position(WOWCacheTestIT.systemOffset);
        buffer.put(new byte[buffer.remaining()]);
        cachePointer.releaseExclusiveLock();
        WOWCacheTestIT.wowCache.store(fileId, 0, cachePointer);
        cachePointer.decrementReadersReferrer();
        WOWCacheTestIT.wowCache.flush();
        final Path path = WOWCacheTestIT.storagePath.resolve(WOWCacheTestIT.wowCache.nativeFileNameById(fileId));
        final OFileClassic file = new OFileClassic(path);
        file.open();
        file.writeByte(WOWCacheTestIT.systemOffset, ((byte) (1)));
        file.close();
        WOWCacheTestIT.wowCache.setChecksumMode(StoreAndThrow);
        WOWCacheTestIT.wowCache.load(fileId, 0, 1, new OModifiableBoolean(), true)[0].decrementReadersReferrer();
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

