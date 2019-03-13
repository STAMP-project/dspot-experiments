package com.orientechnologies.orient.core.storage.cluster;


import OPaginatedCluster.RECORD_STATUS.REMOVED;
import OPaginatedClusterV0.RECORD_STATUS.PRESENT;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OPaginatedClusterException;
import com.orientechnologies.orient.core.storage.OPhysicalPosition;
import com.orientechnologies.orient.core.storage.ORawBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import static OClusterPage.MAX_RECORD_SIZE;


public abstract class LocalPaginatedClusterAbstract {
    protected static String buildDirectory;

    protected static OPaginatedCluster paginatedCluster;

    public static ODatabaseDocumentTx databaseDocumentTx;

    @Test
    public void testDeleteRecordAndAddNewOnItsPlace() throws IOException {
        byte[] smallRecord = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 0);
        LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(physicalPosition.clusterPosition);
        recordVersion = 0;
        Assert.assertEquals(recordVersion, 0);
        physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 1);
        Assert.assertEquals(physicalPosition.recordVersion, recordVersion);
    }

    @Test
    public void testAddOneSmallRecord() throws IOException {
        byte[] smallRecord = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 0);
        ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(physicalPosition.clusterPosition, false);
        Assert.assertNotNull(rawBuffer);
        Assert.assertEquals(rawBuffer.version, recordVersion);
        Assertions.assertThat(rawBuffer.buffer).isEqualTo(smallRecord);
        Assert.assertEquals(rawBuffer.recordType, 1);
    }

    @Test
    public void testAddOneBigRecord() throws IOException {
        byte[] bigRecord = new byte[(2 * 65536) + 100];
        Random mersenneTwisterFast = new Random();
        mersenneTwisterFast.nextBytes(bigRecord);
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 0);
        ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(physicalPosition.clusterPosition, false);
        Assert.assertNotNull(rawBuffer);
        Assert.assertEquals(rawBuffer.version, recordVersion);
        Assertions.assertThat(rawBuffer.buffer).isEqualTo(bigRecord);
        Assert.assertEquals(rawBuffer.recordType, 1);
    }

    @Test
    public void testAddManySmallRecords() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testAddManySmallRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt(((MAX_RECORD_SIZE) - 1))) + 1;
            byte[] smallRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(smallRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, smallRecord);
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testAddManyBigRecords() throws IOException {
        final int records = 5000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testAddManyBigRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = ((mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + (MAX_RECORD_SIZE)) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testAddManyRecords() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testAddManyRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + 1;
            byte[] smallRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(smallRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, smallRecord);
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testAllocatePositionMap() throws IOException {
        OPhysicalPosition position = LocalPaginatedClusterAbstract.paginatedCluster.allocatePosition(((byte) ('d')));
        Assert.assertTrue(((position.clusterPosition) >= 0));
        ORawBuffer rec = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(position.clusterPosition, false);
        Assert.assertNull(rec);
        LocalPaginatedClusterAbstract.paginatedCluster.createRecord(new byte[20], 1, ((byte) ('d')), position);
        rec = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(position.clusterPosition, false);
        Assert.assertNotNull(rec);
    }

    @Test
    public void testManyAllocatePositionMap() throws IOException {
        final int records = 10000;
        List<OPhysicalPosition> positions = new ArrayList<>();
        for (int i = 0; i < records; i++) {
            OPhysicalPosition position = LocalPaginatedClusterAbstract.paginatedCluster.allocatePosition(((byte) ('d')));
            Assert.assertTrue(((position.clusterPosition) >= 0));
            ORawBuffer rec = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(position.clusterPosition, false);
            Assert.assertNull(rec);
            positions.add(position);
        }
        for (int i = 0; i < records; i++) {
            OPhysicalPosition position = positions.get(i);
            LocalPaginatedClusterAbstract.paginatedCluster.createRecord(new byte[20], 1, ((byte) ('d')), position);
            ORawBuffer rec = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(position.clusterPosition, false);
            Assert.assertNotNull(rec);
        }
    }

    @Test
    public void testRemoveHalfSmallRecords() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testRemoveHalfSmallRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt(((MAX_RECORD_SIZE) - 1))) + 1;
            byte[] smallRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(smallRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, smallRecord);
        }
        int deletedRecords = 0;
        Assert.assertEquals(records, LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
        Set<Long> deletedPositions = new HashSet<>();
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                deletedPositions.add(clusterPosition);
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(clusterPosition));
                deletedRecords++;
                Assert.assertEquals((records - deletedRecords), LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
                positionIterator.remove();
            }
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - deletedRecords));
        for (long deletedPosition : deletedPositions) {
            Assert.assertNull(LocalPaginatedClusterAbstract.paginatedCluster.readRecord(deletedPosition, false));
            Assert.assertFalse(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(deletedPosition));
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testHideHalfSmallRecords() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testHideHalfSmallRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt(((MAX_RECORD_SIZE) - 1))) + 1;
            byte[] smallRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(smallRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, smallRecord);
        }
        int hiddenRecords = 0;
        Assert.assertEquals(records, LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
        Set<Long> hiddenPositions = new HashSet<>();
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                hiddenPositions.add(clusterPosition);
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.hideRecord(clusterPosition));
                hiddenRecords++;
                Assert.assertEquals((records - hiddenRecords), LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
                positionIterator.remove();
            }
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - hiddenRecords));
        for (long deletedPosition : hiddenPositions) {
            Assert.assertNull(LocalPaginatedClusterAbstract.paginatedCluster.readRecord(deletedPosition, false));
            Assert.assertFalse(LocalPaginatedClusterAbstract.paginatedCluster.hideRecord(deletedPosition));
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testRemoveHalfBigRecords() throws IOException {
        final int records = 5000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testRemoveHalfBigRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = ((mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + (MAX_RECORD_SIZE)) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        int deletedRecords = 0;
        Assert.assertEquals(records, LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
        Set<Long> deletedPositions = new HashSet<>();
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                deletedPositions.add(clusterPosition);
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(clusterPosition));
                deletedRecords++;
                Assert.assertEquals((records - deletedRecords), LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
                positionIterator.remove();
            }
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - deletedRecords));
        for (long deletedPosition : deletedPositions) {
            Assert.assertNull(LocalPaginatedClusterAbstract.paginatedCluster.readRecord(deletedPosition, false));
            Assert.assertFalse(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(deletedPosition));
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testHideHalfBigRecords() throws IOException {
        final int records = 5000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testHideHalfBigRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = ((mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + (MAX_RECORD_SIZE)) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        int hiddenRecords = 0;
        Assert.assertEquals(records, LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
        Set<Long> hiddenPositions = new HashSet<>();
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                hiddenPositions.add(clusterPosition);
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.hideRecord(clusterPosition));
                hiddenRecords++;
                Assert.assertEquals((records - hiddenRecords), LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
                positionIterator.remove();
            }
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - hiddenRecords));
        for (long hiddenPosition : hiddenPositions) {
            Assert.assertNull(LocalPaginatedClusterAbstract.paginatedCluster.readRecord(hiddenPosition, false));
            Assert.assertFalse(LocalPaginatedClusterAbstract.paginatedCluster.hideRecord(hiddenPosition));
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testRemoveHalfRecords() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testRemoveHalfRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((3 * (MAX_RECORD_SIZE)))) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        int deletedRecords = 0;
        Assert.assertEquals(records, LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
        Set<Long> deletedPositions = new HashSet<>();
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                deletedPositions.add(clusterPosition);
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(clusterPosition));
                deletedRecords++;
                Assert.assertEquals((records - deletedRecords), LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
                positionIterator.remove();
            }
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - deletedRecords));
        for (long deletedPosition : deletedPositions) {
            Assert.assertNull(LocalPaginatedClusterAbstract.paginatedCluster.readRecord(deletedPosition, false));
            Assert.assertFalse(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(deletedPosition));
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testHideHalfRecords() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testHideHalfRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((3 * (MAX_RECORD_SIZE)))) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        int hiddenRecords = 0;
        Assert.assertEquals(records, LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
        Set<Long> hiddenPositions = new HashSet<>();
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                hiddenPositions.add(clusterPosition);
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.hideRecord(clusterPosition));
                hiddenRecords++;
                Assert.assertEquals((records - hiddenRecords), LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
                positionIterator.remove();
            }
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - hiddenRecords));
        for (long deletedPosition : hiddenPositions) {
            Assert.assertNull(LocalPaginatedClusterAbstract.paginatedCluster.readRecord(deletedPosition, false));
            Assert.assertFalse(LocalPaginatedClusterAbstract.paginatedCluster.hideRecord(deletedPosition));
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            Assert.assertEquals(rawBuffer.recordType, 2);
        }
    }

    @Test
    public void testRemoveHalfRecordsAndAddAnotherHalfAgain() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testRemoveHalfRecordsAndAddAnotherHalfAgain seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((3 * (MAX_RECORD_SIZE)))) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        int deletedRecords = 0;
        Assert.assertEquals(records, LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(clusterPosition));
                deletedRecords++;
                Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - deletedRecords));
                positionIterator.remove();
            }
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - deletedRecords));
        for (int i = 0; i < (records / 2); i++) {
            int recordSize = (mersenneTwisterFast.nextInt((3 * (MAX_RECORD_SIZE)))) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), ((long) ((1.5 * records) - deletedRecords)));
    }

    @Test
    public void testHideHalfRecordsAndAddAnotherHalfAgain() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testHideHalfRecordsAndAddAnotherHalfAgain seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((3 * (MAX_RECORD_SIZE)))) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        int hiddenRecords = 0;
        Assert.assertEquals(records, LocalPaginatedClusterAbstract.paginatedCluster.getEntries());
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.hideRecord(clusterPosition));
                hiddenRecords++;
                Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - hiddenRecords));
                positionIterator.remove();
            }
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), (records - hiddenRecords));
        for (int i = 0; i < (records / 2); i++) {
            int recordSize = (mersenneTwisterFast.nextInt((3 * (MAX_RECORD_SIZE)))) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), ((long) ((1.5 * records) - hiddenRecords)));
    }

    @Test
    public void testUpdateOneSmallRecord() throws IOException {
        byte[] smallRecord = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 0);
        recordVersion++;
        smallRecord = new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3 };
        LocalPaginatedClusterAbstract.paginatedCluster.updateRecord(physicalPosition.clusterPosition, smallRecord, recordVersion, ((byte) (2)));
        ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(physicalPosition.clusterPosition, false);
        Assert.assertNotNull(rawBuffer);
        Assert.assertEquals(rawBuffer.version, recordVersion);
        // Assert.assertEquals(rawBuffer.buffer, smallRecord);
        Assertions.assertThat(rawBuffer.buffer).isEqualTo(smallRecord);
        Assert.assertEquals(rawBuffer.recordType, 2);
    }

    @Test
    public void testUpdateOneSmallRecordVersionIsLowerCurrentOne() throws IOException {
        byte[] smallRecord = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 0);
        int updateRecordVersion = 0;
        updateRecordVersion++;
        smallRecord = new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3 };
        LocalPaginatedClusterAbstract.paginatedCluster.updateRecord(physicalPosition.clusterPosition, smallRecord, updateRecordVersion, ((byte) (2)));
        ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(physicalPosition.clusterPosition, false);
        Assert.assertNotNull(rawBuffer);
        Assert.assertEquals(rawBuffer.version, updateRecordVersion);
        // Assert.assertEquals(rawBuffer.buffer, smallRecord);
        Assertions.assertThat(rawBuffer.buffer).isEqualTo(smallRecord);
        Assert.assertEquals(rawBuffer.recordType, 2);
    }

    @Test
    public void testUpdateOneSmallRecordVersionIsMinusTwo() throws IOException {
        byte[] smallRecord = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 0);
        int updateRecordVersion;
        updateRecordVersion = -2;
        smallRecord = new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3 };
        LocalPaginatedClusterAbstract.paginatedCluster.updateRecord(physicalPosition.clusterPosition, smallRecord, updateRecordVersion, ((byte) (2)));
        ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(physicalPosition.clusterPosition, false);
        Assert.assertNotNull(rawBuffer);
        Assert.assertEquals(rawBuffer.version, updateRecordVersion);
        // Assert.assertEquals(rawBuffer.buffer, smallRecord);
        Assertions.assertThat(rawBuffer.buffer).isEqualTo(smallRecord);
        Assert.assertEquals(rawBuffer.recordType, 2);
    }

    @Test
    public void testUpdateOneBigRecord() throws IOException {
        byte[] bigRecord = new byte[(2 * 65536) + 100];
        Random mersenneTwisterFast = new Random();
        mersenneTwisterFast.nextBytes(bigRecord);
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 0);
        recordVersion++;
        bigRecord = new byte[(2 * 65536) + 20];
        mersenneTwisterFast.nextBytes(bigRecord);
        LocalPaginatedClusterAbstract.paginatedCluster.updateRecord(physicalPosition.clusterPosition, bigRecord, recordVersion, ((byte) (2)));
        ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(physicalPosition.clusterPosition, false);
        Assert.assertNotNull(rawBuffer);
        Assert.assertEquals(rawBuffer.version, recordVersion);
        // Assert.assertEquals(rawBuffer.buffer, bigRecord);
        Assertions.assertThat(rawBuffer.buffer).isEqualTo(bigRecord);
        Assert.assertEquals(rawBuffer.recordType, 2);
    }

    @Test
    public void testUpdateManySmallRecords() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testUpdateManySmallRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        Set<Long> updatedPositions = new HashSet<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt(((MAX_RECORD_SIZE) - 1))) + 1;
            byte[] smallRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(smallRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, smallRecord);
        }
        int newRecordVersion;
        newRecordVersion = recordVersion;
        newRecordVersion++;
        for (long clusterPosition : positionRecordMap.keySet()) {
            if (mersenneTwisterFast.nextBoolean()) {
                int recordSize = (mersenneTwisterFast.nextInt(((MAX_RECORD_SIZE) - 1))) + 1;
                byte[] smallRecord = new byte[recordSize];
                mersenneTwisterFast.nextBytes(smallRecord);
                LocalPaginatedClusterAbstract.paginatedCluster.updateRecord(clusterPosition, smallRecord, newRecordVersion, ((byte) (3)));
                positionRecordMap.put(clusterPosition, smallRecord);
                updatedPositions.add(clusterPosition);
            }
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            if (updatedPositions.contains(entry.getKey())) {
                Assert.assertEquals(rawBuffer.version, newRecordVersion);
                Assert.assertEquals(rawBuffer.recordType, 3);
            } else {
                Assert.assertEquals(rawBuffer.version, recordVersion);
                Assert.assertEquals(rawBuffer.recordType, 2);
            }
        }
    }

    @Test
    public void testUpdateManyBigRecords() throws IOException {
        final int records = 5000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testUpdateManyBigRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        Set<Long> updatedPositions = new HashSet<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = ((mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + (MAX_RECORD_SIZE)) + 1;
            byte[] bigRecord = new byte[recordSize];
            mersenneTwisterFast.nextBytes(bigRecord);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(bigRecord, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, bigRecord);
        }
        int newRecordVersion;
        newRecordVersion = recordVersion;
        newRecordVersion++;
        for (long clusterPosition : positionRecordMap.keySet()) {
            if (mersenneTwisterFast.nextBoolean()) {
                int recordSize = ((mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + (MAX_RECORD_SIZE)) + 1;
                byte[] bigRecord = new byte[recordSize];
                mersenneTwisterFast.nextBytes(bigRecord);
                LocalPaginatedClusterAbstract.paginatedCluster.updateRecord(clusterPosition, bigRecord, newRecordVersion, ((byte) (3)));
                positionRecordMap.put(clusterPosition, bigRecord);
                updatedPositions.add(clusterPosition);
            }
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            if (updatedPositions.contains(entry.getKey())) {
                Assert.assertEquals(rawBuffer.version, newRecordVersion);
                Assert.assertEquals(rawBuffer.recordType, 3);
            } else {
                Assert.assertEquals(rawBuffer.version, recordVersion);
                Assert.assertEquals(rawBuffer.recordType, 2);
            }
        }
    }

    @Test
    public void testUpdateManyRecords() throws IOException {
        final int records = 10000;
        long seed = 543264693766L;// System.currentTimeMillis();

        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testUpdateManyRecords seed : " + seed));
        Map<Long, byte[]> positionRecordMap = new HashMap<>();
        Set<Long> updatedPositions = new HashSet<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + 1;
            byte[] record = new byte[recordSize];
            mersenneTwisterFast.nextBytes(record);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(record, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, record);
        }
        int newRecordVersion;
        newRecordVersion = recordVersion;
        newRecordVersion++;
        for (long clusterPosition : positionRecordMap.keySet()) {
            if (mersenneTwisterFast.nextBoolean()) {
                int recordSize = (mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + 1;
                byte[] record = new byte[recordSize];
                mersenneTwisterFast.nextBytes(record);
                LocalPaginatedClusterAbstract.paginatedCluster.updateRecord(clusterPosition, record, newRecordVersion, ((byte) (3)));
                positionRecordMap.put(clusterPosition, record);
                updatedPositions.add(clusterPosition);
            }
        }
        for (Map.Entry<Long, byte[]> entry : positionRecordMap.entrySet()) {
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(entry.getKey(), false);
            Assert.assertNotNull(rawBuffer);
            // Assert.assertEquals(rawBuffer.buffer, entry.getValue());
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(entry.getValue());
            if (updatedPositions.contains(entry.getKey())) {
                Assert.assertEquals(rawBuffer.version, newRecordVersion);
                Assert.assertEquals(rawBuffer.recordType, 3);
            } else {
                Assert.assertEquals(rawBuffer.version, recordVersion);
                Assert.assertEquals(rawBuffer.recordType, 2);
            }
        }
    }

    @Test
    public void testForwardIteration() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testForwardIteration seed : " + seed));
        NavigableMap<Long, byte[]> positionRecordMap = new TreeMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + 1;
            byte[] record = new byte[recordSize];
            mersenneTwisterFast.nextBytes(record);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(record, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, record);
        }
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(clusterPosition));
                positionIterator.remove();
            }
        } 
        OPhysicalPosition physicalPosition = new OPhysicalPosition();
        physicalPosition.clusterPosition = 0;
        OPhysicalPosition[] positions = LocalPaginatedClusterAbstract.paginatedCluster.ceilingPositions(physicalPosition);
        Assert.assertTrue(((positions.length) > 0));
        int counter = 0;
        for (long testedPosition : positionRecordMap.keySet()) {
            Assert.assertTrue(((positions.length) > 0));
            Assert.assertEquals(positions[0].clusterPosition, testedPosition);
            OPhysicalPosition positionToFind = positions[0];
            positions = LocalPaginatedClusterAbstract.paginatedCluster.higherPositions(positionToFind);
            counter++;
        }
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), counter);
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getFirstPosition(), ((long) (positionRecordMap.firstKey())));
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getLastPosition(), ((long) (positionRecordMap.lastKey())));
    }

    @Test
    public void testBackwardIteration() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testBackwardIteration seed : " + seed));
        NavigableMap<Long, byte[]> positionRecordMap = new TreeMap<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + 1;
            byte[] record = new byte[recordSize];
            mersenneTwisterFast.nextBytes(record);
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(record, recordVersion, ((byte) (2)), null);
            positionRecordMap.put(physicalPosition.clusterPosition, record);
        }
        Iterator<Long> positionIterator = positionRecordMap.keySet().iterator();
        while (positionIterator.hasNext()) {
            long clusterPosition = positionIterator.next();
            if (mersenneTwisterFast.nextBoolean()) {
                Assert.assertTrue(LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(clusterPosition));
                positionIterator.remove();
            }
        } 
        OPhysicalPosition physicalPosition = new OPhysicalPosition();
        physicalPosition.clusterPosition = Long.MAX_VALUE;
        OPhysicalPosition[] positions = LocalPaginatedClusterAbstract.paginatedCluster.floorPositions(physicalPosition);
        Assert.assertTrue(((positions.length) > 0));
        positionIterator = positionRecordMap.descendingKeySet().iterator();
        int counter = 0;
        while (positionIterator.hasNext()) {
            Assert.assertTrue(((positions.length) > 0));
            long testedPosition = positionIterator.next();
            Assert.assertEquals(positions[((positions.length) - 1)].clusterPosition, testedPosition);
            OPhysicalPosition positionToFind = positions[((positions.length) - 1)];
            positions = LocalPaginatedClusterAbstract.paginatedCluster.lowerPositions(positionToFind);
            counter++;
        } 
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getEntries(), counter);
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getFirstPosition(), ((long) (positionRecordMap.firstKey())));
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getLastPosition(), ((long) (positionRecordMap.lastKey())));
    }

    @Test
    public void testGetPhysicalPosition() throws IOException {
        final int records = 10000;
        long seed = System.currentTimeMillis();
        Random mersenneTwisterFast = new Random(seed);
        System.out.println(("testGetPhysicalPosition seed : " + seed));
        Set<OPhysicalPosition> positions = new HashSet<>();
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        for (int i = 0; i < records; i++) {
            int recordSize = (mersenneTwisterFast.nextInt((2 * (MAX_RECORD_SIZE)))) + 1;
            byte[] record = new byte[recordSize];
            mersenneTwisterFast.nextBytes(record);
            recordVersion++;
            final OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(record, recordVersion, ((byte) (i)), null);
            positions.add(physicalPosition);
        }
        Set<OPhysicalPosition> removedPositions = new HashSet<>();
        for (OPhysicalPosition position : positions) {
            OPhysicalPosition physicalPosition = new OPhysicalPosition();
            physicalPosition.clusterPosition = position.clusterPosition;
            physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.getPhysicalPosition(physicalPosition);
            Assert.assertEquals(physicalPosition.clusterPosition, position.clusterPosition);
            Assert.assertEquals(physicalPosition.recordType, position.recordType);
            Assert.assertEquals(physicalPosition.recordSize, position.recordSize);
            if (mersenneTwisterFast.nextBoolean()) {
                LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(position.clusterPosition);
                removedPositions.add(position);
            }
        }
        for (OPhysicalPosition position : positions) {
            OPhysicalPosition physicalPosition = new OPhysicalPosition();
            physicalPosition.clusterPosition = position.clusterPosition;
            physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.getPhysicalPosition(physicalPosition);
            if (removedPositions.contains(position))
                Assert.assertNull(physicalPosition);
            else {
                Assert.assertEquals(physicalPosition.clusterPosition, position.clusterPosition);
                Assert.assertEquals(physicalPosition.recordType, position.recordType);
                Assert.assertEquals(physicalPosition.recordSize, position.recordSize);
            }
        }
    }

    @Test
    public void testResurrectRecord() throws IOException {
        byte[] smallRecord = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        int recordVersion = 0;
        recordVersion++;
        recordVersion++;
        OPhysicalPosition physicalPosition = LocalPaginatedClusterAbstract.paginatedCluster.createRecord(smallRecord, recordVersion, ((byte) (1)), null);
        Assert.assertEquals(physicalPosition.clusterPosition, 0);
        Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getRecordStatus(physicalPosition.clusterPosition), PRESENT);
        for (int i = 0; i < 1000; ++i) {
            recordVersion++;
            smallRecord = new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3 };
            try {
                LocalPaginatedClusterAbstract.paginatedCluster.recycleRecord(physicalPosition.clusterPosition, smallRecord, recordVersion, ((byte) (2)));
                Assert.fail("it must be not possible to resurrect a non deleted record");
            } catch (OPaginatedClusterException e) {
                // OK
            }
            Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getRecordStatus(physicalPosition.clusterPosition), OPaginatedCluster.RECORD_STATUS.PRESENT);
            LocalPaginatedClusterAbstract.paginatedCluster.deleteRecord(physicalPosition.clusterPosition);
            Assert.assertEquals(LocalPaginatedClusterAbstract.paginatedCluster.getRecordStatus(physicalPosition.clusterPosition), REMOVED);
            ORawBuffer rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(physicalPosition.clusterPosition, false);
            Assert.assertNull(rawBuffer);
            LocalPaginatedClusterAbstract.paginatedCluster.recycleRecord(physicalPosition.clusterPosition, smallRecord, recordVersion, ((byte) (2)));
            rawBuffer = LocalPaginatedClusterAbstract.paginatedCluster.readRecord(physicalPosition.clusterPosition, false);
            Assert.assertNotNull(rawBuffer);
            Assert.assertEquals(rawBuffer.version, recordVersion);
            // Assert.assertEquals(rawBuffer.buffer, smallRecord);
            Assertions.assertThat(rawBuffer.buffer).isEqualTo(smallRecord);
            Assert.assertEquals(rawBuffer.recordType, 2);
            // UPDATE 10 TIMES WITH A GROWING CONTENT TO STIMULATE DEFRAG AND CHANGE OF PAGES
            for (int k = 0; k < 10; ++k) {
                final byte[] updatedRecord = new byte[10 * k];
                for (int j = 0; j < (updatedRecord.length); ++j) {
                    updatedRecord[j] = ((byte) (j));
                }
                LocalPaginatedClusterAbstract.paginatedCluster.updateRecord(physicalPosition.clusterPosition, updatedRecord, recordVersion, ((byte) (4)));
            }
        }
    }
}

