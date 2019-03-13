/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wali;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static UpdateType.CREATE;
import static UpdateType.DELETE;
import static UpdateType.UPDATE;


@SuppressWarnings("deprecation")
public class TestMinimalLockingWriteAheadLog {
    private static final Logger logger = LoggerFactory.getLogger(TestMinimalLockingWriteAheadLog.class);

    @Test
    public void testTruncatedPartitionHeader() throws IOException {
        final int numPartitions = 4;
        final Path path = Paths.get("target/testTruncatedPartitionHeader");
        deleteRecursively(path.toFile());
        Assert.assertTrue(path.toFile().mkdirs());
        final AtomicInteger counter = new AtomicInteger(0);
        final SerDe<Object> serde = new SerDe<Object>() {
            @Override
            public void readHeader(DataInputStream in) throws IOException {
                if ((counter.getAndIncrement()) == 1) {
                    throw new EOFException("Intentionally thrown for unit test");
                }
            }

            @Override
            public void serializeEdit(Object previousRecordState, Object newRecordState, DataOutputStream out) throws IOException {
                out.write(1);
            }

            @Override
            public void serializeRecord(Object record, DataOutputStream out) throws IOException {
                out.write(1);
            }

            @Override
            public Object deserializeEdit(DataInputStream in, Map<Object, Object> currentRecordStates, int version) throws IOException {
                final int val = in.read();
                return val == 1 ? new Object() : null;
            }

            @Override
            public Object deserializeRecord(DataInputStream in, int version) throws IOException {
                final int val = in.read();
                return val == 1 ? new Object() : null;
            }

            @Override
            public Object getRecordIdentifier(Object record) {
                return 1;
            }

            @Override
            public UpdateType getUpdateType(Object record) {
                return CREATE;
            }

            @Override
            public String getLocation(Object record) {
                return null;
            }

            @Override
            public int getVersion() {
                return 0;
            }
        };
        final WriteAheadRepository<Object> repo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        try {
            final Collection<Object> initialRecs = repo.recoverRecords();
            Assert.assertTrue(initialRecs.isEmpty());
            repo.update(Collections.singletonList(new Object()), false);
            repo.update(Collections.singletonList(new Object()), false);
            repo.update(Collections.singletonList(new Object()), false);
        } finally {
            repo.shutdown();
        }
        final WriteAheadRepository<Object> secondRepo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        try {
            secondRepo.recoverRecords();
        } finally {
            secondRepo.shutdown();
        }
    }

    @Test
    public void testRepoDoesntContinuallyGrowOnOutOfMemoryError() throws IOException, InterruptedException {
        final int numPartitions = 8;
        final Path path = Paths.get("target/minimal-locking-repo");
        deleteRecursively(path.toFile());
        Assert.assertTrue(path.toFile().mkdirs());
        final DummyRecordSerde serde = new DummyRecordSerde();
        final WriteAheadRepository<DummyRecord> repo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        try {
            final Collection<DummyRecord> initialRecs = repo.recoverRecords();
            Assert.assertTrue(initialRecs.isEmpty());
            serde.setThrowOOMEAfterNSerializeEdits(100);
            for (int i = 0; i < 108; i++) {
                try {
                    final DummyRecord record = new DummyRecord(String.valueOf(i), UpdateType.CREATE);
                    repo.update(Collections.singleton(record), false);
                } catch (final OutOfMemoryError oome) {
                    TestMinimalLockingWriteAheadLog.logger.info(("Received OOME on record " + i));
                }
            }
            long expectedSize = sizeOf(path.toFile());
            for (int i = 0; i < 1000; i++) {
                try {
                    final DummyRecord record = new DummyRecord(String.valueOf(i), UpdateType.CREATE);
                    repo.update(Collections.singleton(record), false);
                    Assert.fail("Expected IOE but it didn't happen");
                } catch (final IOException ioe) {
                    // will get IOException because all Partitions have been blacklisted
                }
            }
            long newSize = sizeOf(path.toFile());
            Assert.assertEquals(expectedSize, newSize);
            try {
                repo.checkpoint();
                Assert.fail("Expected OOME but it didn't happen");
            } catch (final OutOfMemoryError oome) {
            }
            expectedSize = sizeOf(path.toFile());
            for (int i = 0; i < 100000; i++) {
                try {
                    final DummyRecord record = new DummyRecord(String.valueOf(i), UpdateType.CREATE);
                    repo.update(Collections.singleton(record), false);
                    Assert.fail("Expected IOE but it didn't happen");
                } catch (final IOException ioe) {
                    // will get IOException because all Partitions have been blacklisted
                }
            }
            newSize = sizeOf(path.toFile());
            Assert.assertEquals(expectedSize, newSize);
        } finally {
            repo.shutdown();
        }
    }

    @Test
    public void testWrite() throws IOException, InterruptedException {
        final int numPartitions = 8;
        final Path path = Paths.get("target/minimal-locking-repo");
        deleteRecursively(path.toFile());
        Assert.assertTrue(path.toFile().mkdirs());
        final DummyRecordSerde serde = new DummyRecordSerde();
        final WriteAheadRepository<DummyRecord> repo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> initialRecs = repo.recoverRecords();
        Assert.assertTrue(initialRecs.isEmpty());
        final List<TestMinimalLockingWriteAheadLog.InsertThread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(new TestMinimalLockingWriteAheadLog.InsertThread(10000, (1000000 * i), repo));
        }
        final long start = System.nanoTime();
        for (final TestMinimalLockingWriteAheadLog.InsertThread thread : threads) {
            thread.start();
        }
        for (final TestMinimalLockingWriteAheadLog.InsertThread thread : threads) {
            thread.join();
        }
        final long nanos = (System.nanoTime()) - start;
        final long millis = TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS);
        System.out.println((("Took " + millis) + " millis to insert 1,000,000 records each in its own transaction"));
        repo.shutdown();
        final WriteAheadRepository<DummyRecord> recoverRepo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> recoveredRecords = recoverRepo.recoverRecords();
        Assert.assertFalse(recoveredRecords.isEmpty());
        Assert.assertEquals(100000, recoveredRecords.size());
        for (final DummyRecord record : recoveredRecords) {
            final Map<String, String> recoveredProps = record.getProperties();
            Assert.assertEquals(1, recoveredProps.size());
            Assert.assertEquals("B", recoveredProps.get("A"));
        }
    }

    @Test
    public void testRecoverAfterIOException() throws IOException {
        final int numPartitions = 5;
        final Path path = Paths.get("target/minimal-locking-repo-test-recover-after-ioe");
        deleteRecursively(path.toFile());
        Files.createDirectories(path);
        final DummyRecordSerde serde = new DummyRecordSerde();
        final WriteAheadRepository<DummyRecord> repo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> initialRecs = repo.recoverRecords();
        Assert.assertTrue(initialRecs.isEmpty());
        serde.setThrowIOEAfterNSerializeEdits(7);// serialize the 2 transactions, then the first edit of the third transaction; then throw IOException

        final List<DummyRecord> firstTransaction = new ArrayList<>();
        firstTransaction.add(new DummyRecord("1", UpdateType.CREATE));
        firstTransaction.add(new DummyRecord("2", UpdateType.CREATE));
        firstTransaction.add(new DummyRecord("3", UpdateType.CREATE));
        final List<DummyRecord> secondTransaction = new ArrayList<>();
        secondTransaction.add(new DummyRecord("1", UPDATE).setProperty("abc", "123"));
        secondTransaction.add(new DummyRecord("2", UPDATE).setProperty("cba", "123"));
        secondTransaction.add(new DummyRecord("3", UPDATE).setProperty("aaa", "123"));
        final List<DummyRecord> thirdTransaction = new ArrayList<>();
        thirdTransaction.add(new DummyRecord("1", DELETE));
        thirdTransaction.add(new DummyRecord("2", DELETE));
        repo.update(firstTransaction, true);
        repo.update(secondTransaction, true);
        try {
            repo.update(thirdTransaction, true);
            Assert.fail("Did not throw IOException on third transaction");
        } catch (final IOException e) {
            // expected behavior.
        }
        repo.shutdown();
        serde.setThrowIOEAfterNSerializeEdits((-1));
        final WriteAheadRepository<DummyRecord> recoverRepo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> recoveredRecords = recoverRepo.recoverRecords();
        Assert.assertFalse(recoveredRecords.isEmpty());
        Assert.assertEquals(3, recoveredRecords.size());
        boolean record1 = false;
        boolean record2 = false;
        boolean record3 = false;
        for (final DummyRecord record : recoveredRecords) {
            switch (record.getId()) {
                case "1" :
                    record1 = true;
                    Assert.assertEquals("123", record.getProperty("abc"));
                    break;
                case "2" :
                    record2 = true;
                    Assert.assertEquals("123", record.getProperty("cba"));
                    break;
                case "3" :
                    record3 = true;
                    Assert.assertEquals("123", record.getProperty("aaa"));
                    break;
            }
        }
        Assert.assertTrue(record1);
        Assert.assertTrue(record2);
        Assert.assertTrue(record3);
    }

    @Test
    public void testRecoverFileThatHasTrailingNULBytesAndTruncation() throws IOException {
        final int numPartitions = 5;
        final Path path = Paths.get("target/testRecoverFileThatHasTrailingNULBytesAndTruncation");
        deleteRecursively(path.toFile());
        Files.createDirectories(path);
        final DummyRecordSerde serde = new DummyRecordSerde();
        final WriteAheadRepository<DummyRecord> repo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> initialRecs = repo.recoverRecords();
        Assert.assertTrue(initialRecs.isEmpty());
        final List<DummyRecord> firstTransaction = new ArrayList<>();
        firstTransaction.add(new DummyRecord("1", UpdateType.CREATE));
        firstTransaction.add(new DummyRecord("2", UpdateType.CREATE));
        firstTransaction.add(new DummyRecord("3", UpdateType.CREATE));
        final List<DummyRecord> secondTransaction = new ArrayList<>();
        secondTransaction.add(new DummyRecord("1", UPDATE).setProperty("abc", "123"));
        secondTransaction.add(new DummyRecord("2", UPDATE).setProperty("cba", "123"));
        secondTransaction.add(new DummyRecord("3", UPDATE).setProperty("aaa", "123"));
        final List<DummyRecord> thirdTransaction = new ArrayList<>();
        thirdTransaction.add(new DummyRecord("1", DELETE));
        thirdTransaction.add(new DummyRecord("2", DELETE));
        repo.update(firstTransaction, true);
        repo.update(secondTransaction, true);
        repo.update(thirdTransaction, true);
        repo.shutdown();
        final File partition3Dir = path.resolve("partition-2").toFile();
        final File journalFile = partition3Dir.listFiles()[0];
        final byte[] contents = Files.readAllBytes(journalFile.toPath());
        // Truncate the contents of the journal file by 8 bytes. Then replace with 28 trailing NUL bytes,
        // as this is what we often see when we have a sudden power loss.
        final byte[] truncated = Arrays.copyOfRange(contents, 0, ((contents.length) - 8));
        final byte[] withNuls = new byte[(truncated.length) + 28];
        System.arraycopy(truncated, 0, withNuls, 0, truncated.length);
        try (final OutputStream fos = new FileOutputStream(journalFile)) {
            fos.write(withNuls);
        }
        final WriteAheadRepository<DummyRecord> recoverRepo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> recoveredRecords = recoverRepo.recoverRecords();
        Assert.assertFalse(recoveredRecords.isEmpty());
        Assert.assertEquals(3, recoveredRecords.size());
        boolean record1 = false;
        boolean record2 = false;
        boolean record3 = false;
        for (final DummyRecord record : recoveredRecords) {
            switch (record.getId()) {
                case "1" :
                    record1 = true;
                    Assert.assertEquals("123", record.getProperty("abc"));
                    break;
                case "2" :
                    record2 = true;
                    Assert.assertEquals("123", record.getProperty("cba"));
                    break;
                case "3" :
                    record3 = true;
                    Assert.assertEquals("123", record.getProperty("aaa"));
                    break;
            }
        }
        Assert.assertTrue(record1);
        Assert.assertTrue(record2);
        Assert.assertTrue(record3);
    }

    @Test
    public void testRecoverFileThatHasTrailingNULBytesNoTruncation() throws IOException {
        final int numPartitions = 5;
        final Path path = Paths.get("target/testRecoverFileThatHasTrailingNULBytesNoTruncation");
        deleteRecursively(path.toFile());
        Files.createDirectories(path);
        final DummyRecordSerde serde = new DummyRecordSerde();
        final WriteAheadRepository<DummyRecord> repo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> initialRecs = repo.recoverRecords();
        Assert.assertTrue(initialRecs.isEmpty());
        final List<DummyRecord> firstTransaction = new ArrayList<>();
        firstTransaction.add(new DummyRecord("1", UpdateType.CREATE));
        firstTransaction.add(new DummyRecord("2", UpdateType.CREATE));
        firstTransaction.add(new DummyRecord("3", UpdateType.CREATE));
        final List<DummyRecord> secondTransaction = new ArrayList<>();
        secondTransaction.add(new DummyRecord("1", UPDATE).setProperty("abc", "123"));
        secondTransaction.add(new DummyRecord("2", UPDATE).setProperty("cba", "123"));
        secondTransaction.add(new DummyRecord("3", UPDATE).setProperty("aaa", "123"));
        final List<DummyRecord> thirdTransaction = new ArrayList<>();
        thirdTransaction.add(new DummyRecord("1", DELETE));
        thirdTransaction.add(new DummyRecord("2", DELETE));
        repo.update(firstTransaction, true);
        repo.update(secondTransaction, true);
        repo.update(thirdTransaction, true);
        repo.shutdown();
        final File partition3Dir = path.resolve("partition-2").toFile();
        final File journalFile = partition3Dir.listFiles()[0];
        // Truncate the contents of the journal file by 8 bytes. Then replace with 28 trailing NUL bytes,
        // as this is what we often see when we have a sudden power loss.
        final byte[] withNuls = new byte[28];
        try (final OutputStream fos = new FileOutputStream(journalFile, true)) {
            fos.write(withNuls);
        }
        final WriteAheadRepository<DummyRecord> recoverRepo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> recoveredRecords = recoverRepo.recoverRecords();
        Assert.assertFalse(recoveredRecords.isEmpty());
        Assert.assertEquals(1, recoveredRecords.size());
        boolean record1 = false;
        boolean record2 = false;
        boolean record3 = false;
        for (final DummyRecord record : recoveredRecords) {
            switch (record.getId()) {
                case "1" :
                    record1 = (record.getUpdateType()) != (DELETE);
                    Assert.assertEquals("123", record.getProperty("abc"));
                    break;
                case "2" :
                    record2 = (record.getUpdateType()) != (DELETE);
                    Assert.assertEquals("123", record.getProperty("cba"));
                    break;
                case "3" :
                    record3 = true;
                    Assert.assertEquals("123", record.getProperty("aaa"));
                    break;
            }
        }
        Assert.assertFalse(record1);
        Assert.assertFalse(record2);
        Assert.assertTrue(record3);
    }

    @Test
    public void testCannotModifyLogAfterAllAreBlackListed() throws IOException {
        final int numPartitions = 5;
        final Path path = Paths.get("target/minimal-locking-repo-test-cannot-modify-after-all-blacklisted");
        deleteRecursively(path.toFile());
        Files.createDirectories(path);
        final DummyRecordSerde serde = new DummyRecordSerde();
        final WriteAheadRepository<DummyRecord> repo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> initialRecs = repo.recoverRecords();
        Assert.assertTrue(initialRecs.isEmpty());
        serde.setThrowIOEAfterNSerializeEdits(3);// serialize the first transaction, then fail on all subsequent transactions

        final List<DummyRecord> firstTransaction = new ArrayList<>();
        firstTransaction.add(new DummyRecord("1", UpdateType.CREATE));
        firstTransaction.add(new DummyRecord("2", UpdateType.CREATE));
        firstTransaction.add(new DummyRecord("3", UpdateType.CREATE));
        final List<DummyRecord> secondTransaction = new ArrayList<>();
        secondTransaction.add(new DummyRecord("1", UPDATE).setProperty("abc", "123"));
        secondTransaction.add(new DummyRecord("2", UPDATE).setProperty("cba", "123"));
        secondTransaction.add(new DummyRecord("3", UPDATE).setProperty("aaa", "123"));
        final List<DummyRecord> thirdTransaction = new ArrayList<>();
        thirdTransaction.add(new DummyRecord("1", DELETE));
        thirdTransaction.add(new DummyRecord("2", DELETE));
        repo.update(firstTransaction, true);
        try {
            repo.update(secondTransaction, true);
            Assert.fail("Did not throw IOException on second transaction");
        } catch (final IOException e) {
            // expected behavior.
        }
        for (int i = 0; i < 4; i++) {
            try {
                repo.update(thirdTransaction, true);
                Assert.fail("Did not throw IOException on third transaction");
            } catch (final IOException e) {
                // expected behavior.
            }
        }
        serde.setThrowIOEAfterNSerializeEdits((-1));
        final List<DummyRecord> fourthTransaction = new ArrayList<>();
        fourthTransaction.add(new DummyRecord("1", DELETE));
        try {
            repo.update(fourthTransaction, true);
            Assert.fail("Successfully updated repo for 4th transaction");
        } catch (final IOException e) {
            // expected behavior
            Assert.assertTrue(e.getMessage().contains("All Partitions have been blacklisted"));
        }
        repo.shutdown();
        serde.setThrowIOEAfterNSerializeEdits((-1));
        final WriteAheadRepository<DummyRecord> recoverRepo = new MinimalLockingWriteAheadLog(path, numPartitions, serde, null);
        final Collection<DummyRecord> recoveredRecords = recoverRepo.recoverRecords();
        Assert.assertFalse(recoveredRecords.isEmpty());
        Assert.assertEquals(3, recoveredRecords.size());
    }

    @Test
    public void testStriping() throws IOException {
        final int numPartitions = 6;
        final Path path = Paths.get("target/minimal-locking-repo-striped");
        deleteRecursively(path.toFile());
        Files.createDirectories(path);
        final SortedSet<Path> paths = new TreeSet<>();
        paths.add(path.resolve("stripe-1"));
        paths.add(path.resolve("stripe-2"));
        final DummyRecordSerde serde = new DummyRecordSerde();
        final WriteAheadRepository<DummyRecord> repo = new MinimalLockingWriteAheadLog(paths, numPartitions, serde, null);
        final Collection<DummyRecord> initialRecs = repo.recoverRecords();
        Assert.assertTrue(initialRecs.isEmpty());
        final TestMinimalLockingWriteAheadLog.InsertThread inserter = new TestMinimalLockingWriteAheadLog.InsertThread(100000, 0, repo);
        inserter.run();
        for (final Path partitionPath : paths) {
            final File[] files = partitionPath.toFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().startsWith("partition");
                }
            });
            Assert.assertEquals(3, files.length);
            for (final File file : files) {
                final File[] journalFiles = file.listFiles();
                Assert.assertEquals(1, journalFiles.length);
            }
        }
        repo.checkpoint();
    }

    @Test
    public void testShutdownWhileBlacklisted() throws IOException {
        final Path path = Paths.get("target/minimal-locking-repo-shutdown-blacklisted");
        deleteRecursively(path.toFile());
        Files.createDirectories(path);
        final SerDe<TestMinimalLockingWriteAheadLog.SimpleRecord> failOnThirdWriteSerde = new SerDe<TestMinimalLockingWriteAheadLog.SimpleRecord>() {
            private int writes = 0;

            @Override
            public void serializeEdit(TestMinimalLockingWriteAheadLog.SimpleRecord previousRecordState, TestMinimalLockingWriteAheadLog.SimpleRecord newRecordState, DataOutputStream out) throws IOException {
                serializeRecord(newRecordState, out);
            }

            @Override
            public void serializeRecord(TestMinimalLockingWriteAheadLog.SimpleRecord record, DataOutputStream out) throws IOException {
                int size = ((int) (record.getSize()));
                out.writeLong(record.getSize());
                for (int i = 0; i < size; i++) {
                    out.write('A');
                }
                if ((++(writes)) == 3) {
                    throw new IOException("Intentional Exception for Unit Testing");
                }
                out.writeLong(record.getId());
            }

            @Override
            public TestMinimalLockingWriteAheadLog.SimpleRecord deserializeEdit(DataInputStream in, Map<Object, TestMinimalLockingWriteAheadLog.SimpleRecord> currentRecordStates, int version) throws IOException {
                return deserializeRecord(in, version);
            }

            @Override
            public TestMinimalLockingWriteAheadLog.SimpleRecord deserializeRecord(DataInputStream in, int version) throws IOException {
                long size = in.readLong();
                for (int i = 0; i < ((int) (size)); i++) {
                    in.read();
                }
                long id = in.readLong();
                return new TestMinimalLockingWriteAheadLog.SimpleRecord(id, size);
            }

            @Override
            public Object getRecordIdentifier(TestMinimalLockingWriteAheadLog.SimpleRecord record) {
                return record.getId();
            }

            @Override
            public UpdateType getUpdateType(TestMinimalLockingWriteAheadLog.SimpleRecord record) {
                return UpdateType.CREATE;
            }

            @Override
            public String getLocation(TestMinimalLockingWriteAheadLog.SimpleRecord record) {
                return null;
            }

            @Override
            public int getVersion() {
                return 0;
            }
        };
        final WriteAheadRepository<TestMinimalLockingWriteAheadLog.SimpleRecord> writeRepo = new MinimalLockingWriteAheadLog(path, 1, failOnThirdWriteSerde, null);
        final Collection<TestMinimalLockingWriteAheadLog.SimpleRecord> initialRecs = writeRepo.recoverRecords();
        Assert.assertTrue(initialRecs.isEmpty());
        writeRepo.update(Collections.singleton(new TestMinimalLockingWriteAheadLog.SimpleRecord(1L, 1L)), false);
        writeRepo.update(Collections.singleton(new TestMinimalLockingWriteAheadLog.SimpleRecord(2L, 2L)), false);
        try {
            // Use a size of 8194 because the BufferedOutputStream has a buffer size of 8192 and we want
            // to exceed this for testing purposes.
            writeRepo.update(Collections.singleton(new TestMinimalLockingWriteAheadLog.SimpleRecord(3L, 8194L)), false);
            Assert.fail("Expected IOException but did not get it");
        } catch (final IOException ioe) {
            // expected behavior
        }
        final Path partitionDir = path.resolve("partition-0");
        final File journalFile = partitionDir.toFile().listFiles()[0];
        final long journalFileSize = journalFile.length();
        verifyBlacklistedJournalContents(journalFile, failOnThirdWriteSerde);
        writeRepo.shutdown();
        // Ensure that calling shutdown() didn't write anything to the journal file
        final long newJournalSize = journalFile.length();
        Assert.assertEquals((("Calling Shutdown wrote " + (newJournalSize - journalFileSize)) + " bytes to the journal file"), newJournalSize, journalFile.length());
    }

    @Test
    public void testDecreaseNumberOfPartitions() throws IOException {
        final Path path = Paths.get("target/minimal-locking-repo-decrease-partitions");
        deleteRecursively(path.toFile());
        Files.createDirectories(path);
        final DummyRecordSerde serde = new DummyRecordSerde();
        final WriteAheadRepository<DummyRecord> writeRepo = new MinimalLockingWriteAheadLog(path, 256, serde, null);
        final Collection<DummyRecord> initialRecs = writeRepo.recoverRecords();
        Assert.assertTrue(initialRecs.isEmpty());
        final DummyRecord record1 = new DummyRecord("1", UpdateType.CREATE);
        writeRepo.update(Collections.singleton(record1), false);
        for (int i = 0; i < 8; i++) {
            final DummyRecord r = new DummyRecord("1", UPDATE);
            r.setProperty("i", String.valueOf(i));
            writeRepo.update(Collections.singleton(r), false);
        }
        writeRepo.shutdown();
        final WriteAheadRepository<DummyRecord> recoverRepo = new MinimalLockingWriteAheadLog(path, 6, serde, null);
        final Collection<DummyRecord> records = recoverRepo.recoverRecords();
        final List<DummyRecord> list = new ArrayList<>(records);
        Assert.assertEquals(1, list.size());
        final DummyRecord recoveredRecord = list.get(0);
        Assert.assertEquals("1", recoveredRecord.getId());
        Assert.assertEquals("7", recoveredRecord.getProperty("i"));
    }

    private static class InsertThread extends Thread {
        private final List<List<DummyRecord>> records;

        private final WriteAheadRepository<DummyRecord> repo;

        public InsertThread(final int numInsertions, final int startIndex, final WriteAheadRepository<DummyRecord> repo) {
            records = new ArrayList<>();
            for (int i = 0; i < numInsertions; i++) {
                final DummyRecord record = new DummyRecord(String.valueOf((i + startIndex)), UpdateType.CREATE);
                record.setProperty("A", "B");
                final List<DummyRecord> list = new ArrayList<>();
                list.add(record);
                records.add(list);
            }
            this.repo = repo;
        }

        @Override
        public void run() {
            try {
                int counter = 0;
                for (final List<DummyRecord> list : records) {
                    final boolean forceSync = (++counter) == (records.size());
                    repo.update(list, forceSync);
                }
            } catch (IOException e) {
                Assert.fail(("Failed to update: " + (e.toString())));
                e.printStackTrace();
            }
        }
    }

    private static class InlineCreationInsertThread extends Thread {
        private final long iterations;

        private final WriteAheadRepository<DummyRecord> repo;

        public InlineCreationInsertThread(final long numInsertions, final WriteAheadRepository<DummyRecord> repo) {
            this.iterations = numInsertions;
            this.repo = repo;
        }

        @Override
        public void run() {
            final List<DummyRecord> list = new ArrayList<>(1);
            list.add(null);
            final UpdateType[] updateTypes = new UpdateType[]{ UpdateType.CREATE, UpdateType.DELETE, UpdateType.UPDATE };
            final Random random = new Random();
            for (long i = 0; i < (iterations); i++) {
                final int updateTypeIndex = random.nextInt(updateTypes.length);
                final UpdateType updateType = updateTypes[updateTypeIndex];
                final DummyRecord record = new DummyRecord(String.valueOf(i), updateType);
                record.setProperty("A", "B");
                list.set(0, record);
                try {
                    repo.update(list, false);
                } catch (final Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    static class SimpleRecord {
        private long id;

        private long size;

        public SimpleRecord(final long id, final long size) {
            this.id = id;
            this.size = size;
        }

        public long getId() {
            return id;
        }

        public long getSize() {
            return size;
        }
    }
}

