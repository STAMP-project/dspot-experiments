/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.storage.contrib.nio.it;


import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.contrib.nio.CloudStorageConfiguration;
import com.google.cloud.storage.contrib.nio.CloudStorageFileSystem;
import com.google.cloud.storage.contrib.nio.CloudStoragePath;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration test for google-cloud-nio.
 *
 * <p>This test actually talks to Google Cloud Storage (you need an account) and tests both reading
 * and writing. You *must* set the {@code GOOGLE_APPLICATION_CREDENTIALS} environment variable for
 * this test to work. It must contain the name of a local file that contains your Service Account
 * JSON Key. We use the project in those credentials.
 *
 * <p>See <a href="https://cloud.google.com/storage/docs/authentication?hl=en#service_accounts">
 * Service Accounts</a> for instructions on how to get the Service Account JSON Key.
 *
 * <p>The short version is this: go to cloud.google.com/console, select your project, search for
 * "API manager", click "Credentials", click "create credentials/service account key", new service
 * account, JSON. The contents of the file that's sent to your browsers is your "Service Account
 * JSON Key".
 */
@RunWith(JUnit4.class)
public class ITGcsNio {
    private static final List<String> FILE_CONTENTS = ImmutableList.of("Tous les ?tres humains naissent libres et ?gaux en dignit? et en droits.", "Ils sont dou?s de raison et de conscience et doivent agir ", "les uns envers les autres dans un esprit de fraternit?.");

    private static final Logger log = Logger.getLogger(ITGcsNio.class.getName());

    private static final String BUCKET = RemoteStorageHelper.generateBucketName();

    private static final String REQUESTER_PAYS_BUCKET = (RemoteStorageHelper.generateBucketName()) + "_rp";

    private static final String SML_FILE = "tmp-test-small-file.txt";

    private static final String TMP_FILE = "tmp/tmp-test-rnd-file.txt";

    private static final int SML_SIZE = 100;

    private static final String BIG_FILE = "tmp-test-big-file.txt";// it's big, relatively speaking.


    private static final int BIG_SIZE = ((2 * 1024) * 1024) - 50;// arbitrary size that's not too round.


    private static final String PREFIX = "tmp-test-file";

    private static String project;

    private static Storage storage;

    private static StorageOptions storageOptions;

    private final Random rnd = new Random();

    // Start of tests related to the "requester pays" feature
    @Test
    public void testFileExistsRequesterPaysNoUserProject() throws IOException {
        CloudStorageFileSystem testBucket = getRequesterPaysBucket(false, "");
        Path path = testBucket.getPath(ITGcsNio.SML_FILE);
        try {
            // fails because we must pay for every access, including metadata.
            Files.exists(path);
            Assert.fail("It should have thrown an exception.");
        } catch (StorageException ex) {
            assertIsRequesterPaysException("testFileExistsRequesterPaysNoUserProject", ex);
        }
    }

    @Test
    public void testFileExistsRequesterPays() throws IOException {
        CloudStorageFileSystem testBucket = getRequesterPaysBucket(false, ITGcsNio.project);
        Path path = testBucket.getPath(ITGcsNio.SML_FILE);
        // should succeed because we specified a project
        Files.exists(path);
    }

    @Test
    public void testFileExistsRequesterPaysWithAutodetect() throws IOException {
        CloudStorageFileSystem testBucket = getRequesterPaysBucket(true, ITGcsNio.project);
        Path path = testBucket.getPath(ITGcsNio.SML_FILE);
        // should succeed because we specified a project
        Files.exists(path);
    }

    @Test
    public void testCantCreateWithoutUserProject() throws IOException {
        CloudStorageFileSystem testBucket = getRequesterPaysBucket(false, "");
        Path path = testBucket.getPath(ITGcsNio.TMP_FILE);
        try {
            // fails
            Files.write(path, "I would like to write".getBytes());
            Assert.fail("It should have thrown an exception.");
        } catch (IOException ex) {
            assertIsRequesterPaysException("testCantCreateWithoutUserProject", ex);
        }
    }

    @Test
    public void testCanCreateWithUserProject() throws IOException {
        CloudStorageFileSystem testBucket = getRequesterPaysBucket(false, ITGcsNio.project);
        Path path = testBucket.getPath(ITGcsNio.TMP_FILE);
        // should succeed because we specified a project
        Files.write(path, "I would like to write, please?".getBytes());
    }

    @Test
    public void testCantReadWithoutUserProject() throws IOException {
        CloudStorageFileSystem testBucket = getRequesterPaysBucket(false, "");
        Path path = testBucket.getPath(ITGcsNio.SML_FILE);
        try {
            // fails
            Files.readAllBytes(path);
            Assert.fail("It should have thrown an exception.");
        } catch (StorageException ex) {
            assertIsRequesterPaysException("testCantReadWithoutUserProject", ex);
        }
    }

    @Test
    public void testCanReadWithUserProject() throws IOException {
        CloudStorageFileSystem testBucket = getRequesterPaysBucket(false, ITGcsNio.project);
        Path path = testBucket.getPath(ITGcsNio.SML_FILE);
        // should succeed because we specified a project
        Files.readAllBytes(path);
    }

    @Test
    public void testCantCopyWithoutUserProject() throws IOException {
        CloudStorageFileSystem testRPBucket = getRequesterPaysBucket(false, "");
        CloudStorageFileSystem testBucket = getTestBucket();
        CloudStoragePath[] sources = new CloudStoragePath[]{ testBucket.getPath(ITGcsNio.SML_FILE), testRPBucket.getPath(ITGcsNio.SML_FILE) };
        CloudStoragePath[] dests = new CloudStoragePath[]{ testBucket.getPath(ITGcsNio.TMP_FILE), testRPBucket.getPath(ITGcsNio.TMP_FILE) };
        for (int s = 0; s < 2; s++) {
            for (int d = 0; d < 2; d++) {
                // normal to normal is out of scope of RP testing.
                if ((s == 0) && (d == 0)) {
                    continue;
                }
                innerTestCantCopyWithoutUserProject((s == 0), (d == 0), sources[s], dests[d]);
            }
        }
    }

    @Test
    public void testCanCopyWithUserProject() throws IOException {
        CloudStorageFileSystem testRPBucket = getRequesterPaysBucket(false, ITGcsNio.project);
        CloudStorageFileSystem testBucket = getTestBucket();
        CloudStoragePath[] sources = new CloudStoragePath[]{ testBucket.getPath(ITGcsNio.SML_FILE), testRPBucket.getPath(ITGcsNio.SML_FILE) };
        CloudStoragePath[] dests = new CloudStoragePath[]{ testBucket.getPath(ITGcsNio.TMP_FILE), testRPBucket.getPath(ITGcsNio.TMP_FILE) };
        for (int s = 0; s < 2; s++) {
            for (int d = 0; d < 2; d++) {
                // normal to normal is out of scope of RP testing.
                if ((s == 0) && (d == 0)) {
                    continue;
                }
                Files.copy(sources[s], dests[d], StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @Test
    public void testAutodetectWhenRequesterPays() throws IOException {
        CloudStorageFileSystem testRPBucket = getRequesterPaysBucket(true, ITGcsNio.project);
        Assert.assertEquals("Autodetect should have detected the RP bucket", testRPBucket.config().userProject(), ITGcsNio.project);
    }

    @Test
    public void testAutodetectWhenNotRequesterPays() throws IOException {
        CloudStorageConfiguration config = CloudStorageConfiguration.builder().autoDetectRequesterPays(true).userProject(ITGcsNio.project).build();
        CloudStorageFileSystem testBucket = CloudStorageFileSystem.forBucket(ITGcsNio.BUCKET, config, ITGcsNio.storageOptions);
        Assert.assertEquals("Autodetect should have detected the bucket is not RP", testBucket.config().userProject(), "");
    }

    // End of tests related to the "requester pays" feature
    @Test
    public void testListBuckets() throws IOException {
        boolean bucketFound = false;
        boolean rpBucketFound = false;
        for (Bucket b : CloudStorageFileSystem.listBuckets(ITGcsNio.project).iterateAll()) {
            bucketFound |= ITGcsNio.BUCKET.equals(b.getName());
            rpBucketFound |= ITGcsNio.REQUESTER_PAYS_BUCKET.equals(b.getName());
        }
        assertWithMessage("listBucket should have found the test bucket").that(bucketFound).isTrue();
        assertWithMessage("listBucket should have found the test requester-pays bucket").that(rpBucketFound).isTrue();
    }

    @Test
    public void testFileExists() throws IOException {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path path = testBucket.getPath(ITGcsNio.SML_FILE);
        assertThat(Files.exists(path)).isTrue();
    }

    @Test
    public void testFileSize() throws IOException {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path path = testBucket.getPath(ITGcsNio.SML_FILE);
        assertThat(Files.size(path)).isEqualTo(ITGcsNio.SML_SIZE);
    }

    @Test(timeout = 60000)
    public void testReadByteChannel() throws IOException {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path path = testBucket.getPath(ITGcsNio.SML_FILE);
        long size = Files.size(path);
        SeekableByteChannel chan = Files.newByteChannel(path, StandardOpenOption.READ);
        assertThat(chan.size()).isEqualTo(size);
        ByteBuffer buf = ByteBuffer.allocate(ITGcsNio.SML_SIZE);
        int read = 0;
        while (chan.isOpen()) {
            int rc = chan.read(buf);
            assertThat(chan.size()).isEqualTo(size);
            if (rc < 0) {
                // EOF
                break;
            }
            assertThat(rc).isGreaterThan(0);
            read += rc;
            assertThat(chan.position()).isEqualTo(read);
        } 
        assertThat(read).isEqualTo(size);
        byte[] expected = new byte[ITGcsNio.SML_SIZE];
        new Random(ITGcsNio.SML_SIZE).nextBytes(expected);
        assertThat(Arrays.equals(buf.array(), expected)).isTrue();
    }

    @Test
    public void testSeek() throws IOException {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path path = testBucket.getPath(ITGcsNio.BIG_FILE);
        int size = ITGcsNio.BIG_SIZE;
        byte[] contents = ITGcsNio.randomContents(size);
        byte[] sample = new byte[100];
        byte[] wanted;
        byte[] wanted2;
        SeekableByteChannel chan = Files.newByteChannel(path, StandardOpenOption.READ);
        assertThat(chan.size()).isEqualTo(size);
        // check seek
        int dest = size / 2;
        chan.position(dest);
        readFully(chan, sample);
        wanted = Arrays.copyOfRange(contents, dest, (dest + 100));
        assertThat(wanted).isEqualTo(sample);
        // now go back and check the beginning
        // (we do 2 locations because 0 is sometimes a special case).
        chan.position(0);
        readFully(chan, sample);
        wanted2 = Arrays.copyOf(contents, 100);
        assertThat(wanted2).isEqualTo(sample);
        // if the two spots in the file have the same contents, then this isn't a good file for this
        // test.
        assertThat(wanted).isNotEqualTo(wanted2);
    }

    @Test
    public void testCreate() throws IOException {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path path = testBucket.getPath(((ITGcsNio.PREFIX) + (randomSuffix())));
        // file shouldn't exist initially. If it does it's either because it's a leftover
        // from a previous run (so we should delete the file)
        // or because we're misconfigured and pointing to an actually important file
        // (so we should absolutely not delete it).
        // So if the file's here, don't try to fix it automatically, let the user deal with it.
        assertThat(Files.exists(path)).isFalse();
        try {
            Files.createFile(path);
            // now it does, and it has size 0.
            assertThat(Files.exists(path)).isTrue();
            long size = Files.size(path);
            assertThat(size).isEqualTo(0);
        } finally {
            // let's not leave files around
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void testWrite() throws IOException {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path path = testBucket.getPath(((ITGcsNio.PREFIX) + (randomSuffix())));
        // file shouldn't exist initially. If it does it's either because it's a leftover
        // from a previous run (so we should delete the file)
        // or because we're misconfigured and pointing to an actually important file
        // (so we should absolutely not delete it).
        // So if the file's here, don't try to fix it automatically, let the user deal with it.
        assertThat(Files.exists(path)).isFalse();
        try {
            Files.write(path, ITGcsNio.FILE_CONTENTS, StandardCharsets.UTF_8);
            // now it does.
            assertThat(Files.exists(path)).isTrue();
            // let's check that the contents is OK.
            ByteArrayOutputStream wantBytes = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(wantBytes, StandardCharsets.UTF_8));
            for (String content : ITGcsNio.FILE_CONTENTS) {
                writer.println(content);
            }
            writer.close();
            SeekableByteChannel chan = Files.newByteChannel(path, StandardOpenOption.READ);
            byte[] gotBytes = new byte[((int) (chan.size()))];
            readFully(chan, gotBytes);
            assertThat(gotBytes).isEqualTo(wantBytes.toByteArray());
        } finally {
            // let's not leave files around
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void testCreateAndWrite() throws IOException {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path path = testBucket.getPath(((ITGcsNio.PREFIX) + (randomSuffix())));
        // file shouldn't exist initially (see above).
        assertThat(Files.exists(path)).isFalse();
        try {
            Files.createFile(path);
            Files.write(path, ITGcsNio.FILE_CONTENTS, StandardCharsets.UTF_8);
            // now it does.
            assertThat(Files.exists(path)).isTrue();
            // let's check that the contents is OK.
            ByteArrayOutputStream wantBytes = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(wantBytes, StandardCharsets.UTF_8));
            for (String content : ITGcsNio.FILE_CONTENTS) {
                writer.println(content);
            }
            writer.close();
            SeekableByteChannel chan = Files.newByteChannel(path, StandardOpenOption.READ);
            byte[] gotBytes = new byte[((int) (chan.size()))];
            readFully(chan, gotBytes);
            assertThat(gotBytes).isEqualTo(wantBytes.toByteArray());
        } finally {
            // let's not leave files around
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void testWriteOnClose() throws Exception {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path path = testBucket.getPath(((ITGcsNio.PREFIX) + (randomSuffix())));
        // file shouldn't exist initially (see above)
        assertThat(Files.exists(path)).isFalse();
        try {
            long expectedSize = 0;
            try (SeekableByteChannel chan = Files.newByteChannel(path, StandardOpenOption.WRITE)) {
                // writing lots of contents to defeat channel-internal buffering.
                for (String s : ITGcsNio.FILE_CONTENTS) {
                    byte[] sBytes = s.getBytes(StandardCharsets.UTF_8);
                    expectedSize += (sBytes.length) * 9999;
                    for (int i = 0; i < 9999; i++) {
                        chan.write(ByteBuffer.wrap(sBytes));
                    }
                }
                try {
                    Files.size(path);
                    // we shouldn't make it to this line. Not using thrown.expect because
                    // I still want to run a few lines after the exception.
                    Assert.fail("Files.size should have thrown an exception");
                } catch (NoSuchFileException nsf) {
                    // that's what we wanted, we're good.
                }
            }
            // channel now closed, the file should be there and with the new contents.
            assertThat(Files.exists(path)).isTrue();
            assertThat(Files.size(path)).isEqualTo(expectedSize);
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void testCopy() throws IOException {
        CloudStorageFileSystem testBucket = getTestBucket();
        Path src = testBucket.getPath(ITGcsNio.SML_FILE);
        Path dst = testBucket.getPath(((ITGcsNio.PREFIX) + (randomSuffix())));
        // file shouldn't exist initially (see above).
        assertThat(Files.exists(dst)).isFalse();
        try {
            Files.copy(src, dst);
            assertThat(Files.exists(dst)).isTrue();
            assertThat(Files.size(dst)).isEqualTo(ITGcsNio.SML_SIZE);
            byte[] got = new byte[ITGcsNio.SML_SIZE];
            readFully(Files.newByteChannel(dst), got);
            assertThat(got).isEqualTo(ITGcsNio.randomContents(ITGcsNio.SML_SIZE));
        } finally {
            // let's not leave files around
            Files.deleteIfExists(dst);
        }
    }

    @Test
    public void testListFiles() throws IOException {
        try (FileSystem fs = getTestBucket()) {
            List<Path> goodPaths = new ArrayList<>();
            List<Path> paths = new ArrayList<>();
            goodPaths.add(fs.getPath("dir/angel"));
            goodPaths.add(fs.getPath("dir/alone"));
            paths.add(fs.getPath("dir/dir2/another_angel"));
            paths.add(fs.getPath("atroot"));
            paths.addAll(goodPaths);
            goodPaths.add(fs.getPath("dir/dir2/"));
            for (Path path : paths) {
                ITGcsNio.fillFile(ITGcsNio.storage, ITGcsNio.BUCKET, path.toString(), ITGcsNio.SML_SIZE);
            }
            List<Path> got = new ArrayList<>();
            for (String folder : new String[]{ "/dir/", "/dir", "dir/", "dir" }) {
                got.clear();
                for (Path path : Files.newDirectoryStream(fs.getPath(folder))) {
                    got.add(path);
                }
                assertWithMessage((("Listing " + folder) + ": ")).that(got).containsExactlyElementsIn(goodPaths);
            }
            // clean up
            for (Path path : paths) {
                Files.delete(path);
            }
        }
    }

    @Test
    public void testRelativityOfResolve() throws IOException {
        try (FileSystem fs = getTestBucket()) {
            Path abs1 = fs.getPath("/dir");
            Path abs2 = abs1.resolve("subdir/");
            Path rel1 = fs.getPath("dir");
            Path rel2 = rel1.resolve("subdir/");
            // children of absolute paths are absolute,
            // children of relative paths are relative.
            assertThat(abs1.isAbsolute()).isTrue();
            assertThat(abs2.isAbsolute()).isTrue();
            assertThat(rel1.isAbsolute()).isFalse();
            assertThat(rel2.isAbsolute()).isFalse();
        }
    }

    @Test
    public void testWalkFiles() throws IOException {
        try (FileSystem fs = getTestBucket()) {
            List<Path> goodPaths = new ArrayList<>();
            List<Path> paths = new ArrayList<>();
            goodPaths.add(fs.getPath("dir/angel"));
            goodPaths.add(fs.getPath("dir/alone"));
            paths.add(fs.getPath("dir/dir2/another_angel"));
            paths.add(fs.getPath("atroot"));
            paths.addAll(goodPaths);
            for (Path path : paths) {
                ITGcsNio.fillFile(ITGcsNio.storage, ITGcsNio.BUCKET, path.toString(), ITGcsNio.SML_SIZE);
            }
            // Given a relative path as starting point, walkFileTree must return only relative paths.
            List<Path> relativePaths = ITGcsNio.PostTraversalWalker.walkFileTree(fs.getPath("dir/"));
            for (Path p : relativePaths) {
                assertWithMessage(("Should have been relative: " + (p.toString()))).that(p.isAbsolute()).isFalse();
            }
            // The 5 paths are:
            // dir/, dir/angel, dir/alone, dir/dir2/, dir/dir2/another_angel.
            assertThat(relativePaths.size()).isEqualTo(5);
            // Given an absolute path as starting point, walkFileTree must return only relative paths.
            List<Path> absolutePaths = ITGcsNio.PostTraversalWalker.walkFileTree(fs.getPath("/dir/"));
            for (Path p : absolutePaths) {
                assertWithMessage(("Should have been absolute: " + (p.toString()))).that(p.isAbsolute()).isTrue();
            }
            assertThat(absolutePaths.size()).isEqualTo(5);
        }
    }

    @Test
    public void testDeleteRecursive() throws IOException {
        try (FileSystem fs = getTestBucket()) {
            List<Path> paths = new ArrayList<>();
            paths.add(fs.getPath("Racine"));
            paths.add(fs.getPath("playwrights/Moliere"));
            paths.add(fs.getPath("playwrights/French/Corneille"));
            for (Path path : paths) {
                Files.write(path, ITGcsNio.FILE_CONTENTS, StandardCharsets.UTF_8);
            }
            ITGcsNio.deleteRecursive(fs.getPath("playwrights/"));
            assertThat(Files.exists(fs.getPath("playwrights/Moliere"))).isFalse();
            assertThat(Files.exists(fs.getPath("playwrights/French/Corneille"))).isFalse();
            assertThat(Files.exists(fs.getPath("Racine"))).isTrue();
            Files.deleteIfExists(fs.getPath("Racine"));
            assertThat(Files.exists(fs.getPath("Racine"))).isFalse();
        }
    }

    @Test
    public void testListFilesInRootDirectory() throws IOException {
        // We must explicitly set the storageOptions, because the unit tests
        // set the fake storage as default but we want to access the real storage.
        CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket(ITGcsNio.BUCKET, CloudStorageConfiguration.builder().permitEmptyPathComponents(true).build(), ITGcsNio.storageOptions);
        // test absolute path, relative path.
        for (String check : new String[]{ ".", "/", "" }) {
            Path rootPath = fs.getPath(check);
            List<Path> pathsFound = new ArrayList<>();
            for (Path path : Files.newDirectoryStream(rootPath)) {
                // The returned paths will match the absolute-ness of the root path
                // (this matches the behavior of the built-in UNIX file system).
                assertWithMessage((("Absolute/relative for " + check) + ": ")).that(path.isAbsolute()).isEqualTo(rootPath.isAbsolute());
                // To simplify the check that we found our files, we normalize here.
                pathsFound.add(path.toAbsolutePath());
            }
            assertWithMessage((("Listing " + check) + ": ")).that(pathsFound).containsExactly(fs.getPath(ITGcsNio.BIG_FILE).toAbsolutePath(), fs.getPath(ITGcsNio.SML_FILE).toAbsolutePath());
        }
    }

    @Test
    public void testFakeDirectories() throws IOException {
        try (FileSystem fs = getTestBucket()) {
            List<Path> paths = new ArrayList<>();
            paths.add(fs.getPath("dir/angel"));
            paths.add(fs.getPath("dir/deepera"));
            paths.add(fs.getPath("dir/deeperb"));
            paths.add(fs.getPath("dir/deeper_"));
            paths.add(fs.getPath("dir/deeper.sea/hasfish"));
            paths.add(fs.getPath("dir/deeper/fish"));
            for (Path path : paths) {
                Files.createFile(path);
            }
            // ends with slash, must be a directory
            assertThat(Files.isDirectory(fs.getPath("dir/"))).isTrue();
            // files are not directories
            assertThat(Files.exists(fs.getPath("dir/angel"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/angel"))).isFalse();
            // directories are recognized even without the trailing "/"
            assertThat(Files.isDirectory(fs.getPath("dir"))).isTrue();
            // also works for absolute paths
            assertThat(Files.isDirectory(fs.getPath("/dir"))).isTrue();
            // non-existent files are not directories (but they don't make us crash)
            assertThat(Files.isDirectory(fs.getPath("di"))).isFalse();
            assertThat(Files.isDirectory(fs.getPath("dirs"))).isFalse();
            assertThat(Files.isDirectory(fs.getPath("dir/deep"))).isFalse();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper/fi"))).isFalse();
            assertThat(Files.isDirectory(fs.getPath("/dir/deeper/fi"))).isFalse();
            // also works for subdirectories
            assertThat(Files.isDirectory(fs.getPath("dir/deeper/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("/dir/deeper/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("/dir/deeper"))).isTrue();
            // dot and .. folders are directories
            assertThat(Files.isDirectory(fs.getPath("dir/deeper/."))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper/.."))).isTrue();
            // dots in the name are fine
            assertThat(Files.isDirectory(fs.getPath("dir/deeper.sea/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper.sea"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper.seax"))).isFalse();
            // the root folder is a directory
            assertThat(Files.isDirectory(fs.getPath("/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath(""))).isTrue();
            // clean up
            for (Path path : paths) {
                Files.delete(path);
            }
        }
    }

    private static class PostTraversalWalker extends SimpleFileVisitor<Path> {
        private final List<Path> paths = new ArrayList<>();

        // Traverse the tree, return the list of files and folders.
        public static ImmutableList<Path> walkFileTree(Path start) throws IOException {
            ITGcsNio.PostTraversalWalker walker = new ITGcsNio.PostTraversalWalker();
            Files.walkFileTree(start, walker);
            return walker.getPaths();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            paths.add(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            paths.add(dir);
            return FileVisitResult.CONTINUE;
        }

        public ImmutableList<Path> getPaths() {
            return ImmutableList.copyOf(paths);
        }
    }
}

