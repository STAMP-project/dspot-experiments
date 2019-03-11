/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.util;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.testutils.CheckedThread;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link FileUtils}.
 */
public class FileUtilsTest extends TestLogger {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    // ------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------
    @Test
    public void testReadAllBytes() throws Exception {
        TemporaryFolder tmpFolder = null;
        try {
            tmpFolder = new TemporaryFolder(new File(this.getClass().getResource("/").getPath()));
            tmpFolder.create();
            final int fileSize = 1024;
            final String testFilePath = (((((tmpFolder.getRoot().getAbsolutePath()) + (File.separator)) + (this.getClass().getSimpleName())) + "_") + fileSize) + ".txt";
            {
                String expectedMD5 = FileUtilsTest.generateTestFile(testFilePath, 1024);
                final byte[] data = FileUtils.readAllBytes(new File(testFilePath).toPath());
                Assert.assertEquals(expectedMD5, FileUtilsTest.md5Hex(data));
            }
            {
                String expectedMD5 = FileUtilsTest.generateTestFile(testFilePath, 4096);
                final byte[] data = FileUtils.readAllBytes(new File(testFilePath).toPath());
                Assert.assertEquals(expectedMD5, FileUtilsTest.md5Hex(data));
            }
            {
                String expectedMD5 = FileUtilsTest.generateTestFile(testFilePath, 5120);
                final byte[] data = FileUtils.readAllBytes(new File(testFilePath).toPath());
                Assert.assertEquals(expectedMD5, FileUtilsTest.md5Hex(data));
            }
        } finally {
            if (tmpFolder != null) {
                tmpFolder.delete();
            }
        }
    }

    @Test
    public void testDeletePathIfEmpty() throws IOException {
        final FileSystem localFs = FileSystem.getLocalFileSystem();
        final File dir = tmp.newFolder();
        Assert.assertTrue(dir.exists());
        final Path dirPath = new Path(dir.toURI());
        // deleting an empty directory should work
        Assert.assertTrue(FileUtils.deletePathIfEmpty(localFs, dirPath));
        // deleting a non existing directory should work
        Assert.assertTrue(FileUtils.deletePathIfEmpty(localFs, dirPath));
        // create a non-empty dir
        final File nonEmptyDir = tmp.newFolder();
        final Path nonEmptyDirPath = new Path(nonEmptyDir.toURI());
        new FileOutputStream(new File(nonEmptyDir, "filename")).close();
        Assert.assertFalse(FileUtils.deletePathIfEmpty(localFs, nonEmptyDirPath));
    }

    @Test
    public void testDeleteQuietly() throws Exception {
        // should ignore the call
        FileUtils.deleteDirectoryQuietly(null);
        File doesNotExist = new File(tmp.getRoot(), "abc");
        FileUtils.deleteDirectoryQuietly(doesNotExist);
        File cannotDeleteParent = tmp.newFolder();
        File cannotDeleteChild = new File(cannotDeleteParent, "child");
        try {
            Assume.assumeTrue(cannotDeleteChild.createNewFile());
            Assume.assumeTrue(cannotDeleteParent.setWritable(false));
            Assume.assumeTrue(cannotDeleteChild.setWritable(false));
            FileUtils.deleteDirectoryQuietly(cannotDeleteParent);
        } finally {
            // noinspection ResultOfMethodCallIgnored
            cannotDeleteParent.setWritable(true);
            // noinspection ResultOfMethodCallIgnored
            cannotDeleteChild.setWritable(true);
        }
    }

    @Test
    public void testDeleteDirectory() throws Exception {
        // deleting a non-existent file should not cause an error
        File doesNotExist = new File(tmp.newFolder(), "abc");
        FileUtils.deleteDirectory(doesNotExist);
        // deleting a write protected file should throw an error
        File cannotDeleteParent = tmp.newFolder();
        File cannotDeleteChild = new File(cannotDeleteParent, "child");
        try {
            Assume.assumeTrue(cannotDeleteChild.createNewFile());
            Assume.assumeTrue(cannotDeleteParent.setWritable(false));
            Assume.assumeTrue(cannotDeleteChild.setWritable(false));
            FileUtils.deleteDirectory(cannotDeleteParent);
            Assert.fail("this should fail with an exception");
        } catch (AccessDeniedException ignored) {
            // this is expected
        } finally {
            // noinspection ResultOfMethodCallIgnored
            cannotDeleteParent.setWritable(true);
            // noinspection ResultOfMethodCallIgnored
            cannotDeleteChild.setWritable(true);
        }
    }

    @Test
    public void testDeleteDirectoryWhichIsAFile() throws Exception {
        // deleting a directory that is actually a file should fails
        File file = tmp.newFile();
        try {
            FileUtils.deleteDirectory(file);
            Assert.fail("this should fail with an exception");
        } catch (IOException ignored) {
            // this is what we expect
        }
    }

    @Test
    public void testCompression() throws IOException {
        final String testFileContent = "Goethe - Faust: Der Tragoedie erster Teil\n" + (((((((((((((("Prolog im Himmel.\n" + "Der Herr. Die himmlischen Heerscharen. Nachher Mephistopheles. Die drei\n") + "Erzengel treten vor.\n") + "RAPHAEL: Die Sonne toent, nach alter Weise, In Brudersphaeren Wettgesang,\n") + "Und ihre vorgeschriebne Reise Vollendet sie mit Donnergang. Ihr Anblick\n") + "gibt den Engeln Staerke, Wenn keiner Sie ergruenden mag; die unbegreiflich\n") + "hohen Werke Sind herrlich wie am ersten Tag.\n") + "GABRIEL: Und schnell und unbegreiflich schnelle Dreht sich umher der Erde\n") + "Pracht; Es wechselt Paradieseshelle Mit tiefer, schauervoller Nacht. Es\n") + "schaeumt das Meer in breiten Fluessen Am tiefen Grund der Felsen auf, Und\n") + "Fels und Meer wird fortgerissen Im ewig schnellem Sphaerenlauf.\n") + "MICHAEL: Und Stuerme brausen um die Wette Vom Meer aufs Land, vom Land\n") + "aufs Meer, und bilden wuetend eine Kette Der tiefsten Wirkung rings umher.\n") + "Da flammt ein blitzendes Verheeren Dem Pfade vor des Donnerschlags. Doch\n") + "deine Boten, Herr, verehren Das sanfte Wandeln deines Tags.");
        final java.nio.file.Path compressDir = tmp.newFolder("compressDir").toPath();
        final java.nio.file.Path extractDir = tmp.newFolder("extractDir").toPath();
        final java.nio.file.Path originalDir = Paths.get("rootDir");
        final java.nio.file.Path emptySubDir = originalDir.resolve("emptyDir");
        final java.nio.file.Path fullSubDir = originalDir.resolve("fullDir");
        final java.nio.file.Path file1 = originalDir.resolve("file1");
        final java.nio.file.Path file2 = originalDir.resolve("file2");
        final java.nio.file.Path file3 = fullSubDir.resolve("file3");
        Files.createDirectory(compressDir.resolve(originalDir));
        Files.createDirectory(compressDir.resolve(emptySubDir));
        Files.createDirectory(compressDir.resolve(fullSubDir));
        Files.copy(new ByteArrayInputStream(testFileContent.getBytes(StandardCharsets.UTF_8)), compressDir.resolve(file1));
        Files.createFile(compressDir.resolve(file2));
        Files.copy(new ByteArrayInputStream(testFileContent.getBytes(StandardCharsets.UTF_8)), compressDir.resolve(file3));
        final Path zip = FileUtils.compressDirectory(new Path(compressDir.resolve(originalDir).toString()), new Path(((compressDir.resolve(originalDir)) + ".zip")));
        FileUtils.expandDirectory(zip, new Path(extractDir.toAbsolutePath().toString()));
        FileUtilsTest.assertDirEquals(compressDir.resolve(originalDir), extractDir.resolve(originalDir));
    }

    // ------------------------------------------------------------------------
    private static class Deleter extends CheckedThread {
        private final File target;

        Deleter(File target) {
            this.target = target;
        }

        @Override
        public void go() throws Exception {
            FileUtils.deleteDirectory(target);
        }
    }
}

