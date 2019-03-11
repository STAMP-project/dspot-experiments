/**
 * Copyright 2019 The Nomulus Authors. All Rights Reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package google.registry.gradle.plugin;


import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.common.collect.ImmutableMap;
import google.registry.gradle.plugin.ProjectData.TaskData.ReportFiles;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link GcsPluginUtilsTest}
 */
@RunWith(JUnit4.class)
public final class GcsPluginUtilsTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testGetContentType_knownTypes() {
        assertThat(GcsPluginUtils.getContentType("path/to/file.html")).isEqualTo("text/html");
        assertThat(GcsPluginUtils.getContentType("path/to/file.htm")).isEqualTo("text/html");
        assertThat(GcsPluginUtils.getContentType("path/to/file.log")).isEqualTo("text/plain");
        assertThat(GcsPluginUtils.getContentType("path/to/file.txt")).isEqualTo("text/plain");
        assertThat(GcsPluginUtils.getContentType("path/to/file.css")).isEqualTo("text/css");
        assertThat(GcsPluginUtils.getContentType("path/to/file.xml")).isEqualTo("text/xml");
        assertThat(GcsPluginUtils.getContentType("path/to/file.zip")).isEqualTo("application/zip");
        assertThat(GcsPluginUtils.getContentType("path/to/file.js")).isEqualTo("text/javascript");
    }

    @Test
    public void testGetContentType_unknownTypes() {
        assertThat(GcsPluginUtils.getContentType("path/to/file.unknown")).isEqualTo("application/octet-stream");
    }

    @Test
    public void testUploadFileToGcs() {
        Storage storage = Mockito.mock(Storage.class);
        GcsPluginUtils.uploadFileToGcs(storage, "my-bucket", Paths.get("my", "filename.txt"), GcsPluginUtils.toByteArraySupplier("my data"));
        Mockito.verify(storage).create(BlobInfo.newBuilder("my-bucket", "my/filename.txt").setContentType("text/plain").build(), "my data".getBytes(StandardCharsets.UTF_8));
        Mockito.verifyNoMoreInteractions(storage);
    }

    @Test
    public void testUploadFilesToGcsMultithread() {
        Storage storage = Mockito.mock(Storage.class);
        GcsPluginUtils.uploadFilesToGcsMultithread(storage, "my-bucket", Paths.get("my", "folder"), ImmutableMap.of(Paths.get("some", "index.html"), GcsPluginUtils.toByteArraySupplier("some web page"), Paths.get("some", "style.css"), GcsPluginUtils.toByteArraySupplier("some style"), Paths.get("other", "index.html"), GcsPluginUtils.toByteArraySupplier("other web page"), Paths.get("other", "style.css"), GcsPluginUtils.toByteArraySupplier("other style")));
        Mockito.verify(storage).create(BlobInfo.newBuilder("my-bucket", "my/folder/some/index.html").setContentType("text/html").build(), "some web page".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(storage).create(BlobInfo.newBuilder("my-bucket", "my/folder/some/style.css").setContentType("text/css").build(), "some style".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(storage).create(BlobInfo.newBuilder("my-bucket", "my/folder/other/index.html").setContentType("text/html").build(), "other web page".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(storage).create(BlobInfo.newBuilder("my-bucket", "my/folder/other/style.css").setContentType("text/css").build(), "other style".getBytes(StandardCharsets.UTF_8));
        Mockito.verifyNoMoreInteractions(storage);
    }

    @Test
    public void testToByteArraySupplier_string() {
        assertThat(GcsPluginUtils.toByteArraySupplier("my string").get()).isEqualTo("my string".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testToByteArraySupplier_stringSupplier() {
        assertThat(GcsPluginUtils.toByteArraySupplier(() -> "my string").get()).isEqualTo("my string".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testToByteArraySupplier_file() throws Exception {
        folder.newFolder("arbitrary");
        File file = folder.newFile("arbitrary/file.txt");
        Files.write(file.toPath(), "some data".getBytes(StandardCharsets.UTF_8));
        assertThat(GcsPluginUtils.toByteArraySupplier(file).get()).isEqualTo("some data".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testCreateReportFiles_destinationIsFile() throws Exception {
        Path root = GcsPluginUtils.toNormalizedPath(folder.newFolder("my", "root"));
        folder.newFolder("my", "root", "some", "path");
        File destination = folder.newFile("my/root/some/path/file.txt");
        Files.write(destination.toPath(), "some data".getBytes(StandardCharsets.UTF_8));
        // Since the entry point is obvious here - any hint given is just ignored.
        File ignoredHint = folder.newFile("my/root/ignored.txt");
        ReportFiles files = GcsPluginUtils.createReportFiles(destination, Optional.of(ignoredHint), root);
        assertThat(files.entryPoint().toString()).isEqualTo("some/path/file.txt");
        assertThat(readAllFiles(files)).containsExactly("some/path/file.txt", "some data");
    }

    @Test
    public void testCreateReportFiles_destinationDoesntExist() throws Exception {
        Path root = GcsPluginUtils.toNormalizedPath(folder.newFolder("my", "root"));
        File destination = root.resolve("non/existing.txt").toFile();
        assertThat(destination.isFile()).isFalse();
        assertThat(destination.isDirectory()).isFalse();
        // Since there are not files, any hint given is obvioulsy wrong and will be ignored.
        File ignoredHint = folder.newFile("my/root/ignored.txt");
        ReportFiles files = GcsPluginUtils.createReportFiles(destination, Optional.of(ignoredHint), root);
        assertThat(files.entryPoint().toString()).isEqualTo("non/existing.txt");
        assertThat(files.files()).isEmpty();
    }

    @Test
    public void testCreateReportFiles_noFiles() throws Exception {
        Path root = GcsPluginUtils.toNormalizedPath(folder.newFolder("my", "root"));
        File destination = folder.newFolder("my", "root", "some", "path");
        folder.newFolder("my", "root", "some", "path", "a", "b");
        folder.newFolder("my", "root", "some", "path", "c");
        // Since there are not files, any hint given is obvioulsy wrong and will be ignored.
        File ignoredHint = folder.newFile("my/root/ignored.txt");
        ReportFiles files = GcsPluginUtils.createReportFiles(destination, Optional.of(ignoredHint), root);
        assertThat(files.entryPoint().toString()).isEqualTo("some/path");
        assertThat(files.files()).isEmpty();
    }

    @Test
    public void testCreateReportFiles_oneFile() throws Exception {
        Path root = GcsPluginUtils.toNormalizedPath(folder.newFolder("my", "root"));
        File destination = folder.newFolder("my", "root", "some", "path");
        folder.newFolder("my", "root", "some", "path", "a", "b");
        folder.newFolder("my", "root", "some", "path", "c");
        Files.write(folder.newFile("my/root/some/path/a/file.txt").toPath(), "some data".getBytes(StandardCharsets.UTF_8));
        // Since the entry point is obvious here - any hint given is just ignored.
        File ignoredHint = folder.newFile("my/root/ignored.txt");
        ReportFiles files = GcsPluginUtils.createReportFiles(destination, Optional.of(ignoredHint), root);
        assertThat(files.entryPoint().toString()).isEqualTo("some/path/a/file.txt");
        assertThat(readAllFiles(files)).containsExactly("some/path/a/file.txt", "some data");
    }

    /**
     * Currently tests the "unimplemented" behavior.
     *
     * <p>TODO(guyben): switch to checking zip file instead.
     */
    @Test
    public void testCreateReportFiles_multipleFiles_noHint() throws Exception {
        Path root = GcsPluginUtils.toNormalizedPath(folder.newFolder("my", "root"));
        File destination = folder.newFolder("my", "root", "some", "path");
        folder.newFolder("my", "root", "some", "path", "a", "b");
        folder.newFolder("my", "root", "some", "path", "c");
        Files.write(folder.newFile("my/root/some/path/index.html").toPath(), "some data".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/a/index.html").toPath(), "wrong index".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/c/style.css").toPath(), "css file".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/my_image.png").toPath(), "images".getBytes(StandardCharsets.UTF_8));
        ReportFiles files = GcsPluginUtils.createReportFiles(destination, Optional.empty(), root);
        assertThat(files.entryPoint().toString()).isEqualTo("some/path/path.zip");
        assertThat(readAllFiles(files).keySet()).containsExactly("some/path/path.zip");
    }

    /**
     * Currently tests the "unimplemented" behavior.
     *
     * <p>TODO(guyben): switch to checking zip file instead.
     */
    @Test
    public void testCreateReportFiles_multipleFiles_withBadHint() throws Exception {
        Path root = GcsPluginUtils.toNormalizedPath(folder.newFolder("my", "root"));
        File destination = folder.newFolder("my", "root", "some", "path");
        // This entry point points to a directory, which isn't an appropriate entry point
        File badEntryPoint = folder.newFolder("my", "root", "some", "path", "a", "b");
        folder.newFolder("my", "root", "some", "path", "c");
        Files.write(folder.newFile("my/root/some/path/index.html").toPath(), "some data".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/a/index.html").toPath(), "wrong index".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/c/style.css").toPath(), "css file".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/my_image.png").toPath(), "images".getBytes(StandardCharsets.UTF_8));
        ReportFiles files = GcsPluginUtils.createReportFiles(destination, Optional.of(badEntryPoint), root);
        assertThat(files.entryPoint().toString()).isEqualTo("some/path/path.zip");
        assertThat(readAllFiles(files).keySet()).containsExactly("some/path/path.zip");
    }

    @Test
    public void testCreateReportFiles_multipleFiles_withGoodHint() throws Exception {
        Path root = GcsPluginUtils.toNormalizedPath(folder.newFolder("my", "root"));
        File destination = folder.newFolder("my", "root", "some", "path");
        folder.newFolder("my", "root", "some", "path", "a", "b");
        folder.newFolder("my", "root", "some", "path", "c");
        // The hint is an actual file nested in the destination directory!
        File goodEntryPoint = folder.newFile("my/root/some/path/index.html");
        Files.write(goodEntryPoint.toPath(), "some data".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/a/index.html").toPath(), "wrong index".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/c/style.css").toPath(), "css file".getBytes(StandardCharsets.UTF_8));
        Files.write(folder.newFile("my/root/some/path/my_image.png").toPath(), "images".getBytes(StandardCharsets.UTF_8));
        ReportFiles files = GcsPluginUtils.createReportFiles(destination, Optional.of(goodEntryPoint), root);
        assertThat(files.entryPoint().toString()).isEqualTo("some/path/index.html");
        assertThat(readAllFiles(files)).containsExactly("some/path/index.html", "some data", "some/path/a/index.html", "wrong index", "some/path/c/style.css", "css file", "some/path/my_image.png", "images");
    }
}

