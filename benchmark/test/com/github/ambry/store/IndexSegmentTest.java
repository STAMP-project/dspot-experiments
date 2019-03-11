/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.store;


import IndexValue.Flags;
import IndexValue.Flags.Delete_Index;
import IndexValue.Flags.Ttl_Update_Index;
import TestUtils.RANDOM;
import Utils.Infinite_Time;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.config.StoreConfig;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static PersistentIndex.VERSION_2;


/**
 * Tests for {@link IndexSegment}s
 */
@RunWith(Parameterized.class)
public class IndexSegmentTest {
    private static final int CUSTOM_ID_SIZE = 10;

    private static final int KEY_SIZE = sizeInBytes();

    private static final int SMALLER_KEY_SIZE = sizeInBytes();

    private static final int LARGER_KEY_SIZE = sizeInBytes();

    private static final MockTime time = new MockTime();

    private static final long DELETE_FILE_SPAN_SIZE = 10;

    private static final long TTL_UPDATE_FILE_SPAN_SIZE = 7;

    private static final StoreKeyFactory STORE_KEY_FACTORY;

    static {
        try {
            STORE_KEY_FACTORY = Utils.getObj("com.github.ambry.store.MockIdFactory");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private final File tempDir;

    private final StoreMetrics metrics;

    private final short version;

    private StoreConfig config;

    private final Properties properties = new Properties();

    /**
     * Creates a temporary directory and sets up metrics.
     *
     * @param version
     * 		the version of the index
     * @param indexMemState
     * 		the value for {@link StoreConfig#storeIndexMemStateName}
     * @throws IOException
     * 		
     */
    public IndexSegmentTest(short version, IndexMemState indexMemState) throws IOException {
        tempDir = StoreTestUtils.createTempDirectory(("indexSegmentDir-" + (UtilsTest.getRandomString(10))));
        MetricRegistry metricRegistry = new MetricRegistry();
        metrics = new StoreMetrics(metricRegistry);
        this.version = version;
        setIndexMemState(indexMemState);
    }

    /**
     * Comprehensive tests for {@link IndexSegment}
     */
    @Test
    public void comprehensiveTest() throws StoreException, IOException {
        if ((version) == (VERSION_2)) {
            for (boolean includeSmall : new boolean[]{ false, true }) {
                for (boolean includeLarge : new boolean[]{ false, true }) {
                    doComprehensiveTest(version, includeSmall, includeLarge);
                }
            }
        } else {
            doComprehensiveTest(version, false, false);
        }
    }

    /**
     * Tests the case when {@link IndexSegment#writeIndexSegmentToFile(Offset)} is provided with different offsets <=
     * {@link IndexSegment#getEndOffset()} and makes sure that only the relevant parts of the segment are written to disk.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void partialWriteTest() throws StoreException, IOException {
        String prevLogSegmentName = LogSegmentNameHelper.getName(0, 0);
        String logSegmentName = LogSegmentNameHelper.getNextPositionName(prevLogSegmentName);
        Offset startOffset = new Offset(logSegmentName, 0);
        MockId id1 = new MockId(("0" + (UtilsTest.getRandomString(((IndexSegmentTest.CUSTOM_ID_SIZE) - 1)))));
        MockId id2 = new MockId(("1" + (UtilsTest.getRandomString(((IndexSegmentTest.CUSTOM_ID_SIZE) - 1)))));
        MockId id3 = new MockId(("2" + (UtilsTest.getRandomString(((IndexSegmentTest.CUSTOM_ID_SIZE) - 1)))));
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        IndexValue value1 = IndexValueTest.getIndexValue(1000, new Offset(logSegmentName, 0), Infinite_Time, IndexSegmentTest.time.milliseconds(), accountId, containerId, version);
        IndexValue value2 = IndexValueTest.getIndexValue(1000, new Offset(logSegmentName, 1000), ((IndexSegmentTest.time.milliseconds()) + 1), IndexSegmentTest.time.milliseconds(), accountId, containerId, version);
        IndexValue value3 = IndexValueTest.getIndexValue(1000, new Offset(logSegmentName, 2000), Infinite_Time, IndexSegmentTest.time.milliseconds(), accountId, containerId, version);
        IndexSegmentTest.time.sleep(TimeUnit.SECONDS.toMillis(1));
        // generate a TTL Update
        IndexValue ttlUpValue2 = IndexValueTest.getIndexValue(value2.getSize(), value2.getOffset(), Infinite_Time, IndexSegmentTest.time.milliseconds(), value2.getAccountId(), value2.getContainerId(), version);
        ttlUpValue2.setNewOffset(new Offset(logSegmentName, 3000));
        ttlUpValue2.setNewSize(50);
        ttlUpValue2.setFlag(Ttl_Update_Index);
        IndexSegmentTest.time.sleep(TimeUnit.SECONDS.toMillis(1));
        // generate a DELETE
        IndexValue delValue2 = IndexValueTest.getIndexValue(value2.getSize(), value2.getOffset(), ttlUpValue2.getFlags(), value2.getExpiresAtMs(), value2.getOffset().getOffset(), IndexSegmentTest.time.milliseconds(), value2.getAccountId(), value2.getContainerId(), version);
        delValue2.setNewOffset(new Offset(logSegmentName, 3050));
        delValue2.setNewSize(100);
        delValue2.setFlag(Delete_Index);
        IndexSegment indexSegment = generateIndexSegment(startOffset);
        // inserting in the opposite order by design to ensure that writes are based on offset ordering and not key ordering
        indexSegment.addEntry(new IndexEntry(id3, value1), new Offset(logSegmentName, 1000));
        indexSegment.addEntry(new IndexEntry(id2, value2), new Offset(logSegmentName, 2000));
        indexSegment.addEntry(new IndexEntry(id1, value3), new Offset(logSegmentName, 3000));
        indexSegment.addEntry(new IndexEntry(id2, ttlUpValue2), new Offset(logSegmentName, 3050));
        indexSegment.addEntry(new IndexEntry(id2, delValue2), new Offset(logSegmentName, 3150));
        // provide end offsets such that nothing is written
        checkNonCreationOfIndexSegmentFile(indexSegment, new Offset(prevLogSegmentName, 0));
        checkNonCreationOfIndexSegmentFile(indexSegment, new Offset(prevLogSegmentName, indexSegment.getStartOffset().getOffset()));
        checkNonCreationOfIndexSegmentFile(indexSegment, new Offset(logSegmentName, 0));
        List<MockId> shouldBeFound = new ArrayList<>();
        List<MockId> shouldNotBeFound = new ArrayList(Arrays.asList(id3, id2, id1));
        for (int safeEndPoint = 1000; safeEndPoint <= 3000; safeEndPoint += 1000) {
            shouldBeFound.add(shouldNotBeFound.remove(0));
            // repeat twice
            for (int i = 0; i < 2; i++) {
                indexSegment.writeIndexSegmentToFile(new Offset(logSegmentName, safeEndPoint));
                Journal journal = new Journal(tempDir.getAbsolutePath(), 3, 3);
                IndexSegment fromDisk = new IndexSegment(indexSegment.getFile(), false, IndexSegmentTest.STORE_KEY_FACTORY, config, metrics, journal, IndexSegmentTest.time);
                Assert.assertEquals("End offset not as expected", new Offset(logSegmentName, safeEndPoint), fromDisk.getEndOffset());
                Assert.assertEquals("Number of items incorrect", shouldBeFound.size(), fromDisk.getNumberOfItems());
                for (MockId id : shouldBeFound) {
                    verifyValues(fromDisk, id, 1, EnumSet.noneOf(Flags.class));
                }
                for (MockId id : shouldNotBeFound) {
                    Assert.assertNull("Values for key should not have been found", fromDisk.find(id));
                }
            }
        }
        // now persist the ttl update only
        indexSegment.writeIndexSegmentToFile(new Offset(logSegmentName, 3050));
        Journal journal = new Journal(tempDir.getAbsolutePath(), 3, 3);
        IndexSegment fromDisk = new IndexSegment(indexSegment.getFile(), false, IndexSegmentTest.STORE_KEY_FACTORY, config, metrics, journal, IndexSegmentTest.time);
        Assert.assertEquals("Number of items incorrect", 4, fromDisk.getNumberOfItems());
        for (MockId id : new MockId[]{ id1, id2, id3 }) {
            int valueCount = (id.equals(id2)) ? 2 : 1;
            EnumSet<IndexValue.Flags> flags = (id.equals(id2)) ? EnumSet.of(Ttl_Update_Index) : EnumSet.noneOf(Flags.class);
            verifyValues(fromDisk, id, valueCount, flags);
        }
        // now persist the delete too
        indexSegment.writeIndexSegmentToFile(new Offset(logSegmentName, 3150));
        journal = new Journal(tempDir.getAbsolutePath(), 3, 3);
        fromDisk = new IndexSegment(indexSegment.getFile(), false, IndexSegmentTest.STORE_KEY_FACTORY, config, metrics, journal, IndexSegmentTest.time);
        Assert.assertEquals("Number of items incorrect", 5, fromDisk.getNumberOfItems());
        for (MockId id : new MockId[]{ id1, id2, id3 }) {
            int valueCount = (id.equals(id2)) ? 3 : 1;
            EnumSet<IndexValue.Flags> flags = (id.equals(id2)) ? EnumSet.of(Delete_Index, Ttl_Update_Index) : EnumSet.noneOf(Flags.class);
            verifyValues(fromDisk, id, valueCount, flags);
        }
    }

    /**
     * Tests some corner cases with
     * {@link IndexSegment#getIndexEntriesSince(StoreKey, FindEntriesCondition, List, AtomicLong, boolean)}
     * - tests that all values of a key are returned even if the find entries condition max size expires when the first
     * value is loaded
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void getIndexEntriesCornerCasesTest() throws StoreException, IOException {
        String logSegmentName = LogSegmentNameHelper.getName(0, 0);
        MockId id1 = new MockId(("0" + (UtilsTest.getRandomString(((IndexSegmentTest.CUSTOM_ID_SIZE) - 1)))));
        MockId id2 = new MockId(("1" + (UtilsTest.getRandomString(((IndexSegmentTest.CUSTOM_ID_SIZE) - 1)))));
        MockId id3 = new MockId(("2" + (UtilsTest.getRandomString(((IndexSegmentTest.CUSTOM_ID_SIZE) - 1)))));
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        IndexValue value1 = IndexValueTest.getIndexValue(1000, new Offset(logSegmentName, 0), Infinite_Time, IndexSegmentTest.time.milliseconds(), accountId, containerId, version);
        IndexValue value2 = IndexValueTest.getIndexValue(1000, new Offset(logSegmentName, 1000), Infinite_Time, IndexSegmentTest.time.milliseconds(), accountId, containerId, version);
        IndexValue value3 = IndexValueTest.getIndexValue(1000, new Offset(logSegmentName, 2000), Infinite_Time, IndexSegmentTest.time.milliseconds(), accountId, containerId, version);
        IndexSegmentTest.time.sleep(TimeUnit.SECONDS.toMillis(1));
        // generate a TTL Update
        IndexValue ttlUpValue2 = IndexValueTest.getIndexValue(value2.getSize(), value2.getOffset(), Infinite_Time, IndexSegmentTest.time.milliseconds(), value2.getAccountId(), value2.getContainerId(), version);
        ttlUpValue2.setNewOffset(new Offset(logSegmentName, 3000));
        ttlUpValue2.setNewSize(50);
        ttlUpValue2.setFlag(Ttl_Update_Index);
        IndexSegmentTest.time.sleep(TimeUnit.SECONDS.toMillis(1));
        // generate a DELETE
        IndexValue delValue2 = IndexValueTest.getIndexValue(value2.getSize(), value2.getOffset(), ttlUpValue2.getFlags(), value2.getExpiresAtMs(), value2.getOffset().getOffset(), IndexSegmentTest.time.milliseconds(), value2.getAccountId(), value2.getContainerId(), version);
        delValue2.setNewOffset(new Offset(logSegmentName, 3050));
        delValue2.setNewSize(100);
        delValue2.setFlag(Delete_Index);
        IndexSegment indexSegment = generateIndexSegment(new Offset(logSegmentName, 0));
        // inserting in the opposite order by design to ensure that writes are based on offset ordering and not key ordering
        indexSegment.addEntry(new IndexEntry(id3, value1), new Offset(logSegmentName, 1000));
        indexSegment.addEntry(new IndexEntry(id2, value2), new Offset(logSegmentName, 2000));
        indexSegment.addEntry(new IndexEntry(id1, value3), new Offset(logSegmentName, 3000));
        indexSegment.addEntry(new IndexEntry(id2, ttlUpValue2), new Offset(logSegmentName, 3050));
        indexSegment.addEntry(new IndexEntry(id2, delValue2), new Offset(logSegmentName, 3150));
        indexSegment.writeIndexSegmentToFile(new Offset(logSegmentName, 3150));
        indexSegment.seal();
        List<IndexEntry> entries = new ArrayList<>();
        for (boolean sealed : new boolean[]{ false, true }) {
            Journal journal = new Journal(tempDir.getAbsolutePath(), 3, 3);
            IndexSegment fromDisk = new IndexSegment(indexSegment.getFile(), sealed, IndexSegmentTest.STORE_KEY_FACTORY, config, metrics, journal, IndexSegmentTest.time);
            // getIndexEntriesSince with maxSize = 0 should not return anything
            FindEntriesCondition condition = new FindEntriesCondition(0);
            Assert.assertFalse("getIndexEntriesSince() should not return anything", fromDisk.getIndexEntriesSince(null, condition, entries, new AtomicLong(0), false));
            Assert.assertEquals("There should be no entries returned", 0, entries.size());
            // getIndexEntriesSince with maxSize <= 1000 should return only the first key (id1)
            condition = new FindEntriesCondition(1000);
            Assert.assertTrue("getIndexEntriesSince() should return one entry", fromDisk.getIndexEntriesSince(null, condition, entries, new AtomicLong(0), false));
            Assert.assertEquals("There should be one entry returned", 1, entries.size());
            Assert.assertEquals("Key in entry is incorrect", id1, entries.get(0).getKey());
            Assert.assertEquals("Value in entry is incorrect", value3.getBytes(), entries.get(0).getValue().getBytes());
            entries.clear();
            // getIndexEntriesSince with maxSize > 1000 and <= 2150 should return four entries
            for (int maxSize : new int[]{ 1001, 2050, 2150 }) {
                condition = new FindEntriesCondition(maxSize);
                Assert.assertTrue("getIndexEntriesSince() should return entries", fromDisk.getIndexEntriesSince(null, condition, entries, new AtomicLong(0), false));
                Assert.assertEquals("There should be four entries returned", 4, entries.size());
                Assert.assertEquals("Key in entry is incorrect", id1, entries.get(0).getKey());
                Assert.assertEquals("Value in entry is incorrect", value3.getBytes(), entries.get(0).getValue().getBytes());
                Assert.assertEquals("Key in entry is incorrect", id2, entries.get(1).getKey());
                Assert.assertEquals("Value in entry is incorrect", value2.getBytes(), entries.get(1).getValue().getBytes());
                Assert.assertEquals("Key in entry is incorrect", id2, entries.get(2).getKey());
                Assert.assertEquals("Value in entry is incorrect", ttlUpValue2.getBytes(), entries.get(2).getValue().getBytes());
                Assert.assertEquals("Key in entry is incorrect", id2, entries.get(3).getKey());
                Assert.assertEquals("Value in entry is incorrect", delValue2.getBytes(), entries.get(3).getValue().getBytes());
                entries.clear();
            }
            // getIndexEntriesSince with maxSize > 2150 should return five entries
            condition = new FindEntriesCondition(2151);
            Assert.assertTrue("getIndexEntriesSince() should return entries", fromDisk.getIndexEntriesSince(null, condition, entries, new AtomicLong(0), false));
            Assert.assertEquals("There should be five entries returned", 5, entries.size());
            Assert.assertEquals("Key in entry is incorrect", id1, entries.get(0).getKey());
            Assert.assertEquals("Value in entry is incorrect", value3.getBytes(), entries.get(0).getValue().getBytes());
            Assert.assertEquals("Key in entry is incorrect", id2, entries.get(1).getKey());
            Assert.assertEquals("Value in entry is incorrect", value2.getBytes(), entries.get(1).getValue().getBytes());
            Assert.assertEquals("Key in entry is incorrect", id2, entries.get(2).getKey());
            Assert.assertEquals("Value in entry is incorrect", ttlUpValue2.getBytes(), entries.get(2).getValue().getBytes());
            Assert.assertEquals("Key in entry is incorrect", id2, entries.get(3).getKey());
            Assert.assertEquals("Value in entry is incorrect", delValue2.getBytes(), entries.get(3).getValue().getBytes());
            Assert.assertEquals("Key in entry is incorrect", id3, entries.get(4).getKey());
            Assert.assertEquals("Value in entry is incorrect", value1.getBytes(), entries.get(4).getValue().getBytes());
            entries.clear();
            // getIndexEntriesSince with maxSize > 0 and <= 1150 starting from id2 should return two entries
            for (int maxSize : new int[]{ 1, 1050, 1150 }) {
                condition = new FindEntriesCondition(maxSize);
                Assert.assertTrue("getIndexEntriesSince() should return entries", fromDisk.getIndexEntriesSince(id1, condition, entries, new AtomicLong(0), false));
                Assert.assertEquals("There should be three entries returned", 3, entries.size());
                Assert.assertEquals("Key in entry is incorrect", id2, entries.get(0).getKey());
                Assert.assertEquals("Value in entry is incorrect", value2.getBytes(), entries.get(0).getValue().getBytes());
                Assert.assertEquals("Key in entry is incorrect", id2, entries.get(1).getKey());
                Assert.assertEquals("Value in entry is incorrect", ttlUpValue2.getBytes(), entries.get(1).getValue().getBytes());
                Assert.assertEquals("Key in entry is incorrect", id2, entries.get(2).getKey());
                Assert.assertEquals("Value in entry is incorrect", delValue2.getBytes(), entries.get(2).getValue().getBytes());
                entries.clear();
            }
        }
    }
}

