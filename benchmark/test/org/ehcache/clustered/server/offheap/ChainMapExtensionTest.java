/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.server.offheap;


import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import org.ehcache.clustered.ChainUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.offheapstore.ReadWriteLockedOffHeapClockCache;
import org.terracotta.offheapstore.paging.OffHeapStorageArea;
import org.terracotta.offheapstore.paging.PageSource;
import org.terracotta.offheapstore.storage.PointerSize;
import org.terracotta.offheapstore.storage.portability.Portability;
import org.terracotta.offheapstore.storage.portability.WriteContext;
import org.terracotta.offheapstore.util.Factory;


/**
 * Test extensibility of chain map storage engine, including binary engine capabilities.
 */
public class ChainMapExtensionTest {
    private static final int ADDRESS_OFFSET = 0;

    private static final int HASH_OFFSET = 8;

    private static final int EXTENDED_HEADER_LENGTH = 16;

    private static final long NULL_ENCODING = Long.MAX_VALUE;

    private static final int STORAGE_KEY_LENGTH_OFFSET = 0;

    private static final int STORAGE_VALUE_LENGTH_OFFSET = 4;

    private static final int STORAGE_HEADER_OFFSET = 8;

    static final ByteBuffer EMPTY_HEADER_NODE;

    static {
        ByteBuffer emptyHeader = ByteBuffer.allocateDirect(ChainMapExtensionTest.EXTENDED_HEADER_LENGTH);
        emptyHeader.putLong(ChainMapExtensionTest.ADDRESS_OFFSET, ChainMapExtensionTest.NULL_ENCODING);
        emptyHeader.putLong(ChainMapExtensionTest.HASH_OFFSET, (-1));
        EMPTY_HEADER_NODE = emptyHeader.asReadOnlyBuffer();
    }

    @Test
    public void testAppend() {
        OffHeapChainMap<String> map = getChainMapWithExtendedStorageEngine();
        map.append("foo", ChainMapExtensionTest.buffer(1));
        Assert.assertThat(map.get("foo"), contains(ChainMapExtensionTest.element(1)));
        ChainStorageEngine<String> se = map.getStorageEngine();
        Assert.assertThat(se, CoreMatchers.is(CoreMatchers.instanceOf(ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine.class)));
        ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine<String> ese = ((ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine<String>) (se));
        map = getNewMap(ese);
        Assert.assertThat(map.get("foo"), contains(ChainMapExtensionTest.element(1)));
    }

    @Test
    public void testAppendAndReplace() {
        OffHeapChainMap<String> map = getChainMapWithExtendedStorageEngine();
        map.append("foo", ChainMapExtensionTest.buffer(1));
        Assert.assertThat(map.get("foo"), contains(ChainMapExtensionTest.element(1)));
        map.replaceAtHead("foo", ChainUtils.chainOf(ChainMapExtensionTest.buffer(1)), ChainUtils.chainOf());
        ChainStorageEngine<String> se = map.getStorageEngine();
        Assert.assertThat(se, CoreMatchers.is(CoreMatchers.instanceOf(ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine.class)));
        @SuppressWarnings("unchecked")
        ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine<String> ese = ((ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine) (se));
        map = getNewMap(ese);
        Assert.assertThat(map.get("foo"), Matchers.emptyIterable());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultipleAppendAndReplace() {
        OffHeapChainMap<String> map = getChainMapWithExtendedStorageEngine();
        for (int i = 1; i <= 20; i++) {
            map.append(("foo" + i), ChainMapExtensionTest.buffer(i));
            Assert.assertThat(map.get(("foo" + i)), contains(ChainMapExtensionTest.element(i)));
        }
        for (int i = 1; i <= 20; i++) {
            Assert.assertThat(map.getAndAppend(("foo" + i), ChainMapExtensionTest.buffer(1)), contains(ChainMapExtensionTest.element(i)));
        }
        for (int i = 10; i < 15; i++) {
            map.replaceAtHead(("foo" + i), ChainUtils.chainOf(ChainMapExtensionTest.buffer(i), ChainMapExtensionTest.buffer(1)), ChainUtils.chainOf());
        }
        ChainStorageEngine<String> se = map.getStorageEngine();
        Assert.assertThat(se, CoreMatchers.is(CoreMatchers.instanceOf(ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine.class)));
        ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine<String> ese = ((ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine<String>) (se));
        map = getNewMap(ese);
        for (int i = 1; i <= 20; i++) {
            if ((i < 10) || (i >= 15)) {
                Assert.assertThat(map.get(("foo" + i)), contains(ChainMapExtensionTest.element(i), ChainMapExtensionTest.element(1)));
            } else {
                Assert.assertThat(map.get(("foo" + i)), Matchers.emptyIterable());
            }
        }
    }

    private static final class ExtendedHeaderForTest {
        private final ByteBuffer data;

        private final WriteContext writeContext;

        ExtendedHeaderForTest(ByteBuffer buffer, WriteContext writeContext) {
            this.data = buffer;
            this.writeContext = writeContext;
        }

        long getAddress() {
            return getLong(ChainMapExtensionTest.ADDRESS_OFFSET);
        }

        void setAddress(long val) {
            writeContext.setLong(ChainMapExtensionTest.ADDRESS_OFFSET, val);
        }

        int getHash() {
            long hashAndSize = (getLong(ChainMapExtensionTest.HASH_OFFSET)) >> 32;
            return ((int) (hashAndSize));
        }

        int getSize() {
            long hashAndSize = getLong(ChainMapExtensionTest.HASH_OFFSET);
            return ((int) (hashAndSize));
        }

        void setHashAndSize(int hash, int size) {
            long val = (((long) (hash)) << 32) | size;
            writeContext.setLong(ChainMapExtensionTest.HASH_OFFSET, val);
        }

        private long getLong(int address) {
            return data.getLong(address);
        }
    }

    public static class ExtendedOffHeapChainStorageEngine<K> extends OffHeapChainStorageEngine<K> {
        private final OffHeapStorageArea extendedArea;

        private final Set<Long> chainAddresses;

        private volatile boolean bypassEngineCommands = false;

        public static <K> Factory<? extends ChainStorageEngine<K>> createFactory(PageSource source, Portability<? super K> keyPortability, int minPageSize, int maxPageSize, boolean thief, boolean victim, PageSource cachePageSource) {
            return ((Factory<ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine<K>>) (() -> new ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine<>(source, keyPortability, minPageSize, maxPageSize, thief, victim, cachePageSource)));
        }

        private ExtendedOffHeapChainStorageEngine(PageSource source, Portability<? super K> keyPortability, int minPageSize, int maxPageSize, boolean thief, boolean victim, PageSource cachePageSource) {
            super(source, keyPortability, minPageSize, maxPageSize, thief, victim, ChainMapExtensionTest.EMPTY_HEADER_NODE);
            this.extendedArea = new OffHeapStorageArea(PointerSize.LONG, new ExtendedEngineOwner(), cachePageSource, minPageSize, maxPageSize, thief, victim);
            this.chainAddresses = new HashSet<>();
        }

        @Override
        public Long writeMapping(K key, InternalChain value, int hash, int metadata) {
            bypassEngineCommands = true;
            try {
                return super.writeMapping(key, value, hash, metadata);
            } finally {
                bypassEngineCommands = false;
            }
        }

        @Override
        public void freeMapping(long encoding, int hash, boolean removal) {
            if (removal) {
                // free the chain here if we are removing..otherwise chainFreed will be invoked from within
                chainFreed(encoding);
            }
            super.freeMapping(encoding, hash, removal);
        }

        @Override
        public void chainAttached(long chainAddress) {
            localPut(chainAddress);
        }

        @Override
        public void chainFreed(long chainAddress) {
            if (bypassEngineCommands) {
                // do not do anything when in write mapping
                return;
            }
            localRemove(chainAddress);
        }

        @Override
        public void chainModified(long chainAddress) {
            if (bypassEngineCommands) {
                return;
            }
            localPut(chainAddress);
        }

        @Override
        public void chainMoved(long fromChainAddress, long toChainAddress) {
            if (bypassEngineCommands) {
                return;
            }
            localMove(fromChainAddress, toChainAddress);
        }

        private ChainMapExtensionTest.ExtendedHeaderForTest createAtExtensionAddress(long chainAddress) {
            return new ChainMapExtensionTest.ExtendedHeaderForTest(ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine.getExtensionHeader(chainAddress), ChainMapExtensionTest.ExtendedOffHeapChainStorageEngine.getExtensionWriteContext(chainAddress));
        }

        void replayIntoMap(ReadWriteLockedOffHeapClockCache<K, InternalChain> newMap) {
            Lock l = newMap.writeLock();
            l.lock();
            try {
                chainAddresses.forEach(( a) -> {
                    ChainMapExtensionTest.ExtendedHeaderForTest hdr = createAtExtensionAddress(a);
                    long address = hdr.getAddress();
                    int keyLength = extendedArea.readInt((address + (ChainMapExtensionTest.STORAGE_KEY_LENGTH_OFFSET)));
                    int valueLength = extendedArea.readInt((address + (ChainMapExtensionTest.STORAGE_VALUE_LENGTH_OFFSET)));
                    ByteBuffer keyBuffer = extendedArea.readBuffer((address + (ChainMapExtensionTest.STORAGE_HEADER_OFFSET)), keyLength);
                    ByteBuffer valueBuffer = extendedArea.readBuffer(((address + (ChainMapExtensionTest.STORAGE_HEADER_OFFSET)) + keyLength), valueLength);
                    newMap.installMappingForHashAndEncoding(hdr.getHash(), keyBuffer, valueBuffer, 0);
                });
            } finally {
                l.unlock();
            }
        }

        private void localPut(long chainAddress) {
            ByteBuffer keyBuffer = super.readBinaryKey(chainAddress);
            int hash = super.readKeyHash(chainAddress);
            ByteBuffer valueBuffer = super.readBinaryValue(chainAddress);
            writeToExtendedArea(chainAddress, hash, keyBuffer, valueBuffer);
        }

        private void writeToExtendedArea(long chainAddress, int hash, ByteBuffer keyBuffer, ByteBuffer valueBuffer) {
            ChainMapExtensionTest.ExtendedHeaderForTest hdr = createAtExtensionAddress(chainAddress);
            long address = hdr.getAddress();
            if (address != (ChainMapExtensionTest.NULL_ENCODING)) {
                // free previous
                extendedArea.free(address);
            } else {
                chainAddresses.add(chainAddress);
            }
            int size = ((2 * (Integer.BYTES)) + (keyBuffer.remaining())) + (valueBuffer.remaining());
            address = extendedArea.allocate(size);
            hdr.setAddress(address);
            hdr.setHashAndSize(hash, size);
            extendedArea.writeInt((address + (ChainMapExtensionTest.STORAGE_KEY_LENGTH_OFFSET)), keyBuffer.remaining());
            extendedArea.writeInt((address + (ChainMapExtensionTest.STORAGE_VALUE_LENGTH_OFFSET)), valueBuffer.remaining());
            extendedArea.writeBuffer((address + (ChainMapExtensionTest.STORAGE_HEADER_OFFSET)), keyBuffer.duplicate());
            extendedArea.writeBuffer(((address + (ChainMapExtensionTest.STORAGE_HEADER_OFFSET)) + (keyBuffer.remaining())), valueBuffer.duplicate());
        }

        private void localRemove(long chainAddress) {
            ChainMapExtensionTest.ExtendedHeaderForTest node = createAtExtensionAddress(chainAddress);
            long address = node.getAddress();
            if (address != (ChainMapExtensionTest.NULL_ENCODING)) {
                extendedArea.free(node.getAddress());
                chainAddresses.remove(chainAddress);
            }
            node.setAddress(ChainMapExtensionTest.NULL_ENCODING);
        }

        private void localMove(long fromChainAddress, long toChainAddress) {
            ChainMapExtensionTest.ExtendedHeaderForTest fromHeader = createAtExtensionAddress(fromChainAddress);
            ChainMapExtensionTest.ExtendedHeaderForTest toHeader = createAtExtensionAddress(toChainAddress);
            chainAddresses.remove(fromChainAddress);
            chainAddresses.add(toChainAddress);
            toHeader.setAddress(fromHeader.getAddress());
            toHeader.setHashAndSize(fromHeader.getHash(), fromHeader.getSize());
        }

        private class ExtendedEngineOwner implements OffHeapStorageArea.Owner {
            @Override
            public boolean evictAtAddress(long address, boolean shrink) {
                return false;
            }

            @Override
            public Lock writeLock() {
                return owner.writeLock();
            }

            @Override
            public boolean isThief() {
                return owner.isThiefForTableAllocations();
            }

            @Override
            public boolean moved(long from, long to) {
                // for now not supported
                return false;
            }

            @Override
            public int sizeOf(long address) {
                return ((extendedArea.readInt((address + (ChainMapExtensionTest.STORAGE_KEY_LENGTH_OFFSET)))) + (extendedArea.readInt((address + (ChainMapExtensionTest.STORAGE_VALUE_LENGTH_OFFSET))))) + (ChainMapExtensionTest.STORAGE_HEADER_OFFSET);
            }
        }
    }
}

