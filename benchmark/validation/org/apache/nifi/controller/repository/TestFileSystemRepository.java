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
package org.apache.nifi.controller.repository;


import DataUnit.B;
import Level.WARN;
import Logger.ROOT_LOGGER_NAME;
import NiFiProperties.CONTENT_ARCHIVE_CLEANUP_FREQUENCY;
import NiFiProperties.PROPERTIES_FILE_PATH;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.nifi.controller.repository.claim.ContentClaim;
import org.apache.nifi.controller.repository.claim.StandardContentClaim;
import org.apache.nifi.controller.repository.claim.StandardResourceClaimManager;
import org.apache.nifi.processor.DataUnit;
import org.apache.nifi.stream.io.StreamUtils;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.LoggerFactory;


public class TestFileSystemRepository {
    public static final int NUM_REPO_SECTIONS = 1;

    public static final File helloWorldFile = new File("src/test/resources/hello.txt");

    private FileSystemRepository repository = null;

    private StandardResourceClaimManager claimManager = null;

    private final File rootFile = new File("target/content_repository");

    private NiFiProperties nifiProperties;

    @Test
    public void testMinimalArchiveCleanupIntervalHonoredAndLogged() throws Exception {
        // We are going to construct our own repository using different properties, so
        // we need to shutdown the existing one.
        shutdown();
        Logger root = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));
        ListAppender<ILoggingEvent> testAppender = new ListAppender();
        testAppender.setName("Test");
        testAppender.start();
        root.addAppender(testAppender);
        final Map<String, String> addProps = new HashMap<>();
        addProps.put(CONTENT_ARCHIVE_CLEANUP_FREQUENCY, "1 millis");
        final NiFiProperties localProps = NiFiProperties.createBasicNiFiProperties(null, addProps);
        repository = new FileSystemRepository(localProps);
        repository.initialize(new StandardResourceClaimManager());
        repository.purge();
        boolean messageFound = false;
        String message = "The value of nifi.content.repository.archive.cleanup.frequency property " + ("is set to '1 millis' which is below the allowed minimum of 1 second (1000 milliseconds). " + "Minimum value of 1 sec will be used as scheduling interval for archive cleanup task.");
        // Must synchronize on testAppender, because the call to append() is synchronized and this synchronize
        // keyword guards testAppender.list. Since we are accessing testAppender.list, we must do so in a thread-safe manner.
        synchronized(testAppender) {
            for (ILoggingEvent event : testAppender.list) {
                String actualMessage = event.getFormattedMessage();
                if (actualMessage.equals(message)) {
                    Assert.assertEquals(event.getLevel(), WARN);
                    messageFound = true;
                    break;
                }
            }
        }
        Assert.assertTrue(messageFound);
    }

    @Test
    public void testBogusFile() throws IOException {
        repository.shutdown();
        System.setProperty(PROPERTIES_FILE_PATH, TestFileSystemRepository.class.getResource("/conf/nifi.properties").getFile());
        File bogus = new File(rootFile, "bogus");
        try {
            bogus.mkdir();
            bogus.setReadable(false);
            repository = new FileSystemRepository(nifiProperties);
            repository.initialize(new StandardResourceClaimManager());
        } finally {
            bogus.setReadable(true);
            Assert.assertTrue(bogus.delete());
        }
    }

    @Test
    public void testCreateContentClaim() throws IOException {
        // value passed to #create is irrelevant because the FileSystemRepository does not currently support loss tolerance.
        final ContentClaim claim = repository.create(true);
        Assert.assertNotNull(claim);
        Assert.assertEquals(1, repository.getClaimantCount(claim));
    }

    @Test
    public void testReadClaimThenWriteThenReadMore() throws IOException {
        final ContentClaim claim = repository.create(false);
        final OutputStream out = repository.write(claim);
        out.write("hello".getBytes());
        out.flush();
        final InputStream in = repository.read(claim);
        final byte[] buffer = new byte[5];
        StreamUtils.fillBuffer(in, buffer);
        Assert.assertEquals("hello", new String(buffer));
        out.write("good-bye".getBytes());
        out.close();
        final byte[] buffer2 = new byte[8];
        StreamUtils.fillBuffer(in, buffer2);
        Assert.assertEquals("good-bye", new String(buffer2));
    }

    @Test
    public void testClaimantCounts() throws IOException {
        final ContentClaim claim = repository.create(true);
        Assert.assertNotNull(claim);
        Assert.assertEquals(1, repository.getClaimantCount(claim));
        Assert.assertEquals(2, repository.incrementClaimaintCount(claim));
        Assert.assertEquals(3, repository.incrementClaimaintCount(claim));
        Assert.assertEquals(4, repository.incrementClaimaintCount(claim));
        Assert.assertEquals(5, repository.incrementClaimaintCount(claim));
        repository.decrementClaimantCount(claim);
        Assert.assertEquals(4, repository.getClaimantCount(claim));
        repository.decrementClaimantCount(claim);
        Assert.assertEquals(3, repository.getClaimantCount(claim));
        repository.decrementClaimantCount(claim);
        Assert.assertEquals(2, repository.getClaimantCount(claim));
        repository.decrementClaimantCount(claim);
        Assert.assertEquals(1, repository.getClaimantCount(claim));
        repository.decrementClaimantCount(claim);
        Assert.assertEquals(0, repository.getClaimantCount(claim));
        repository.remove(claim);
    }

    @Test
    public void testResourceClaimReused() throws IOException {
        final ContentClaim claim1 = repository.create(false);
        final ContentClaim claim2 = repository.create(false);
        // should not be equal because claim1 may still be in use
        Assert.assertNotSame(claim1.getResourceClaim(), claim2.getResourceClaim());
        try (final OutputStream out = repository.write(claim1)) {
        }
        final ContentClaim claim3 = repository.create(false);
        Assert.assertEquals(claim1.getResourceClaim(), claim3.getResourceClaim());
    }

    @Test
    public void testResourceClaimNotReusedAfterRestart() throws IOException, InterruptedException {
        final ContentClaim claim1 = repository.create(false);
        try (final OutputStream out = repository.write(claim1)) {
        }
        repository.shutdown();
        Thread.sleep(1000L);
        repository = new FileSystemRepository(nifiProperties);
        repository.initialize(new StandardResourceClaimManager());
        repository.purge();
        final ContentClaim claim2 = repository.create(false);
        Assert.assertNotSame(claim1.getResourceClaim(), claim2.getResourceClaim());
    }

    @Test
    public void testWriteWithNoContent() throws IOException {
        final ContentClaim claim1 = repository.create(false);
        try (final OutputStream out = repository.write(claim1)) {
            out.write("Hello".getBytes());
        }
        final ContentClaim claim2 = repository.create(false);
        Assert.assertEquals(claim1.getResourceClaim(), claim2.getResourceClaim());
        try (final OutputStream out = repository.write(claim2)) {
        }
        final ContentClaim claim3 = repository.create(false);
        Assert.assertEquals(claim1.getResourceClaim(), claim3.getResourceClaim());
        try (final OutputStream out = repository.write(claim3)) {
            out.write(" World".getBytes());
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final InputStream in = repository.read(claim1)) {
            StreamUtils.copy(in, baos);
        }
        Assert.assertEquals("Hello", baos.toString());
        baos.reset();
        try (final InputStream in = repository.read(claim2)) {
            StreamUtils.copy(in, baos);
        }
        Assert.assertEquals("", baos.toString());
        Assert.assertEquals(0, baos.size());
        baos.reset();
        try (final InputStream in = repository.read(claim3)) {
            StreamUtils.copy(in, baos);
        }
        Assert.assertEquals(" World", baos.toString());
    }

    @Test
    public void testRemoveDeletesFileIfNoClaimants() throws IOException {
        final ContentClaim claim = repository.create(true);
        Assert.assertNotNull(claim);
        Assert.assertEquals(1, repository.getClaimantCount(claim));
        repository.incrementClaimaintCount(claim);
        final Path claimPath = getPath(claim);
        final String maxAppendableClaimLength = nifiProperties.getMaxAppendableClaimSize();
        final int maxClaimLength = DataUnit.parseDataSize(maxAppendableClaimLength, B).intValue();
        // Create the file.
        try (final OutputStream out = repository.write(claim)) {
            out.write(new byte[maxClaimLength]);
        }
        int count = repository.decrementClaimantCount(claim);
        Assert.assertEquals(1, count);
        Assert.assertTrue(Files.exists(claimPath));
        // ensure that no Exception is thrown here.
        repository.remove(null);
        Assert.assertTrue(Files.exists(claimPath));
        count = repository.decrementClaimantCount(claim);
        Assert.assertEquals(0, count);
        repository.remove(claim);
        Assert.assertFalse(Files.exists(claimPath));
    }

    @Test
    public void testImportFromFile() throws IOException {
        final ContentClaim claim = repository.create(false);
        final File testFile = new File("src/test/resources/hello.txt");
        final File file1 = new File("target/testFile1");
        final Path path1 = file1.toPath();
        final File file2 = new File("target/testFile2");
        final Path path2 = file2.toPath();
        Files.copy(testFile.toPath(), path1, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(testFile.toPath(), path2, StandardCopyOption.REPLACE_EXISTING);
        repository.importFrom(path1, claim);
        Assert.assertTrue(file1.exists());
        Assert.assertTrue(file2.exists());
        // try to read the data back out.
        final Path path = getPath(claim);
        final byte[] data = Files.readAllBytes(path);
        final byte[] expected = Files.readAllBytes(testFile.toPath());
        Assert.assertTrue(Arrays.equals(expected, data));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final InputStream in = repository.read(claim)) {
            StreamUtils.copy(in, baos);
        }
        Assert.assertTrue(Arrays.equals(expected, baos.toByteArray()));
    }

    @Test
    public void testImportFromStream() throws IOException {
        final ContentClaim claim = repository.create(false);
        final byte[] data = "hello".getBytes();
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        repository.importFrom(bais, claim);
        final Path claimPath = getPath(claim);
        Assert.assertTrue(Arrays.equals(data, Files.readAllBytes(claimPath)));
    }

    @Test
    public void testExportToOutputStream() throws IOException {
        final ContentClaim claim = repository.create(true);
        try (final OutputStream out = repository.write(claim)) {
            Files.copy(TestFileSystemRepository.helloWorldFile.toPath(), out);
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repository.exportTo(claim, baos);
        final byte[] data = baos.toByteArray();
        Assert.assertTrue(Arrays.equals(Files.readAllBytes(TestFileSystemRepository.helloWorldFile.toPath()), data));
    }

    @Test
    public void testExportToFile() throws IOException {
        final ContentClaim claim = repository.create(true);
        try (final OutputStream out = repository.write(claim)) {
            Files.copy(TestFileSystemRepository.helloWorldFile.toPath(), out);
        }
        final File outFile = new File("target/testExportToFile");
        final Path outPath = outFile.toPath();
        Files.deleteIfExists(outPath);
        final byte[] expected = Files.readAllBytes(TestFileSystemRepository.helloWorldFile.toPath());
        repository.exportTo(claim, outPath, false);
        Assert.assertTrue(Arrays.equals(expected, Files.readAllBytes(outPath)));
        repository.exportTo(claim, outPath, true);
        final byte[] doubleExpected = new byte[(expected.length) * 2];
        System.arraycopy(expected, 0, doubleExpected, 0, expected.length);
        System.arraycopy(expected, 0, doubleExpected, expected.length, expected.length);
        Assert.assertTrue(Arrays.equals(doubleExpected, Files.readAllBytes(outPath)));
    }

    @Test
    public void testSize() throws IOException {
        final ContentClaim claim = repository.create(true);
        final Path path = getPath(claim);
        Files.createDirectories(path.getParent());
        final byte[] data = "The quick brown fox jumps over the lazy dog".getBytes();
        try (final OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            out.write(data);
        }
        Assert.assertEquals(data.length, repository.size(claim));
    }

    @Test(expected = ContentNotFoundException.class)
    public void testSizeWithNoContent() throws IOException {
        final ContentClaim claim = new StandardContentClaim(new org.apache.nifi.controller.repository.claim.StandardResourceClaim(claimManager, "container1", "section 1", "1", false), 0L);
        Assert.assertEquals(0L, repository.size(claim));
    }

    @Test(expected = ContentNotFoundException.class)
    public void testReadWithNoContent() throws IOException {
        final ContentClaim claim = new StandardContentClaim(new org.apache.nifi.controller.repository.claim.StandardResourceClaim(claimManager, "container1", "section 1", "1", false), 0L);
        final InputStream in = repository.read(claim);
        in.close();
    }

    @Test
    public void testReadWithContent() throws IOException {
        final ContentClaim claim = repository.create(true);
        final Path path = getPath(claim);
        Files.createDirectories(path.getParent());
        final byte[] data = "The quick brown fox jumps over the lazy dog".getBytes();
        try (final OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            out.write(data);
        }
        try (final InputStream inStream = repository.read(claim)) {
            Assert.assertNotNull(inStream);
            final byte[] dataRead = readFully(inStream, data.length);
            Assert.assertTrue(Arrays.equals(data, dataRead));
        }
    }

    @Test
    public void testReadWithContentArchived() throws IOException {
        Assume.assumeFalse(isWindowsEnvironment());// skip if on windows

        final ContentClaim claim = repository.create(true);
        final Path path = getPath(claim);
        Files.deleteIfExists(path);
        Path archivePath = FileSystemRepository.getArchivePath(path);
        Files.createDirectories(archivePath.getParent());
        final byte[] data = "The quick brown fox jumps over the lazy dog".getBytes();
        try (final OutputStream out = Files.newOutputStream(archivePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            out.write(data);
        }
        try (final InputStream inStream = repository.read(claim)) {
            Assert.assertNotNull(inStream);
            final byte[] dataRead = readFully(inStream, data.length);
            Assert.assertArrayEquals(data, dataRead);
        }
    }

    @Test(expected = ContentNotFoundException.class)
    public void testReadWithNoContentArchived() throws IOException {
        Assume.assumeFalse(isWindowsEnvironment());// skip if on windows

        final ContentClaim claim = repository.create(true);
        final Path path = getPath(claim);
        Files.deleteIfExists(path);
        Path archivePath = FileSystemRepository.getArchivePath(path);
        Files.deleteIfExists(archivePath);
        repository.read(claim).close();
    }

    @Test
    public void testWrite() throws IOException {
        final ContentClaim claim = repository.create(true);
        final byte[] data = "The quick brown fox jumps over the lazy dog".getBytes();
        try (final OutputStream out = repository.write(claim)) {
            out.write(data);
        }
        final Path path = getPath(claim);
        Assert.assertTrue(Arrays.equals(data, Files.readAllBytes(path)));
    }

    @Test
    public void testRemoveWhileWritingToClaim() throws IOException {
        final ContentClaim claim = repository.create(false);
        final OutputStream out = repository.write(claim);
        // write at least 1 MB to the output stream so that when we close the output stream
        // the repo won't keep the stream open.
        final String maxAppendableClaimLength = nifiProperties.getMaxAppendableClaimSize();
        final int maxClaimLength = DataUnit.parseDataSize(maxAppendableClaimLength, B).intValue();
        final byte[] buff = new byte[maxClaimLength];
        out.write(buff);
        out.write(buff);
        // false because claimant count is still 1, so the resource claim was not removed
        Assert.assertFalse(repository.remove(claim));
        Assert.assertEquals(0, repository.decrementClaimantCount(claim));
        // false because claimant count is 0 but there is an 'active' stream for the claim
        Assert.assertFalse(repository.remove(claim));
        out.close();
        Assert.assertTrue(repository.remove(claim));
    }

    @Test
    public void testMarkDestructableDoesNotArchiveIfStreamOpenAndWrittenTo() throws IOException, InterruptedException {
        FileSystemRepository repository = null;
        try {
            final List<Path> archivedPaths = Collections.synchronizedList(new ArrayList<Path>());
            // We are creating our own 'local' repository in this test so shut down the one created in the setup() method
            shutdown();
            repository = new FileSystemRepository(nifiProperties) {
                @Override
                protected boolean archive(Path curPath) throws IOException {
                    archivedPaths.add(curPath);
                    return true;
                }
            };
            final StandardResourceClaimManager claimManager = new StandardResourceClaimManager();
            repository.initialize(claimManager);
            repository.purge();
            final ContentClaim claim = repository.create(false);
            // Create a stream and write a bit to it, then close it. This will cause the
            // claim to be put back onto the 'writableClaimsQueue'
            try (final OutputStream out = repository.write(claim)) {
                Assert.assertEquals(1, claimManager.getClaimantCount(claim.getResourceClaim()));
                out.write("1\n".getBytes());
            }
            Assert.assertEquals(1, claimManager.getClaimantCount(claim.getResourceClaim()));
            int claimantCount = claimManager.decrementClaimantCount(claim.getResourceClaim());
            Assert.assertEquals(0, claimantCount);
            Assert.assertTrue(archivedPaths.isEmpty());
            claimManager.markDestructable(claim.getResourceClaim());
            // Wait for the archive thread to have a chance to run
            Thread.sleep(2000L);
            // Should still be empty because we have a stream open to the file.
            Assert.assertTrue(archivedPaths.isEmpty());
            Assert.assertEquals(0, claimManager.getClaimantCount(claim.getResourceClaim()));
        } finally {
            if (repository != null) {
                repository.shutdown();
            }
        }
    }

    @Test
    public void testWriteCannotProvideNullOutput() throws IOException {
        FileSystemRepository repository = null;
        try {
            final List<Path> archivedPathsWithOpenStream = Collections.synchronizedList(new ArrayList<Path>());
            // We are creating our own 'local' repository in this test so shut down the one created in the setup() method
            shutdown();
            repository = new FileSystemRepository(nifiProperties) {
                @Override
                protected boolean archive(Path curPath) throws IOException {
                    if ((getOpenStreamCount()) > 0) {
                        archivedPathsWithOpenStream.add(curPath);
                    }
                    return true;
                }
            };
            final StandardResourceClaimManager claimManager = new StandardResourceClaimManager();
            repository.initialize(claimManager);
            repository.purge();
            final ContentClaim claim = repository.create(false);
            Assert.assertEquals(1, claimManager.getClaimantCount(claim.getResourceClaim()));
            int claimantCount = claimManager.decrementClaimantCount(claim.getResourceClaim());
            Assert.assertEquals(0, claimantCount);
            Assert.assertTrue(archivedPathsWithOpenStream.isEmpty());
            OutputStream out = repository.write(claim);
            out.close();
            repository.decrementClaimantCount(claim);
            ContentClaim claim2 = repository.create(false);
            Assert.assertEquals(claim.getResourceClaim(), claim2.getResourceClaim());
            out = repository.write(claim2);
            final boolean archived = repository.archive(claim.getResourceClaim());
            Assert.assertFalse(archived);
        } finally {
            if (repository != null) {
                repository.shutdown();
            }
        }
    }

    /**
     * We have encountered a situation where the File System Repo is moving
     * files to archive and then eventually aging them off while there is still
     * an open file handle. This test is meant to replicate the conditions under
     * which this would happen and verify that it is fixed.
     *
     * The condition that caused this appears to be that a Process Session
     * created a Content Claim and then did not write to it. It then decremented
     * the claimant count (which reduced the count to 0). This was likely due to
     * creating the claim in ProcessSession.write(FlowFile, StreamCallback) and
     * then having an Exception thrown when the Process Session attempts to read
     * the current Content Claim. In this case, it would not ever get to the
     * point of calling FileSystemRepository.write().
     *
     * The above sequence of events is problematic because calling
     * FileSystemRepository.create() will remove the Resource Claim from the
     * 'writable claims queue' and expects that we will write to it. When we
     * call FileSystemRepository.write() with that Resource Claim, we return an
     * OutputStream that, when closed, will take care of adding the Resource
     * Claim back to the 'writable claims queue' or otherwise close the
     * FileOutputStream that is open for that Resource Claim. If
     * FileSystemRepository.write() is never called, or if the OutputStream
     * returned by that method is never closed, but the Content Claim is then
     * decremented to 0, we can get into a situation where we do archive the
     * content (because the claimant count is 0 and it is not in the 'writable
     * claims queue') and then eventually age it off, without ever closing the
     * OutputStream. We need to ensure that we do always close that Output
     * Stream.
     */
    @Test
    public void testMarkDestructableDoesNotArchiveIfStreamOpenAndNotWrittenTo() throws IOException, InterruptedException {
        FileSystemRepository repository = null;
        try {
            final List<Path> archivedPathsWithOpenStream = Collections.synchronizedList(new ArrayList<Path>());
            // We are creating our own 'local' repository in this test so shut down the one created in the setup() method
            shutdown();
            repository = new FileSystemRepository(nifiProperties) {
                @Override
                protected boolean archive(Path curPath) throws IOException {
                    if ((getOpenStreamCount()) > 0) {
                        archivedPathsWithOpenStream.add(curPath);
                    }
                    return true;
                }
            };
            final StandardResourceClaimManager claimManager = new StandardResourceClaimManager();
            repository.initialize(claimManager);
            repository.purge();
            final ContentClaim claim = repository.create(false);
            Assert.assertEquals(1, claimManager.getClaimantCount(claim.getResourceClaim()));
            int claimantCount = claimManager.decrementClaimantCount(claim.getResourceClaim());
            Assert.assertEquals(0, claimantCount);
            Assert.assertTrue(archivedPathsWithOpenStream.isEmpty());
            // This would happen when FlowFile repo is checkpointed, if Resource Claim has claimant count of 0.
            // Since the Resource Claim of interest is still 'writable', we should not archive it.
            claimManager.markDestructable(claim.getResourceClaim());
            // Wait for the archive thread to have a chance to run
            long totalSleepMillis = 0;
            final long startTime = System.nanoTime();
            while ((archivedPathsWithOpenStream.isEmpty()) && (totalSleepMillis < 5000)) {
                Thread.sleep(100L);
                totalSleepMillis = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - startTime));
            } 
            // Should still be empty because we have a stream open to the file so we should
            // not actually try to archive the data.
            Assert.assertTrue(archivedPathsWithOpenStream.isEmpty());
            Assert.assertEquals(0, claimManager.getClaimantCount(claim.getResourceClaim()));
        } finally {
            if (repository != null) {
                repository.shutdown();
            }
        }
    }

    @Test
    public void testMergeWithHeaderFooterDemarcator() throws IOException {
        testMerge("HEADER", "FOOTER", "DEMARCATOR");
    }

    @Test
    public void testMergeWithHeaderFooter() throws IOException {
        testMerge("HEADER", "FOOTER", null);
    }

    @Test
    public void testMergeWithHeaderOnly() throws IOException {
        testMerge("HEADER", null, null);
    }

    @Test
    public void testMergeWithFooterOnly() throws IOException {
        testMerge(null, "FOOTER", null);
    }

    @Test
    public void testMergeWithDemarcator() throws IOException {
        testMerge(null, null, "DEMARCATOR");
    }

    @Test
    public void testWithHeaderDemarcator() throws IOException {
        testMerge("HEADER", null, "DEMARCATOR");
    }

    @Test
    public void testMergeWithFooterDemarcator() throws IOException {
        testMerge(null, "FOOTER", "DEMARCATOR");
    }

    @Test
    public void testMergeWithoutHeaderFooterDemarcator() throws IOException {
        testMerge(null, null, null);
    }
}

